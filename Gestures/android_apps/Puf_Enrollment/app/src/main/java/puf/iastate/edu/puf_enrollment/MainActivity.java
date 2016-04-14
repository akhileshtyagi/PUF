package puf.iastate.edu.puf_enrollment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
    public static final String nameKey = "nameKey";
    public static final String pufPrefs = "pufPrefs" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get name
        SharedPreferences prefs = getSharedPreferences(pufPrefs, MODE_PRIVATE);
        String name = prefs.getString(nameKey, null);
        if (name != null) {
            TextView nameView = (TextView) findViewById(R.id.currentProfile);
            nameView.setText(name);
        }
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
        Intent intent = new Intent(this, Authenticate.class);
        startActivity(intent);
        finish();
    }

    /**
     * Allow user to choose profile to use
     * @param v The enrollment start button
     */
    public void grab_profile(View v) {
        Intent intent = new Intent(this, ShowProfile.class);
        startActivity(intent);
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
