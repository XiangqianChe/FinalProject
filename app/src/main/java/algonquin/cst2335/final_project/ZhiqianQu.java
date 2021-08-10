package algonquin.cst2335.final_project;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

/**
 * This file was created for the part of Android. extend from  AppCompatActivity
 * @author Zhiqian Qu
 *  * @version 1.0
 *  * @since 2021-8-1
 */
public class ZhiqianQu extends AppCompatActivity {

    public static final String SOCCERREFERENCES = "SoccerPrefs" ;
    private Button forecastBtn = null;
    Toolbar toolbar;
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showRatingDialogBox();
        setContentView(R.layout.activity_zhiqian_qu);

        toolbar = findViewById(R.id.soccerToolbar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle;
        NavigationView navigationView;

        /**
         * Navigation drawer from start
         */
        drawerLayout = findViewById(R.id.drawer_layout_soccer);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView  = findViewById(R.id.soccer_popout_menu);
        navigationView.setNavigationItemSelectedListener((item)->{
            onOptionsItemSelected(item);
           // drawerLayout.closeDrawer(GravityCompat.START);
            return false;
        });

        //showRatingDialogBox();

    }


    /**
     *  shows rate dialog.
     */
    public void showRatingDialogBox(){
        Dialog rankDialog = new Dialog(this, R.style.SoccerGameFullHeightDialog);
        rankDialog.setContentView(R.layout.soccer_rating);
        rankDialog.setCancelable(true);
        RatingBar ratingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);

        SharedPreferences soccerGamePrefs = getSharedPreferences(SOCCERREFERENCES, Context.MODE_PRIVATE);
        float rateValue = soccerGamePrefs.getFloat("rate", 0);
        ratingBar.setRating(rateValue);

        TextView text = (TextView) rankDialog.findViewById(R.id.rank_dialog_text1);
        text.setText(R.string.soccer_rating_text);

        Button confirmBtn = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefsEditor = soccerGamePrefs.edit();
                prefsEditor.putFloat("rate", ratingBar.getRating());
                prefsEditor.commit();
                rankDialog.dismiss();
                //this is the place need to be modified
                setContentView(R.layout.activity_zhiqian_qu);
            }
        });
        rankDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.soccer_top_bar, menu);
        return true;
    }

    /**
     * call the method displaySoccerGameUsage and go back.
     * @param item  Sets the visibility of the menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.back:
                goBack();
                break;
            case R.id.soccerHelper:
                soccerInstruction();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    /**
     * explaining for how to use it
     */
    private void soccerInstruction() {

        AlertDialog helperDialog = new AlertDialog.Builder(this)
        .setTitle(R.string.soccer_helper_title)
        .setMessage(getResources().getString(R.string.soccer_usage))
        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
        })
        .show();
    }

    /**
     * chooose if go back
     */
    private void goBack() {

        AlertDialog helperDialog = new AlertDialog.Builder(this)
                .setTitle("Go back")
                .setMessage("Do you want to go back to main menu?")
                .setNegativeButton("confirm",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .show();
    }
}