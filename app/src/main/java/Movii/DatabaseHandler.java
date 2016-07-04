package Movii;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Geofrey on 2/3/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "movie_share4";
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    String userTable = "CREATE TABLE users " +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "user_type INTEGER, " +
            "username TEXT,"+
        "phone_no VARCHAR(50))";
                 db.execSQL(userTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String sqlUsers = "DROP TABLE IF EXISTS users";
        db.execSQL(sqlUsers);
         onCreate(db);
    }
	
	

}
