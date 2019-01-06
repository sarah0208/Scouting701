package com.vandenrobotics.saga.data.model;

/**
 * Created by Programming701-A on 12/13/2017.
 */

//object class used to hold data for one row in the comp table and to hold the keys for that table
public class Competitions {

    public static final String TABLE = "Competitions";
    //KEYs to be referred to in the repo where the table will be made - used for column names
    public static final String KEY_CompId = "CompID";
    public static final String KEY_CompName = "CompName";
    public static final String KEY_CompStartDate = "CompDate";

    //all the values held in one comp row in the comp table
    private String compId;
    private String compName;
    private String compDate;

    //getters and setters for each of the values
    public String getCompId(){
        return compId;
    }

    public void setCompId(String s){
        compId = s;
    }

    public String getCompName(){
        return compName;
    }

    public void setCompName(String s){
        compName = s;
    }

    public String getCompDate(){
        return compDate;
    }

    public void setCompDate(String s){
         compDate = s;
    }
}
