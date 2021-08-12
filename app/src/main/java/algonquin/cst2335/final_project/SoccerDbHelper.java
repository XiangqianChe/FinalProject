package algonquin.cst2335.final_project;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * this file wae ceated for using database. extent from SQLiteOpenHelper
 *
 * @author Zhiqian Qu
 * @version 1.0
 * @since 2021-8-1
 */
public class SoccerDbHelper extends SQLiteOpenHelper {
    /**
     * These virables will be used to create a table
     */
    private Context context;
    private static final String DATABASE_NAME = "soccer.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "soccer";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_PUBDATE= "pubdate";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_DESCRIPTION= "description";
    private static final String COLUMN_THUMBNAIL= "thumbnail";

    /**
     * This is a constructor with one parameter
     * @param context
     */
    SoccerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * Creates the soccer table
     * @param db  SQLiteDatabase handler
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_PUBDATE + " TEXT, " +
                COLUMN_URL + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_THUMBNAIL + " TEXT);";
        db.execSQL(query);
    }

    /**
     * Drop table
     * @param db  SQLiteDatabase handler
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Insert into table
     *
     * @param item  SoccerRssItem
     */
    void addItem(SoccerInformation item){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE, item.getTitle());
        cv.put(COLUMN_PUBDATE, item.getTitle());
        cv.put(COLUMN_URL, item.getLink());
        cv.put(COLUMN_DESCRIPTION, item.getDescription());
        cv.put(COLUMN_THUMBNAIL, item.getThumbnail());

        long result = db.insert(TABLE_NAME,null, cv);
        if(result == -1){
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context, "Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * use query to get data from table
     * @return Cursor read data from it
     */
    Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if(db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    /**
     * Delete a row
     * @param row_id String an id of row
     */
    void deleteItemById(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, "id=?", new String[]{row_id});
        if(result == -1){
            Toast.makeText(context, "Failed to Delete.", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Successfully Deleted.", Toast.LENGTH_SHORT).show();
        }
    }

}
