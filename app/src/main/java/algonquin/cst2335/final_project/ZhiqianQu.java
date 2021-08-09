package algonquin.cst2335.final_project;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;


public class ZhiqianQu extends AppCompatActivity {
    /**
     * The key of SharedPreferences for the soccer game rating
     */
    public static final String SOCCERREFERENCES = "SoccerPrefs" ;
    private Button forecastBtn = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showRatingDialogBox();
    }


    /**
     * The method shows a rating dialog.
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

        Button submitButton = (Button) rankDialog.findViewById(R.id.rank_dialog_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor prefsEditor = soccerGamePrefs.edit();
                prefsEditor.putFloat("rate", ratingBar.getRating());
                prefsEditor.commit();
                rankDialog.dismiss();
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
     * call the method displaySoccerGameUsage
     * @param item  Sets the visibility of the menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
                goBack();
                break;
            case R.id.soccerHelper:
                soccerGameUsage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void soccerGameUsage() {

        AlertDialog helperDialog = new AlertDialog.Builder(this)
        .setTitle(R.string.soccer_helper_title)
        .setMessage(getResources().getString(R.string.soccer_helper_usage))
        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
        })
        .show();
    }

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