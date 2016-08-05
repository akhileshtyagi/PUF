package isu.edu.secondarytestauthenticationservice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Random;

import keyboardAuthenticationInterface.KeyboardAuthenticationService;
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

                Log.d(TAG, "*** TESTS BEGIN ***");

                // call functions to test each functionality
                test_is_result_available();

                test_submit_data();

                test_get_result();

                Log.d(TAG, "*** TESTS END ***");
            }
        });

        // start the test thread
        test_thread.start();
    }

    private void test_is_result_available(){
        // wait for new result to be set
        //long sleep_amount = KeyboardAuthenticationService.frequency * 3 / 2;

        // sleep 1.5 times the frequency of new result generation by the service
        //try{ Thread.sleep(sleep_amount); } catch(Exception e){ e.printStackTrace(); }

        // trigger the authentication  by submitting a number of events
        int number = KeyboardAuthenticationService.event_count;
        long seed = 100l;

        submit_random_events(number, seed);

        // check to see if new result available returns true
        boolean is_available = keyboard_authentication.is_result_available();

        // is this correct?
        if(is_available != true){
            Log.d(TAG, "is_result_available() should have returned true, but was false");
        }

        // receive the result
        keyboard_authentication.get_result();

        // check to see if new result available returns false
        is_available = keyboard_authentication.is_result_available();

        // is this correct?
        if(is_available != false){
            Log.d(TAG, "is_result_available() should have returned false, but was true");
        }
    }

    private void test_submit_data(){
        double intended_result = 1.0;

        // submit some data
        int number = KeyboardAuthenticationService.model_size;
        long seed = 100l;

        // do it twice to fill both models with the same information
        submit_random_events(number, seed);
        submit_random_events(number, seed);

        // test that the result reflects the data which was submitted
        double actual_result = keyboard_authentication.get_result();

        if(actual_result != intended_result){
            Log.d(TAG, "intended result was: " + intended_result + ", but actual result was: " + actual_result);
        }
    }

    /** effectively test submit data */
    private void test_get_result(){
        //Log.d(TAG, "test_get_result");
    }

    /**
     * submits random events to the service
     *
     * parameters
     *  number - the number of events to be submitted
     *  seed - seed for the random number generation which
     *      determines the contents of the events submitted
     *
     * having the same seed and number will result in idential generations
     */
    private void submit_random_events(int number, long seed){
        Random random = new Random(seed);
        MotionEvent motion_event;

        for(int i=0; i<number; i++){
            // create the MotionEvent
            long downTime = i;
            long eventTime = i;
            int action = MotionEvent.ACTION_DOWN;
            float x = random.nextFloat();
            float y = random.nextFloat();
            float pressure = random.nextFloat();
            float size = random.nextFloat();
            int metaState = 0;
            float xPrecision = 3;
            float yPrecision = 3;
            int deviceId = 0;
            int edgeFlags = 0;
            motion_event = MotionEvent.obtain(
                    downTime, eventTime, action, x, y, pressure, size,
                    metaState, xPrecision, yPrecision, deviceId, edgeFlags
            );

            // submit the MotionEvent
            keyboard_authentication.submit_data(motion_event);
        }
    }
}
