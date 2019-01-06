package com.vandenrobotics.saga.tabs;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.vandenrobotics.saga.R;
import com.vandenrobotics.saga.activities.MatchActivity;
import com.vandenrobotics.saga.data.model.Stats;
import com.vandenrobotics.saga.data.repo.StatsRepo;
import com.vandenrobotics.saga.views.NumberPicker;

import java.util.ArrayList;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class TeleFragment extends Fragment {

    private MatchActivity mActivity;
    private boolean viewAssigned = false;

    private ConstraintLayout teleField;

    private CheckBox teleFragCb_disabled;
    private CheckBox teleFragCb_redCard;
    private CheckBox teleFragCb_yellowCard;

    private NumberPicker teleFragNp_foulNum;
    private NumberPicker teleFragNp_techFoulNum;

    private String mEvent;
    private int mMatchNum;
    private int mTeamNum;
    private int mMatchPos;
    private String mAlliance;

    private final String mSwitch1 = "Sw1";
    private final String mScale = "Scl";
    private final String mSwitch2 = "Sw2";
    private final String mExchange = "Ex";

    private StatsRepo statsRepo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_tele, container, false);
        mActivity = (MatchActivity)getActivity();
        mEvent = mActivity.mEvent;
        mMatchNum = mActivity.mMatchNumber;
        mTeamNum = mActivity.mTeamNumber;
        mMatchPos =mActivity.mDeviceNumber;
        mAlliance = mActivity.mAllianceColor;

        statsRepo = new StatsRepo();

        if(!viewAssigned) assignViews(rootView);
        if(viewAssigned)loadData();

        return rootView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        assignViews(view);
        if (viewAssigned)loadData();
    }

    @Override
    public void onPause(){
        //gets data from teleTab when clicked out/ paused
        super.onPause();
        Stats stats = saveData();
        statsRepo.setTeleStats(stats);
    }
    public Stats saveData(){
        Stats stats = new Stats();
        stats.setCompId(mEvent);
        stats.setMatchNum(mMatchNum);
        stats.setTeamNum(mTeamNum);
        int disabled = (teleFragCb_disabled.isChecked() ? 1 : 0);
        stats.setDisabled(disabled);
        int redCard = (teleFragCb_redCard.isChecked() ? 1 : 0);
        stats.setRedCard(redCard);
        int yellowCard = (teleFragCb_yellowCard.isChecked() ? 1 : 0);
        stats.setYellowCard(yellowCard);
        int foul = (teleFragNp_foulNum.getValue());
        stats.setFoul(foul);
        int techFoul = (teleFragNp_techFoulNum.getValue());
        stats.setTechFoul(techFoul);
        Log.d("TeleFrag saveData", "team id " + stats.getTeamNum());
        return stats;
    }
    @Override
    public void onResume() {
        //when resumed, loads data onto the tab
        super.onResume();
        assignViews(getView());
        if (viewAssigned) loadData();
    }
    private void loadData() {
        Stats stats = statsRepo.getTeleStats(mEvent, mMatchNum, mMatchPos);
        teleFragCb_disabled.setChecked(stats.getDisabled() == 1);
        teleFragCb_redCard.setChecked(stats.getRedCard() == 1);
        teleFragCb_yellowCard.setChecked(stats.getYellowCard() == 1);

        teleFragNp_techFoulNum.setValue(stats.getTechFoul());
        teleFragNp_techFoulNum.setValue(stats.getFoul());
    }
    public void assignViews(View view){
        try {
            teleField = (ConstraintLayout) view.findViewById(R.id.field_view2);
            if (mAlliance.equals("RED")){
                teleField.setBackground(getResources().getDrawable(R.drawable.field2018red));
            }else{
                teleField.setBackground(getResources().getDrawable(R.drawable.field2018blue));
            }
            teleFragCb_disabled = (CheckBox) view.findViewById(R.id.teleCb_disabled);
            teleFragCb_redCard = (CheckBox) view.findViewById(R.id.teleCb_redCard);
            teleFragCb_yellowCard = (CheckBox) view.findViewById(R.id.teleCb_yellowCard);
            teleFragNp_foulNum = (NumberPicker) view.findViewById(R.id.teleNp_foulNum);
            teleFragNp_techFoulNum = (NumberPicker) view.findViewById(R.id.teleNp_techFoulNum);

            viewAssigned = true;
        }catch(Exception e){
            e.printStackTrace();
            viewAssigned = false;
        }

    }

}
