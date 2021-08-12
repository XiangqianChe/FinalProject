package algonquin.cst2335.final_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * create a database
 */
class OpenHelper extends SQLiteOpenHelper {

    public static final String name = "Database";
    public static final int version = 1;
    public static final String TABLE_NAME = "StationInfo";
    public static final String col_title = "Title";
    public static final String col_lat = "Latitude";
    public static final String col_long = "Longitude";
    public static final String col_tel = "Tel";

    public OpenHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + col_title + " TEXT,"
                + col_lat + " TEXT,"
                + col_long + " TEXT,"
                + col_tel + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
    }
}
