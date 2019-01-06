package com.vandenrobotics.saga.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.vandenrobotics.saga.R;
import com.vandenrobotics.saga.data.model.Competitions;
import com.vandenrobotics.saga.data.model.Matches;
import com.vandenrobotics.saga.data.model.Teams;
import com.vandenrobotics.saga.data.repo.CompetitionsRepo;
import com.vandenrobotics.saga.data.repo.MatchInfoRepo;
import com.vandenrobotics.saga.data.repo.MatchesRepo;
import com.vandenrobotics.saga.data.repo.StatsRepo;
import com.vandenrobotics.saga.data.repo.TeamsRepo;
import com.vandenrobotics.saga.dialogs.AddEventDialogFragment;
import com.vandenrobotics.saga.dialogs.DialogListener;
import com.vandenrobotics.saga.tools.JSONTools;
import com.vandenrobotics.saga.tools.TheBlueAllianceRestClient;
import com.vandenrobotics.saga.views.EventArrayAdapter;
import com.vandenrobotics.saga.views.EventListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.vandenrobotics.saga.tools.JSONTools.sortJSONArray;

public class MainActivity extends AppCompatActivity implements DialogListener {

    private final String TAG = MainActivity.class.getSimpleName();

    private EventListView tbaListView;

    private EventArrayAdapter tbaAdapter;

    private ListView downloadedListView;
    private ArrayAdapter<Competitions> downloadedAdapter;

    private CompetitionsRepo competitionsRepo;
    private MatchesRepo matchesRepo;
    private MatchInfoRepo matchInfoRepo;
    private TeamsRepo teamsRepo;
    private StatsRepo statsRepo;
    private OwnershipRepo ownershipRepo;
    private CubesRepo cubesRepo;
    private VaultRepo vaultRepo;

    public AddEventDialogFragment addEventDialogFragment;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //....Initialize variables....
        // reset all Blue Alliance Events so that the list does not appear when we do not have internet connection
        tbaAdapter = new EventArrayAdapter(new ArrayList<JSONObject>(), this);

        tbaAdapter.setNotifyOnChange(true);

        tbaListView = (EventListView) findViewById(R.id.tbaEventListView);
        tbaListView.setAdapter(tbaAdapter);
        tbaListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                JSONObject event = (JSONObject) adapter.getItemAtPosition(position);

                Competitions competitions = new Competitions();

