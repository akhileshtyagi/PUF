package puf.iastate.edu.puf_enrollment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * Created by nmont on 10/8/15.
 */
public class PinPatternGen  extends AppCompatActivity {
    public static final String nameKey = "nameKey";
    public static final String pufPrefs = "pufPrefs" ;
    private TextView mPinView, mNameView, mProfileLabel;
    private SeekBar mSeekBar;
    private int pin;
    private int seek;
    private String name;
    SharedPreferences sharedpreferences;
    private char loadedProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_enter);

        mPinView = (TextView) findViewById(R.id.pin_edit_text);
        mNameView = (TextView) findViewById(R.id.name_edit_text);
        mProfileLabel = (TextView) findViewById(R.id.profile_label);
        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        sharedpreferences = getSharedPreferences(pufPrefs, Context.MODE_PRIVATE);

        Intent i = getIntent();
        loadedProfile = i.getCharExtra("profile", 'A');
        mProfileLabel.setText("Profile " + loadedProfile);

    }

    /**
     * Button press to begin gesture training
     * @param v
     */
    public void begin_pressed(View v) {

        try {
            //Grab pin and name
            pin = Integer.valueOf(mPinView.getText().toString());
            name = mNameView.getText().toString();
            seek = mSeekBar.getProgress();
            if(seek < 3) seek = 3;

            //Save name
            SharedPreferences.Editor editor = sharedpreferences.edit();

            // Shared Preference is "nameKeyA" or "nameKeyB"
            editor.putString(nameKey + loadedProfile, name);
            editor.apply();

            //Pass pin to gesture training activity
            Intent authenticate = new Intent(this, RegisterGesturesActivity.class);
            authenticate.putExtra("pin", pin);
            authenticate.putExtra("mode", "enroll");
            authenticate.putExtra("seek", seek);
            authenticate.putExtra("profile", loadedProfile);
            authenticate.putExtra("name", name);
            startActivity(authenticate);
            finish();

            Toast.makeText(this, "Beginning Enrollment", Toast.LENGTH_SHORT).show();
        } catch ( NumberFormatException nfe ) {
            Toast.makeText(this, "Pin is not a number", Toast.LENGTH_SHORT).show();
        }
    }

}
