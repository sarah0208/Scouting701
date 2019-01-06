package com.vandenrobotics.saga.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class JSONTools {

    public static ArrayList<JSONObject> parseJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++)
            jsonObjects.add(jsonArray.getJSONObject(i));
        return jsonObjects;
    }

    public static ArrayList<JSONObject> sortJSonArrayMatchList(ArrayList<JSONObject> match_list){
        ArrayList<JSONObject> sortedmatchlist = new ArrayList<>();
        for (int i = 0; i < match_list.size(); i++){
                sortedmatchlist.add(null);
        }
        for (int i = 0; i < sortedmatchlist.size(); i++){
            try {
                if (match_list.get(i).getString("comp_level").equals("qm")){
                sortedmatchlist.set(match_list.get(i).getInt("match_number") - 1,match_list.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            }
        sortedmatchlist.removeAll(Collections.singleton(null));
        return sortedmatchlist;
    }

    public static ArrayList<JSONObject> sortJSONArray(ArrayList<JSONObject> jsonObjects, final String sortParam) {
        ArrayList<JSONObject> jso = jsonObjects;

        Collections.sort(jso, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    return a.getString(sortParam).compareTo(b.getString(sortParam));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });

        return jso;
    }

    public static ArrayList<JSONObject> sortJSONArray(ArrayList<JSONObject> jsonObjects, final String sortParam1, final String sortParam2) {
        ArrayList<JSONObject> jso = jsonObjects;

        Collections.sort(jso, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                try {
                    int result = a.getString(sortParam1).compareToIgnoreCase(b.getString(sortParam1));
                    return ((result!=0) ? result : a.getString(sortParam2).compareToIgnoreCase(b.getString(sortParam2)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return -1;
            }
        });

        return jso;
    }

//    public static ArrayList<JSONObject> sortJSONArray(Competitions, final String sortParam1, final String sortParam2){
//
//    }
}
