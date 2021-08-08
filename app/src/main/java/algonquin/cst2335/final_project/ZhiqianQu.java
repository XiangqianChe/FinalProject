package algonquin.cst2335.final_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;


public class ZhiqianQu extends AppCompatActivity {
    /**
     * The key of SharedPreferences for the soccer game rating
     */
    public static final String SOCCERGAMEPREFERENCES = "SoccerPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_zhiqian_qu);
        showRatingDialogBox();
    }

    /**
     * The method shows a rating dialog box which asks to rate the application using 5 stars.
     */
    public void showRatingDialogBox(){
        Dialog rankDialog = new Dialog(this, R.style.SoccerGameFullHeightDialog);
        rankDialog.setContentView(R.layout.soccer_rating_dialog);
        rankDialog.setCancelable(true);
        RatingBar ratingBar = (RatingBar) rankDialog.findViewById(R.id.dialog_ratingbar);

        SharedPreferences soccerGamePrefs = getSharedPreferences(SOCCERGAMEPREFERENCES, Context.MODE_PRIVATE);
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
        //now that the dialog is set up, it's time to show it
        rankDialog.show();
    }
}