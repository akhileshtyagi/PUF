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
import wrapper.KeyboardAuthentication;

/**
 * Created by tim on 7/1/16.
 *
 * This service acts as an intermediary
 * between the keyboard application and
 * other applications which use the authenticated value.
 */
public class KeyboardAuthenticationService extends Service {
    final String TAG = "KeyboardAuthenticationService";

    private Chain chain;

    private boolean new_result_available;
    private double result;

    private boolean result_dirty;

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

        this.new_result_available = false;
        result = 0.0;

        //TODO set result_dirty = false when result is computed
        //TODO create a thread to evaluate result occasionally

        Log.d(TAG, "service created");
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

        Log.d("KAS", "service startup finished");

        return START_STICKY;
    }

    /**
     * commands given to messenger to interact with service
     */
    static final int MSG_IS_RESULT_AVAILABLE = 1;
    static final int MSG_RECEIVE_RESULT = 2;
    static final int MSG_SUBMIT_DATA = 3;

    /**
     * constants defined for contents of the bundle
     */
    static final String RESULT_KEY = "result";
    static final String AVAILABLE_KEY = "available";

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
                case MSG_IS_RESULT_AVAILABLE:
                    // reply with the intent list to the caller
                    Message message = new Message();

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
                    Message message = new Message();

                    // package the data ( result ) we want to send back
                    Bundle result_bundle = new Bundle();
                    result_bundle.putDouble(RESULT_KEY, receive_result());

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
                case MSG_SUBMIT_DATA:
                    //TODO
                    MotionEvent motion_event = message.obj;

                    // insert the data into the service
                    send_data();

                    // decode message and add to list
                    intent_list.add(IntentRecord.decode_message(msg));

                    Log.d(TAG, "intents received number: " + intent_list.size());
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
        chain.add_touch(motion_event_to_touch(motion_event));

        this.result_dirty = true;
    }

    /**
     * converts a MotionEvent into a Touch
     * This Touch object can then be provided to the Chain
     */
    private Touch motion_event_to_touch(MotionEvent motion_event){
        //TODO
        return null;
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
