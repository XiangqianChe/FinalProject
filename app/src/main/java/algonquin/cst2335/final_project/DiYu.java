package algonquin.cst2335.final_project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

/**
 * This is the main activity, the functionalities of three buttons are in here, stationholder and routeholder are here, and queries are here
 * @author Di Yu
 * @version 1.0
 */
public class DiYu extends AppCompatActivity {

    ArrayList<String> buses = new ArrayList<String>();
    ArrayList<String> stations = new ArrayList<String>();
    ArrayList<Long> stopId = new ArrayList<Long>();
    SQLiteDatabase db;
    RouteAdapter routeAdapter;
    RecyclerView rv1;
    int clickedPosition=-1;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_di_yu);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.bus_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.bus_nav_open,R.string.bus_nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        db = new BusHelper(this).getWritableDatabase();

        Cursor results=db.rawQuery("Select * from "+BusHelper.TABLE_NAME,null);
        int idInd = results.getColumnIndex("_id");
        int stopNoInd = results.getColumnIndex(BusHelper.col_stop_no);

        while (results.moveToNext()){
            stopId.add(results.getLong(idInd));
            stations.add(results.getString(stopNoInd));
        }

        rv1 = findViewById(R.id.recyclerView1);
        StationAdapter stationAdapter=new StationAdapter();
        rv1.setAdapter(stationAdapter);
        rv1.setLayoutManager(new LinearLayoutManager(this));

        FrameLayout fy = findViewById(R.id.frameLayout);
        EditText et = findViewById(R.id.textView);
        Button btnSearch = findViewById(R.id.button);
        Button btnAdd = findViewById(R.id.button2);
        Button btnDelete = findViewById(R.id.delete);

        RecyclerView rv2 = findViewById(R.id.recyclerView2);
        routeAdapter=new RouteAdapter();
        rv2.setAdapter(routeAdapter);
        rv2.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener( clk -> {
            ContentValues newRow=new ContentValues();
            newRow.put(BusHelper.col_stop_no,  et.getText().toString());
            stations.add(et.getText().toString());
            Long stationId=db.insert(BusHelper.TABLE_NAME,null,newRow);
            stopId.add(stationId);
            stationAdapter.notifyItemInserted(stations.size());
            Toast.makeText(getApplicationContext(), " You successfully add the stop number"  ,Toast.LENGTH_LONG).show();
        });

        btnDelete.setOnClickListener( clk -> {
            if(clickedPosition>=0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DiYu.this);
                builder.setMessage("Do you want to delete this stop number: " + stations.get(clickedPosition))
                        .setTitle("Question:")
                        .setNegativeButton("No", (dialog, cl) -> {
                        })
                        .setPositiveButton("yes", (dialog, cl) -> {
                            Long removedStopID = stopId.remove(clickedPosition);
                            db.delete(BusHelper.TABLE_NAME, "_id=?", new String[]{Long.toString(removedStopID)});
                            String removedStopNumber = stations.remove(clickedPosition);
                            //stations.remove("_id");
                            stationAdapter.notifyItemRemoved(clickedPosition);

                            Snackbar.make(rv1, "You deleted stop number #" + clickedPosition, Snackbar.LENGTH_LONG)
                                    .setAction("undo", click -> {
                                        stations.add(removedStopNumber);//(removedStopNumber);
                                        stopId.add(removedStopID);
                                        ContentValues newRow=new ContentValues();
                                        newRow.put(BusHelper.col_stop_no,  removedStopNumber);
                                        newRow.put("_id",  removedStopID);
                                        db.insert(BusHelper.TABLE_NAME, null,newRow);
                                        stationAdapter.notifyItemInserted(stations.size());
                                    }).show();
                            clickedPosition = -1;

                        }).show();

            }
            });

        btnSearch.setOnClickListener( clk -> {
            String stopNo=et.getText().toString();
            queryStationRoutes(stopNo);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bus_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch (item.getItemId()){
            case R.id.bus_help:
                new AlertDialog.Builder(this)
                        .setTitle("Bus app industry")
                        .setNegativeButton("help",(a,b)->{})
                        .show();
                break;
        }

        return true;
    }


    /**
     * this method is to deal with the query for the buses passing by this bus stop
     * @param stopNo stop number
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void queryStationRoutes(String stopNo) {
        buses.clear();
        rv1.removeAllViews();
        routeAdapter.notifyDataSetChanged();
        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(()->{
            URL url = null;
            try {
                String stringURL="https://api.octranspo1.com/v2.0/GetRouteSummaryForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo="
                        +stopNo
                        +"&format=json";
                url = new URL(stringURL);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.getInputStream();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String text = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                JSONObject theDoc = new JSONObject(text).getJSONObject("GetRouteSummaryForStopResult");
                JSONArray busesJSON = theDoc.getJSONObject("Routes").getJSONArray("Route");
                for(int i=0;i<busesJSON.length();++i){
                    buses.add(busesJSON.getJSONObject(i).getString("RouteNo"));
                }
                runOnUiThread(()->{
                    routeAdapter.setStation(stopNo);
                    routeAdapter.notifyItemInserted(buses.size());
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * this method is to deal with the query for the next trips for stop
     * @param stopNo stop number
     * @param routeNo route number
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void queryNextTripsForStop(String stopNo, String routeNo) {

        Executor newThread = Executors.newSingleThreadExecutor();
        newThread.execute(()->{
            URL url = null;
            try {

                String stringURL="https://api.octranspo1.com/v2.0/GetNextTripsForStop?appID=223eb5c3&&apiKey=ab27db5b435b8c8819ffb8095328e775&stopNo="
                        +stopNo+"&routeNo="
                        +routeNo+"&format=json";
                url = new URL(stringURL);
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                String text=new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))
                        .lines()
                        .collect(Collectors.joining("\n"));
                JSONObject doc=new JSONObject(text);

                JSONObject tripInfo = doc.getJSONObject("GetNextTripsForStopResult").getJSONObject("Route").getJSONArray("RouteDirection")
                        .getJSONObject(0).getJSONObject("Trips")
                        .getJSONArray("Trip").getJSONObject(0);
                runOnUiThread(()->{
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new BusDetails(tripInfo,stopNo)).commit();
                });

            } catch ( IOException e) {
                e.printStackTrace();
            } catch (JSONException e){
                e.printStackTrace();
            }
        });
    }


    class StationHolder extends RecyclerView.ViewHolder{

        int position;
        TextView bus_item;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public StationHolder(View itemView) {
            super(itemView);
            bus_item = itemView.findViewById(R.id.bus_item);

            itemView.setOnClickListener(view->{
                int position = getAbsoluteAdapterPosition();
                clickedPosition=position;
                queryStationRoutes(stations.get(position));
            });

        }
    }

    class RouteHolder extends RecyclerView.ViewHolder{

        int position;
        TextView bus_item;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public RouteHolder(View itemView,String station) {
            super(itemView);
            bus_item = itemView.findViewById(R.id.bus_item);

            itemView.setOnClickListener(view->{
                int position = getAbsoluteAdapterPosition();
                queryNextTripsForStop(station,buses.get(position));
            });
        }
    }

    class RouteAdapter extends RecyclerView.Adapter<RouteHolder> {

        private String station;
        public void setStation(String station) {
            this.station = station;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public RouteHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=getLayoutInflater();
            View loadedRow=inflater.inflate(R.layout.bus_station_item,parent,false);
            return new RouteHolder(loadedRow,station);
        }

        @Override
        public void onBindViewHolder(RouteHolder holder, int position) {
            holder.bus_item.setText("bus number: "+buses.get(position));
            holder.position=position;
        }

        @Override
        public int getItemCount() {
            return buses.size();
        }
    }

    class StationAdapter extends RecyclerView.Adapter<StationHolder> {

        @RequiresApi(api = Build.VERSION_CODES.N)
        public StationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=getLayoutInflater();
            View loadedRow=inflater.inflate(R.layout.bus_station_item,parent,false);
            return new StationHolder(loadedRow);
        }

        @Override
        public void onBindViewHolder(StationHolder holder, int position) {
            holder.bus_item.setText("Station number: "+stations.get(position));
            holder.position=position;
        }

        @Override
        public int getItemCount() {
            return stations.size();
        }
    }


}
