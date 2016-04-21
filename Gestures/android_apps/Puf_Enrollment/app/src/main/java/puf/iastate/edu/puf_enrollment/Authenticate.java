package puf.iastate.edu.puf_enrollment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import dataTypes.Challenge;
import dataTypes.Profile;
import dataTypes.Response;
import dataTypes.UserDevicePair;

public class Authenticate extends Activity {

    private List<Double> pressure_vector, distance_vector, time_vector;
    private ArrayList<Challenge> mChallenges;
    private static final String TAG = "AuthenticateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
        TextView mTV = (TextView) findViewById(R.id.authenticating_status);

        TextView CI_tv = (TextView) findViewById(R.id.auth_confidence_interval);
        TextView pressure_CI_tv = (TextView) findViewById(R.id.auth_pressure_ci);
        TextView distance_CI_tv = (TextView) findViewById(R.id.auth_distance_ci);
        TextView time_CI_tv = (TextView) findViewById(R.id.auth_time_ci);
        TextView vector_info_tv = (TextView) findViewById(R.id.vector_info_ci);
        vector_info_tv.setMovementMethod(new ScrollingMovementMethod());
        Gson gson = new Gson();
        String json;

        mChallenges = new ArrayList<>();
        SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.profile", Context.MODE_PRIVATE);
        Intent intent = getIntent();
        char loadedProfile = intent.getCharExtra("profile", 'A');

        try {
            String default_value = getResources().getString(R.string.profile_default_string);
            if (loadedProfile == 'A') {
                json = sharedPref.getString(getString(R.string.profile_string_a), default_value);
            } else {
                json = sharedPref.getString(getString(R.string.profile_string_b), default_value);
            }
            mChallenges.add(gson.fromJson(json, Challenge.class));

        } catch (JsonParseException e) {
            Log.e(TAG, "Error in Parsing JSON: " + e.toString());
        }

        sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.response", Context.MODE_PRIVATE);
        json = sharedPref.getString(getString(R.string.authenticate_response), "");

        Response mResponse = gson.fromJson(json, Response.class);

        UserDevicePair udPair = new UserDevicePair(0,mChallenges);
        Profile mProfile = udPair.getChallenges().get(0).getProfile();

        boolean validUser = udPair.authenticate(mResponse.getNormalizedResponse(), mChallenges.get(0).getChallengeID());
        if(validUser) mTV.setText("Valid Authentication! number of points = " + mResponse.getNormalizedResponse().size());
        else mTV.setText("Denied, number of points = " + mResponse.getNormalizedResponse().size());

        pressure_CI_tv.setText("Pressure CI: " + udPair.getNew_response_pressure_CI(mResponse.getNormalizedResponse(), mProfile));
        distance_CI_tv.setText("Distance CI: " +  udPair.getNew_response_distance_CI(mResponse.getNormalizedResponse(), mProfile));
        time_CI_tv.setText("Time CI: " + udPair.getNew_response_time_CI(mResponse.getNormalizedResponse(), mProfile));
        CI_tv.setText("Authentication CI: " + udPair.getNew_response_confidence_interval());

        String default_value = getResources().getString(R.string.profile_default_string);
        StringBuilder sb = new StringBuilder();

        pressure_vector = udPair.getNew_response_point_vector(UserDevicePair.RatioType.PRESSURE);
        time_vector = udPair.getNew_response_point_vector(UserDevicePair.RatioType.TIME);
        distance_vector = udPair.getNew_response_point_vector(UserDevicePair.RatioType.DISTANCE);

        sb.append("Pressure Vectors\n");
        for(int i = 0; i < pressure_vector.size(); i++) {
            sb.append( "Pressure[" + i + "]: " + pressure_vector.get(i) + "\n");
        }

        sb.append("\nTime Vectors\n");
        for(int i = 0; i < time_vector.size(); i++) {
            sb.append( "Time[" + i + "]: " + time_vector.get(i) + "\n");
        }

        sb.append("\nDistance Vectors\n");
        for(int i = 0; i < distance_vector.size(); i++) {
            sb.append( "Distance[" + i + "]: " + distance_vector.get(i) + "\n");
        }

        vector_info_tv.setText(sb.toString());

        Challenge mChallenge = mChallenges.get(0);
        udPair.dumpUserDevicePairData(mChallenge);
    }

}
