package wrapper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import android.view.MotionEvent;
import components.Touch;
import keyboardAuthenticationInterface.KeyboardAuthenticationService;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Wraps the functionality of KeyboardAuthenticationService
 *
 * This class handles all the messenger code
 * which accomplishes two things:
 * 1. reduces code duplication in classes using this library
 * 2. makes the service easier to use
 */
public class KeyboardAuthentication {
    final String TAG = "KeyboardAuthentication";

    private volatile boolean has_replied;

    /** these are the results returned by KeyboardAuthenrticationService */
    private volatile boolean is_result_available;
    private volatile double result;

    /** variables for the service messenger */
    Messenger keyboard_authentication_service;
    boolean keyboard_authentication_service_bound;

    /** describes the context from which this class was created */
    Context context;

    /** used to queue messages if the service is not yet bound */
    private volatile Queue<Message> message_queue;

    /** thread tries to send messages periodically if ther are any in the queue */
    private Thread message_thread;

    public KeyboardAuthentication(Context context){
        this.context = context;

        has_replied = false;

        is_result_available = false;
        result = -1.0;

        keyboard_authentication_service = null;
        keyboard_authentication_service_bound = false;

        this.message_thread = null;

        // a linked list is a queue
        this.message_queue = new LinkedList<Message>();

        // bind service
        bind_keyboard_authentication_service();
    }

    /**
     * start keyboard_authentication_service if
     * it is not already started
     *
     * subsequently bind the service
     */
    private void bind_keyboard_authentication_service(){
        // ask the service to do some work
        Intent intent = new Intent(context, KeyboardAuthenticationService.class);
        intent.setData(KeyboardAuthenticationService.get_start_uri());

        // start the service
        context.startService(intent);

        // define a service connection
        ServiceConnection service_connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                keyboard_authentication_service = new Messenger(binder);
                keyboard_authentication_service_bound = true;

                Log.d(TAG, "KeyboardAuthenticationService connected");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                keyboard_authentication_service_bound = false;

                //Log.d("ServiceConnection", "onServiceDisconnected");
            }
        };

        // test the bind to see if its successful
        boolean bind_successful = context.bindService(intent, service_connection, Context.BIND_AUTO_CREATE);
        // log if the bind was successfull
        Log.d(TAG, "bind successful: " + bind_successful);
    }

    /**
     * this method is provided to wait until KeyboardAuthenticationService is bound.
     * This should be called before calling the other methods to ensure there will not be a null pointer exception.
     *
     * returns true if the service is bound within the time frame provided
     * if time frame provided is 0,
     * simply check to see if the service is bound
     *
     * returns false of the service is not bound
     *
     * time_frame is in milliseconds
     */
    public boolean wait_for_bind(long time_frame){
        long sleep_time = 100;

        // check if the service is bound,
        // if not bound, wait sleep time and check again
        // this goes on until maximum sleep time or service bound
        do{
            // sleep
            if(!keyboard_authentication_service_bound) {
                try {
                    Thread.sleep(sleep_time);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            time_frame -= sleep_time;
        }while(time_frame > 0 && !keyboard_authentication_service_bound);

        return keyboard_authentication_service_bound;

    }

    /**
     * returns true if there is a new result to be retrieved
     *
     * new results are defined to be those result values
     * which have been set, but
     * not retrieved
     */
    public boolean is_result_available(){
        Message message = new Message();

        message.what = KeyboardAuthenticationService.MSG_IS_RESULT_AVAILABLE;
        message.replyTo = messenger;

        // send the message
        try{ keyboard_authentication_service.send(message); }
        catch(Exception e){ e.printStackTrace(); }

        // wait for response
        wait_for_reply();

        return is_result_available;
    }

    /**
     * provides touch screen data to the service
     */
    public void submit_data(MotionEvent motion_event){
        Message message = new Message();

        message.what = KeyboardAuthenticationService.MSG_SUBMIT_MOTIONEVENT_DATA;
        message.obj = motion_event;

        // send the message
        try{ keyboard_authentication_service.send(message); }
        catch(Exception e){ e.printStackTrace(); }
    }

    /**
     * provides touch screen data to the service
     */
    public void submit_data(Touch touch){
        Message message = new Message();

        message.what = KeyboardAuthenticationService.MSG_SUBMIT_TOUCH_DATA;
        message.obj = touch;

        // send the message
        try{
            if(keyboard_authentication_service_bound) {
                keyboard_authentication_service.send(message);
            }else{
                //TODO if data can not be sent, add it to a queue and try to send later
                //TODO data will not be sent if keyboard authetnication has not yet been connected
                queue_send(message);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * puts the message in a queue to be sent when keyboard_authentication_service has been bound
     */
    private void queue_send(Message message){
        // add the message to the queue
        message_queue.add(message);

        // if there is no running thread which tries to send messages periodically, start one
        if(message_thread == null || !message_thread.isAlive()) {
            message_thread = new Thread(new Runnable(){
                @Override
                public void run(){
                    // try to send messages in the queue,
                    // while there are messages to send
                    while(message_queue.peek() != null) {
                        if(keyboard_authentication_service_bound) {
                            try{ keyboard_authentication_service.send(message_queue.poll()); }
                            catch(Exception e){ e.printStackTrace(); }

                            Log.d(TAG, "queue send successful");
                        }
                    }

                    // if there are no messages in the queue, exit
                }
            });

            // start the message thread
            message_thread.start();
        }
    }

    /**
     * returns the current value of result
     */
    public double get_result(){
        Message message = new Message();

        message.what = KeyboardAuthenticationService.MSG_RECEIVE_RESULT;
        message.replyTo = messenger;

        // send the message
        try{ keyboard_authentication_service.send(message); }
        catch(Exception e){ e.printStackTrace(); }

        // wait for response
        wait_for_reply();

        return result;
    }

    /**
     * wait for the KeyboardAuthenticationService to reply
     *
     * TODO may want to modify this method
     * It will not wait forever
     * this means the functions:
     *  get_result()
     *  is_result_available()
     *
     * may return stale values.
     */
    private void wait_for_reply(){
        int wait_max = 100;
        int times_waited = 0;
        long wait_time = 20;

        has_replied = false;

        // loop while intent_collection_service_bound is false
        while(!has_replied && times_waited < wait_max){
            try{ Thread.sleep(wait_time); }catch(Exception e){ e.printStackTrace(); }

            times_waited++;
        }
    }

    /** MESSENGER IMPLEMENTATION */

    /**
     * commands given to messenger to interact with service
     */
    public static final int MSG_RESULT_RESPONSE = 1;
    public static final int MSG_AVAILABLE_RESPONSE = 2;

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
            //Log.d(TAG, msg.toString());
            switch (msg.what) {
                case MSG_RESULT_RESPONSE:
                    result = msg.getData().getDouble(KeyboardAuthenticationService.RESULT_KEY);
                    break;
                case MSG_AVAILABLE_RESPONSE:
                    is_result_available = msg.getData().getBoolean(KeyboardAuthenticationService.AVAILABLE_KEY);
                    break;
                default:
                    super.handleMessage(msg);
            }

            // indicate a reply has been received
            has_replied = true;
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger messenger = new Messenger(new KeyboardAuthentication.IncomingHandler());
}
