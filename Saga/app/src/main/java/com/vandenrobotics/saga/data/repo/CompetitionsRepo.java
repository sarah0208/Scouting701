package com.vandenrobotics.saga.data.repo;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vandenrobotics.saga.data.DatabaseManager;
import com.vandenrobotics.saga.data.model.Competitions;

import java.util.ArrayList;

/**
 * Created by Programming701-A on 12/18/2017.
 */

public class CompetitionsRepo {
    private final String TAG = CompetitionsRepo.class.getSimpleName();

    private Competitions competitions;

    public CompetitionsRepo(){

        competitions = new Competitions();
    }

    public static String createTable(){
        return "CREATE TABLE " + Competitions.TABLE + "("
                + Competitions.KEY_CompId + "   TEXT not null PRIMARY KEY , "
                + Competitions.KEY_CompName + " TEXT , "
                + Competitions.KEY_CompStartDate + " TEXT  )  ";
    }

    public int insert(Competitions competitions){
        int compId = -1;
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        ContentValues values = new ContentValues();
        values.put(Competitions.KEY_CompId, competitions.getCompId());
        values.put(Competitions.KEY_CompName, competitions.getCompName());
        values.put(Competitions.KEY_CompStartDate, competitions.getCompDate());

        compId = (int) db.insertWithOnConflict(Competitions.TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);

        DatabaseManager.getInstance().closeDatabase();

        return compId;
    }

    public void deleteAll(){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Competitions.TABLE, null, null);
        DatabaseManager.getInstance().closeDatabase();
    }

    public void delete(String event){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Competitions.TABLE, Competitions.KEY_CompId + " =  \"" + event + "\"", null) ;
        DatabaseManager.getInstance().closeDatabase();
    }


    public Competitions getCompetition(String event){
        Competitions competitions = new Competitions();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT Competitions." + Competitions.KEY_CompId
                + ", Competitions." + Competitions.KEY_CompName
                + ", Competitions." + Competitions.KEY_CompStartDate
                + " FROM " + Competitions.TABLE
                + " WHERE Competition." + Competitions.KEY_CompId + " = " + event;

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if ( cursor.moveToFirst()){
            competitions.setCompId(cursor.getString(cursor.getColumnIndex(Competitions.KEY_CompId)));
            competitions.setCompName(cursor.getString(cursor.getColumnIndex(Competitions.KEY_CompName)));
            competitions.setCompDate(cursor.getString(cursor.getColumnIndex(Competitions.KEY_CompStartDate)));

        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return competitions;
    }

    public ArrayList<Competitions> getAllCompetitions(){
        ArrayList<Competitions> savedCompetitions = new ArrayList<>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        String selectQuery = " SELECT " + Competitions.KEY_CompId
                + ", " + Competitions.KEY_CompName
                + ", " + Competitions.KEY_CompStartDate
                + " FROM " + Competitions.TABLE;

        Log.d(TAG, selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do {
                Competitions competitions = new Competitions();
                competitions.setCompId(cursor.getString(cursor.getColumnIndex(Competitions.KEY_CompId)));
                competitions.setCompName(cursor.getString(cursor.getColumnIndex(Competitions.KEY_CompName)));
                competitions.setCompDate(cursor.getString(cursor.getColumnIndex(Competitions.KEY_CompStartDate)));

                savedCompetitions.add(competitions);
            }while(cursor.moveToNext());

        }
        cursor.close();
        DatabaseManager.getInstance().closeDatabase();
        return savedCompetitions;
    }
}
