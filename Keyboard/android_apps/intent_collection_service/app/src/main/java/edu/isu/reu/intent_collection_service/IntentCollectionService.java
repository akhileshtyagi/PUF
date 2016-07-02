package edu.isu.reu.intent_collection_service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by element on 6/25/16.
 */
public class IntentCollectionService extends Service {
    private final IBinder binder = new IntentCollectionBinder(this);

    /**
     * provide an enum with a list of possible commands
     */
    public enum Command{
        STOP("stop"),
        START("start");

        private String string_value;
        private Uri uri_value;

        Command(String string){
            this.string_value = string;
            this.uri_value = Uri.parse(string);
        }

        public String get_string(){
            return this.string_value;
        }

        public Uri get_uri(){
            return this.uri_value;
        }
    }

    /** stores a list of the intents which ave been handled */
    private List<Intent> intent_list;

        /**
         * create an constructor
         */
        public IntentCollectionService(){
            super();

            this.intent_list = new ArrayList<>();

            Log.d("ICS", "created service");
        }

    /**
     * preform startup actions
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Notification notification = create_notification();

        // start the service in the foreground
        // this will prevent it from being killed in most situations
        startForeground(5000, notification);

        Log.d("ICS", "started successfully");

        return START_STICKY;
    }

    /**
     * preform closedown actions
     */
    @Override
    public void onDestroy(){
        //TODO print when this service is stopped
        Log.d("ICS", "service stopped!");
    }


    /**
     * provide binder
     * this will allow bound applications to submit intents
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("bound", "bound");

        return binder;
    }

    /**
     * create a notification to be shown while this service is active
     */
    private Notification create_notification(){
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Intent Collector")
                .setContentText("Intent collector is running.")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .build();

        return notification;
    }

    /**
     * handle an intent
     */
    protected void handle_intent(Intent workIntent){
        // add the incoming intent to the list of handled intents
        this.intent_list.add(workIntent);

        Log.d("ICS", "intents received number: " + this.intent_list.size());
    }
}
