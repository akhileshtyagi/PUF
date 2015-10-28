package puf.iastate.edu.puf_enrollment;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class RegisterGesturesActivity extends AppCompatActivity implements PufDrawView.ResponseListener{

    private long mSeed; //Seed for generating challenges
    private ArrayList<Point> mCurChallenge; //Current challenge

    private ChallengeGenerator mCg;

    private TextView mUpdateView;
    private TextView mSeedView;
    private PufDrawView mPdv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gestures);

        //Setup views
        mPdv = (PufDrawView) findViewById(R.id.pufDrawView);
        mUpdateView = (TextView) findViewById(R.id.updateView);
        mSeedView = (TextView) findViewById(R.id.seedView);

        mPdv.setUpdateView(mUpdateView);

        //Grab pin from PinPatternGen Activity
        Intent i = getIntent();
        mCg = new ChallengeGenerator(i.getIntExtra("pin", 0)); //Default value is nada

        //Set the seed for referential purposes
        mSeed = mCg.getSeed();
        mSeedView.setText("Seed: " + mSeed);

        //Setup an initial challenge and give the challenge
        mCurChallenge = mCg.generateChallenge();
        mPdv.giveChallenge(mCurChallenge.toArray(new Point[mCurChallenge.size()]));
    }

    /**
     * When an attempt is received, we save the response, and issue a new challenge.
     * TODO: Currently does this indefinitely
     * @param response
     */
    @Override
    public void onResponseAttempt(ArrayList<Point> response) {
        writeResponseCsv(response, "DeviceName", "UserName");
        mCurChallenge = mCg.generateChallenge();
        mPdv.giveChallenge(mCurChallenge.toArray(new Point[mCurChallenge.size()]));
    }

    /**
     * Writes the response to a given challenge to a CSV file
     * @param response
     */
    public void writeResponseCsv(ArrayList<Point> response, String deviceName, String testerName)
    {
        File baseDir = new File(getFilesDir(), "581Proj");

        if (!baseDir.exists())
        {
            baseDir.mkdirs();
        }

        String fileName = mSeed + ": " + deviceName + " " + testerName + " " + getCurrentLocalTime() + ".csv";

        File f = new File(baseDir, fileName);

        try
        {
            f.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(f));
            String[] challengeHeaders = {"ChallengeX", "ChallengeY", "Tester Name", "Device Name"};
            csvWrite.writeNext(challengeHeaders);
            for( int i = 0; i < mCurChallenge.size(); i++)
            {
                Point point = mCurChallenge.get(i);
                String[] row = { Float.toString(point.x),
                        Float.toString(point.y),
                        testerName,
                        deviceName };
                csvWrite.writeNext(row);
            }

            String[] headers = { "X", "Y", "PRESSURE" };

            csvWrite.writeNext(headers);

            for( int i = 0; i < response.size(); i++)
            {
                Point point = response.get(i);
                String[] row = { Float.toString(point.x),
                        Float.toString(point.y),
                        Float.toString(point.pressure) };
                csvWrite.writeNext(row);
            }

            csvWrite.close();

            Toast.makeText(this, "Challenge response written to CSV.", Toast.LENGTH_SHORT).show();
        }
        catch( Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getCurrentLocalTime()
    {
        Calendar c = Calendar.getInstance();
        String format = "yyyy-MM-dd hh:mm:ss aa";
        SimpleDateFormat localSdf = new SimpleDateFormat(format);
        return localSdf.format(c.getTime());
    }
}
