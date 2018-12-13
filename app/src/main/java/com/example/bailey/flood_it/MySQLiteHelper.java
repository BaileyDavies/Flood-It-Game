package com.example.bailey.flood_it;
/*DECELERATION: Class based upon guide obtained from Kevin Wilson */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class MySQLiteHelper extends SQLiteOpenHelper{
    static final String TABLE_SCORES_EASY = "easy_scores";
    static final String TABLE_SCORES_MEDIUM = "medium_scores";
    static final String TABLE_SCORES_HARD = "hard_scores";
    static final String TABLE_SCORES_EXTREME = "extreme_scores";


    static final String COLUMN_ID = "id";
    static final String COLUMN_SCORE = "score";
    static final String COLUMN_NAME = "name";

    private static final String DATABASE_NAME = "flooditScores.db";
    private static final int DATABASE_VERSION = 1;

    /*Variables the store the SQL statements needed to create the tables that will store the scores for the different difficulties*/
    private static final String DATABASE_CREATE = "create table " + TABLE_SCORES_EASY + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SCORE + "  integer not null, " + COLUMN_NAME + " text not null)";

    private static final String DATABASE_TABLE_MEDIUM = "create table " + TABLE_SCORES_MEDIUM + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SCORE + "  integer not null, " + COLUMN_NAME + " text not null)";

    private static final String DATABASE_TABLE_HARD = "create table " + TABLE_SCORES_HARD + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SCORE + "  integer not null, " + COLUMN_NAME + " text not null)";

    private static final String DATABASE_TABLE_EXTREME = "create table " + TABLE_SCORES_EXTREME + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_SCORE + "  integer not null, " + COLUMN_NAME + " text not null)";


    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase database) {
        /*Create the database and its tables*/
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_TABLE_MEDIUM);
        database.execSQL(DATABASE_TABLE_HARD);
        database.execSQL(DATABASE_TABLE_EXTREME);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to " + newVersion + " which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES_EASY + TABLE_SCORES_MEDIUM + TABLE_SCORES_MEDIUM + TABLE_SCORES_EXTREME);
        onCreate(db);
    }



}
