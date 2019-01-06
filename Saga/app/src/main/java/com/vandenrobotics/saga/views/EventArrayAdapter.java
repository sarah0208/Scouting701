package com.vandenrobotics.saga.views;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EventArrayAdapter extends ArrayAdapter<JSONObject> implements SectionIndexer {

    private ArrayList<JSONObject> competitons;
    private Context context;

    // Headers for each comp week to display
    private static String[] sections = {"WK1", "WK2", "WK3", "WK4", "WK5", "WK6", "WK7", "CMP"};
    // The week in the year that the comp weeks start. In order
    private static final int[] weekOfYear = {7,8,9,10,11,12,13,14};

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public EventArrayAdapter(ArrayList<JSONObject> competitions, Context ctx) {
        super(ctx, android.R.layout.simple_list_item_2, android.R.id.text1, competitions);
        this.competitons = competitions;
        this.context = ctx;
    }

    public int getCount() {
        return competitons.size();
    }

    public JSONObject getItem(int position) {
        return competitons.get(position);
    }

    public long getItemId(int position) {
        return competitons.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View view = super.getView(position, convertView, parent);
        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

        try {
            text1.setText(competitons.get(position).getString("name"));
            text2.setText("Start Date: " + competitons.get(position).getString("start_date"));
        } catch (JSONException e){
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public int getPositionForSection(int section) {
        int pos = 0;

        try {
            // Creating calender object for start of selected week
            Calendar selectedStartSectionWeek = Calendar.getInstance();
            selectedStartSectionWeek.setWeekDate(selectedStartSectionWeek.getWeekYear(), weekOfYear[section] - 1, Calendar.SATURDAY);

            // ""  end of selected week
            Calendar selectedEndSectionWeek = Calendar.getInstance();
            selectedEndSectionWeek.setWeekDate(selectedEndSectionWeek.getWeekYear(), weekOfYear[section] + 1, Calendar.SUNDAY);

            // For for first event of selected week
            for (JSONObject event : competitons) {
                Calendar eventDate = Calendar.getInstance();
                eventDate.setTime(simpleDateFormat.parse(event.getString("start_date")));

                if (eventDate.after(selectedStartSectionWeek) && eventDate.before(selectedEndSectionWeek)) {
                    pos = competitons.indexOf(event);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pos;
    }

    @Override
    public int getSectionForPosition(int arg0) {
        // Doesn't appear to be used
        return 0;
    }

    @Override
    public Object[] getSections() {
        return sections;
    }

}
