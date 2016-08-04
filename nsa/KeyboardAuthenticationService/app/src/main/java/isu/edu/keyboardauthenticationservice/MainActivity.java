package isu.edu.keyboardauthenticationservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import keyboardAuthenticationInterface.KeyboardAuthenticationService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start the keyboard authentication service
        start_keyboard_authentication_service();
    }

    /**
     * start the keyboard authentication service
     */
    private void start_keyboard_authentication_service() {
        // ask the service to do some work
        Intent start_intent = new Intent(this, KeyboardAuthenticationService.class);
        start_intent.setData(KeyboardAuthenticationService.get_start_uri());

        // start the service
        this.startService(start_intent);
    }
}

/**
 <intent-filter>
 <action android:name="android.intent.action.MAIN" />
 <category android:name="android.intent.category.LAUNCHER" />
 <!-- Service name -->
 <action android:name="isu.edu.keyboardauthenticationservice.keyboardAuthenticationService.KeyboardAuthenticationService" />
 </intent-filter>
 */
