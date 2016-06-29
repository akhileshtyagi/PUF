package edu.isu.reu.intent_collection_service;


import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import java.util.List;

/**
 * TODO list
 *
 * This Activity provides:
 *      -- preferences for a service collecting system intents
 *      -- when this activity is created, it starts the service if not already running
 *      -- buttons to call other activities which analyze these system intents
 *          ++ these other activities will also display graphically, the intent graph
 */

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatActivity {
    public final long TIME_INTERVAL = 3000;

    IntentCollectionService intent_collection_service;
    boolean intent_collection_service_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set up action bar
        setupActionBar();

        // create buttson and button listeners
        setup_buttons();

        // start the intent collection service
        start_collection_service();

        // start a dummy service to add intents
        //TODO this should be changed to get real intents at some point
        //start_dummy_intent_adder_service();
        start_dummy_intent_adder_thread();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * create buttson and button listeners
     *
     * buttons are:
     *      - stop collection
     *      - start collection
     *      - intent graph
     *      - transition matrix
     *      - incoming and outgoing vectors
     *
     * the goal of these presences is to modify the functionality of the
     * intent collection service
     */
    private void setup_buttons(){
        //TODO
    }

    /**
     * start the collection service
     */
    private void start_collection_service(){
        // ask the service to do some work
        Intent say_hello_intent = new Intent(this, IntentCollectionService.class);
        say_hello_intent.setData(IntentCollectionService.Command.START.get_uri());

        // start the service
        this.startService(say_hello_intent);

        bind_intent_collection_service();
    }

    /**
     * bind the intent collection service
     */
    private void bind_intent_collection_service(){
        //Intent bind_intent = new Intent(this, IntentCollectionService.class);
        Intent bind_intent = new Intent(this, IntentCollectionService.class);

        // define a service connection
        ServiceConnection service_connection = new ServiceConnection() {
            //TODO this method doesn't seem to be getting called
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                IntentCollectionBinder intent_collection_binder = (IntentCollectionBinder) binder;
                intent_collection_service = intent_collection_binder.get_service();

                intent_collection_service_bound = true;

                //TODO figure out why this log call never runs
                Log.d("ServiceConnection", "onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                intent_collection_service_bound = false;

                Log.d("ServiceConnection", "onServiceDisconnected");
            }
        };

        // test the bind to see if its successful
        boolean bind_successful = getApplicationContext().bindService(bind_intent, service_connection, Context.BIND_AUTO_CREATE);

        // log if the bind was successfull
        Log.d("DIAS", "bind successful: " + bind_successful);
    }

    /**
     * stop the collection service
     */
    private void stop_collection_service(){
        Intent stop_intent = new Intent(this, IntentCollectionService.class);
        stop_intent.setData(IntentCollectionService.Command.STOP.get_uri());

        // stop the service
        stopService(stop_intent);
    }

    /**
     * start dummy intent adder service
     */
    private void start_dummy_intent_adder_service(){
        // ask the service to do some work
        Intent say_hello_intent = new Intent(this, DummyIntentAdderService.class);

        // start the service
        this.startService(say_hello_intent);
    }

    /**
     * start dummy intent adder thread
     */
    private void start_dummy_intent_adder_thread(){
        // create the task
        Runnable task = new Runnable(){
            @Override
            public void run(){
                // forever
                while(true){
                    // use binding to add a dummy Intent
                    if(intent_collection_service_bound) {
                        // add dummy Intent
                        intent_collection_service.handle_intent(new Intent());

                        // say that intent has been added
                        Log.d("DummyThread", "intent added!");
                    }

                    // wait TIME_INTERVAL
                    try{ Thread.sleep(TIME_INTERVAL); }catch(Exception e){ e.printStackTrace(); }
                }
            }
        };

        // create thread
        new Thread(task).start();
    }
}
