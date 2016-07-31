package wrapper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.MotionEvent;
import keyboardAuthenticationInterface.KeyboardAuthenticationService;

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

    public KeyboardAuthentication(){

    }

    /**
     * returns true if there is a new result to be retrieved
     *
     * new results are defined to be those result values
     * which have been set, but
     * not retrieved
     */
    public boolean is_result_available(){
        //TODO
    }

    /**
     * provides touch screen data to the service
     */
    public void submit_data(MotionEvent motion_event){
        //TODO
    }

    /**
     * returns the current value of result
     */
    public double get_result(){
        //TODO
    }

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
            Log.d(TAG, msg.toString());
            switch (msg.what) {
                case MSG_RESULT_RESPONSE:
                    //TODO remember to set replyTo
                    break;
                case MSG_AVAILABLE_RESPONSE:
                    //TODO remember to set replyTo
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    final Messenger messenger = new Messenger(new KeyboardAuthenticationService.IncomingHandler());
}
