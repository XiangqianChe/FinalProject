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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * fragment 1 for movie list
 * @author Zhe Wang
 */
public class MovieListFragment extends Fragment {
    EditText title;
    Button btn_search;
    RecyclerView rv_movie;
    ArrayList<MovieInfo> infos = new ArrayList<>();
    MovieAdapter adapter;
    OpenHelper opener;
    SQLiteDatabase db;

    /**
     * create a database
     */
    private class OpenHelper extends SQLiteOpenHelper {

        public static final String name = "Database";
        public static final int version = 1;
        public static final String TABLE_NAME = "MovieInfo";
        public static final String col_title = "Title";
        public static final String col_year = "Year";
        public static final String col_rating = "Rating";
        public static final String col_runtime = "Runtime";
        public static final String col_mainactors = "MainActors";
        public static final String col_plot = "Plot";
        public static final String col_poster = "PosterURL";

        public OpenHelper(Context context) {
            super(context, name, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + col_title + " TEXT,"
                    + col_year + " TEXT,"
                    + col_rating + " TEXT,"
                    + col_runtime + " TEXT,"
                    + col_mainactors + " TEXT,"
                    + col_plot + " TEXT,"
                    + col_poster + " TEXT);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_NAME);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /**
         * views in activity_zhe_wang.xml
         */
        View movieLayout = inflater.inflate(R.layout.activity_zhe_wang, container, false);
        title = movieLayout.findViewById(R.id.title);
        btn_search = movieLayout.findViewById(R.id.btn_search);
        rv_movie = movieLayout.findViewById(R.id.rv_movie);

        /**
         * shared preferences to put edit texts into file system
         */
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String out_title = sharedPreferences.getString("Title","");
        title.setText(out_title);

        /**
         * database to hold history search records
         */
        opener = new OpenHelper(getContext());
        db = opener.getWritableDatabase();
        Cursor results = db.rawQuery("select * from " + OpenHelper.TABLE_NAME + ";", null);
        int _idCol = results.getColumnIndex("_id");
        int titleCol = results.getColumnIndex(OpenHelper.col_title);
        int yearCol = results.getColumnIndex(OpenHelper.col_year);
        int ratingCol = results.getColumnIndex(OpenHelper.col_rating);
        int runtimeCol = results.getColumnIndex(OpenHelper.col_runtime);
        int mainactorsCol = results.getColumnIndex(OpenHelper.col_mainactors);
        int plotCol = results.getColumnIndex(OpenHelper.col_plot);
        int posterCol = results.getColumnIndex(OpenHelper.col_poster);

        while(results.moveToNext()) {
            long id = results.getInt(_idCol);
            String r_title = results.getString(titleCol);
            String r_year = results.getString(yearCol);
            String r_rating = results.getString(ratingCol);
            String r_runtime = results.getString(runtimeCol);
            String r_mainactors = results.getString(mainactorsCol);
            String r_plot = results.getString(plotCol);
            String r_poster = results.getString(posterCol);
            infos.add(new MovieInfo(id, r_title, r_year, r_rating, r_runtime, r_mainactors, r_plot, r_poster));
        }
        /**
         * put infos into recycler view
         */
        adapter = new MovieAdapter();
        rv_movie.setAdapter(adapter);
        rv_movie.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        /**
         * click search button for movie infos
         */
        btn_search.setOnClickListener(click->{
            Executor newThread = Executors.newSingleThreadExecutor();
            /**
             * display last searched edit texts
             */
            String in_title = title.getText().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("Title", in_title);
            editor.apply();

            /**
             * new thread to manage search
             */
            newThread.execute(()->{
                try {
                    /**
                     * clear infos list
                     */
                    /**
                     * search to get a json file and retrieve data
                     */
                    String stringURL = "https://www.omdbapi.com/?apikey=6c9862c2&t=" + URLEncoder.encode(in_title, "UTF-8");
                    URL url = new URL(stringURL);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    String text = (new BufferedReader(
                            new InputStreamReader(in, StandardCharsets.UTF_8)))
                            .lines()
                            .collect(Collectors.joining("\n"));
                    JSONObject jsonObject = new JSONObject(text);
                    String info_title = jsonObject.getString("Title");
                    String info_year = jsonObject.getString("Year");
                    String info_rating = jsonObject.getString("Rated");
                    String info_runtime = jsonObject.getString("Runtime");
                    String info_mainactors = jsonObject.getString("Actors");
                    String info_plot = jsonObject.getString("Plot");
                    String info_poster = jsonObject.getString("Poster");
                    MovieInfo movieInfo = new MovieInfo(info_title, info_year, info_rating, info_runtime, info_mainactors, info_plot, info_poster);

                    ContentValues newRow = new ContentValues();
                    newRow.put(OpenHelper.col_title, movieInfo.getTitle());
                    newRow.put(OpenHelper.col_year, movieInfo.getYear());
                    newRow.put(OpenHelper.col_rating, movieInfo.getRating());
                    newRow.put(OpenHelper.col_runtime, movieInfo.getRuntime());
                    newRow.put(OpenHelper.col_mainactors, movieInfo.getMainactors());
                    newRow.put(OpenHelper.col_plot, movieInfo.getPlot());
                    newRow.put(OpenHelper.col_poster, movieInfo.getPoster());
                    long newId = db.insert(OpenHelper.TABLE_NAME, OpenHelper.col_title, newRow);
                    movieInfo.setId(newId);

                    infos.add(movieInfo);
                    /**
                     * insert searched data into a list
                     */
                    ZheWang parentActivity = (ZheWang) getContext();
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
            title.setText("");
        });
        return movieLayout;
    }

    /**
     * delete a certain info
     * @param chosenInfo
     * @param chosenPosition
     */
    public void notifyInfoDeleted(MovieInfo chosenInfo, int chosenPosition) {
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
                    MovieInfo removedInfo = infos.get(chosenPosition);
                    infos.remove(chosenPosition);
                    adapter.notifyItemRemoved(chosenPosition);
                    db.delete(OpenHelper.TABLE_NAME, "_id=?", new String[] {Long.toString(removedInfo.getId())});

                    /**
                     * snack bar to undo delete
                     */
                    Snackbar.make(btn_search, "You deleted info #" + chosenPosition, Snackbar.LENGTH_LONG)
                            .setAction("Undo", click2->{
                                infos.add(chosenPosition, removedInfo);
                                adapter.notifyItemInserted(chosenPosition);
                                db.execSQL("Insert into " + OpenHelper.TABLE_NAME + " values('"
                                        + removedInfo.getId() + "','"
                                        + removedInfo.getTitle() + "','"
                                        + removedInfo.getYear() + "','"
                                        + removedInfo.getRating() + "','"
                                        + removedInfo.getRuntime() + "','"
                                        + removedInfo.getMainactors() + "','"
                                        + removedInfo.getPlot() + "','"
                                        + removedInfo.getPoster() + "');");
                            })
                            .show();
                })
                .create().show();
    }

    /**
     * rows in recycler view
     */
    private class RowView extends RecyclerView.ViewHolder {

        TextView info_title;

        int position = -1;

        public RowView(View itemView) {
            super(itemView);

            itemView.setOnClickListener(click->{
                ZheWang parentActivity = (ZheWang)getContext();
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
    private class MovieAdapter extends RecyclerView.Adapter<RowView> {
        @Override
        public RowView onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = getLayoutInflater();
            View loadedRow = inflater.inflate(R.layout.movie_info, parent, false);
            return new RowView(loadedRow);
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
     * movie info
     */
    public class MovieInfo{
        long id;
        String title;
        String year;
        String rating;
        String runtime;
        String mainactors;
        String plot;
        String poster;

        public MovieInfo(String title, String year, String rating, String runtime, String mainactors, String plot, String poster) {
            this.title = title;
            this.year = year;
            this.rating = rating;
            this.runtime = runtime;
            this.mainactors = mainactors;
            this.plot = plot;
            this.poster = poster;
        }

        public MovieInfo(long id, String title, String year, String rating, String runtime, String mainactors, String plot, String poster) {
            this.id = id;
            this.title = title;
            this.year = year;
            this.rating = rating;
            this.runtime = runtime;
            this.mainactors = mainactors;
            this.plot = plot;
            this.poster = poster;
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

        public String getYear() {
            return year;
        }

        public String getRating() {
            return rating;
        }

        public String getRuntime() {
            return runtime;
        }

        public String getMainactors() {
            return mainactors;
        }

        public String getPlot() {
            return plot;
        }

        public String getPoster() {
            return poster;
        }
    }
}
