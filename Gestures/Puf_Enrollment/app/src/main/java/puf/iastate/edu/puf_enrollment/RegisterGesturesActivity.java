package puf.iastate.edu.puf_enrollment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import data.DataReader;
import dataTypes.Challenge;
import dataTypes.Response;

public class RegisterGesturesActivity extends AppCompatActivity implements PufDrawView.ResponseListener{

    private long mSeed; //Seed for generating challenges
    private ArrayList<Point> mCurChallenge; //Current challenge
    private int mRemainingSwipes; //Remaining swipes until enrolled
    private String mode; //Either "enroll" or "authenticate"
    private ChallengeGenerator mCg;

    private TextView mUpdateView;
    private TextView mSeedView;
    private TextView mRemainingView;
    private PufDrawView mPdv;

    private Challenge mChallenge;
    private ArrayList<Response> mResponses;
    private ArrayList<dataTypes.Point> mChallengePoints;
    private boolean mChallengePointsAssigned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gestures);

        //Setup views
        mPdv = (PufDrawView) findViewById(R.id.pufDrawView);
        mUpdateView = (TextView) findViewById(R.id.updateView);
        mSeedView = (TextView) findViewById(R.id.seedView);
        mRemainingView = (TextView) findViewById(R.id.entriesRemainingView);

        mPdv.setUpdateView(mUpdateView);

        //Grab pin from PinPatternGen Activity
        Intent i = getIntent();



        //Get mode of operation (enroll or authenticate)
        mode = i.getStringExtra("mode");
        if(mode.equals("authenticate")) {
            mRemainingView.setText("Authenticating");
            mSeed = seed.curseed;
            System.out.println("CurrentSeed = "+ mSeed);
        }
        //Set the seed for referential purposes
        else {
            seed.curseed = i.getIntExtra("pin", 0); //Default value is nada
            mSeed = seed.curseed;
            seed.curseed= mSeed;
            System.out.println("CurrentSeed = "+ mSeed);
        }

        mCg = new ChallengeGenerator(mSeed);
        mSeedView.setText("Seed: " + mSeed);

        //Setup an initial challenge and give the challenge
        mCurChallenge = mCg.generateChallenge();
        mPdv.giveChallenge(mCurChallenge.toArray(new Point[mCurChallenge.size()]));
        mResponses = new ArrayList<>();
        mChallengePoints = new ArrayList<>();

        //Initialize remaining swipes
        mRemainingSwipes = 20;
        mChallengePointsAssigned = false;
    }

    /**
     * When an attempt is received, we save the response, and issue a new challenge.
     * TODO: Currently does this indefinitely
     * @param response
     */
    @Override
    public void onResponseAttempt(ArrayList<Point> response) {
        if (mode.equals("enroll")) {
            if (mRemainingSwipes-- == 0) {
                DataReader reader = new DataReader(new File(Environment.getExternalStorageDirectory() + "/PUFProfile"));
                Gson gson = new GsonBuilder()
                        .serializeNulls().serializeSpecialFloatingPointValues().create();
                String json = gson.toJson(mChallenge, mChallenge.getClass());

                Log.d("distance mu", mChallenge.getProfile().getPointDistanceMuSigmaValues().getMuValues().toString());
                Log.d("distance sigma", mChallenge.getProfile().getPointDistanceMuSigmaValues().getSigmaValues().toString());

                SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.profile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.profile_string), json);
                editor.commit();
                finish();
            }
            writeResponseCsv(response, "DeviceName", "UserName");
            mCurChallenge = mCg.generateChallenge();
            //mPdv.giveChallenge(mCurChallenge.toArray(new Point[mCurChallenge.size()]));
            mRemainingView.setText(mRemainingSwipes + " Left");
        }
        else if (mode.equals("authenticate")) {
            writeResponseCsv(response, "DeviceName", "UserName");
            Gson gson = new Gson();
            String json = gson.toJson(mResponses.get(0), mResponses.get(0).getClass());
            SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.response", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.profile_string), json);
            editor.commit();
            finish();
        }
    }

    /**
     * Writes the response to a given challenge to a CSV file
     * @param response
     */
    public void writeResponseCsv(ArrayList<Point> response, String deviceName, String testerName)
    {
        ArrayList<dataTypes.Point> points = new ArrayList<>();
        File baseDir;

        //File baseDir = new File(getFilesDir(), "PUFProfile");
        if(mode.equals("enroll")) baseDir = new File(Environment.getExternalStorageDirectory(), "PUFProfile");
        else baseDir = new File(Environment.getExternalStorageDirectory(), "PUFAuthenticate");

        if (!baseDir.exists())
        {
            baseDir.mkdirs();
        }

        String fileName = mSeed + ": " + getCurrentLocalTime() + ".csv";

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

                if(!mChallengePointsAssigned) mChallengePoints.add(new dataTypes.Point(point.x,point.y,0));
            }

            if(!mChallengePointsAssigned) {
                mChallenge = new Challenge(mChallengePoints, (int)mSeed);
                mChallengePointsAssigned = true;
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
                points.add(new dataTypes.Point(point.x, point.y, point.pressure));
            }
            Response tempResponse = new Response(points);
            mResponses.add(new Response(tempResponse.getNormalizedResponse()));
            mChallenge.addResponse(tempResponse);
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
