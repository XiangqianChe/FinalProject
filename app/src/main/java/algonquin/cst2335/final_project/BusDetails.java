package algonquin.cst2335.final_project;

import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

public class BusDetails extends Fragment {
    JSONArray routes;
    RecyclerView routesView;
    TextView destination;
    TextView startTime;
    TextView adjTime;
    TextView latitude;
    TextView longitude;
    TextView speed;
    final String stopNo;
    int chosenPosition;
    DiYu parent;

    public BusDetails(JSONArray routes, String stopNo, int chosenPosition){
        this.routes=routes;
        this.stopNo=stopNo;
        this.chosenPosition=chosenPosition;
    }
}

