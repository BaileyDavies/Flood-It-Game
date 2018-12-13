package com.example.bailey.flood_it;
/*DECELERATION: Class based upon guide obtained from Kevin Wilson */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;

class ScoresDataSource {
    private SQLiteDatabase database;

    private MySQLiteHelper dbHelper;
    //String array that holds the String values of the tables
    private String[] tableSelect = {
            MySQLiteHelper.TABLE_SCORES_EASY,
            MySQLiteHelper.TABLE_SCORES_MEDIUM,
            MySQLiteHelper.TABLE_SCORES_HARD,
            MySQLiteHelper.TABLE_SCORES_EXTREME
    };

    //String array that holds the String values of the columns
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_SCORE,
            MySQLiteHelper.COLUMN_NAME
    };

    ScoresDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /*Method that is used to add score to the specified table (insertInto) accessed from the tableSelect array*/
    void createScore(String name, Integer score, Integer insertInto) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_NAME, name);
        values.put(MySQLiteHelper.COLUMN_SCORE, score);
        //insert new score and name into the specified database table passed by parameter and return the row id of the newly inserted row
        long insertId = database.insert(tableSelect[insertInto], null, values);
        //run query to retrieve that row
        Cursor cursor = database.query(tableSelect[insertInto],
                 allColumns,
                MySQLiteHelper.COLUMN_ID + " = " + insertId,
                null,
                null,
                null,
                null);

        cursor.moveToFirst();
        cursor.close();

    }

    /*Method that is used to return scores form a specified table*/
    List<Score> getAllScores(Integer tableToSelect) {
        List<Score> scores = new ArrayList<>();
        //Query that selects all the values from the database table passed by reference
        Cursor cursor = database.query(tableSelect[tableToSelect],
                allColumns,
                null,
                null,
                null,
                null, MySQLiteHelper.COLUMN_SCORE + " DESC"
        );

        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            Score score = cursorToScore(cursor);
            scores.add(score);
            cursor.moveToNext();
        }

        cursor.close();
        return scores;
    }

    private Score cursorToScore(Cursor cursor){
        /*Translates the cursor to a score*/
        Score score = new Score();

        score.setId(cursor.getLong(0));
        score.setScore(cursor.getInt(1));
        score.setName(cursor.getString(2));
        return score;

    }
}
