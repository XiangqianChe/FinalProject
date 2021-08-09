package algonquin.cst2335.final_project;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * fragment 1 for station list
 * @author Xiangqian Che
 */
public class StationFragment extends Fragment {

    EditText et_lat;
    EditText et_long;
    Button btn_search;
    RecyclerView rv_station;
    ArrayList<StationInfo> infos = new ArrayList<>();
    ArrayList<StationFragment.StationInfo> infos_favorites = new ArrayList<>();
    StationAdapter adapter;
    StationAdapter adapter_favorites;
//    OpenHelper opener;
    SQLiteDatabase db;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * views in activity_xiangqian_che.xml
         */
        View stationLayout = inflater.inflate(R.layout.activity_xiangqian_che, container, false);
        et_lat = stationLayout.findViewById(R.id.et_lat);
        et_long = stationLayout.findViewById(R.id.et_long);
        btn_search = stationLayout.findViewById(R.id.btn_search);
        rv_station = stationLayout.findViewById(R.id.rv_station);

        /**
         * shared preferences to put edit texts into file system
         */
        SharedPreferences sp = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String sp_lat = sp.getString("Latitude","");
        String sp_long = sp.getString("Longitude","");
        et_lat.setText(sp_lat);
        et_long.setText(sp_long);

        /**
         * database to hold favorites
         */

