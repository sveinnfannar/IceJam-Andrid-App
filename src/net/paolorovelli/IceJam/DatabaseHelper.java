package net.paolorovelli.IceJam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Class.
 *
 * @autor Paolo Rovelli and Sveinn Fannar Kristjánsson.
 * @date 03/31/2013
 * @time 10:14AM
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    //Database parameters:
    private static final String DATABASE_NAME = "IceJamDB";
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_TABLE_NAME = "level";

    //Table attributes:
    private static final String DATABASE_TABLE_ATTR_ID = "id";
    private static final String DATABASE_TABLE_ATTR_CHALLENGE = "challenge";
    private static final String DATABASE_TABLE_ATTR_LEVEL = "level";
    private static final String DATABASE_TABLE_ATTR_SOLVED = "solved";


    /**
     * Class constructor.
     *
     * @param context
     */
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + DATABASE_TABLE_NAME + " (" +
                        DATABASE_TABLE_ATTR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DATABASE_TABLE_ATTR_CHALLENGE + " TEXT, " +
                        DATABASE_TABLE_ATTR_LEVEL + " TEXT, " +
                        DATABASE_TABLE_ATTR_SOLVED + " INTEGER);";

        db.execSQL( create );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
        onCreate(db);
    }


    /**
     * Get if a level is solved or not
     *
     * @param challenge
     * @param level
     * @return  true if the level is solved (solved = true), false otherwise.
     */
    public boolean isSolved(String challenge, String level) {
        String query = "SELECT solved FROM " + DATABASE_TABLE_NAME +
                        " WHERE " + DATABASE_TABLE_ATTR_CHALLENGE + "=? AND " + DATABASE_TABLE_ATTR_LEVEL + "=?";

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String [] {challenge, level});

        //If the level is founded (solved)...
        if( cursor.moveToFirst() ) {
            return true;
        }

        return false;  // level not founded (solved)!
    }


    /**
     * Insert in the database the references of the level solved
     *
     * @param challenge
     * @param level
     */
    public void solved(String challenge, String level) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("challenge", challenge);
        values.put("level", level);
        values.put("solved", "true");

        db.insertOrThrow(DATABASE_TABLE_NAME, null, values);
    }
}