                try {
                    competitions.setCompId(event.getString("key"));
                    competitions.setCompName(event.getString("name"));
                    competitions.setCompDate(event.getString("start_date"));

                    Log.d(TAG, "Adding " + competitions.getCompId() + " : " + competitions.getCompName() + " : " +competitions.getCompDate());

                    // The row number where it was inserted, -1 if already exist
                    int row = competitionsRepo.insert(competitions);

                    // if new row
                    if (row != -1)
                        downloadNewEvent(competitions);
                    else
                    {
                        AlertDialog.Builder messageAlreadyDownloaded = new AlertDialog.Builder(MainActivity.this);
                        messageAlreadyDownloaded.setTitle(R.string.text_titleAlreadyDownloaded);
                        messageAlreadyDownloaded.setMessage(R.string.text_messageAlreadyDownloaded)
                                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // pass through and close the dialog
                                    }
                                })
                                .show();

                    }

                    // Update data and notify adapter to show new added comp


                    downloadedAdapter.clear();
                    downloadedAdapter.addAll(competitionsRepo.getAllCompetitions());

                    // TODO when to export DB
                } catch (JSONException e){
                    Log.d(TAG, "Error Inserting Competition");
                }

                // perform TheBlueAllianceRestClient get on that value, and run the saving of data and pictures, etc. to a competition file

            }
        });

        // ....Downloaded events....
        competitionsRepo =  new CompetitionsRepo();

        downloadedListView = (ListView) findViewById(R.id.downloadEventListView);
        downloadedAdapter = new ArrayAdapter<Competitions>(this, android.R.layout.simple_list_item_2, android.R.id.text1, new ArrayList<Competitions>()){
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                convertView = super.getView(position, convertView,parent);
                TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
                text1.setTextColor(ResourcesCompat.getColor(getResources(), R.color.White, null));
                TextView text2 = (TextView) convertView.findViewById(android.R.id.text2);
                text2.setTextColor(ResourcesCompat.getColor(getResources(), R.color.White, null));

                text1.setText(getItem(position).getCompName());
                text2.setText(String.format("Start Date: %s" , getItem(position).getCompDate()));

                return convertView;
            }
        };

        //This will auto update list when we add data
        downloadedAdapter.setNotifyOnChange(true);
        //Adding data to adapter
        downloadedAdapter.addAll(competitionsRepo.getAllCompetitions());
        downloadedListView.setAdapter(downloadedAdapter);
        downloadedListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3) {
                Competitions event = (Competitions) adapter.getItemAtPosition(position);

                // send the event key to the ScoutActivity to gather all data in that directory
                startActivity(loadEventToScout(event));
            }
        });

        // delete downloaded event feature
        downloadedListView.setOnItemLongClickListener(new ListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View v, final int position, long arg3) {
                AlertDialog.Builder messageConfirmDelete = new AlertDialog.Builder(MainActivity.this);
                messageConfirmDelete.setTitle(R.string.text_titleConfirmDelete);
                messageConfirmDelete.setMessage(R.string.text_messageConfirmDelete)
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                String event = downloadedAdapter.getItem(position).getCompId();

                                competitionsRepo.delete(event);
                                matchesRepo = new MatchesRepo();
                                matchesRepo.deleteForComp(event);
                                matchInfoRepo = new MatchInfoRepo();
                                matchInfoRepo.deleteForComp(event);
                                statsRepo = new StatsRepo();
                                statsRepo.deleteForComp(event);
                                ownershipRepo = new OwnershipRepo();
                                ownershipRepo.deleteForComp(event);
                                cubesRepo = new CubesRepo();
                                cubesRepo.deleteForComp(event);
                                vaultRepo = new VaultRepo();
                                vaultRepo.deleteForComp(event);
                                downloadedAdapter.clear();
                                downloadedAdapter.addAll(competitionsRepo.getAllCompetitions());

                            }
                        })
                        .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // pass through and close the dialog
                            }
                        })
                        .show();
                return true; // do not also run the regular click event
            }
        });
        downLoadEvents();
    }

    public void downLoadEvents(){
        Toast.makeText(this, "Downloading events", Toast.LENGTH_LONG).show();
        // create a progress dialog to make a visual representation of the downloads and reading files
        final ProgressDialog progressDialog = ProgressDialog.show(this, getResources().getString(R.string.text_titleProgress), getResources().getString(R.string.text_messageProgressEventList));
        progressDialog.setCancelable(true);

        // check online status to see if we can load the Blue Alliance Data, otherwise load the dialog without it
        if (TheBlueAllianceRestClient.isOnline(this)) {
            TheBlueAllianceRestClient.get(this, "events/", new JsonHttpResponseHandler(){
                // no need to pass a year to the API, as it will default to the current year, which is always what we want
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray events) {
                    Log.d(TAG, "Successfully downloaded events");
                    // handle the incoming JSONArray of events and populate the list view
                    try {
                        tbaAdapter.addAll(sortJSONArray(JSONTools.parseJSONArray(events), "start_date", "name"));

                        Log.d(TAG, tbaAdapter.getCount() + " events found" );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialog.dismiss();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject jo){
                    Log.d(TAG, "Failed to download events");
                    progressDialog.dismiss();
                }

            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.refresh){
            downloadedAdapter.clear();
            downloadedAdapter.addAll(competitionsRepo.getAllCompetitions());
            downLoadEvents();
        }else if (id == R.id.action_add_event){
            addEventDialogFragment = new AddEventDialogFragment();
            addEventDialogFragment.show(getSupportFragmentManager(), "DialogFragment");
        }
        else if (id == R.id.about) {
            AlertDialog.Builder messageAbout = new AlertDialog.Builder(this);
            messageAbout.setTitle(R.string.text_titleAbout);
            messageAbout.setMessage(R.string.text_messageAbout).show();
        }
        else if (id == R.id.action_settings){

        }
        else if (id == R.id.exit_the_app){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void downloadNewEvent(final Competitions event){

        // add event to the list of downloaded events if it is new, then proceed to grab all downloadable information about it
        // if the event is not new, create a dialog alerting the user that the event has already been downloaded.

        // create a dialog to show user that the activity is working
        final ProgressDialog progressDialog = ProgressDialog.show(this, getResources().getString(R.string.text_titleProgress), getResources().getString(R.string.text_messageProgressDownload));
        progressDialog.setCancelable(true);

//        boolean newEvent = true;
//
//        // check to make sure the event is new
       //TODO check if this works, maybe to compile. Not sure if correct
//        for (int i = 0; i < downloadedAdapter.getCount(); i++) {
//            if (downloadedAdapter.getItem(i).getCompName().equals(event.getCompName())) {
//                newEvent = false;
//                break;
//            }
//        }


//        if (newEvent) {
            if(TheBlueAllianceRestClient.isOnline(MainActivity.this)) {
                TheBlueAllianceRestClient.get(MainActivity.this, "event/" + event.getCompId() + "/matches", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray teams) {
                        // handle the incoming JSONArray of teams and write them to a file
                        try {
                            final ArrayList<JSONObject> matchlist = JSONTools.sortJSonArrayMatchList(JSONTools.parseJSONArray(teams));
                            Log.d(TAG, matchlist.size()+"");

                            for (int i = 0; i < matchlist.size(); i++){

                                //save the same match num to be used for all team in this match
                                int matchNum = matchlist.get(i).getInt("match_number");

                                //separate nested JSON objects into two groups of teams from the two alliances
                                JSONObject alliances = matchlist.get(i).getJSONObject("alliances");
                                JSONObject red = alliances.getJSONObject("red");
                                JSONArray teamR = red.getJSONArray("teams");
                                JSONObject blue = alliances.getJSONObject("blue");
                                JSONArray teamB = blue.getJSONArray("teams");

                                for(int j = 0; j < 3; j++){

                                    Matches matches =  new Matches();
                                    matches.setCompId(event.getCompId());
                                    matches.setMatchNum(matchNum);
                                    //save position 1-3 for Red Teams
                                    matches.setMatchPos(j+1);
                                    //get each team while removing formatting (frc#)
                                    matches.setTeamNum(Integer.parseInt(teamR.getString(j).substring(3)));
                                    matchesRepo.insert(matches);
                                }

                                for (int j = 0; j < 3; j++){

                                    Matches matches =  new Matches();
                                    matches.setCompId(event.getCompId());
                                    matches.setMatchNum(matchNum);
                                    //save position 4-6 for Blue Teams
                                    matches.setMatchPos(j+4);
                                    //get each team while removing formatting (frc#)
                                    matches.setTeamNum(Integer.parseInt(teamB.getString(j).substring(3)));
                                    matchesRepo.insert(matches);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                });


                TheBlueAllianceRestClient.get(MainActivity.this, "event/" + event.getCompId() + "/teams", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray teams) {
                        // handle the incoming JSONArray of teams and write them to a file
                        try {
                            final ArrayList<JSONObject> teamlist = JSONTools.sortJSONArray(JSONTools.parseJSONArray(teams), "team_number");

                            teamsRepo =  new TeamsRepo();

                            for (int i = 0; i < teamlist.size(); i++){
                                Teams teamsInfo = new Teams();
                                teamsInfo.setTeamNum(teamlist.get(i).getInt("team_number"));
                                teamsInfo.setTeamName(teamlist.get(i).getString("nickname"));

                                Log.d(TAG, "Adding Team: " + teamsInfo.getTeamNum());

                                if (teamsInfo != null){
                                    int j  = teamsRepo.insert(teamsInfo);

                                    if (j == -1){
                                        Log.d(TAG, teamsInfo.getTeamNum() + " has already been added");
                                    }
                                }else{
                                    Log.d(TAG, "No Teams for the event: " + event.getCompName());
                                    teamsInfo.setTeamNum(0);
                                    teamsInfo.setTeamName("DefaultTeam");
                                    teamsRepo.insert(teamsInfo);
                                }
                            }


                            //TODO determine what this does
//                                downloadedEvents.add(event);
//                                downloadedEvents = sortJSONArray(downloadedEvents, "start_date", "name");
//                                downloadedAdapter.notifyDataSetChanged();

                            //ExternalStorageTools.writeEvents(downloadedEvents);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                });

            } else {
                progressDialog.dismiss();
                AlertDialog.Builder messageInternetConnectionError = new AlertDialog.Builder(MainActivity.this);
                messageInternetConnectionError.setTitle(R.string.text_titleNoInternet);
                messageInternetConnectionError.setMessage(R.string.text_messageNoInternet)
                        .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // pass through and close the dialog
                            }
                        })
                        .show();
            }
    }

    private Intent loadEventToScout(Competitions event){
        Intent intent = new Intent(this, OptionScreenActivity.class);
        intent.putExtra("event", event.getCompId());
        return intent;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        downloadedAdapter.clear();
        downloadedAdapter.addAll(competitionsRepo.getAllCompetitions());
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {

    }
}
