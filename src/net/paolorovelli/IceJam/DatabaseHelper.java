package net.paolorovelli.IceJam;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.Cursor;
import android.content.ContentValues;

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
    private static final String DATABASE_TABLE_ATTR_MOVES = "moves";


    /**
     * Class constructor.
     *
     * @param context  the context.
     */
    DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        //Delete the SQLite DB:
        //context.deleteDatabase(DATABASE_NAME);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String create = "CREATE TABLE " + DATABASE_TABLE_NAME + " (" +
                        DATABASE_TABLE_ATTR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        DATABASE_TABLE_ATTR_CHALLENGE + " TEXT, " +
                        DATABASE_TABLE_ATTR_LEVEL + " TEXT, " +
                        DATABASE_TABLE_ATTR_SOLVED + " INTEGER, " +
                        DATABASE_TABLE_ATTR_MOVES + " INTEGER);";

        db.execSQL( create );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE_NAME);
        onCreate(db);
    }


    /**
     * Get if a level is solved or not.
     *
     * @param challenge  the challenge to which the level belong.
     * @param level  the level solved.
     * @return the number of moves needed to solve the puzzle, zero if never solved.
     */
    public int isSolved(String challenge, String level) {
        //SQL query:
        String query = "SELECT MIN(" + DATABASE_TABLE_ATTR_MOVES + ") AS " + DATABASE_TABLE_ATTR_MOVES +
                       " FROM " + DATABASE_TABLE_NAME +
                       " WHERE " + DATABASE_TABLE_ATTR_CHALLENGE + "=? AND " + DATABASE_TABLE_ATTR_LEVEL + "=?";

        //Execute the query:
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(query, new String [] {challenge, level});

        //Read the result of the query:
        int moves = 0;
        if( cursor.moveToFirst() ) {  // moves the Cursor to the first row in the result set...
            moves = cursor.getInt( cursor.getColumnIndex(DATABASE_TABLE_ATTR_MOVES) );
        }

        //Close the Cursor:
        cursor.close();

        return moves;
    }


    /**
     * Insert in the database the references of the level solved
     *
     * @param challenge  the challenge to which the level belong.
     * @param level  the level solved.
     * @param moves  the number of moves needed to solve the level.
     */
    public void solved(String challenge, String level, int moves) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("challenge", challenge);
        values.put("level", level);
        values.put("solved", 1);
        values.put("moves", moves);

        db.insertOrThrow(DATABASE_TABLE_NAME, null, values);
    }
}
