package isu.edu.testauthenticationservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import keyboardAuthenticationInterface.IKeyboardAuthentication;

/**
 * Useful stack over flow,
 * describes why Android is returning a BinderProxy instead of a Binder
 * <p>
 * http://stackoverflow.com/questions/28364724/getting-java-lang-classcastexception-android-os-binderproxy-every-time-i-declar
 */

/**
 * TODO list
 *
 * The goal of this App is to test
 *      [ ] sending data to keyboard authentication service
 *      [ ] receiving data from keyboard authentication service
 *
 * Intermediate tasks
 *      [x] bind to the service started by the other application
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Thread to preform the tests and log the results
     *
     * This is necessary because the service is not bound
     * until after onCreate() has been exited.
     *
     * This is necessarily true because the
     * ServiceConnection runs in the same thread as onCreate
     */
    private class TestRunnable implements Runnable {
        final String TAG = "TEST_THREAD";

        @Override
        public void run(){
            // wait for the service to be bound
            // a max of 10 seconds
            long total_time = 0;
            long wait_increment = 250;
            long max_time = 10000;
            while(total_time < max_time && keyboard_authentication_service_bound == false) {
                try {
                    Thread.sleep(wait_increment);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                total_time += wait_increment;
            }

            // if not bound in 10 seconds, exit
            if(total_time >= max_time){
                Log.d(TAG, "Service was not bound");
                return;
            }

            // otherwise, run all the tests and report results
            // test that service has connected
            Log.d(TAG, "object: " + keyboard_authentication_service);
            Log.d(TAG, "is_bound: " + keyboard_authentication_service_bound);

            // test sending information
            boolean send_pass = test_sending_information();
            Log.d(TAG, "receive pass: " + send_pass);

            // test acquiring information
            boolean receive_pass = test_receiving_information();
            Log.d(TAG, "receive pass: " + receive_pass);

            // unbind the service
            unbind_service();
        }
    }

    /**
     * volatile might be necessary because a separate thread is
     * being used to preform the tests
     */
    private volatile IKeyboardAuthentication keyboard_authentication_service;
    private volatile boolean keyboard_authentication_service_bound;

    /**
     * bind request does not START
     * until after leaving onCreate().
     *
     * This is why the tests are not working ( i believe )
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set instance variables
        keyboard_authentication_service = null;
        keyboard_authentication_service_bound = false;

        // bind to the service
        bind_service();

        // create a separate thread to preform the tests
        Runnable test_runnable = new TestRunnable();

        // start the thread
        new Thread(test_runnable).start();
    }

    /**
     * this method binds KeyboardAuthenticationService which
     * has been created elsewhere
     */
    void bind_service() {
        // Establish a connection with the service.  We use an explicit
        // class name because there is no reason to be able to let other
        // applications replace our component.
        //bindService(new Intent(this, MessengerService.class), mConnection, Context.BIND_AUTO_CREATE);
        try {
            //Intent intent = new Intent("isu.edu.keyboardauthenticationservice.KeyboardAuthenticationService");
            //Intent intent = new Intent(this, KeyboardAuthenticationService.class);
            Intent intent = new Intent();
            Log.d("TAS", "Before init intent.componentName");

            // set action is implicit?
            // set the action to be preformed (starting the service)
            //intent.setAction("isu.edu.keyboardauthenticationservice.KeyboardAuthenticationService");

            // explicity set the component to handle the intent
            intent.setComponent(new ComponentName(
                    "isu.edu.keyboardauthenticationservice",
                    "keyboardAuthenticationInterface.KeyboardAuthenticationService"));

            Log.d("TAS", "Before bindService");
            if (bindService(intent, KeyboardServiceConnection, 0)) {
                Log.d("TAS", "Binding returned true");
            } else {
                Log.d("TAS", "Binding returned false");
            }
        } catch (SecurityException e) {
            Log.e("TAS", "can't bind to ModemWatcherService, check permission in Manifest");
        }

        Log.d("TAS", "Binding finished.");
    }

    /**
     * unbind the service
     */
    void unbind_service() {
        if (this.keyboard_authentication_service_bound) {
            // Detach our existing connection.
            unbindService(KeyboardServiceConnection);
            Log.d("TAS", "Unbinding.");
        }
    }

    /**
     * send some information to the service
     */
    private boolean test_sending_information() {
        // create data to be sent
        //Data data = new Data();
        //data.compare_result = 0.0;
        //data.confidence = 0.0;
        double data = 1.0;

        // send information
        try {
            keyboard_authentication_service.sendData(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return true so long as there are no errors
        return true;
    }

    /**
     * poll the service to see if ther eis information
     * pull the information
     */
    private boolean test_receiving_information() {
        double result = -1.0;

        // pull the service until there is information to be pulled
        try {
            if (keyboard_authentication_service.isNewResultAvailable()) {
                // pull the information
                result = keyboard_authentication_service.receiveResult();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // return true if the result is valid
        return result >= 0.0;
    }

    /**
     * Class (variable) for interacting with the main interface of the service.
     */
    private ServiceConnection KeyboardServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder binder) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  We are communicating with our
            // service through an IDL interface, so get a client-side
            // representation of that from the raw service object.

            //KeyboardAuthenticationBinder keyboard_authentication_binder = (KeyboardAuthenticationBinder)binder;
            //keyboard_authentication_service = (KeyboardAuthenticationInterface)(keyboard_authentication_binder.get_service());

            keyboard_authentication_service = IKeyboardAuthentication.Stub.asInterface(binder);
            keyboard_authentication_service_bound = true;

            Log.d("TAS", "Attached.");
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            keyboard_authentication_service = null;
            keyboard_authentication_service_bound = false;

            Log.d("TAS", "Detatched.");
        }
    };
}
