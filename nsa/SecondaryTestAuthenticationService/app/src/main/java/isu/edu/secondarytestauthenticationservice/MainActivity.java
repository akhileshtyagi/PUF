package isu.edu.secondarytestauthenticationservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import wrapper.KeyboardAuthentication;

/**
 * the purpse of this class is to test the functionality of
 * KeyboardAuthenticationInterface
 */
public class MainActivity extends AppCompatActivity {
    final String TAG = "SecondaryTestApplicatio";

    private KeyboardAuthentication keyboard_authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the service communication
        keyboard_authentication = new KeyboardAuthentication(getApplicationContext());

        /* separate thread must be created to run the tests
           this is as a result of the fact that
           the service is not bound until after onCreate exits.
         */
        Thread test_thread = new Thread(new Runnable() {
            @Override
            public void run(){
//                try{ Thread.sleep(3000); }
//                catch(Exception e){ e.printStackTrace(); }

                // wait for the service to be bound
                boolean bound = keyboard_authentication.wait_for_bind(2000);
                Log.d(TAG,"wait_for_bind success: " + bound);

                // call functions to test each functionality
                test_is_result_available();

                test_submit_data();

                test_get_result();
            }
        });

        // start the test thread
        test_thread.start();
    }

    private void test_is_result_available(){
        Log.d(TAG, "test_is_result_available");
        //TODO
    }

    private void test_submit_data(){
        Log.d(TAG, "test_submit_data");
        //TODO
    }

    private void test_get_result(){
        Log.d(TAG, "test_get_result");
        //TODO
    }
}
