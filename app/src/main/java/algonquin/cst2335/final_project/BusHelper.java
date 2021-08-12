package algonquin.cst2335.final_project;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * this class is to do some procedures of creating or upgrading database
 * @author Di Yu
 * @Version 1.0
 */
public class BusHelper extends SQLiteOpenHelper {
    public static final String name = "TheDatabase";
    public static final int version = 1;
    public static final String TABLE_NAME = "BusMessages";
    public static final String col_stop_no="stopNo";

    public BusHelper(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table " + TABLE_NAME + "(_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + col_stop_no+ " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
        onCreate(db);
    }

}