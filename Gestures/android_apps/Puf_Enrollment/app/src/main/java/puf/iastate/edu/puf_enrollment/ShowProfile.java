package puf.iastate.edu.puf_enrollment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import dataTypes.Challenge;
import puf.iastate.edu.puf_enrollment.R;

public class ShowProfile extends Activity {

    private static final String TAG = "ShowProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);
        TextView mDataTV = (TextView) findViewById(R.id.profile_data);
        mDataTV.setMovementMethod(new ScrollingMovementMethod());
        StringBuilder sb = new StringBuilder();
        String json;
        Gson gson = new Gson();

        SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.profile", Context.MODE_PRIVATE);

        try {
            for(int i = 0; i < 2; i++) {
                String default_value = getResources().getString(R.string.profile_default_string);
                if(i == 0) {
                    sb.append("PROFILE A\n");
                    json = sharedPref.getString(getString(R.string.profile_string_a), default_value);
                } else {
                    sb.append("\n\nPROFILE B\n");
                    json = sharedPref.getString(getString(R.string.profile_string_b), default_value);
                }

                Challenge mChallenge = gson.fromJson(json, Challenge.class);

                Log.d("number of responses", new Integer(mChallenge.getProfile().getNormalizedResponses().size()).toString());

                sb.append("Number of Normalized Points: " + mChallenge.getNormalizedElementsCount() + "\n\n");
                sb.append(String.format("Profile Grade: %.4f\n", mChallenge.getProfile().getConfidence_interval()));
                sb.append(String.format("Number of Points Confidence Interval: %.4f\n", mChallenge.getProfile().get_num_motion_event_contribution()));
                sb.append(String.format("Standard Deviation of Points Confidence Interval: %.4f\n", mChallenge.getProfile().get_sd_motion_event_contribution()));
                sb.append(String.format("Standard Deviation of Pressure Confidence Interval: %.4f\n", mChallenge.getProfile().get_sd_pressure_contribution()));
                sb.append(String.format("Standard Deviation of Time Confidence Interval: %.4f\n", mChallenge.getProfile().get_sd_time_contribution()));
                sb.append(String.format("Standard Deviation of Distance Confidence Interval: %.4f\n\n", mChallenge.getProfile().get_sd_distance_contribution()));

                sb.append("Challenge Points:\n");

                sb.append("X: " + mChallenge.getChallengePattern().get(0).getX() + ", Y: " + mChallenge.getChallengePattern().get(0).getY() + "\n");
                sb.append("X: " + mChallenge.getChallengePattern().get(1).getX() + ", Y: " + mChallenge.getChallengePattern().get(1).getY() + "\n");
                sb.append("X: " + mChallenge.getChallengePattern().get(2).getX() + ", Y: " + mChallenge.getChallengePattern().get(2).getY() + "\n");
                sb.append("X: " + mChallenge.getChallengePattern().get(3).getX() + ", Y: " + mChallenge.getChallengePattern().get(3).getY() + "\n");

                sb.append("\nNormalized Pressure Mu and Sigma Values\n");

                for(int j = 0; j < mChallenge.getProfile().getPressureMuSigmaValues().getMuValues().size(); j++) {
                    sb.append("Mu: " + String.format("%.4f",mChallenge.getProfile().getPressureMuSigmaValues().getMuValues().get(j)) + ", Sigma: " + String.format("%.4f",mChallenge.getProfile().getPressureMuSigmaValues().getSigmaValues().get(j)) + "\n");
                }

                sb.append("\nNormalized Time Mu and Sigma Values\n");

                for(int j = 0; j < mChallenge.getProfile().getTimeDistanceMuSigmaValues().getMuValues().size(); j++) {
                    sb.append("Mu: " + String.format("%.4f",mChallenge.getProfile().getTimeDistanceMuSigmaValues().getMuValues().get(j)) + ", Sigma: " + String.format("%.4f", mChallenge.getProfile().getTimeDistanceMuSigmaValues().getSigmaValues().get(j)) + "\n");
                }

                sb.append("\nNormalized Point Distance Mu and Sigma Values\n");

                for(int j = 0; j <mChallenge.getProfile().getPointDistanceMuSigmaValues().getMuValues().size(); j++) {
                    sb.append("Mu: " + String.format("%.4f",mChallenge.getProfile().getPointDistanceMuSigmaValues().getMuValues().get(j)) + ", Sigma: " + String.format("%.4f",mChallenge.getProfile().getPointDistanceMuSigmaValues().getSigmaValues().get(j)) + "\n");
                }


            }

            mDataTV.setText(sb.toString());
        } catch (JsonParseException e) {
            Log.e(TAG, "Error in Parsing JSON: " + e.toString());
        }



    }

}
