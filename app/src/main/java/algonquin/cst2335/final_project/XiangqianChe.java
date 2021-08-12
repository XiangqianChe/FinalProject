package algonquin.cst2335.final_project;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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

    StationListFragment sf;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_cxq);

        /**
         * replace ActionBar with Toolbar
         */
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        /**
         * Navigation drawer from start
         */
        drawerLayout = findViewById(R.id.station_drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView  = findViewById(R.id.station_popout_menu);
        navigationView.setNavigationItemSelectedListener((item)->{
            onOptionsItemSelected(item);
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });

        /**
         * fragment 1 for station list
         */
        sf = new StationListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.frameLayout,sf)
                .commit();
    }

    /**
     * fragment 2 for station detail
     * @param stationInfo
     * @param position
     */
    public void userClickedInfo(StationListFragment.StationInfo stationInfo, int position, int type) {
        StationDetailFragment sdf = new StationDetailFragment(stationInfo, position, type);
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, sdf).commit();
    }

    /**
     * call notifyInfoDeleted() in StationFragment.java to delete a certain info
     * @param chosenInfo
     * @param chosenPosition
     */
    public void notifyInfoDeleted(StationListFragment.StationInfo chosenInfo, int chosenPosition) {
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
            case R.id.station_help:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Enter latitude and longitude from -90 to 90, and search." +
                        "A list will be displayed below. Click one to see details." +
                        "You can add one to your favorites.")
                        .setTitle("Help Manual: ")
                        .create().show();
                break;
            case R.id.station_favorites:
                sf.notifyConvertToFavorite();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}