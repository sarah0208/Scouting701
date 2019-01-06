package com.vandenrobotics.saga.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import com.vandenrobotics.saga.R;
import com.vandenrobotics.saga.data.model.MatchInfo;
import com.vandenrobotics.saga.data.model.Stats;
import com.vandenrobotics.saga.data.repo.MatchInfoRepo;
import com.vandenrobotics.saga.data.repo.MatchesRepo;
import com.vandenrobotics.saga.data.repo.StatsRepo;
import com.vandenrobotics.saga.data.repo.TeamsRepo;
import com.vandenrobotics.saga.views.NumberPicker;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import com.vandenrobotics.saga2018.data.model.MatchData;

public class ScoutActivity extends Activity {

    private final String TAG = ScoutActivity.class.getSimpleName();

    private String mEvent;
    private String mType;
    private int mDeviceNumber;
    private int mCurMatch;
    private int mTeamNumber;
    private ArrayList<Integer> team_numbers;

    private TeamsRepo teamsRepo;
    private MatchInfoRepo matchInfoRepo;
    private MatchInfo mMatchInfo;
    private MatchesRepo matchesRepo;
    private StatsRepo statsRepo;

    private Spinner spinnerDevices;
    private ArrayAdapter<CharSequence> deviceAdapter;
    private NumberPicker pickerMatches;
    private static Spinner spinnerTeams;
    private ArrayAdapter<Integer> teamAdapter;

    private CheckBox DataTransfer;
    private Button SendViaBluetooth;

    private final int MAX_MATCHES = 200; // a reasonable amount of matches to expect any event to have less than
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;

