package keyboardAuthenticationInterface;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.util.Log;
import android.net.Uri;
import android.view.MotionEvent;
import components.Chain;
import components.Touch;
import runtime.ChainBuilder;
import wrapper.KeyboardAuthentication;

/**
 * Created by tim on 7/1/16.
 *
 * This service acts as an intermediary
 * between the keyboard application and
 * other applications which use the authenticated value.
 */
public class KeyboardAuthenticationService extends Service {
    private final String TAG = "KeyboardAuthenticationService";

    /* chain variables */
    final int window = 3;
    final int token = 5;
    final int threshold = 2000;
    public static final int model_size = 500;

    /* authentication thread variables */
    public static final long frequency = 10000;
    public static final int event_count = 250;

    private volatile ChainBuilder chain;
    private volatile int motion_event_count;

    private volatile boolean new_result_available;
    private volatile double result;

    private volatile boolean result_dirty;

    /**
     * returns the uri for starting the service
     */
    public static Uri get_start_uri(){
        return Uri.parse("START");
    }

    /**
     * create a constructor
     */
    public KeyboardAuthenticationService(){
        super();

        motion_event_count = 0;

        this.new_result_available = false;
        result = 0.0;

        //TODO Chain parameters can be modified
        //TODO increasing model_size up will result in increased accuracies
        //TODO window, token, threshold are set to values where best results have been seen
        chain = new ChainBuilder(window, token, threshold, model_size, model_size);

        //Log.d(TAG, "service created");
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

        // start a thread which will authenticate periodically on a separate thread
        //TODO these can be modified
        start_authentication_thread(frequency, event_count);

//        Log.d(TAG, "service startup finished");

        return START_STICKY;
    }

    /**
     * start a separate thread which will preform the authentications periodically
     *
     * things which can be modified about this:
     *  frequency - frequency of authentications in milliseconds
     *  event_count - number of Motionevents to take inbetween authentications
     *
     * The thread will do whichever condition occurs first
     *
     * special cases:
     *  frequency == 0
     *      This will cause event_count to be the only condition considered
     *  event_count == 0
     *      This will cause frequency to be the only condition considered
     *  both == 0
     *      The authentication will never be preformed, stop it!
     */
    private void start_authentication_thread(final long frequency, final int event_count){
        new Thread(new Runnable(){
            private volatile boolean stop;

            @Override
            public void run(){
                stop = false;

                long wait_interval = 1000;
                long wait_after_previous_authentication = 0;
                int previous_authentication_size = 0;

                // while the thread is not stopped
                while(!stop){
                    // test condition to determine if authentication is necessary
                    if(result_dirty &&
                            ((wait_after_previous_authentication > frequency && frequency > 0) ||
                                    ((motion_event_count - previous_authentication_size) > event_count && event_count > 0))){
                        // preform the authentication
                        chain.authenticate();

                        // wait for the authentication to finish
                        while(chain.get_authenticate_state() == ChainBuilder.State.IN_PROGRESS){
                            try{
                                Thread.sleep(wait_interval / 10);
                            } catch(Exception e){ e.printStackTrace(); }
                        }

                        // set result based on the probability stored in the authentication thread
                        result = chain.get_authenticate_thread().get_auth_probability();

//                        Log.d(TAG, "preforming authentication, result: " + result);

                        // set variables which will be used to determine when the next authentication should happen
                        wait_after_previous_authentication = 0;
                        previous_authentication_size = motion_event_count;

                        // the result is not longer dirty
                        result_dirty = false;

                        // indicate there is a new result, which has not yet been read
                        new_result_available = true;
                    }

                    // wait for awhile
                    try{
                        Thread.sleep(wait_interval);
                        wait_after_previous_authentication += wait_interval;
                    } catch(Exception e){ e.printStackTrace(); }
                }
            }

            public void stop(){
                stop = true;
            }
        }).start();
    }

