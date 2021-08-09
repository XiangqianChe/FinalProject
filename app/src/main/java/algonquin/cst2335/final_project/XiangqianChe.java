package algonquin.cst2335.final_project;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

/**
 * main activity for the charging station
 * @author Xiangqian Che
 */
public class XiangqianChe extends AppCompatActivity {

    StationFragment sf;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_fragment);

        /**
         * replace ActionBar with Toolbar
         */
//        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        /**
         * Navigation drawer from start
         */
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView  = findViewById(R.id.popout_menu);
        navigationView.setNavigationItemSelectedListener((item)->{
            onOptionsItemSelected(item);
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });

        /**
         * fragment 1 for station list
         */
        sf = new StationFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment, sf);
        ft.commit();
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment, new StationFragment()).commit();
    }

    /**
     * fragment 2 for station detail
     * @param stationInfo
     * @param position
     */
    public void userClickedInfo(StationFragment.StationInfo stationInfo, int position) {
        StationDetailFragment sdf = new StationDetailFragment(stationInfo, position);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, sdf).commit();
    }

    /**
     * call notifyInfoDeleted() in StationFragment.java to delete a certain info
     * @param chosenInfo
     * @param chosenPosition
     */
    public void notifyInfoDeleted(StationFragment.StationInfo chosenInfo, int chosenPosition) {
        sf.notifyInfoDeleted(chosenInfo, chosenPosition);
    }


    /**
     * create options for menu/toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_xiangqian_che, menu);
        return true;
    }

    /**
     * select an option of menu/toolbar
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This is an empty help menu!")
                        .setTitle("Help Manual: ")
                        .create().show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}