package puf.iastate.edu.puf_enrollment;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import dataTypes.Response;

public class SecretAcrivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_acrivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    /**
     * cause the list of responses to be written as Response[] in json format
     * @param v
     */
    public void write_profile_as_json(View v){
        // read in the profile as a list of responses
        ArrayList<Response> profile_responses = get_profile_responses();

        // write the list of responses in json format


        //Log.d("stuff", "stuff");
    }

    /**
     * present one challenge 100 times to the user
     */
    public void present_challenge(View v){

    }

    /**
     * contains the process for grabbing responses
     */
    private ArrayList<Response> get_profile_responses(){
        return null;
    }
}
