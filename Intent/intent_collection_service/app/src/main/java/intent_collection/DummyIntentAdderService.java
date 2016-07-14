package intent_collection;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import edu.isu.reu.intent_collection_service.R;
import intent_record.IntentCollectionService;

/**
 * Created by tim on 6/27/16.
 *
 * This service will add fake intents
 * at TIME_INTERVAL into the
 * IntentCollectionService
 *
 * NOTE: IntentCollectionService must be running first
 */
public class DummyIntentAdderService extends Service {
    /** time interval inbetween adding intents */
    public final long TIME_INTERVAL = 5000;

    /** tracks the state of intent collection service */
    private IntentCollectionService intent_collection_service;
    private boolean intent_collection_service_bound;

    /**
     * constructor
     * starts a separate thread to add events at interval
     */
    public DummyIntentAdderService(){
        super();

        // bind the intent collection service
        bind_intent_collection_service();

        // create the task
        Runnable task = new Runnable(){
            @Override
            public void run(){
                // forever
                while(true){
                    // use binding to add a dummy Intent
                    //intent_collection_service.handle_intent(new Intent());

                    // wait TIME_INTERVAL
                    try{ Thread.sleep(TIME_INTERVAL); }catch(Exception e){ e.printStackTrace(); }
                }
            }
        };

        // create thread
        new Thread(task).start();
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

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * bind the intent collection service
     */
    private void bind_intent_collection_service(){
        // get IntentCVollectionService binding
        //TODO This does not work. I think I need activity reference instead of service reference.
        Log.d("isitnull:", "" + this.toString());

        //Intent bind_intent = new Intent(this, IntentCollectionService.class);
        Intent bind_intent = new Intent(getApplicationContext(), IntentCollectionService.class);

        // define a service connection
        ServiceConnection service_connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                IntentCollectionBinder intent_collection_binder = (IntentCollectionBinder) binder;
                intent_collection_service = intent_collection_binder.get_service();

                intent_collection_service_bound = true;

                Log.d("ServiceConnection", "onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                intent_collection_service_bound = false;

                Log.d("ServiceConnection", "onServiceDisconnected");
            }
        };

        // test the bind to see if its successful
        boolean bind_successful = bindService(bind_intent, service_connection, Context.BIND_AUTO_CREATE);

        // log if the bind was successfull
        Log.d("DIAS", "bind successful: " + bind_successful);
    }

    /**
     * create a notification to be shown while this service is active
     */
    private Notification create_notification(){
        Notification notification = new Notification.Builder(this)
                .setContentTitle("Dummy Intent Adder")
                .setContentText("Dummy Intent Adder is running.")
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .build();

        return notification;
    }
}
