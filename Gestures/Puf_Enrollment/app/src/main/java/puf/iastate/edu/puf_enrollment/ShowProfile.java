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

        Gson gson = new Gson();

        SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.profile", Context.MODE_PRIVATE);

        try {
            String default_value = getResources().getString(R.string.profile_default_string);
            String json = sharedPref.getString(getString(R.string.profile_string), default_value);
            Challenge mChallenge = gson.fromJson(json, Challenge.class);

            StringBuilder sb = new StringBuilder();

            Log.d("number of responses", new Integer(mChallenge.getProfile().getNormalizedResponses().size()).toString());

            sb.append("Number of Normalized Points: " + mChallenge.getNormalizedElementsCount() + "\n\n");
            sb.append("Challenge Points:\n");
            sb.append("X: " + mChallenge.getChallengePattern().get(0).getX() + ", Y: " + mChallenge.getChallengePattern().get(0).getY() + "\n");
            sb.append("X: " + mChallenge.getChallengePattern().get(1).getX() + ", Y: " + mChallenge.getChallengePattern().get(1).getY() + "\n");
            sb.append("X: " + mChallenge.getChallengePattern().get(2).getX() + ", Y: " + mChallenge.getChallengePattern().get(2).getY() + "\n");
            sb.append("X: " + mChallenge.getChallengePattern().get(3).getX() + ", Y: " + mChallenge.getChallengePattern().get(3).getY() + "\n");

            sb.append("\nNormalized Pressure Mu and Sigma values\n");

            for(int i = 0; i < mChallenge.getNormalizedElementsCount(); i++) {
                sb.append("Mu: " + String.format("%.4f",mChallenge.getProfile().getPressureMuSigmaValues().getMuValues().get(i)) + ", Sigma: " + String.format("%.4f",mChallenge.getProfile().getPressureMuSigmaValues().getSigmaValues().get(i)) + "\n");
            }

            sb.append("\nNormalized Time Mu and Sigma values\n");

            for(int i = 0; i < mChallenge.getNormalizedElementsCount(); i++) {
                sb.append("Mu: " + String.format("%.4f",mChallenge.getProfile().getTimeDistanceMuSigmaValues().getMuValues().get(i)) + ", Sigma: " + String.format("%.4f", mChallenge.getProfile().getTimeDistanceMuSigmaValues().getSigmaValues().get(i)) + "\n");
            }

            sb.append("\nNormalized Point Distance Mu and Sigma values\n");

            for(int i = 0; i < mChallenge.getNormalizedElementsCount(); i++) {
                sb.append("Mu: " + String.format("%.4f",mChallenge.getProfile().getPointDistanceMuSigmaValues().getMuValues().get(i)) + ", Sigma: " + String.format("%.4f",mChallenge.getProfile().getPointDistanceMuSigmaValues().getSigmaValues().get(i)) + "\n");
            }

            mDataTV.setText(sb.toString());
        } catch (JsonParseException e) {
            Log.e(TAG, "Error in Parsing JSON: " + e.toString());
        }


    }

}
