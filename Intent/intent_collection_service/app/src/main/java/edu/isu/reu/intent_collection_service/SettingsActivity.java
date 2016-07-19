package edu.isu.reu.intent_collection_service;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import intent_collection.DummyIntentAdderService;
import intent_record.IntentCollectionService;
import intent_record.IntentRecord;
import intent_visualizer.IntentVisualizer;

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
 * TODO list
 * [ ] implement an aidl binder which will allow communication with this service from other apps
 * [ ] implement a graphical way of viewing intents
 * [ ] add buttons to enable use of the different functionalities
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
    public final String TAG = "IC_Interface";

    Messenger intent_collection_service;
    boolean intent_collection_service_bound;

    IntentRecord intent_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the xml layout
        setContentView(R.layout.layout_settings_activity);

        // set up action bar
        setupActionBar();

        // create buttson and button listeners
        setup_buttons();

        // start the intent collection service
        //start_collection_service();
        this.intent_record = new IntentRecord(this);

        //TODO use the intent_record library for submitting intent data

        // start a dummy service to add intents
        //TODO this should be changed to get real intents at some point
        //TODO there is likely code elsewhere which will provide this functionality
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
        // get the layout to add buttons to
        LinearLayout linear_layout = (LinearLayout) findViewById(R.id.master_layout);

        // stop collection
        linear_layout.addView(create_button("stop collection",
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        //TODO
                    }
                }));

        // start collection
        linear_layout.addView(create_button("start collection",
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        //TODO
                    }
                }));

        // intent graph
        linear_layout.addView(create_button("View intent graph",
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        Intent intent = new Intent(getApplicationContext(), IntentVisualizer.class);
                        startActivity(intent);
                    }
                }));

        // transition matrix
        linear_layout.addView(create_button("View transition matrix",
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        //TODO
                    }
                }));

        // incoming and outgoing intent vectors
        linear_layout.addView(create_button("View incoming, outgoing intent vectors",
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        //TODO
                    }
                }));

        // data listing
        linear_layout.addView(create_button("List data",
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        Log.d(TAG, "listing data");

                        //TODO
                    }
                }));

        // set the current layout to the one we just created
        setContentView(linear_layout);
    }

    /**
     * build a button with the given String as text
     */
    private Button create_button(String button_text, View.OnClickListener on_click_listener){
        Button button = new Button(this);

        // set button properties
        button.setText(button_text);
        button.setOnClickListener(on_click_listener);

        return button;
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

        // bind to the service
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
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                intent_collection_service = new Messenger(binder);

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
//                    if(intent_collection_service_bound) {
//                        // create the dummy mesage
//                        Message message = new Message();
//                        message.what = 1;
//
//                        // add dummy Intent
//                        try{intent_collection_service.send(message);}
//                        catch(Exception e){e.printStackTrace();}
//
//                        // say that intent has been added
//                        Log.d("DummyThread", "intent added!");
//                    }

                    //TODO set real information in IntentData
                    // use IntentRecord to try to send an intent
                    intent_record.send_intent_data(new Intent(), new Intent(), new Intent());

                    // wait TIME_INTERVAL
                    try{ Thread.sleep(TIME_INTERVAL); }catch(Exception e){ e.printStackTrace(); }
                }
            }
        };

        // create thread
        new Thread(task).start();
    }
}
