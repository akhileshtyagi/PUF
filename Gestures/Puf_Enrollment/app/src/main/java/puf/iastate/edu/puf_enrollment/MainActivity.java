package puf.iastate.edu.puf_enrollment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity  {

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
        Intent intent = new Intent(this, Authenticate.class);
        startActivity(intent);
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
}
