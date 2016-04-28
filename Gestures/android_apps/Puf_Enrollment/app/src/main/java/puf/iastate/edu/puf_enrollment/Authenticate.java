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
import java.text.DecimalFormat;
import java.text.NumberFormat;
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

    private double PRESSURE_THRESHOLD = .2;

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

        Double p_vector, d_vector, t_vector;    // Pressure, distance, and time temp vectors

        NumberFormat formatter = new DecimalFormat("#0.00");

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


        udPair.authenticate(mResponse.getNormalizedResponse(), mChallenges.get(0).getChallengeID());

        pressure_vector = udPair.getNew_response_point_vector(UserDevicePair.RatioType.PRESSURE);
        time_vector = udPair.getNew_response_point_vector(UserDevicePair.RatioType.TIME);
        distance_vector = udPair.getNew_response_point_vector(UserDevicePair.RatioType.DISTANCE);

        double total_pressure_vector = 0;
        double avg_pressure_vector = 0;
        int pressure_size = pressure_vector.size();

        double total_distance_vector = 0;
        double avg_distance_vector = 0;
        int distance_size = pressure_vector.size();

        double total_time_vector = 0;
        double avg_time_vector = 0;
        int time_size= pressure_vector.size();

        for(int i = 0; i < pressure_vector.size(); i++){
            p_vector = pressure_vector.get(i);
            d_vector = distance_vector.get(i);
            t_vector = time_vector.get(i);

            if(p_vector < 1.5) total_pressure_vector += p_vector;
            else pressure_size--;

            total_distance_vector += d_vector;
            total_time_vector += t_vector;
        }

        avg_pressure_vector = total_pressure_vector / pressure_size;
        avg_distance_vector = total_distance_vector / distance_size;
        avg_time_vector = total_time_vector / time_size;

        if(avg_pressure_vector < PRESSURE_THRESHOLD) mTV.setText("Valid!");
        else mTV.setText("Denied!");

        pressure_CI_tv.setText("Average Pressure Mu: " + formatter.format(avg_pressure_vector));
        distance_CI_tv.setText("Average Distance Mu: " +  formatter.format(avg_distance_vector));
        time_CI_tv.setText("Average Time Mu: " + formatter.format(avg_time_vector));
//        pressure_CI_tv.setText("Pressure CI: " + udPair.getNew_response_pressure_CI(mResponse.getNormalizedResponse(), mProfile));
//        distance_CI_tv.setText("Distance CI: " +  udPair.getNew_response_distance_CI(mResponse.getNormalizedResponse(), mProfile));
//        time_CI_tv.setText("Time CI: " + udPair.getNew_response_time_CI(mResponse.getNormalizedResponse(), mProfile));
//        CI_tv.setText("Authentication CI: " + udPair.getNew_response_confidence_interval());

        String default_value = getResources().getString(R.string.profile_default_string);
        StringBuilder sb = new StringBuilder();


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
