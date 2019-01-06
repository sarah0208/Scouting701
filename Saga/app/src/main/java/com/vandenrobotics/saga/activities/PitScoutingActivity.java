package com.vandenrobotics.saga.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.vandenrobotics.saga.R;
import com.vandenrobotics.saga.data.model.PitData;
import com.vandenrobotics.saga.data.repo.PitDataRepo;
import com.vandenrobotics.saga.data.repo.TeamsRepo;
import com.vandenrobotics.saga.tools.ExternalStorageTools;

import java.util.ArrayList;
import java.util.Collections;

public class PitScoutingActivity extends AppCompatActivity {

    private CheckBox pitCb_autoCrossBasline;
    private CheckBox pitCb_autoCubeInSwitch;
    private CheckBox pitCb_autoCubeInScale;
    private CheckBox pitCb_autoCubeInExchange;
    private CheckBox pitCb_autoOther;
    private CheckBox pitCb_canGetSwitch;
    private CheckBox pitCb_canGetScale;
    private EditText pitEt_canClimb;
    private EditText pitEt_progLang;
    private RadioButton pitRb_yes;
    private EditText pitEt_averageSpeed;
    private CheckBox pitCb_cycleGround;
    private CheckBox pitCb_cyclePortal;
    private CheckBox pitCb_cycleSwitches;
    private EditText pitEt_driveTrain;
    private EditText pitEt_intakeLift;
    private EditText pitEt_comments;
    private Button pitB_saveButton;
    private ArrayList<Integer> team_numbers;
    private TeamsRepo teamsRepo;
    private PitDataRepo pitDataRepo;
    private Spinner spinnerTeams;
    private ArrayAdapter<Integer> teamAdapter;
    private int mTeamNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pit_scouting);

        pitDataRepo = new PitDataRepo();

        teamsRepo = new TeamsRepo();
        team_numbers = teamsRepo.getAllTeamNums();
        Collections.sort(team_numbers);
        spinnerTeams = (Spinner) findViewById(R.id.pit_spinnerTeamNum);
        teamAdapter = new ArrayAdapter<>(this, R.layout.spinner_base, team_numbers);
        teamAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerTeams.setSelection(teamAdapter.getPosition(mTeamNumber));
        spinnerTeams.setAdapter(teamAdapter);

        spinnerTeams.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long arg3){
                mTeamNumber = Integer.parseInt(spinnerTeams.getItemAtPosition(position).toString());
                ArrayList<Integer> teamsHaveData = pitDataRepo.getTeams();
                if (!teamsHaveData.isEmpty()){
                    boolean teamLoaded = false;
                    for(int i : teamsHaveData){
                        if(i == mTeamNumber) {
                            PitData pitData = pitDataRepo.getTeamData(mTeamNumber);
                            loadData(pitData);
                            teamLoaded = true;
                        }
                    }
                    if (!teamLoaded){
                        PitData pitData = new PitData(mTeamNumber);
                        loadData(pitData);
                    }
                }else{
                    for (int team : teamsRepo.getAllTeamNums()){
                        PitData pitData = new PitData(team);
                        pitDataRepo.insert(pitData);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter){

            }
        });

        pitCb_autoCrossBasline = (CheckBox)findViewById(R.id.pitCb_autoCrossesBaseline);
        pitCb_autoCubeInExchange = (CheckBox)findViewById(R.id.pitCb_autoCubeInExchange);
        pitCb_autoCubeInScale = (CheckBox)findViewById(R.id.pitCb_autoCubeInScale);
        pitCb_autoCubeInSwitch = (CheckBox)findViewById(R.id.pitCb_autoCubeInSwitch);
        pitCb_autoOther = (CheckBox)findViewById(R.id.pitCb_autoOther);
        pitCb_canGetScale = (CheckBox)findViewById(R.id.pitCb_canGetScale);
        pitCb_canGetSwitch = (CheckBox)findViewById(R.id.pitCb_canGetSwitch);
        pitEt_averageSpeed = (EditText)findViewById(R.id.pitEt_averageSpeed);
        pitEt_canClimb = (EditText)findViewById(R.id.pitEt_canClimb);
        pitRb_yes = (RadioButton)findViewById(R.id.pitRb_yes);
        pitCb_cycleGround = (CheckBox)findViewById(R.id.pitCb_cycleGround);
        pitCb_cyclePortal = (CheckBox)findViewById(R.id.pitCb_cyclePortal);
        pitCb_cycleSwitches = (CheckBox)findViewById(R.id.pitCb_cycleSwitches);
        pitEt_driveTrain = (EditText)findViewById(R.id.pitEt_driveTrain);
        pitEt_intakeLift = (EditText)findViewById(R.id.pitEt_intakeLift);
        pitB_saveButton = (Button)findViewById(R.id.pitB_savebutton);
        pitEt_progLang = (EditText)findViewById(R.id.pitEt_progLang);
        pitEt_comments = (EditText) findViewById(R.id.pitEt_comments);

    }

   public void save(View view){
       PitData pitdata = new PitData(mTeamNumber);

       int aCB = (pitCb_autoCrossBasline.isChecked()?1:0);
       pitdata.setAutoBaseline(aCB);
       int aCE = (pitCb_autoCubeInExchange.isChecked()?1:0);
       pitdata.setAutoCubeInExchange(aCE);
       int aCSC = (pitCb_autoCubeInScale.isChecked()?1:0);
       pitdata.setAutoCubeInScale(aCSC);
       int aCSW = (pitCb_autoCubeInSwitch.isChecked()?1:0);
       pitdata.setAutoCubeInSwitch(aCSW);
       int aO = (pitCb_autoOther.isChecked()?1:0);
       pitdata.setAutoOther(aO);
       int cGSC = (pitCb_canGetScale.isChecked()?1:0);
       pitdata.setGetScale(cGSC);
       int cGSW = (pitCb_canGetSwitch.isChecked()?1:0);
       pitdata.setGetSwitch(cGSW);

       pitdata.setSpeed(pitEt_averageSpeed.getText().toString());
       pitdata.setClimb(pitEt_canClimb.getText().toString());
       pitdata.setDriveTrain(pitEt_driveTrain.getText().toString());
       pitdata.setIntakeAndMech(pitEt_intakeLift.getText().toString());
       pitdata.setLang(pitEt_progLang.getText().toString());
       pitdata.setComments(pitEt_comments.getText().toString());

       int yes = (pitRb_yes.isChecked()?1:0);
       pitdata.setFloorPickUp(yes);
       int cG = (pitCb_cycleGround.isChecked()?1:0);
       pitdata.setCycleGround(cG);
       int cP = (pitCb_cyclePortal.isChecked()?1:0);
       pitdata.setCyclePortal(cP);
       int cS = (pitCb_cycleSwitches.isChecked()?1:0);
       pitdata.setCycleSwitches(cS);

       if (pitDataRepo.insert(pitdata) == -1){
           pitDataRepo.update(pitdata);
       }

       Toast.makeText(this, "Saved Data", Toast.LENGTH_LONG).show();
       ExternalStorageTools.writeDatabaseToES();

   }

   public void loadData(PitData pitdata){

       pitCb_autoCrossBasline.setChecked(pitdata.getAutoBaseline()==1);
       pitCb_autoCubeInExchange.setChecked(pitdata.getAutoCubeInExchange()==1);
       pitCb_autoCubeInScale.setChecked(pitdata.getAutoCubeInScale()==1);
       pitCb_autoCubeInSwitch.setChecked(pitdata.getAutoCubeInSwitch()==1);
       pitCb_autoOther.setChecked(pitdata.getAutoOther()==1);
       pitCb_canGetScale.setChecked(pitdata.getGetScale()==1);
       pitCb_canGetSwitch.setChecked(pitdata.getGetSwitch()==1);
       pitCb_cycleSwitches.setChecked(pitdata.getCycleSwitches()==1);
       pitCb_cycleGround.setChecked(pitdata.getCycleGround()==1);
       pitCb_cyclePortal.setChecked(pitdata.getCyclePortal()==1);
       pitEt_averageSpeed.setText(pitdata.getSpeed());
       pitEt_canClimb.setText(pitdata.getClimb());
       pitEt_driveTrain.setText(pitdata.getDriveTrain());
       pitEt_intakeLift.setText(pitdata.getIntakeAndMech());
       pitEt_progLang.setText(pitdata.getLang());
       pitEt_comments.setText(pitdata.getComments());

   }


}
