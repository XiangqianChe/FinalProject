package algonquin.cst2335.final_project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class DiYu extends AppCompatActivity {

    ArrayList<String> buses = new ArrayList<String>();
    ArrayList<String> stations = new ArrayList<String>();
    ArrayList<Long> stopId = new ArrayList<Long>();
    SQLiteDatabase db;
    RouteAdapter routeAdapter;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_di_yu);
        db = new BusHelper(this).getWritableDatabase();

        Cursor results=db.rawQuery("Select * from "+BusHelper.TABLE_NAME,null);
        int idInd = results.getColumnIndex("_id");
        int stopNoInd = results.getColumnIndex(BusHelper.col_stop_no);

        while (results.moveToNext()){
            stopId.add(results.getLong(idInd));
            stations.add(results.getString(stopNoInd));
        }

        RecyclerView rv1 = findViewById(R.id.recyclerView1);
        StationAdapter stationAdapter=new StationAdapter();
        rv1.setAdapter(stationAdapter);
        rv1.setLayoutManager(new LinearLayoutManager(this));

        FrameLayout fy = findViewById(R.id.frameLayout);
        EditText et = findViewById(R.id.textView);
        Button btnSearch = findViewById(R.id.button);
        Button btnAdd = findViewById(R.id.button2);

        RecyclerView rv2 = findViewById(R.id.recyclerView2);
        routeAdapter=new RouteAdapter();
        rv2.setAdapter(routeAdapter);
        rv2.setLayoutManager(new LinearLayoutManager(this));

        btnAdd.setOnClickListener( clk -> {
            ContentValues newRow=new ContentValues();
            newRow.put(BusHelper.col_stop_no,  et.getText().toString());
            stations.add(et.getText().toString());
            db.insert(BusHelper.TABLE_NAME,null,newRow);
            stationAdapter.notifyItemInserted(stations.size());
        });

        btnSearch.setOnClickListener( clk -> {
            String stopNo=et.getText().toString();
            queryStationRoutes(stopNo);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void queryStationRoutes(String stopNo){

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
                    routeAdapter.notifyItemInserted(buses.size());
                });
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }
    private class BusStationHolder extends RecyclerView.ViewHolder{

        int position;
        TextView bus_item;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public BusStationHolder(View itemView) {
            super(itemView);
            bus_item = itemView.findViewById(R.id.bus_item);
            itemView.setOnClickListener(view->{
                int position = getAbsoluteAdapterPosition();
                queryStationRoutes(stations.get(position));
            });

        }
    }

    private class RouteAdapter extends RecyclerView.Adapter<BusStationHolder> {



        @RequiresApi(api = Build.VERSION_CODES.N)
        public BusStationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=getLayoutInflater();
            View loadedRow=inflater.inflate(R.layout.bus_station_item,parent,false);
            return new BusStationHolder(loadedRow);
        }

        @Override
        public void onBindViewHolder(BusStationHolder holder, int position) {
            holder.bus_item.setText("bus number: "+buses.get(position));
            holder.position=position;
        }

        @Override
        public int getItemCount() {
            return buses.size();
        }
    }

    private class StationAdapter extends RecyclerView.Adapter<BusStationHolder> {



        @RequiresApi(api = Build.VERSION_CODES.N)
        public BusStationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater=getLayoutInflater();
            View loadedRow=inflater.inflate(R.layout.bus_station_item,parent,false);
            return new BusStationHolder(loadedRow);
        }

        @Override
        public void onBindViewHolder(BusStationHolder holder, int position) {
            holder.bus_item.setText("Station number: "+stations.get(position));
            holder.position=position;
        }

        @Override
        public int getItemCount() {
            return stations.size();
        }
    }
}