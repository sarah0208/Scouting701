package com.vandenrobotics.saga.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.vandenrobotics.saga.R;
import com.vandenrobotics.saga.data.model.Competitions;
import com.vandenrobotics.saga.data.repo.CompetitionsRepo;

/**
 * Created by Programming701-A on 1/23/2018.
 */

public class AddEventDialogFragment extends DialogFragment {
    final static private String TAG = AddEventDialogFragment.class.getSimpleName();

    // Use this instance of the interface to deliver action events
    DialogListener mListener;

    private EditText editTextCompId;
    private EditText editTextCompName;
    private EditText editTextCompDate;

    private String compId  = "Default";
    private String compName = "Default";
    private String compDate = "XXXX-XX-XX";

    private CompetitionsRepo competitionsRepo;

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
        View view = inflater.inflate(R.layout.dialog_add_event, null);

        assignViews(view);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ADD EVENT")
                .setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(mListener != null) {
                            mListener.onDialogPositiveClick(AddEventDialogFragment.this);
                            Competitions competitions = new Competitions();
                            competitions.setCompId(editTextCompId.getText().toString());
                            competitions.setCompName(editTextCompName.getText().toString());
                            competitions.setCompDate(editTextCompDate.getText().toString());
                            competitionsRepo.insert(competitions);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mListener.onDialogNegativeClick(AddEventDialogFragment.this);
                    }
                });
        return builder.create();
    }

    private void assignViews(View view){
        competitionsRepo =  new CompetitionsRepo();

        editTextCompId = (EditText) view.findViewById(R.id.editableTextEventId);
        editTextCompName = (EditText) view.findViewById(R.id.editableTextEventName);
        editTextCompDate = (EditText) view.findViewById(R.id.editableTextEventDate);

    }
}
