package algonquin.cst2335.final_project;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 *
 * @author Zhiqian Qu
 * @version 1.0
 * @since 2021-8-1
 */
public class FragmentLeftOfSoccer extends Fragment {
    private View soccerListView = null;
    private RecyclerView recyclerView = null;
    private final List<SoccerRssItem> itemList = new ArrayList<SoccerRssItem>();
    private SoccerDbHelper database;
    private String title;
    private String link;
    private String publicDate;
    private String description;
    private String thumbnail;
    private AlertDialog dialog = null;

    /**
     * This is a non-argument constructor
     */
    public FragmentLeftOfSoccer() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * @param inflater LayoutInflater used to instantiate the contents of layout XML files into their corresponding View objects
     * @param container ViewGroup is the base class for Layouts in android
     * @param savedInstanceState Bundle used to pass data between Activities
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        database = new SoccerDbHelper(getContext());
        dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Lording")
                .setMessage("Please wait")
                .setView(new ProgressBar(getActivity().getApplicationContext()))
                .show();

        soccerListView = inflater.inflate(R.layout.left_fragment_of_soccer, container, false);
        soccerListView.setBackgroundColor(Color.BLACK);
        Button showSavedDataBtn = soccerListView.findViewById(R.id.soccerDisplaySavedNewsBtn);
        showSavedDataBtn.setOnClickListener(clk -> {
            if (loadDataToAdpter()) {
                recyclerView.getAdapter().notifyDataSetChanged();
            }

        });
        return soccerListView;
    }

    /**
     * load data from URL
     *
     * @param view The View returned by onCreateView
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     *                           saved state as given here
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Executor exec = Executors.newSingleThreadExecutor();
        exec.execute(() -> {
            // The work thread to retrieving data from RSS url
            try {
                String stringURL = "https://www.goal.com/en/feeds/news";
                URL url = new URL(stringURL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(in, "UTF-8");

                int eventType = xpp.getEventType(); //loop control variable
                boolean insideItem = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    //if we are at a START_TAG (opening tag)
                    if (eventType == XmlPullParser.START_TAG) {
                        //if the tag is called "item"
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                        }
                        //if the tag is called "title"
                        else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                // extract the text between <title> and </title>
                                title = xpp.nextText();
                            }
                        }
                        //if the tag is called "pubDate"
                        else if (xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                publicDate = xpp.nextText();
                            }
                        }
                        //if the tag is called "link"
                        else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem) {
                                // extract the text between <link> and </link>
                                link = xpp.nextText();
                            }
                        } else if (xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                description = xpp.nextText();
                            }
                        } else if (xpp.getName().equalsIgnoreCase("media:thumbnail")) {
                            if (insideItem) {
                                // extract the text between <link> and </link>
                                thumbnail = xpp.getAttributeValue(null, "url");
                            }
                        }
                    }
                    //if we are at an END_TAG and the END_TAG is called "item"
                    else if (eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")) {
                        itemList.add(new SoccerRssItem(title, link, publicDate, description, thumbnail));
                        insideItem = false;
                    }

                    eventType = xpp.next(); //move to next element
                }

                // UI thread to display the title list with a recycle view
                getActivity().runOnUiThread(() -> {
                    recyclerView = soccerListView.findViewById(R.id.soccerRecyclerView);
                    recyclerView.setAdapter(new SoccerItemRecyclerViewAdapter(itemList));
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    dialog.hide();
                });
            } catch (IOException | XmlPullParserException ioe) {
                Log.e("Connection error:", ioe.getMessage());
            }
        });
    }

    /**
     * Load data to the list is bound to adapter of recycle view
     *
     * @return successfully load or not
     */
    private boolean loadDataToAdpter() {
        Cursor cursor = database.readAllData();
        if (cursor.getCount() != 0) {
            if (!itemList.isEmpty()) {
                itemList.clear();
            }

            while (cursor.moveToNext()) {
                SoccerRssItem item = new SoccerRssItem();
                item.setId(cursor.getInt(0));
                item.setTitle(cursor.getString(1));
                item.setPubDate(cursor.getString(2));
                item.setLink(cursor.getString(3));
                item.setDescription(cursor.getString(4));
                item.setThumbnail(cursor.getString(5));

                itemList.add(item);
            }
            return true;
        } else {
            Snackbar snackbar = Snackbar.make(getView(), "Not found your favourites in database", Snackbar.LENGTH_SHORT);
            snackbar.show();
            return false;
        }
    }

    /**
     * The RecyclerView for the RSS news list.
     */
    class SoccerItemRecyclerViewAdapter
            extends RecyclerView.Adapter
            <SoccerItemRecyclerViewAdapter.ViewHolder> {

        private final List<SoccerRssItem> mValues;

        SoccerItemRecyclerViewAdapter(List<SoccerRssItem> items) {
            mValues = items;
        }

        /**
         * This method inflates the layout for the RSS list.
         *
         * @param parent   ViewGroup into which the new view will be added.
         * @param viewType The view type of the new View.
         * @return A new ViewHolder
         */
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.soccer_single_list, parent, false);
            return new ViewHolder(view);
        }

        /**
         * This method implements a listener with setOnClickListener().
         * When the user taps a item title, the code uses an bundle to
         * pass the item is selected to right fragment.
         *
         * @param holder   ViewHolder
         * @param position Position of the item in the array.
         */
        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            // display title list
            holder.mContentView.setText(mValues.get(position).getTitle());

            // pass a details of a  to the right fragment
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("soccerNews", mValues.get(position));
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    FragmentRightOfSoccer fragmentRightOfSoccer = new FragmentRightOfSoccer();
                    fragmentRightOfSoccer.setArguments(bundle);
                    transaction.replace(R.id.soccerDetailContainer, fragmentRightOfSoccer, null).addToBackStack(null).commit();
                }
            });
        }

        /**
         * Get the count of news list items.
         *
         * @return Integer count
         */
        @Override
        public int getItemCount() {
            return mValues.size();
        }

        /**
         * ViewHolder describes an item view and metadata about its place
         * within the RecyclerView.
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mContentView;
            final View mView;

            ViewHolder(View view) {
                super(view);
                mView = view;
                mContentView = view.findViewById(R.id.soccer_news_item);
            }
        }
    }

}