package algonquin.cst2335.final_project;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


/**
 * extend from Fragment,this class was created for the right of soccer
 * @author Zhiqian
 * @version 1.0
 * @since 2021-8-1
 */

public class FragmentRightOfSoccer extends Fragment {
    /**
     * These vorables are used to represents the things show in the right fragment
     */
    private TextView publicDate;
    private TextView url;
    private TextView description;
    private ImageView thumbnail;
    private static SoccerRssItem rssItem;
    private static Button changedBtn;

    /**
     * this is a non-argument construction
     */
    public FragmentRightOfSoccer() {

    }

    /**
     * onCreate
     * @param savedInstanceState is a kind of Bundle. Bundle used to pass data between Activities
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * Create the view of fragment.
     * @param inflater  is a LayoutInflater type
     * @param container  is a ViewGroup type
     * @param savedInstanceState is a Bundle type
     *
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View soccerDetailView = inflater.inflate(R.layout.right_fragment_of_soccer, container, false);
        publicDate = soccerDetailView.findViewById(R.id.soccerRSSPubDate);
        url = soccerDetailView.findViewById(R.id.soccerRSSUrl);
        description = soccerDetailView.findViewById(R.id.soccerRSSDescription);
        thumbnail = soccerDetailView.findViewById(R.id.soccerRSSImage);

        Bundle bundle = this.getArguments();
        displayNewsDetail(bundle);

        if(changedBtn == null) {
            changedBtn = soccerDetailView.findViewById(R.id.soccerSaveNewsBtn);
        }
        if (rssItem != null && rssItem.getId() != SoccerRssItem.INVALID_ID){
            changedBtn.setText("remove");//change the name of button into "remove"
        }

        changedBtn.setOnClickListener(clk -> {
            if (rssItem.getId() != SoccerRssItem.INVALID_ID){
                removeItemFromDatabase(rssItem.getId());
            }else{
                saveItemToDatabase(rssItem);
            }

        });

        Button urlBtn = soccerDetailView.findViewById(R.id.soccerOpenUrlBtn);// used to open the url in browser
        urlBtn.setOnClickListener(clk -> {
            if(rssItem != null){
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.getLink()));
                startActivity(browser);
            }
        });
        return soccerDetailView;
    }

    /**
     * onCreateOptionsMenu specify the options menu for an activity
     * @param menu is the type of Menu
     * @param inflater MenuInflater is used to instantiate menu XML files into Menu objects
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.soccer_top_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    /**
     * the detail of list, which includes title,link and photo
     * @param bundle   is used to passe pass data between Activities
     */
    private void displayNewsDetail(Bundle bundle){
        if (bundle != null) {
            rssItem = bundle.getParcelable("soccerNews");
            Picasso.get().load(rssItem.getThumbnail()).into(thumbnail);
            url.setText("URL: " + rssItem.getLink());
            publicDate.setText("PubDate: " + rssItem.getPubDate());
            description.setText("Description: " + rssItem.getDescription());
        }
    }

    /**
     * save a new record
     * @param item   this is the record that be saved
     *
     */
    private void saveItemToDatabase(SoccerRssItem item){
        if(item.getId() == SoccerRssItem.INVALID_ID){
            SoccerDbHelper db = new SoccerDbHelper(getContext());
            db.addItem(rssItem);
        }
    }

    /**
     * delete saved record
     * @param id  The id that you should delete
     */
    private void removeItemFromDatabase(int id){
        SoccerDbHelper db = new SoccerDbHelper(getContext());
        db.deleteItemById(String.valueOf(id));
    }
}