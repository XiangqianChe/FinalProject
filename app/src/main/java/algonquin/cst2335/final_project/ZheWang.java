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
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

/**
 * main activity for the movie
 * @author Zhe Wang
 */
public class ZheWang extends AppCompatActivity {

    MovieListFragment movieListFragment;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_list_fragment);

        /**
         * replace ActionBar with Toolbar
         */
        toolbar = findViewById(R.id.movie_toolbar);
        setSupportActionBar(toolbar);
        /**
         * Navigation drawer from start
         */
        drawerLayout = findViewById(R.id.movie_drawer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView  = findViewById(R.id.movie_popout_menu);
        navigationView.setNavigationItemSelectedListener((item)->{
            onOptionsItemSelected(item);
            drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });

        /**
         * fragment 1 for movie list
         */
        movieListFragment = new MovieListFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragment, movieListFragment);
        ft.commit();
        //getSupportFragmentManager().beginTransaction().add(R.id.fragment, new MovieListFragment()).commit();
    }

    /**
     * fragment 2 for movie detail
     * @param movieInfo
     * @param position
     */
    public void userClickedInfo(MovieListFragment.MovieInfo movieInfo, int position) {
        MovieDetailFragment movieDetailFragment = new MovieDetailFragment(movieInfo, position);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment, movieDetailFragment).commit();
    }

    /**
     * call notifyInfoDeleted() in MovieFragment.java to delete a certain info
     * @param chosenInfo
     * @param chosenPosition
     */
    public void notifyInfoDeleted(MovieListFragment.MovieInfo chosenInfo, int chosenPosition) {
        movieListFragment.notifyInfoDeleted(chosenInfo, chosenPosition);
    }


    /**
     * create options for menu/toolbar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_zhe_wang,menu);
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
                builder.setMessage("Enter a movie name and click search. Movies searche are listed under. Click to see details.")
                        .setTitle("Help Manual: ")
                        .create().show();
                break;
        }
        return true;
    }
}