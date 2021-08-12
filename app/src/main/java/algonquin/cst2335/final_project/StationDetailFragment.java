package algonquin.cst2335.final_project;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * fragment 2 for station detail
 * @author Xiangqian Che
 */
public class StationDetailFragment extends Fragment {

    StationFragment.StationInfo chosenInfo;
    int chosenPosition;
    SQLiteDatabase db;

    public  StationDetailFragment(StationFragment.StationInfo info, int position) {
        chosenInfo = info;
        chosenPosition = position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.station_detail_fragment, container, false);
        OpenHelper opener = new OpenHelper(getContext());
        db = opener.getWritableDatabase();
        TextView detail_title = detailView.findViewById(R.id.detail_title);
        TextView detail_lat = detailView.findViewById(R.id.detail_lat);
        TextView detail_long = detailView.findViewById(R.id.detail_long);
        TextView detail_tel = detailView.findViewById(R.id.detail_tel);

        detail_title.setText("Title is: " + chosenInfo.getTitle());
        detail_lat.setText("Latitude is: " + chosenInfo.getLat());
        detail_long.setText("Longitude is: " + chosenInfo.getLong());
        detail_tel.setText("Tel is: " + chosenInfo.getTel());
        /**
         * click to redirect to google maps
         */
        Button btn_gmap = detailView.findViewById(R.id.btn_gmap);
        btn_gmap.setOnClickListener(click->{
            Uri gmmIntentUri = Uri.parse("geo:" + chosenInfo.getLat() + "," + chosenInfo.getLong());
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            //if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            //}
        });
        /**
         * click to add to favorites
         */
        Button btn_favorites = detailView.findViewById(R.id.btn_favorites);
        btn_favorites.setOnClickListener(click->{

            ContentValues newRow = new ContentValues();
            newRow.put(OpenHelper.col_title, chosenInfo.getTitle());
            newRow.put(OpenHelper.col_lat, chosenInfo.getLat());
            newRow.put(OpenHelper.col_long, chosenInfo.getLong());
            newRow.put(OpenHelper.col_tel, chosenInfo.getTel());
            db.insert(OpenHelper.TABLE_NAME, null, newRow);
        });
        /**
         * close button
         */
        Button btn_close = detailView.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(click_close->{
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
        /**
         * delete button
         */
        Button btn_delete = detailView.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(click_delete->{
            XiangqianChe parentActicity = (XiangqianChe)getContext();
            parentActicity.notifyInfoDeleted(chosenInfo, chosenPosition);
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
        return detailView;
    }
}
