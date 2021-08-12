package algonquin.cst2335.final_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * this fragment shows the routes that passes a specific station
 * @author Di Yu
 * @Version 1.0
 */
public class BusDetails extends Fragment {
    JSONObject routeInfo;
    TextView destination;
    TextView startTime;
    TextView adjTime;
    TextView latitude;
    TextView longitude;
    TextView speed;
    final String stopNo;
    int chosenPosition;

    public BusDetails(JSONObject routeInfo, String stopNo){
        this.routeInfo=routeInfo;
        this.stopNo=stopNo;
        this.chosenPosition=chosenPosition;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.next_bus, container, false);

        destination = inflate.findViewById(R.id.destination);
        latitude = inflate.findViewById(R.id.latitude);
        longitude = inflate.findViewById(R.id.longitude);
        speed = inflate.findViewById(R.id.GPSspeed);
        startTime = inflate.findViewById(R.id.startTime);
        adjTime = inflate.findViewById(R.id.adjustedScheduleTime);

        try {
            destination.setText("destination: " + routeInfo.getString("TripDestination"));
            latitude.setText("latitude: "+routeInfo.getString("Latitude"));
            longitude.setText("longitude: "+routeInfo.getString("Longitude"));
            speed.setText("speed: "+routeInfo.getString("GPSSpeed"));
            startTime.setText("start time: " + routeInfo.getString("TripStartTime"));
            adjTime.setText("adjustedScheduleTime: "+routeInfo.getString("AdjustedScheduleTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return inflate;
    }
}

