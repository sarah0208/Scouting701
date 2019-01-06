package com.vandenrobotics.saga.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vandenrobotics.saga.data.DatabaseManager;
import com.vandenrobotics.saga.data.model.Competitions;
import com.vandenrobotics.saga.data.model.Stats;
import com.vandenrobotics.saga.data.model.Teams;

/**
 * Created by Programming701-A on 12/18/2017.
 */

public class StatsRepo {

    private Stats stats;

    private final String TAG = StatsRepo.class.getSimpleName();
    public StatsRepo(){
        stats = new Stats();
    }

    //Holds String to execute SQLite where the Stats Table is created and specified how it is made
    public static String createTable(){
        return "CREATE TABLE " + Stats.TABLE + "("
                + Stats.KEY_CompId + " TEXT not null , "
                + Stats.KEY_MatchNum + " INTEGER not null , "
                + Stats.KEY_TeamNum + " INTEGER not null , "
                //Makes sure the match position is between 1 and 6 for each tablet
                + Stats.KEY_MatchPosition + " INTEGER not null CHECK ( " + Stats.KEY_MatchPosition + " BETWEEN 1 AND 6 ), "
                + Stats.KEY_NoShow + " INTEGER , "
                + Stats.KEY_HadAuto + " INTEGER , "
                + Stats.KEY_RobotDisabled + " INTEGER , "
                + Stats.KEY_RedCard + " INTEGER , "
                + Stats.KEY_YellowCard + " INTEGER , "
                + Stats.KEY_Fouls + " INTEGER , "
                + Stats.KEY_TechFouls + " INTEGER , " +
                //makes the CompId, MatchNum and MatchPos Primary Key so there needs
                //to be a unique combination of these attributes in each row in the Stats table
                "PRIMARY KEY ( '" + Stats.KEY_CompId
                + "' , '" + Stats.KEY_MatchNum
                + "' , '" + Stats.KEY_MatchPosition + "' ), "
                //makes sure CompId column exists in the Competitions Table
                + " FOREIGN KEY ( " + Stats.KEY_CompId + " ) REFERENCES " + Competitions.TABLE
                + " ( " + Competitions.KEY_CompId + " ), "
                //makes sure TeamNum column exists in the Team Table
                + " FOREIGN KEY ( " + Stats.KEY_TeamNum + " ) REFERENCES " + Teams.TABLE
                + " ( " + Teams.KEY_TeamNum + " ))";
    }

    //Holds String to execute SQLite where the Stats Index is created to specify that there
    //Is a unique combination of CompId, Match# and Team# in every row in the stats table
    public static String createIndex(){
        return "CREATE UNIQUE INDEX '" + Stats.INDEX + "' ON "
                + Stats.TABLE
                + " ( " + Stats.KEY_CompId
                + " , " + Stats.KEY_MatchNum
                + " , " + Stats.KEY_TeamNum + " )";
    }

    //inserts all values of a stats row object into the sql database
    public int insertAll(Stats stats){
        int statsId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Stats.KEY_CompId, stats.getCompId());
        values.put(Stats.KEY_MatchNum, stats.getMatchNum());
        values.put(Stats.KEY_TeamNum, stats.getTeamNum());
        values.put(Stats.KEY_MatchPosition, stats.getMatchPos());
        values.put(Stats.KEY_NoShow, stats.getNoShow());
        values.put(Stats.KEY_HadAuto, stats.getHadAuto());;
        values.put(Stats.KEY_RobotDisabled, stats.getDisabled());
        values.put(Stats.KEY_RedCard, stats.getRedCard());
        values.put(Stats.KEY_YellowCard, stats.getYellowCard());
        values.put(Stats.KEY_Fouls, stats.getFoul());
        values.put(Stats.KEY_TechFouls, stats.getTechFoul());

        //check if there is a conflict. It should return -1 if there is a copy of the exact combination of the Primary Keys
        statsId=(int)db.insertWithOnConflict(Stats.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        DatabaseManager.getInstance().closeDatabase();
        //return to check conflict
        return statsId;
    }

    //updates the first part of the row with a new team
    public int updatePart(Stats stats){
        int statsId;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Stats.KEY_TeamNum, stats.getTeamNum());

