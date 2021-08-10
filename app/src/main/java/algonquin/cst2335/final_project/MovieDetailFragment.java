package algonquin.cst2335.final_project;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * fragment 2 for movie detail
 * @author Zhe Wang
 */
public class MovieDetailFragment extends Fragment {
    MovieListFragment.MovieInfo chosenInfo;
    int chosenPosition;

    public  MovieDetailFragment(MovieListFragment.MovieInfo info, int position) {
        chosenInfo = info;
        chosenPosition = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View detailView = inflater.inflate(R.layout.movie_detail_fragment, container, false);
        ZheWang parent=(ZheWang) getContext();
        TextView detail_title = detailView.findViewById(R.id.detail_title);
        TextView detail_year = detailView.findViewById(R.id.detail_year);
        TextView detail_rating = detailView.findViewById(R.id.detail_rating);
        TextView detail_runtime = detailView.findViewById(R.id.detail_runtime);
        TextView detail_main_actors = detailView.findViewById(R.id.detail_main_actors);
        TextView detail_plot = detailView.findViewById(R.id.detail_plot);
        ImageView poster = detailView.findViewById(R.id.poster);
        detail_title.setText("Title is: " + chosenInfo.getTitle());
        detail_year.setText("Year is: " + chosenInfo.getYear());
        detail_rating.setText("Rating is: " + chosenInfo.getRating());
        detail_runtime.setText("Runtime is: " + chosenInfo.getRuntime());
        detail_main_actors.setText("Main actors are: " + chosenInfo.getMainactors());
        detail_plot.setText("Plot is: " + chosenInfo.getPlot());
        /**
         * set poster
         */
        File file = null;
        String imgPath = null;
        try {
            imgPath = URLEncoder.encode(chosenInfo.getTitle()+".png", "UTF-8");
            file = new File(parent.getFilesDir(),imgPath);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(file!=null&&file.exists()){
            Bitmap image = BitmapFactory.decodeFile(parent.getFilesDir()+"/"+imgPath);
            poster.setImageBitmap(image);
        }
        else {
            Executor executor= Executors.newSingleThreadExecutor();
            executor.execute(()->{
                try {
                    URL imgURL = new URL(chosenInfo.getPoster());
                    HttpURLConnection connection = (HttpURLConnection)imgURL.openConnection();
                    connection.connect();
                    int responseCode = connection.getResponseCode();
                    if(responseCode == 200) {
                        Bitmap image = BitmapFactory.decodeStream(connection.getInputStream());
                        image.compress(Bitmap.CompressFormat.PNG, 100, parent.openFileOutput(URLEncoder.encode(chosenInfo.getTitle()+".png", "UTF-8"), Activity.MODE_PRIVATE));
                        parent.runOnUiThread(()->{
                            poster.setImageBitmap(image);
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
        /**
         * close button
         */
        Button btn_close = detailView.findViewById(R.id.btn_close);
        btn_close.setOnClickListener(click_close->{
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
        /**
         * delete button
         */
        Button btn_delete = detailView.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(click_delete->{
            ZheWang parentActicity = (ZheWang)getContext();
            parentActicity.notifyInfoDeleted(chosenInfo, chosenPosition);
            getParentFragmentManager().beginTransaction().remove(this).commit();
        });
        return detailView;
    }
}
