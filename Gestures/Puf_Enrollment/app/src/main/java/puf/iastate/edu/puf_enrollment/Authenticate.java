package puf.iastate.edu.puf_enrollment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.util.ArrayList;

import dataTypes.Challenge;
import dataTypes.Response;
import dataTypes.UserDevicePair;

public class Authenticate extends Activity {

    private static final String TAG = "AuthenticateActivity";
    private ArrayList<Challenge> mChallenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);

        mChallenges = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.profile", Context.MODE_PRIVATE);

        try {
            String default_value = getResources().getString(R.string.profile_default_string);
            String json = sharedPref.getString(getString(R.string.profile_string), default_value);
            mChallenges.add(gson.fromJson(json, Challenge.class));
            long pin = mChallenges.get(0).getChallengeID();

            //Pass pin to gesture training activity


            Intent authenticate = new Intent(this, RegisterGesturesActivity.class);
            authenticate.putExtra("pin", pin);
            authenticate.putExtra("mode", "authenticate");
            authenticate.putExtra("seed", mChallenges.get(0).getChallengeID());
            startActivity(authenticate);

        } catch (JsonParseException e) {
            Log.e(TAG, "Error in Parsing JSON: " + e.toString());
        }

    }

    public void check_authentication(View v) {
        TextView mTV = (TextView) findViewById(R.id.authenticating_status);
        Gson gson = new Gson();

        SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.response", Context.MODE_PRIVATE);
        String json = sharedPref.getString(getString(R.string.profile_string), "");
        Response mResponse = gson.fromJson(json, Response.class);

        UserDevicePair udPair = new UserDevicePair(0,mChallenges);

        boolean validUser = udPair.authenticate(mResponse.getResponse(), mChallenges.get(0).getChallengeID());
        if(validUser) mTV.setText("Valid Authentication!");
        else mTV.setText("Denied");

    }

}
