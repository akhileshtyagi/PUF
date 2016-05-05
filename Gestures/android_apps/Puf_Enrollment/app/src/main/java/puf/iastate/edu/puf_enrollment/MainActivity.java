package puf.iastate.edu.puf_enrollment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import dataTypes.Challenge;


public class MainActivity extends AppCompatActivity  {
    public static final String nameKeyA = "nameKeyA";
    public static final String nameKeyB = "nameKeyB";
    public static final String pufPrefs = "pufPrefs";
    private static final String EMAIL = "isunexus7@gmail.com";

    public LinearLayout boxA;
    public LinearLayout boxB;

    public char loadedProfile;
    private String nameA;
    private String nameB;

    private static final String TAG = "AuthenticateActivity";
    private ArrayList<Challenge> mChallenges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get profile names
        SharedPreferences prefs = getSharedPreferences(pufPrefs, MODE_PRIVATE);
        nameA = prefs.getString(nameKeyA, null);
        if (nameA != null) {
            TextView nameView = (TextView) findViewById(R.id.currentProfileA);
            nameView.setText(nameA);
        }

        nameB = prefs.getString(nameKeyB, null);
        if (nameB != null) {
            TextView nameView = (TextView) findViewById(R.id.currentProfileB);
            nameView.setText(nameB);
        }

        boxA = (LinearLayout) findViewById(R.id.profileBoxA);
        boxB = (LinearLayout) findViewById(R.id.profileBoxB);
        loadedProfile = '0';
    }

    /**
     * Start enrollment process
     * @param v The enrollment start button
     */
    public void enroll_pressed(View v) {

        if(loadedProfile == '0') {
            Toast toast = Toast.makeText(getApplicationContext(), "please select profile", Toast.LENGTH_LONG);
            toast.show();
        } else {
            Intent enroll = new Intent(this, PinPatternGen.class);
            enroll.putExtra("profile", loadedProfile);
            startActivity(enroll);
        }
    }

    /**
     * Start example authentication process
     * @param v authentication start button
     */
    public void authenticate_pressed(View v) {
        if(loadedProfile == '0') {
            Toast toast = Toast.makeText(getApplicationContext(), "please select profile", Toast.LENGTH_LONG);
            toast.show();
        } else {
            mChallenges = new ArrayList<>();
            Gson gson = new Gson();
            SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.profile", Context.MODE_PRIVATE);
            Intent authenticate = new Intent(this, RegisterGesturesActivity.class);

            try {
                String default_value = getResources().getString(R.string.profile_default_string);
                String json;
                if (loadedProfile == 'A') {
                    json = sharedPref.getString(getString(R.string.profile_string_a), default_value);
                    authenticate.putExtra("name", nameA);
                } else {
                    json = sharedPref.getString(getString(R.string.profile_string_b), default_value);
                    authenticate.putExtra("name", nameB);
                }
                mChallenges.add(gson.fromJson(json, Challenge.class));
                long pin = mChallenges.get(0).getChallengeID();

                //Pass pin to gesture training activity

                authenticate.putExtra("pin", pin);
                authenticate.putExtra("mode", "authenticate");
                authenticate.putExtra("seed", mChallenges.get(0).getChallengeID());
                authenticate.putExtra("loadedProfile", loadedProfile);
                startActivity(authenticate);

            } catch (JsonParseException e) {
                Log.e(TAG, "Error in Parsing JSON: " + e.toString());
            }
        }
    }

    /**
     * Allow user to choose profile to use
     * @param v The enrollment start button
     */
    public void grab_profile(View v) {
        Intent intent = new Intent(this, ShowProfile.class);
        startActivity(intent);
    }

    public void change_backgroundA(View v) {
        boxA.setBackgroundColor(Color.parseColor("#ADACAE"));
        boxB.setBackgroundColor(Color.parseColor("#FFFFFF"));
        loadedProfile = 'A';
    }

    public void change_backgroundB(View v) {
        boxB.setBackgroundColor(Color.parseColor("#ADACAE"));
        boxA.setBackgroundColor(Color.parseColor("#FFFFFF"));
        loadedProfile = 'B';
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int itemId = item.getItemId();
        if (itemId == R.id.action_settings)
        {
            // Display Settings page
            Intent preferenceIntent = new Intent(this, SettingsActivity.class);
            startActivity(preferenceIntent);
            return true;
        }
        else if (itemId == R.id.action_emailcsvs)
        {
            // Send emails
            sendCSVEmail();
            return true;
        }
        else if (itemId == R.id.clean_csvs) {
            deleteCSVs();
            return true;
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
    }

    private void sendCSVEmail()
    {
        String to = EMAIL;
        String subject = "Gesture PUF Profiles";
        String message = "";

        Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
        i.setType("plain/text");
        i.putExtra(Intent.EXTRA_EMAIL, new String[] { to });
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, message);

        /*
         * Date dateVal = new Date(); String filename = dateVal.toString(); data
         * = File.createTempFile("Report", ".csv"); FileWriter out =
         * (FileWriter) GenerateCsv.generateCsvFile( data, "Name,Data1");
         */

        File baseDir = new File(Environment.getExternalStorageDirectory(),
                "UD_PUF");
        if (!baseDir.exists())
        {
            baseDir.mkdirs();
        }
        List<File> files = getListFiles(baseDir);

        startActivity(Intent.createChooser(i, "E-mail"));

        ArrayList<Uri> uris = new ArrayList<Uri>();
        // convert from paths to Android friendly Parcelable Uri's
        for (File fileIn : files)
        {
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }
        i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(Intent.createChooser(i, "Send mail..."));
    }

    private List<File> getListFiles(File parentDir)
    {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        for (File file : files)
        {
            if (file.isDirectory())
            {
                inFiles.addAll(getListFiles(file));
            }
            else
            {
                if (file.getName().endsWith(".csv"))
                {
                    inFiles.add(file);
                }
            }
        }
        return inFiles;
    }

    // Deletes all files in the UD_PUF directory
    private void deleteCSVs () {
        File dir = new File(Environment.getExternalStorageDirectory(), "UD_PUF");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        Toast toast = Toast.makeText(getApplicationContext(), "Saved Profiles Deleted", Toast.LENGTH_LONG);
        toast.show();
    }


    /**
     * start the super secret activity
     */
    public void secret_pressed(View v){
        Intent intent = new Intent(this, SecretAcrivity.class);
        startActivity(intent);
    }

    public void normalize_test_pressed(View v){
        Intent intent = new Intent(this, NormalizeTestActivity.class);
        startActivity(intent);
    }
}
