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
 * Show the detail of a soccer news,which should be the left fragment
 * @author Zhiqian
 * @version 1.0
 * @since 2021-8-1
 */

public class FragmentRightOfSoccer extends Fragment {

    private TextView pubDate;
    private TextView url;
    private TextView description;
    private ImageView thumbnail;
    private static SoccerRssItem newsItem;
    private static Button toggleBtn;

    /**
     * this is a non-argument construction
     */
    public FragmentRightOfSoccer() {

    }

    /**
     * Implement the onCreate interface
     * @param savedInstanceState Bundle used to pass data between Activities
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    /**
     * Creates the view for the fragment.
     * @param inflater  is used to instantiate the contents of layout XML files into their corresponding View objects
     * @param container  is a view used to contain other views
     * @param savedInstanceState Bundle used to pass data between Activities
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        View soccerDetailView = inflater.inflate(R.layout.right_fragment_of_soccer, container, false);
        pubDate = soccerDetailView.findViewById(R.id.soccerRSSPubDate);
        url = soccerDetailView.findViewById(R.id.soccerRSSUrl);
        description = soccerDetailView.findViewById(R.id.soccerRSSDescription);
        thumbnail = soccerDetailView.findViewById(R.id.soccerRSSImage);

        Bundle bundle = this.getArguments();
        displayNewsDetail(bundle);

        if(toggleBtn == null) {
            toggleBtn = soccerDetailView.findViewById(R.id.soccerSaveNewsBtn);
        }
        if (newsItem != null && newsItem.getId() != SoccerRssItem.INVALID_ID){
            toggleBtn.setText("remove");//change the name of button into "remove"
        }

        toggleBtn.setOnClickListener(clk -> {
            if (newsItem.getId() != SoccerRssItem.INVALID_ID){
                removeItemFromDatabase(newsItem.getId());
            }else{
                saveItemToDatabase(newsItem);
            }
        });

        Button urlBtn = soccerDetailView.findViewById(R.id.soccerOpenUrlBtn);// used to open the url in browser
        urlBtn.setOnClickListener(clk -> {
            if(newsItem != null){
                Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
                startActivity(browser);
            }
        });
        return soccerDetailView;
    }

    /**
     * onCreateOptionsMenu specify the options menu for an activity
     * @param menu  Menu
     * @param inflater MenuInflater is used to instantiate menu XML files into Menu objects
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.soccer_top_bar, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }





    /**
     * show the detail of an item, which include title,url and picture
     * @param bundle   is used to passe pass data between Activities
     */
    private void displayNewsDetail(Bundle bundle){
        if (bundle != null) {
            newsItem = bundle.getParcelable("soccerNews");
            Picasso.get().load(newsItem.getThumbnail()).into(thumbnail);
            url.setText("URL: " + newsItem.getLink());
            pubDate.setText("PubDate: " + newsItem.getPubDate());
            description.setText("Description: " + newsItem.getDescription());
        }
    }

    /**
     * save a new record
     * @param item   this is the record that you want to save
     */
    private void saveItemToDatabase(SoccerRssItem item){
        if(item.getId() == SoccerRssItem.INVALID_ID){
            SoccerDbHelper database = new SoccerDbHelper(getContext());
            database.addItem(newsItem);
        }
    }

    /**
     * delete saved record
     * @param id  The id that you should delete
     */
    private void removeItemFromDatabase(int id){
        SoccerDbHelper database = new SoccerDbHelper(getContext());
        database.deleteItemById(String.valueOf(id));
    }
}