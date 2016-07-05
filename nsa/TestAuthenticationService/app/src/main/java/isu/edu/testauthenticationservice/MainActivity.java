package isu.edu.testauthenticationservice;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.inputmethodservice.Keyboard;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import KeyboardAuthenticationInterface.*;

/**
 * TODO list
 *
 * The goal of this App is to test
 *      [ ] sending data to keyboard authentication service
 *      [ ] receiving data from keyboard authentication service
 *
 * Intermediate tasks
 *      [ ] bind to the service started by the other application
 */
public class MainActivity extends AppCompatActivity {
    private KeyboardAuthenticationInterface keyboard_authentication_service;
    private boolean keyboard_authentication_service_bound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set instance variables
        keyboard_authentication_service = null;
        keyboard_authentication_service_bound = false;

        // bind to the service
        bind_service();

        // test sending information
        boolean send_pass = test_sending_information();
        Log.d("TAS", "receive pass: " + send_pass);

        // test acquiring information
        boolean receive_pass = test_receiving_information();
        Log.d("TAS", "receive pass: " + receive_pass);

        // unbind the service
        unbind_service();
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
            /**
            <service android:name=".ModemWatcherService"
            android:label="@string/app_name"
            android:exported="true">
            <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LAUNCHER" />
            <!-- Service name -->
            <action android:name="com.admetric.modemwatcher.Service" />
            </intent-filter>
            </service>

            new ComponentName("com.admetric.modemwatcher",
                    "com.admetric.modemwatcher.ModemWatcherService")
            */

            //Intent intent = new Intent("isu.edu.keyboardauthenticationservice.KeyboardAuthenticationService");
            //Intent intent = new Intent(this, KeyboardAuthenticationService.class);
            Intent intent = new Intent(this, KeyboardAuthenticationService.class);
            Log.d("TAS", "Before init intent.componentName");

            // set action is implicit?
            // set the action to be preformed (starting the service)
            //intent.setAction("isu.edu.keyboardauthenticationservice.KeyboardAuthenticationService");

            // explicity set the component to handle the intent
            //intent.setComponent(new ComponentName(
             //       "isu.edu.keyboardauthenticationservice",
              //      "isu.edu.keyboardauthenticationservice.KeyboardAuthenticationService"));

            Log.d("TAS", "Before bindService");
            if (bindService(intent, KeyboardServiceConnection, 0)){
                Log.d("TAS", "Binding to Modem Watcher returned true");
            } else {
                Log.d("TAS", "Binding to Modem Watcher returned false");
            }
        } catch (SecurityException e) {
            Log.e("TAS", "can't bind to ModemWatcherService, check permission in Manifest");
        }

        Log.d("TAS", "Binding.");
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
        Data data = new Data();
        data.compare_result = 0.0;
        data.confidence = 0.0;

        // send information
        keyboard_authentication_service.sendData(data);

        // return true so long as there are no errors
        return true;
    }

    /**
     * poll the service to see if ther eis information
     * pull the information
     */
    private boolean test_receiving_information() {
        Result result = null;

        // pull the service until there is information to be pulled
        if(keyboard_authentication_service.isNewResultAvailable()){
            // pull the information
            result = keyboard_authentication_service.receiveResult();
        }

        // return true if the result is not null
        return result != null;
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
            KeyboardAuthenticationBinder keyboard_authentication_binder = (KeyboardAuthenticationBinder)binder;
            keyboard_authentication_service = keyboard_authentication_binder.get_service();
            Log.d("TAS", "Attached.");

            keyboard_authentication_service_bound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            keyboard_authentication_service = null;
            keyboard_authentication_service_bound = false;
        }
    };
}