        OpenHelper opener = new OpenHelper(getContext());
        db = opener.getWritableDatabase();
//        Cursor results = db.rawQuery("select * from " + OpenHelper.TABLE_NAME + ";", null);
//        int _idCol = results.getColumnIndex("_id");
//        int titleCol = results.getColumnIndex(OpenHelper.col_title);
//        int latCol = results.getColumnIndex(OpenHelper.col_lat);
//        int longCol = results.getColumnIndex(OpenHelper.col_long);
//        int telCol = results.getColumnIndex(OpenHelper.col_tel);
//        while(results.moveToNext()) {
//            long id = results.getInt(_idCol);
//            String r_title = results.getString(titleCol);
//            String r_lat = results.getString(latCol);
//            String r_long = results.getString(longCol);
//            String r_tel = results.getString(telCol);
//            infos.add(new StationInfo(id, r_title, r_lat, r_long, r_tel));
//        }
        /**
         * put infos into recycler view
         */
        adapter = new StationAdapter(infos);
        adapter_favorites = new StationAdapter(infos_favorites);
        rv_station.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        /**
         * click search button for charging station infos
         */
        btn_search.setOnClickListener(click->{
            rv_station.setAdapter(adapter);
            Executor newThread = Executors.newSingleThreadExecutor();
            /**
             * display last searched edit texts
             */
            String latitude = et_lat.getText().toString();
            String longitude = et_long.getText().toString();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Latitude", latitude);
            editor.putString("Longitude", longitude);
            editor.apply();

            /**
             * reset recycler view after search
             */
            if (rv_station.getChildCount() > 0 ) {
                rv_station.removeAllViews();
                adapter.notifyDataSetChanged();
            }
            /**
             * new thread to manage search
             */
            newThread.execute(()->{
                try {
                    /**
                     * clear infos list
                     */
                    infos.clear();
                    /**
                     * search to get a json file and retrieve data
                     */
                    String stringURL = "https://api.openchargemap.io/v3/poi/?countrycode=CA&latitude="+latitude+"&longitude="+longitude+"&maxresults=10&key=123";
                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String text = (new BufferedReader(
                            new InputStreamReader(in, StandardCharsets.UTF_8)))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    JSONArray jsonArray = new JSONArray(text);
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject st = jsonArray.getJSONObject(i);
                        JSONObject ai = st.getJSONObject("AddressInfo");
                        String info_title = ai.getString("Title");
                        String info_lat = ai.getString("Latitude");
                        String info_long = ai.getString("Longitude");
                        String info_tel = ai.getString("ContactTelephone1");
                        StationInfo si = new StationInfo(info_title, info_lat, info_long, info_tel);

//                        ContentValues newRow = new ContentValues();
//                        newRow.put(OpenHelper.col_title, si.getTitle());
//                        newRow.put(OpenHelper.col_lat, si.getLat());
//                        newRow.put(OpenHelper.col_long, si.getLong());
//                        newRow.put(OpenHelper.col_tel, si.getTel());
//                        long newId = db.insert(OpenHelper.TABLE_NAME, OpenHelper.col_title, newRow);
//                        si.setId(newId);

                        infos.add(si);
                    }
                    /**
                     * insert searched data into a list
                     */
                    XiangqianChe parentActivity = (XiangqianChe)getContext();
                    parentActivity.runOnUiThread(()->{
                        adapter.notifyItemInserted(infos.size() - 1);
                    });
                } catch (IOException | JSONException e) {
                    Log.e("Connection error: ", e.getMessage());
                }
            });
            /**
             * empty the edit texts after search
             */
            et_lat.setText("");
            et_long.setText("");
        });
        return stationLayout;
    }

    /**
     * delete a certain info
     * @param chosenInfo
     * @param chosenPosition
     */
    public void notifyInfoDeleted(StationInfo chosenInfo, int chosenPosition) {
        /**
         * alert dialog to delete or cancel delete
         */
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Do you want to delete info #" + chosenInfo.getTitle())
                .setTitle("Question: ")
                .setNegativeButton("No", (dialog, click)->{
                    //position = getAbsoluteAdapterPosition();
                    Toast.makeText(getContext(), "You did not delete info #" + chosenPosition, Toast.LENGTH_LONG).show();
                })
                .setPositiveButton("Yes", (dialog, click)->{
                    //position = getAbsoluteAdapterPosition();
                    infos_favorites.remove(chosenPosition);
                    adapter_favorites.notifyItemRemoved(chosenPosition);
                    db.delete(OpenHelper.TABLE_NAME, "_id=?", new String[] {Long.toString(chosenInfo.getId())});

                    /**
                     * snack bar to undo delete
                     */
                    Snackbar.make(btn_search, "You deleted info #" + chosenPosition, Snackbar.LENGTH_LONG)
                            .setAction("Undo", click2->{
                                infos_favorites.add(chosenPosition, chosenInfo);

                                ContentValues newRow = new ContentValues();
                                newRow.put("_id", chosenInfo.getId());
                                newRow.put(OpenHelper.col_title, chosenInfo.getTitle());
                                newRow.put(OpenHelper.col_lat, chosenInfo.getLat());
                                newRow.put(OpenHelper.col_long, chosenInfo.getLong());
                                newRow.put(OpenHelper.col_tel, chosenInfo.getTel());
                                db.insert(OpenHelper.TABLE_NAME, null, newRow);

                                adapter_favorites.notifyItemInserted(chosenPosition);
                            })
                            .show();
                })
                .create().show();
    }

    public void notifyConvertToFavorite() {
        infos_favorites.clear();
        Cursor results = db.rawQuery("select * from " + OpenHelper.TABLE_NAME + ";", null);
        int _idCol = results.getColumnIndex("_id");
        int titleCol = results.getColumnIndex(OpenHelper.col_title);
        int latCol = results.getColumnIndex(OpenHelper.col_lat);
        int longCol = results.getColumnIndex(OpenHelper.col_long);
        int telCol = results.getColumnIndex(OpenHelper.col_tel);
        while(results.moveToNext()) {
            long id = results.getInt(_idCol);
            String r_title = results.getString(titleCol);
            String r_lat = results.getString(latCol);
            String r_long = results.getString(longCol);
            String r_tel = results.getString(telCol);
            infos_favorites.add(new StationInfo(id, r_title, r_lat, r_long, r_tel));
        }

        rv_station.setAdapter(adapter_favorites);

    }

    /**
     * rows in recycler view
     */
    private class RowView extends RecyclerView.ViewHolder {

        TextView info_title;

        int position = -1;

        public RowView(View itemView, List<StationInfo> infos) {
            super(itemView);

            itemView.setOnClickListener(click->{
                XiangqianChe parentActivity = (XiangqianChe)getContext();
                int position = getAbsoluteAdapterPosition();
                parentActivity.userClickedInfo(infos.get(position), position);
            });

            info_title = itemView.findViewById(R.id.info_title);
        }

        public void setPosition(int p) {
            position = p;
        }
    }

    /**
     * container to hold rows of recycler view
     */
    class StationAdapter extends RecyclerView.Adapter<RowView> {
        List<StationInfo> infos;
        StationAdapter(List<StationInfo> infos){
            this.infos=infos;
        }

        @Override
        public RowView onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View loadedRow = inflater.inflate(R.layout.station_info, parent, false);
            return new RowView(loadedRow, infos);
        }

        @Override
        public void onBindViewHolder(RowView holder, int position) {
            holder.info_title.setText(infos.get(position).getTitle());
            holder.setPosition(position);
        }

        @Override
        public int getItemCount() {
            return infos.size();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }

    /**
     * station info
     */
    class StationInfo{
        long id;
        String title;
        String latitude;
        String longitude;
        String tel;

        public StationInfo(String title, String latitude, String longitude, String tel) {
            this.title = title;
            this.latitude = latitude;
            this.longitude = longitude;
            this.tel = tel;
        }

        public StationInfo(long id, String title, String latitude, String longitude, String tel) {
            this.id = id;
            this.title = title;
            this.latitude = latitude;
            this.longitude = longitude;
            this.tel = tel;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getLat() {
            return latitude;
        }

        public String getLong() {
            return longitude;
        }

        public String getTel() {
            return tel;
        }
    }


}