        //updates row with the same CompId, Match# and Match Position
        statsId = db.update(Stats.TABLE, values,Stats.KEY_CompId + " =  '" + stats.getCompId() + "' AND "
                + Stats.KEY_MatchNum + " = " + stats.getMatchNum() + " AND "
                + Stats.KEY_MatchPosition + " = " + stats.getMatchPos(), null);
        DatabaseManager.getInstance().closeDatabase();
        return statsId;
    }

    //deletes all rows in the stats table
    public void delete( ) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Stats.TABLE, null,null);
        DatabaseManager.getInstance().closeDatabase();
    }

    //deletes all rows in the stats table for the specified comp
    public void deleteForComp(String event) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Stats.TABLE, Stats.KEY_CompId + " =  '" + event + "'",null);
        DatabaseManager.getInstance().closeDatabase();
    }

    /*
    * All set functions save each part of a single row for each phase of the match
    * using the initial insert that adds the comp, match, team and match pos
    * used for saving data
    * */

    //save the Init Stats to the database for the current comp, match and team
    public void setInitStats(String event, int match, int team, int noshow){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values =  new ContentValues();
        values.put(Stats.KEY_NoShow, noshow);

        db.update(Stats.TABLE, values, Stats.KEY_CompId + " =  '" + event + "' AND "
                + Stats.KEY_MatchNum + " = " + match + " AND "
                + Stats.KEY_TeamNum + " = " + team, null);
        DatabaseManager.getInstance().closeDatabase();
    }

    //save the Auto Stats to the database for the current comp, match and team
    public void setAutoStats(Stats stats){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        Log.d("StatsRepo auto", "team id " + stats.getTeamNum());
        values.put(Stats.KEY_HadAuto, stats.getHadAuto());

        db.update(Stats.TABLE, values, Stats.KEY_CompId + " =  '" + stats.getCompId() + "' AND "
                + Stats.KEY_MatchNum + " = " + stats.getMatchNum() + " AND "
                + Stats.KEY_TeamNum + " = " + stats.getTeamNum(), null);
        DatabaseManager.getInstance().closeDatabase();
    }

    //save the Tele Stats to the database for the current comp, match and team
    public void setTeleStats(Stats stats) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        Log.d("StatsRepo", "Team id " + stats.getTeamNum());
        values.put(Stats.KEY_RobotDisabled, stats.getDisabled());
        values.put(Stats.KEY_RedCard, stats.getRedCard());
        values.put(Stats.KEY_YellowCard, stats.getYellowCard());
        values.put(Stats.KEY_Fouls, stats.getFoul());
        values.put(Stats.KEY_TechFouls, stats.getTechFoul());
        Log.d("StatsRepo", "updating table"+values.toString());
        try {
            db.update(Stats.TABLE, values, Stats.KEY_CompId + " =  \"" + stats.getCompId() + "\" AND "
                    + Stats.KEY_MatchNum + " = " + stats.getMatchNum() + " AND "
                    + Stats.KEY_TeamNum + " = " + stats.getTeamNum(), null);
        }catch(Exception e){
            Log.e("StatsRepo", "Exception updating database" + e.getMessage());

        }
        DatabaseManager.getInstance().closeDatabase();
    }

    /*
    * get functions gets their phase from part of the row with the current comp, match, and position
    * used for loading data
    * */

    //get the Auto stats from the row with the current comp, match and match position
    public Stats getAutoStats(String event, int match, int matchPos){
        Stats stats = new Stats();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        //makes the selection query for the stats table to get the auto stats
        String selectQuery = " SELECT Stats." + Stats.KEY_HadAuto
                + " FROM " + Stats.TABLE
                + " WHERE Stats." + Stats.KEY_CompId + " = \"" + event + "\""
                + " AND Stats." + Stats.KEY_MatchNum + " = " + match
                + " AND Stats." + Stats.KEY_MatchPosition + " = " + matchPos;

        Log.d(TAG, selectQuery);
        //uses the selection query to get rows from the database one at a time
        Cursor cursor = db.rawQuery(selectQuery, null);
        //gets the first row that matches the specifications from the selection query
        if (cursor.moveToFirst()){
            stats.setHadAuto(cursor.getInt(cursor.getColumnIndex(Stats.KEY_HadAuto)));
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        //returns a stats row object with the values from the database
        return stats;
    }

    //get the Tele stats from the row with the current comp, match and match position
    public Stats getTeleStats (String event, int match, int matchPos) {
        Stats stats = new Stats();

        //makes the selection query for the stats table to get the tele stats
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT Stats." + Stats.KEY_RobotDisabled
                + ", Stats." + Stats.KEY_RedCard
                + ", Stats." + Stats.KEY_YellowCard
                + ", Stats." + Stats.KEY_Fouls
                + ", Stats." + Stats.KEY_TechFouls
                + " FROM " + Stats.TABLE
                + " WHERE Stats." + Stats.KEY_CompId + " = \"" + event + "\""
                + " AND Stats." + Stats.KEY_MatchNum + " = " + match
                + " AND Stats." + Stats.KEY_MatchPosition + " = " + matchPos;

        Log.d(TAG, selectQuery);
        //uses the selection query to get rows from the database one at a time
        Cursor cursor = db.rawQuery(selectQuery, null);
        //gets the first row that matches the specifications from the selection query
        if (cursor.moveToFirst()){
            stats.setDisabled(cursor.getInt(cursor.getColumnIndex(Stats.KEY_RobotDisabled)));
            stats.setRedCard(cursor.getInt(cursor.getColumnIndex(Stats.KEY_RedCard)));
            stats.setYellowCard(cursor.getInt(cursor.getColumnIndex(Stats.KEY_YellowCard)));
            stats.setFoul(cursor.getInt(cursor.getColumnIndex(Stats.KEY_Fouls)));
            stats.setTechFoul(cursor.getInt(cursor.getColumnIndex(Stats.KEY_TechFouls)));
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        //returns a stats row object with the values from the database
        return stats;
    }
}
