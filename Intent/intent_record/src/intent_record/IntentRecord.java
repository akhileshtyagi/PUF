package intent_record;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * This class allows the send of Intents
 * to a service via a messenger.
 * All necessary data about the intent will be sent.
 *
 * When this class is created,
 * It will connect to the system service.
 *
 * The service will then be used to send information
 * via a messenger
 *
 * This code provides one central place where
 * interactions with the service will be handled.
 * In other words,
 * it encapulates all the service communication
 */
public class IntentRecord {
    final static String TAG = "IntentRecord";
    final static boolean BYPASS_WAIT = false;

    /** the context used to create the intent record */
    Context context;

    Messenger intent_collection_service;
    boolean intent_collection_service_bound;

    /** contains the list of intents retrieved from the IntentCollectionService */
    ArrayList<IntentData> intent_data_list;
    boolean intent_data_dirty;

    /**
     * constructor
     */
    public IntentRecord(Context context){
        this.context = context;

        this.intent_collection_service = null;
        this.intent_collection_service_bound = false;

        this.intent_data_list = new ArrayList<>();
        this.intent_data_dirty = true;

        // send the message to the bound service
        // this also binds the service
        //start_collection_service();

        // determine if the service is running
        boolean service_is_running = is_service_running(context, IntentCollectionService.class);
        if(service_is_running) {
            bind_intent_collection_service();
        }
    }

    /**
     * determine if the given service is running
     *
     * return true if the service is running
     */
    //TODO test
    private boolean is_service_running(Context context, Class c){
        ActivityManager manager = (ActivityManager)context.   getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){

            if (c.getName().equals(service.service.getClassName())) {
                //running
                return true;
            }
        }

        return false;
    }

    /**
     * destruct the IntentRecord
     */
    public void close(){
        // cause the service connection to be closed
        unbind_intent_collection_service();
    }

    /**
     * receive a list of all intents accumulated by the service thus far
     */
    public ArrayList<IntentData> receive_intent_data(){
        intent_data_dirty = true;

        // send a message to the IntentCollectionService saying we want to be sent the list
        Message message = new Message();
        message.what = IntentCollectionService.MSG_RESPOND_INTENT_LIST;
        message.replyTo = messenger;

        // send the message to the IntentCollectionService
        if(BYPASS_WAIT || wait_for_bind()) {
            try {
                intent_collection_service.send(message);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "could not receive intent data, service not bound");
            }
        }

        //TODO this would cause programs to freeze because
        //TODO code to receive the intent list will not run until after this method
        //TOOD completes
        // wait until the list has been received, then return the list
//        while(this.intent_data_dirty){
//            try{ Thread.sleep(100); }catch(Exception e){ e.printStackTrace(); }
//        }

        return intent_data_list;
    }

    /**
     * another way to send IntentData
     */
    public void send_intent_data(IntentData intent_data){
        // convert the IntentData into a message
        Message message = encode_message(intent_data);

        // send the message to the IntentCollectionService
        if(BYPASS_WAIT || wait_for_bind()) {
            try {
                intent_collection_service.send(message);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "could not send intent data, service not bound");
            }
        }
    }

    /**
     * sends an IntentData to intent_recording_service
     */
    public void send_intent_data(Intent intent, String sender, String receiver){
        // create the IntentData
        send_intent_data(new IntentData(intent, sender, receiver));
    }

    /**
     * define a Service connection which will be used
     */
    // define a service connection
    ServiceConnection service_connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            intent_collection_service = new Messenger(binder);
            intent_collection_service_bound = true;

            Log.d("ServiceConnection", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            intent_collection_service_bound = false;

            Log.d("ServiceConnection", "onServiceDisconnected");
        }
    };

    /**
     * bind the intent collection service
     */
    private void bind_intent_collection_service(){
        Intent bind_intent = new Intent(context, IntentCollectionService.class);

        // test the bind to see if its successful
        boolean bind_successful = context.bindService(bind_intent, service_connection, Context.BIND_AUTO_CREATE);

        // log if the bind was successfull
        Log.d(TAG, "bind successful: " + bind_successful);
    }

    /**
     * onDestroy, unbind intent_connection_service
     */
    private void unbind_intent_collection_service(){
        context.unbindService(service_connection);
    }

    /**
     * waits for the intent collection service to be bound before continuing
     *
     * returns true if bound,
     * false if not bound
     *
     * wait for a maximum of 1 second
     */
    private boolean wait_for_bind(){
        int wait_max = 10;
        int times_waited = 0;
        long wait_time = 20;

        // loop while intent_collection_service_bound is false
        while(!this.intent_collection_service_bound || times_waited < wait_max){
            try{ Thread.sleep(wait_time); }catch(Exception e){ e.printStackTrace(); }

            times_waited++;
        }

        return this.intent_collection_service_bound;
    }

    /**
     * start the collection service
     */
    public void start_collection_service(){
        // ask the service to do some work
        Intent say_hello_intent = new Intent(context, IntentCollectionService.class);
        say_hello_intent.setData(IntentCollectionService.Command.START.get_uri());

        // start the service
        context.startService(say_hello_intent);

        // bind to the service
        bind_intent_collection_service();
    }

    /** STATIC METHODS */

    /**
     * decode a message and store it as intent_record.IntentData
     */
    public static IntentData decode_message(Message message){
        return (IntentData)message.obj;
    }

    /**
     * Encode an intent as message to be submitted
     * to service
     */
    private static Message encode_message(IntentData intent_data){
        Message message = new Message();

        // write message fields
        message.what = IntentCollectionService.MSG_INTENT_DATA;
        message.obj = intent_data;

        return message;
    }

    /**
     * MESSENGER
     */
    /**
     * commands given to messenger to interact with service
     */
    static final int MSG_INTENT_LIST = 1;

    /**
     * Handler of incoming messages from clients.
     */
    class IncomingHandler extends Handler {
        /**
         * In this method,
         * we decide what to do with received messages.
         * Some of the received messages will contain
         * IntentData in the form:
         * Intent, sender, receiver
         */
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, msg.toString());
            switch (msg.what) {
                case MSG_INTENT_LIST:
                    // add the IntentList contained in the message to the instance variable
                    intent_data_list = (ArrayList<IntentData>)msg.obj;
                    intent_data_dirty = false;

                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger messenger = new Messenger(new IntentRecord.IncomingHandler());
}
