package puf.iastate.edu.puf_enrollment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity  {
    public static final String nameKeyA = "nameKeyA";
    public static final String nameKeyB = "nameKeyB";
    public static final String pufPrefs = "pufPrefs";

    public LinearLayout boxA;
    public LinearLayout boxB;

    public char loadedProfile;
    private String nameA;
    private String nameB;

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
            Intent authenticate = new Intent(this, Authenticate.class);
            authenticate.putExtra("profile", loadedProfile);
            if(loadedProfile == 'A') {
                authenticate.putExtra("name", nameA);
            } else {
                authenticate.putExtra("name", nameB);
            }
            startActivity(authenticate);
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

    public void new_normalize_test_pressed(View v){
        Intent intent = new Intent(this, NewNormalizeTestActivity.class);
        startActivity(intent);
    }
}
