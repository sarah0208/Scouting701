package com.vandenrobotics.saga.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.vandenrobotics.saga.R;
import com.vandenrobotics.saga.data.model.Teams;
import com.vandenrobotics.saga.data.repo.TeamsRepo;

/**
 * Created by Programming701-A on 1/20/2018.
 */

public class AddTeamDialogFragment extends DialogFragment {

    final static private String TAG = AddTeamDialogFragment.class.getSimpleName();

    // Use this instance of the interface to deliver action events
    DialogListener mListener;

    private EditText editTextTeamNum;
    private EditText editTextTeamName;

    private int teamNum  = 0;
    private String teamName = "Default";

    private TeamsRepo teamsRepo;

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
        View view = inflater.inflate(R.layout.dialog_add_team, null);

        assignViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ADD TEAM")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mListener != null) {
                            Teams teams = new Teams();
                            mListener.onDialogPositiveClick(AddTeamDialogFragment.this);
                            teamNum = Integer.parseInt(editTextTeamNum.getText().toString());
                            teamName = editTextTeamName.getText().toString();

                            teams.setTeamNum(teamNum);
                            teams.setTeamName(teamName);

                            teamsRepo = new TeamsRepo();
                            if (teamsRepo.insert(teams) == -1){
                                teamsRepo.update(teams);
                            }
                            Log.d(TAG, "Added Team: " + teamNum + " - " + teamName);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(AddTeamDialogFragment.this);
                    }
                });
        return builder.create();
    }

    private void assignViews(View view){

        editTextTeamNum = (EditText) view.findViewById(R.id.editableTextTeamNum);
        editTextTeamName = (EditText) view.findViewById(R.id.editableTextTeamName);

    }
}
