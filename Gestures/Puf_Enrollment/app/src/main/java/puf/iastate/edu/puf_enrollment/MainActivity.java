package puf.iastate.edu.puf_enrollment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.io.FilenameFilter;

import dataTypes.Challenge;

public class MainActivity extends AppCompatActivity  {

    private static final String TAG = "MainActivity";
    private File mPath = new File("/sdcard/PUFProfile/");
    private String mChosenFile;
    private static final String FTYPE = ".csv";
    private static final int DIALOG_LOAD_FILE = 1000;
    private String[] mFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Start enrollment process
     * @param v The enrollment start button
     */
    public void enroll_pressed(View v) {
        Intent intent = new Intent(this, PinPatternGen.class);
        startActivity(intent);
    }

    /**
     * Start example authentication process
     * @param v authentication start button
     */
    public void authenticate_pressed(View v) {
        Toast.makeText(this, "Launching Authentication", Toast.LENGTH_SHORT).show();
    }

    /**
     * Allow user to choose profile to use
     * @param v The enrollment start button
     */
    public void grab_profile(View v) {
        TextView mTV = (TextView) findViewById(R.id.profile_info);
        TextView mDataTV = (TextView) findViewById(R.id.profile_data);

        Gson gson = new Gson();

        SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.profile", Context.MODE_PRIVATE);

        try {
            String default_value = getResources().getString(R.string.profile_default_string);
            String json = sharedPref.getString(getString(R.string.profile_string), default_value);
            Challenge mChallenge = gson.fromJson(json, Challenge.class);
            mTV.setText("Using Profile: " + mChallenge.getChallengeID());

            StringBuilder sb = new StringBuilder();

            sb.append("Challange Poitns:\n");
            sb.append("X: " + mChallenge.getChallengePattern().get(0).getX() + ", Y: " + mChallenge.getChallengePattern().get(0).getY() + "\n");
            sb.append("X: " + mChallenge.getChallengePattern().get(1).getX() + ", Y: " + mChallenge.getChallengePattern().get(1).getY() + "\n");
            sb.append("X: " + mChallenge.getChallengePattern().get(2).getX() + ", Y: " + mChallenge.getChallengePattern().get(2).getY() + "\n");
            sb.append("X: " + mChallenge.getChallengePattern().get(3).getX() + ", Y: " + mChallenge.getChallengePattern().get(3).getY() + "\n");

            mDataTV.setText(sb.toString());
        } catch (JsonParseException e) {
            Log.e(TAG,"Error in Parsing JSON: " + e.toString());
        }



    }



}
