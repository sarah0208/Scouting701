package com.vandenrobotics.saga.data.model;

/**
 * Created by Programming701-A on 1/17/2018.
 */

public class PitData {

    public static final String TABLE = "PitData";
    public static final String KEY_TeamNum = "TeamNumber";
    public static final String KEY_AutoOther = "AutoOther";
    public static final String KEY_IntakeAndMech = "TypeOfIntakeAndMech";
    public static final String KEY_DriveTrain = "TypeOfDriveTrain";
    public static final String KEY_Speed = "AverageSpeed";
    public static final String KEY_Lang = "ProgrammingLanguage";
    public static final String KEY_Comments = "Comments";

    private int teamNum;
    private int autoOther;
    private String intakeAndMech;
    private String driveTrain;
    private String speed;
    private String lang;
    private String comments;

    public PitData(int team){
        teamNum = team;
        autoOther = 0;
        intakeAndMech = "";
        driveTrain = "";
        lang = "";
        comments = "";
    }

    public int getTeamNum(){
        return teamNum;
    }
    public void setTeamNum(int s){
        teamNum = s;
    }

    public int getAutoOther(){
        return autoOther;
    }
    public void setAutoOther(int s){
        autoOther = s;
    }

    public String getIntakeAndMech(){
        return intakeAndMech;
    }
    public void setIntakeAndMech(String s){
        intakeAndMech = s;
    }

    public String getDriveTrain(){
        return driveTrain;
    }
    public void setDriveTrain(String s){
        driveTrain = s;
    }

    public String getSpeed(){
        return speed;
    }
    public void setSpeed(String s){
        speed = s;
    }

    public String getLang(){
        return lang;
    }
    public void setLang(String s){
        lang = s;
    }

    public String getComments(){
        return comments;
    }
    public void setComments(String s){
        comments = s;
    }
}

