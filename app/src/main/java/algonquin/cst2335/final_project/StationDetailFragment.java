package algonquin.cst2335.final_project;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * fragment 2 for station detail
 * @author Xiangqian Che
 */
public class StationDetailFragment extends Fragment {

    StationListFragment.StationInfo chosenInfo;
    int chosenPosition;
    SQLiteDatabase db;
    int type;

    public  StationDetailFragment(StationListFragment.StationInfo info, int position, int type) {
        chosenInfo = info;
        chosenPosition = position;
        this.type=type;
    }

    private void setUpSearch(View detailView){
        setUp(detailView);

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
    }

    private void setUp(View detailView){
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
         * close button
         */
        Button btn_close = detailView.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(click_close->{
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });

    }

    private void setUpFavorite(View detailView){
        setUp(detailView);

        /**
         * delete button
         */
        Button btn_delete = detailView.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(click_delete->{
            XiangqianChe parentActicity = (XiangqianChe)getContext();
            parentActicity.notifyInfoDeleted(chosenInfo, chosenPosition);
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailView =null;
        switch (type){
            case 0:
                detailView =inflater.inflate(R.layout.station_detail_fragment, container, false);
                setUpSearch(detailView);
                break;
            case 1:
                detailView =inflater.inflate(R.layout.station_favorite_detail_fragment, container, false);
                setUpFavorite(detailView);
        }
        return detailView;
    }
}