    //creates everything
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout);
        mEvent = getIntent().getStringExtra("event");
        mType = getIntent().getStringExtra("type");

        statsRepo = new StatsRepo();

        matchInfoRepo =  new MatchInfoRepo();
        if(matchInfoRepo.getMatchInfo(mEvent, mType).getScoutType() == "None"){
            MatchInfo matchInfo = new MatchInfo();
            matchInfo.setCompId(mEvent);
            matchInfo.setScoutType(mType);
            matchInfo.setCurrentMatch(1);
            matchInfo.setDeviceNum(1);
            matchInfoRepo.insert(matchInfo);
        }

        mMatchInfo = matchInfoRepo.getMatchInfoNext(mEvent, mType);
        mCurMatch = mMatchInfo.getCurrentMatch();
        mDeviceNumber = mMatchInfo.getDeviceNum();

        //TODO add matchlist logic
        matchesRepo = new MatchesRepo();

        SendViaBluetooth = (Button)findViewById(R.id.button_sendData);
        SendViaBluetooth.setEnabled(false);
        DataTransfer = (CheckBox)findViewById(R.id.checkBox_enableDataTransfer);
        DataTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DataTransfer.isChecked())
                    enableDataTransfer();
                else
                    diableDataTransfer();
            }
        });

        teamsRepo = new TeamsRepo();
        team_numbers = teamsRepo.getAllTeamNums();
        Collections.sort(team_numbers);

        matchesRepo = new MatchesRepo();

        spinnerDevices = (Spinner)findViewById(R.id.spinnerDeviceNumber);
        deviceAdapter = ArrayAdapter.createFromResource(this, R.array.deviceOptions, R.layout.spinner_base);
        deviceAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerDevices.setAdapter(deviceAdapter);
        spinnerDevices.setSelection(mDeviceNumber - 1);

        pickerMatches = (NumberPicker)findViewById(R.id.pickerMatch);
        pickerMatches.something(true);
        pickerMatches.setMinValue(1);
        pickerMatches.setMaxValue(MAX_MATCHES);
        pickerMatches.setValue(mCurMatch);

        spinnerTeams = (Spinner)findViewById(R.id.spinnerTeamNumber);
        teamAdapter = new ArrayAdapter<>(this, R.layout.spinner_base, team_numbers);
        teamAdapter.setDropDownViewResource(R.layout.spinner_dropdown);
        spinnerTeams.setSelection(teamAdapter.getPosition(mTeamNumber));
        spinnerTeams.setAdapter(teamAdapter);

        spinnerDevices.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            //loads data based on what team is selected
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long arg3) {
                mDeviceNumber=spinnerDevices.getSelectedItemPosition()+1;
                mTeamNumber = (matchesRepo.getTeamInfo(mEvent, mDeviceNumber, mCurMatch).getTeamNum() != 0 ?
                        matchesRepo.getTeamInfo(mEvent, mDeviceNumber, mCurMatch).getTeamNum() : 0);

                //TODO  add support for setting teams when no match list

                pickerMatches.setValue(mCurMatch);
                spinnerTeams.setSelection(teamAdapter.getPosition(mTeamNumber));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapter){

            }
        });

        spinnerTeams.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapter, View v, int position, long arg3) {
                mTeamNumber = Integer.parseInt(spinnerTeams.getItemAtPosition(position).toString());
                Log.d("ScoutActivity", "Setting mteam number in OnItemSelected" + mTeamNumber);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapter) {

            }
        });
    }

    public void activityMatch(View view) {
        // load the new match, passing all the info to it

        mDeviceNumber = spinnerDevices.getSelectedItemPosition() + 1;
        mCurMatch = pickerMatches.getValue();
        MatchInfo matchInfo = new MatchInfo();
        matchInfo.setCompId(mEvent);
        matchInfo.setScoutType(mType);
        matchInfo.setCurrentMatch(mCurMatch);
        matchInfo.setDeviceNum(mDeviceNumber);
        matchInfoRepo.update(matchInfo);

        mTeamNumber = (int) spinnerTeams.getSelectedItem();
        Log.d(TAG, "Setting mteam number" + mTeamNumber);

        Stats stats = new Stats();
        stats.setCompId(mEvent);
        stats.setMatchNum(mCurMatch);
        stats.setTeamNum(mTeamNumber);
        stats.setMatchPos(mDeviceNumber);

        Log.d(TAG, "" + stats.getTeamNum());
        if(statsRepo.insertAll(stats) == -1){
            statsRepo.updatePart(stats);
        }

        Intent intent = new Intent(this, MatchActivity.class);
        try {
            intent.putExtra("event", mEvent);
            intent.putExtra("matchNumber", mCurMatch);
            intent.putExtra("type", mType);
            intent.putExtra("teamNumber", mTeamNumber);
            intent.putExtra("deviceNumber", mDeviceNumber);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        startActivity(intent);
        this.finish();
    }

    public static void upDateTeam(int currentMatch){

        spinnerTeams.setSelection(currentMatch);

    }

    //sends the data via bluetooth
    public void sendDataViaBluetooth(View v) {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_LONG).show();
        } else {
            enableBluetooth();
        }

    }

    public void enableBluetooth(){
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);

        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU){

            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            File f = new File(Environment.getExternalStorageDirectory().toString(), "ScoutingData.db");
            Log.d(TAG, "Getting data from " + Environment.getExternalStorageDirectory().toString() + "/ScoutingData.db");
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> appList = pm.queryIntentActivities(intent, 0);

            if(appList.size() > 0){
                String packageName = null;
                String className = null;
                boolean found = false;
                for (ResolveInfo info: appList){
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")){
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }
                if (!found){
                    Toast.makeText(this, "Bluetooth hasn't been found", Toast.LENGTH_LONG).show();
                }
                else {
                    intent.setClassName(packageName, className);
                    startActivity(intent);
                }
            }
            else{
                Toast.makeText(this, "Bluetooth is not cancelled", Toast.LENGTH_LONG).show();
            }
        }

    }
    private void enableDataTransfer(){
        SendViaBluetooth.setEnabled(true);
    }

    private void diableDataTransfer(){
        SendViaBluetooth.setEnabled(false);
    }

}
