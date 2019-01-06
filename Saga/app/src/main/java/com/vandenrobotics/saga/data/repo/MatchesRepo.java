package com.vandenrobotics.saga.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vandenrobotics.saga.data.DatabaseManager;
import com.vandenrobotics.saga.data.model.Competitions;
import com.vandenrobotics.saga.data.model.Matches;
import com.vandenrobotics.saga.data.model.Teams;

/**
 * Created by Programming701-A on 12/18/2017.
 */

public class MatchesRepo {

    private final String TAG = MatchesRepo.class.getSimpleName();

    public static String createTable(){
        return "CREATE TABLE " + Matches.TABLE + "("
                + Matches.KEY_CompId + " TEXT not null , "
                + Matches.KEY_MatchNumber + " INTEGER not null PRIMARY KEY , "
                + Matches.KEY_TeamNumber + " INTEGER not null , "
                + Matches.KEY_MatchPosition + " INTEGER not null , "
                + " FOREIGN KEY ( " + Matches.KEY_CompId + " ) REFERENCES " + Competitions.TABLE
                + " ( " + Competitions.KEY_CompId + " ), "
                + " FOREIGN KEY ( " + Matches.KEY_TeamNumber + " ) REFERENCES " + Teams.TABLE
                + " ( " + Teams.KEY_TeamNum + " ))";
    }

    public static String createIndex(){
        return "CREATE UNIQUE INDEX '" + Matches.INDEX + "' ON "
                + Matches.TABLE
                + " ( " + Matches.KEY_CompId
                + " , " + Matches.KEY_MatchNumber
                + " , " + Matches.KEY_TeamNumber
                + " , " + Matches.KEY_MatchPosition + " )";
    }

    public int insert(Matches matches){
        int matchesId = -1;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Matches.KEY_CompId, matches.getCompId());
        values.put(Matches.KEY_MatchNumber, matches.getMatchNum());
        values.put(Matches.KEY_TeamNumber, matches.getTeamNum());
        values.put(Matches.KEY_MatchPosition, matches.getMatchPos());

        matchesId = (int) db.insertWithOnConflict(Matches.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        DatabaseManager.getInstance().closeDatabase();
        return matchesId;
    }

    public void update(Matches matches){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Matches.KEY_CompId, matches.getCompId());
        values.put(Matches.KEY_MatchNumber, matches.getMatchNum());
        values.put(Matches.KEY_TeamNumber, matches.getTeamNum());
        values.put(Matches.KEY_MatchPosition, matches.getMatchPos());

        db.update(Matches.TABLE, values, Matches.KEY_CompId + " = '" + matches.getCompId() + " '"
                + " AND " + Matches.KEY_MatchNumber + " = " + matches.getMatchNum(), null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public void deleteForComp(String event){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Matches.TABLE, Matches.KEY_CompId + " =  '" + event + "'", null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public Teams getTeamInfo(String event, int position, int number){
        Teams teamInfo = new Teams();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT Matches." + Matches.KEY_TeamNumber
                + ", Teams."+ Teams.KEY_TeamName
                + " FROM " + Matches.TABLE
                + " JOIN " + Teams.TABLE
                + " ON Matches." + Matches.KEY_TeamNumber + " = Teams." + Teams.KEY_TeamNum
                + " WHERE Matches." + Matches.KEY_CompId + " = '" + event + "'"
                + " AND Matches." + Matches.KEY_MatchPosition + " = " + position
                + " AND Matches." + Matches.KEY_MatchNumber + " = " + number;

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()){
            teamInfo.setTeamNum(cursor.getInt(cursor.getColumnIndex(Teams.KEY_TeamNum)));
            teamInfo.setTeamName(cursor.getString(cursor.getColumnIndex(Teams.KEY_TeamName)));
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return teamInfo;

    }

}
