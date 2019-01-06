package com.vandenrobotics.saga.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.vandenrobotics.saga.R;
import com.vandenrobotics.saga.data.model.Matches;
import com.vandenrobotics.saga.data.repo.MatchesRepo;
import com.vandenrobotics.saga.data.repo.TeamsRepo;
import com.vandenrobotics.saga.views.NumberPicker;

import java.util.ArrayList;

/**
 * Created by Programming701-A on 1/21/2018.
 */

public class CreateMatchlistDialogFragment extends DialogFragment {

    final static private String TAG = CreateMatchlistDialogFragment.class.getSimpleName();

    // Use this instance of the interface to deliver action events
    DialogListener mListener;

    private Spinner spinnerRed1;
    private Spinner spinnerRed2;
    private Spinner spinnerRed3;
    private Spinner spinnerBlue1;
    private Spinner spinnerBlue2;
    private Spinner spinnerBlue3;

    private Button buttonAddMatch;

    private com.vandenrobotics.saga.views.NumberPicker mMatchNum;

    private TeamsRepo teamsRepo;
    private MatchesRepo matchesRepo;
    private ArrayList<Matches> matchlist;

    private ArrayList<Integer> team_numbers;
    private ArrayList<Integer> teamsForMatch;
    private ArrayAdapter<Integer> teamAdapter;

    public String mEvent;

    private boolean differentTeams = true;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_matchlist, null);

        assignViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create Matchlist")
                .setView(view)
                .setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mListener != null) {
                            mListener.onDialogPositiveClick(CreateMatchlistDialogFragment.this);
                            for (Matches matches: matchlist){
                                if(matchesRepo.insert(matches) == -1){
                                    matchesRepo.update(matches);
                                }
                            }
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(CreateMatchlistDialogFragment.this);
                    }
                });
        return builder.create();
    }

    private void assignViews(View view){
        teamsRepo = new TeamsRepo();
        matchesRepo = new MatchesRepo();
        teamsForMatch = new ArrayList<>();
        matchlist = new ArrayList<>();

        spinnerRed1 = (Spinner) view.findViewById(R.id.spinnerR1);
        spinnerRed2 = (Spinner) view.findViewById(R.id.spinnerR2);
        spinnerRed3 = (Spinner) view.findViewById(R.id.spinnerR3);
        spinnerBlue1 = (Spinner) view.findViewById(R.id.spinnerB1);
        spinnerBlue2 = (Spinner) view.findViewById(R.id.spinnerB2);
        spinnerBlue3 = (Spinner) view.findViewById(R.id.spinnerB3);

        buttonAddMatch = (Button) view.findViewById(R.id.buttonAddMatch);

        mMatchNum = (NumberPicker) view.findViewById(R.id.numberPickerMatch);
        mMatchNum.setMinValue(1);
        mMatchNum.setValue(1);

        team_numbers = teamsRepo.getAllTeamNums();
        teamAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_base, team_numbers);
        teamAdapter.setDropDownViewResource(R.layout.spinner_dropdown);

        spinnerRed1.setAdapter(teamAdapter);
        spinnerRed2.setAdapter(teamAdapter);
        spinnerRed3.setAdapter(teamAdapter);
        spinnerBlue1.setAdapter(teamAdapter);
        spinnerBlue2.setAdapter(teamAdapter);
        spinnerBlue3.setAdapter(teamAdapter);

        buttonAddMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int Red1  = Integer.parseInt(spinnerRed1.getSelectedItem().toString());
                int Red2  = Integer.parseInt(spinnerRed2.getSelectedItem().toString());
                int Red3  = Integer.parseInt(spinnerRed3.getSelectedItem().toString());
                int Blue1  = Integer.parseInt(spinnerBlue1.getSelectedItem().toString());
                int Blue2  = Integer.parseInt(spinnerBlue2.getSelectedItem().toString());
                int Blue3  = Integer.parseInt(spinnerBlue3.getSelectedItem().toString());

                teamsForMatch.add(Red1);
                teamsForMatch.add(Red2);
                teamsForMatch.add(Red3);
                teamsForMatch.add(Blue1);
                teamsForMatch.add(Blue2);
                teamsForMatch.add(Blue3);

                differentTeams = true;

                for (int i = 0; i < teamsForMatch.size()-1; i++){
                    if (teamsForMatch.get(0) == teamsForMatch.get(i+1)){
                        differentTeams = false;
                    }
                    if (i >= 1){
                        if(teamsForMatch.get(1) == teamsForMatch.get(i+1)){
                            differentTeams = false;
                        }
                    }
                    if (i >= 2){
                        if(teamsForMatch.get(2) == teamsForMatch.get(i+1)){
                            differentTeams = false;
                        }
                    }
                    if (i >= 3){
                        if(teamsForMatch.get(3) == teamsForMatch.get(i+1)){
                            differentTeams = false;
                        }
                    }
                    if (i >= 4){
                        if (teamsForMatch.get(4) == teamsForMatch.get(i+1)){
                            differentTeams = false;
                        }
                    }
                }

                if(differentTeams) {
                    int matchNum = mMatchNum.getValue();
                    for (int i = 0; i < 6; i++) {
                        Matches matches = new Matches();
                        matches.setCompId(mEvent);
                        matches.setMatchNum(matchNum);
                        matches.setTeamNum(teamsForMatch.get(i));
                        matches.setMatchPos(i);
                        matchlist.add(matches);
                        Toast.makeText(getContext(), "Added Match", Toast.LENGTH_SHORT).show();
                    }
                    mMatchNum.increment();
                }else{
                    Toast.makeText(getContext(), "Input Different Teams", Toast.LENGTH_LONG).show();
                }
            }
        });


    }
}