    /**
     * commands given to messenger to interact with service
     */
    public static final int MSG_IS_RESULT_AVAILABLE = 1;
    public static final int MSG_RECEIVE_RESULT = 2;
    public static final int MSG_SUBMIT_MOTIONEVENT_DATA = 3;
    public static final int MSG_SUBMIT_TOUCH_DATA = 4;

    /**
     * constants defined for contents of the bundle
     */
    public static final String RESULT_KEY = "result";
    public static final String AVAILABLE_KEY = "available";

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

            Message message;

            switch (msg.what) {
                case MSG_IS_RESULT_AVAILABLE:
                    // reply with the intent list to the caller
                    message = new Message();

                    // package the data ( result ) we want to send back
                    Bundle boolean_bundle = new Bundle();
                    boolean_bundle.putBoolean(AVAILABLE_KEY, is_new_result_available());

                    // return the intent list to the requesting context
                    message.setData(boolean_bundle);
                    message.what = KeyboardAuthentication.MSG_AVAILABLE_RESPONSE;

                    // send the message to the sender
                    try{
                        msg.replyTo.send(message);
                    }catch(Exception e){ e.printStackTrace(); }

                    break;
                case MSG_RECEIVE_RESULT:
                    // reply with the intent list to the caller
                    message = new Message();

                    // package the data ( result ) we want to send back
                    Bundle result_bundle = new Bundle();
                    result_bundle.putDouble(RESULT_KEY, receive_result());

                    //Log.d(TAG, "SENDING RESult: " + result);

                    // return the intent list to the requesting context
                    message.setData(result_bundle);
                    message.what = KeyboardAuthentication.MSG_RESULT_RESPONSE;

                    // send the message to the sender
                    try{
                        // 1 is it being set?
                        // 2 is it null even when set?, why wouldl this be?
                        msg.replyTo.send(message);

                        //Messenger reply_messenger = new Messenger(message.replyTo.getBinder());
                        //reply_messenger.send(message);
                    }catch(Exception e){ e.printStackTrace(); }

                    break;
                case MSG_SUBMIT_MOTIONEVENT_DATA:
                    MotionEvent motion_event = (MotionEvent)msg.obj;

                    // insert the data into the service
                    send_data(motion_event);
                    break;
                case MSG_SUBMIT_TOUCH_DATA:
                    Touch touch = (Touch)msg.obj;

                    // insert the data into the service
                    send_data(touch);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger messenger = new Messenger(new IncomingHandler());

    @Override
    public IBinder onBind(Intent intent) {
        // return the binder interface we created
        return messenger.getBinder();
    }

    private boolean is_new_result_available() {
        return this.new_result_available;
    }

    /**
     * This is the result of the keyboard authentication
     *
     * This method should be called if
     * the desire is to acquire information from the service
     */
    private double receive_result() {
        this.new_result_available = false;
        return this.result;
    }

    /**
     * This method is used to send data to the service
     */
    private void send_data(MotionEvent motion_event) {
        chain.handle_touch(motion_event_to_touch(motion_event));

        motion_event_count++;
        this.result_dirty = true;
    }

    private void send_data(Touch touch){
        chain.handle_touch(touch);

        motion_event_count++;
        this.result_dirty = true;
    }

    /**
     * converts a MotionEvent into a Touch
     * This Touch object can then be provided to the Chain
     */
    private Touch motion_event_to_touch(MotionEvent motion_event){
        //TODO get key value
        // translate the (x,y) coordinate of the montion event into a keycode
        // NOTE: this may not be an accurate translation, but
        // what matters is that it is consistent
        //TODO
        //int keycode = motion_event;
        int keycode = 0;
        double pressure = motion_event.getPressure();
        long timestamp = motion_event.getEventTime();

        Touch touch = new Touch(keycode, pressure, timestamp);

        return touch;
    }

    /**
     * create a notification to be shown while this service is active
     */
    private Notification create_notification(){
        Notification notification = new Notification.Builder(this)
                .setContentTitle("KAS")
                .setContentText("KeyboardAuthenticationService is running.")
                .build();

        return notification;
    }
}
