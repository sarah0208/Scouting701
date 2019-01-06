package com.vandenrobotics.saga.data.model;

/**
 * Created by Programming701-A on 12/13/2017.
 */

public class Matches {

    public static final String TABLE = "Matches";

    public static final String INDEX = "comp_match_team_pos";

    public static final String KEY_CompId = "CompID";
    public static final String KEY_MatchNumber = "MatchNumber";
    public static final String KEY_TeamNumber = "TeamNumber";
    public static final String KEY_MatchPosition = "MatchPosition";

    private String compId;
    private int matchNum;
    private int teamNum;
    private int matchPos;

    public String getCompId(){
        return compId;
    }

    public void setCompId(String s){
        compId = s;
    }

    public int getMatchNum(){
        return matchNum;
    }

    public void setMatchNum(int i){
        matchNum = i;
    }

    public int getTeamNum(){
        return teamNum;
    }

    public void setTeamNum(int i){
        teamNum = i;
    }

    public int getMatchPos(){
        return matchPos;
    }

    public void setMatchPos(int i){
        matchPos = i;
    }
}
