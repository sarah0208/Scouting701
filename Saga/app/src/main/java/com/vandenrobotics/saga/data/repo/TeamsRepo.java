package com.vandenrobotics.saga.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vandenrobotics.saga.data.DatabaseManager;
import com.vandenrobotics.saga.data.model.Teams;

import java.util.ArrayList;

/**
 * Created by Programming701-A on 12/18/2017.
 */

public class TeamsRepo {

    private final String TAG = TeamsRepo.class.getSimpleName();

    //Holds String to execute SQLite where the Teams Table is created and specified how it is made
    public static String createTable(){
        return "CREATE TABLE " + Teams.TABLE + "("
                + Teams.KEY_TeamNum + " INTEGER not null PRIMARY KEY , "
                + Teams.KEY_TeamName + " TEXT  )  ";
    }

    //inserts a new team row into the database
    public int insert(Teams teams){
        int teamId = -1;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Teams.KEY_TeamNum, teams.getTeamNum());
        values.put(Teams.KEY_TeamName, teams.getTeamName());

        //checks for any conflict and with return -1 if there is
        teamId=(int)db.insertWithOnConflict(Teams.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        DatabaseManager.getInstance().closeDatabase();

        //returns result of conflict
        return teamId;
    }

    //updates a team name for a team
    public void update(Teams teams){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Teams.KEY_TeamName, teams.getTeamName());

        //inserting row
        db.update(Teams.TABLE, values, Teams.KEY_TeamNum + " = " + teams.getTeamNum(), null);
        DatabaseManager.getInstance().closeDatabase();

    }

    //deletes all rows in team table
    public void delete(){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Teams.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();

    }

    //returns an array of ints with all the team #'s in the database
    public ArrayList<Integer> getAllTeamNums(){
        ArrayList<Integer> teamsNum = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        //makes selection query to get all teams
        String selectQuery = " SELECT " + Teams.KEY_TeamNum
                + " FROM " + Teams.TABLE;

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        //moves to first row that matches selection query
        if (cursor.moveToFirst()){
            do {
                //adds the team # from the row
                teamsNum.add(cursor.getInt(cursor.getColumnIndex(Teams.KEY_TeamNum)));
            //adds the teams while there are rows that matches selection query
            }while(cursor.moveToNext());
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        //returns the arraylist of team #'s
        return teamsNum;
    }

    //gets the team name for a specific team #
    public String getTeamName(int num){
        String teamName = "";

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + Teams.KEY_TeamName
                + " FROM " + Teams.TABLE
                + " WHERE Teams." + Teams.KEY_TeamNum + " = " + num;

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            teamName = cursor.getString(cursor.getColumnIndex(Teams.KEY_TeamName));
        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        Log.d("TeamsRepo", "end get team");
        return teamName;
    }
}
