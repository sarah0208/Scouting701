package com.vandenrobotics.saga.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.vandenrobotics.saga.R;
import com.vandenrobotics.saga.dialogs.AddTeamDialogFragment;
import com.vandenrobotics.saga.dialogs.CreateMatchlistDialogFragment;
import com.vandenrobotics.saga.dialogs.DialogListener;

/**
 * Created by Programming701-A on 1/13/2016.
 */
public class OptionScreenActivity extends AppCompatActivity implements DialogListener{

    private String mEvent;

    public AddTeamDialogFragment addTeamDialogFragment;
    public CreateMatchlistDialogFragment createMatchlistDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.option_screen);
        mEvent = getIntent().getStringExtra("event");

        addTeamDialogFragment = new AddTeamDialogFragment();
        createMatchlistDialogFragment = new CreateMatchlistDialogFragment();
        createMatchlistDialogFragment.mEvent = mEvent;

    }

    public void pit_scout(View view){
        Intent intent = new Intent(this, PitScoutingActivity.class );
        intent.putExtra("event", mEvent);
        startActivity(intent);

    }
    public void match_scout(View view){
        Intent intent2 = new Intent(this, TypeActivity.class);
        intent2.putExtra("event", mEvent);
        startActivity(intent2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_opition, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_team) {
            addTeamDialogFragment.show(getSupportFragmentManager(), "DialogFragment");
        }else if(id == R.id.add_matchlist){
            createMatchlistDialogFragment.show(getSupportFragmentManager(), "DialogFragment");
        }
        //TODO add option to send via bluetooth


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
