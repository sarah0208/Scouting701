package com.vandenrobotics.saga.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vandenrobotics.saga.data.DatabaseManager;
import com.vandenrobotics.saga.data.model.Competitions;
import com.vandenrobotics.saga.data.model.MatchInfo;

/**
 * Created by Programming701-A on 1/19/2018.
 */

public class MatchInfoRepo {

    private final String TAG = MatchInfoRepo.class.getSimpleName();

    public static String createTable(){
        return "CREATE TABLE " + MatchInfo.TABLE + "("
                + MatchInfo.KEY_CompId + " TEXT not null , "
                + MatchInfo.KEY_CurrentMatch + " INTEGER not null , "
                + MatchInfo.KEY_DeviceNum + " INTEGER not null ,"
                + "PRIMARY KEY ( '" + MatchInfo.KEY_CompId
                + "'" + MatchInfo.KEY_CurrentMatch + "' ),"
                + "FOREIGN KEY ( " + MatchInfo.KEY_CompId + " ) REFERENCES " + Competitions.TABLE
                + " ( " + Competitions.KEY_CompId + " ))";
    }

    public static String createIndex(){
        return "CREATE UNIQUE INDEX '" + MatchInfo.INDEX + "' ON "
                + MatchInfo.TABLE
                + " ( " + MatchInfo.KEY_CompId
                + " , " + MatchInfo.KEY_CurrentMatch
                + " , " + MatchInfo.KEY_DeviceNum + " )";
    }

    public int insert(MatchInfo matchInfo){
        int matchInfoID;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(MatchInfo.KEY_CompId, matchInfo.getCompId());
        values.put(MatchInfo.KEY_CurrentMatch, matchInfo.getCurrentMatch());
        values.put(MatchInfo.KEY_DeviceNum, matchInfo.getDeviceNum());

        matchInfoID = (int) db.insertWithOnConflict(MatchInfo.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        DatabaseManager.getInstance().closeDatabase();

        return matchInfoID;
    }

    public void update(MatchInfo matchInfo){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(MatchInfo.KEY_CurrentMatch, matchInfo.getCurrentMatch());
        values.put(MatchInfo.KEY_DeviceNum, matchInfo.getDeviceNum());

        db.updateWithOnConflict(MatchInfo.TABLE, values, MatchInfo.KEY_CompId + " = '" + matchInfo.getCompId(), null, SQLiteDatabase.CONFLICT_IGNORE);
        DatabaseManager.getInstance().closeDatabase();
    }

    public void delete(){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(MatchInfo.TABLE, null, null);
    }

    public void deleteForComp(String event){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(MatchInfo.TABLE, MatchInfo.KEY_CompId + " = '" + event + "'", null);
    }

    public MatchInfo getMatchInfo(String event, String type){
         MatchInfo matchInfo = new MatchInfo();
         matchInfo.setCompId(event);

         SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
         String selectQuery = " SELECT MatchInfo." + MatchInfo.KEY_CurrentMatch
                 + " , MatchInfo." + MatchInfo.KEY_DeviceNum
                 + " FROM " + MatchInfo.TABLE
                 + " WHERE " + MatchInfo.KEY_CompId + " = '" + event + "'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            matchInfo.setCurrentMatch(cursor.getInt(cursor.getColumnIndex(MatchInfo.KEY_CurrentMatch)));
            matchInfo.setDeviceNum(cursor.getInt(cursor.getColumnIndex(MatchInfo.KEY_DeviceNum)));
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return matchInfo;
    }

    public MatchInfo getMatchInfoNext(String event, String type){
        MatchInfo matchInfo = new MatchInfo();
        matchInfo.setCompId(event);

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT MatchInfo." + MatchInfo.KEY_CurrentMatch
                + " , MatchInfo." + MatchInfo.KEY_DeviceNum
                + " FROM " + MatchInfo.TABLE
                + " WHERE " + MatchInfo.KEY_CompId + " = '" + event + "'";

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToLast()){
            matchInfo.setCurrentMatch(cursor.getInt(cursor.getColumnIndex(MatchInfo.KEY_CurrentMatch)));
            matchInfo.setDeviceNum(cursor.getInt(cursor.getColumnIndex(MatchInfo.KEY_DeviceNum)));
        }

        cursor.close();
        DatabaseManager.getInstance().closeDatabase();

        return matchInfo;
    }
}
