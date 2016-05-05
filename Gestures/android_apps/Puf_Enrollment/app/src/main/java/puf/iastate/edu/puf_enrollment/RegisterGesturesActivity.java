package puf.iastate.edu.puf_enrollment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVWriter;

import org.apache.http.auth.AUTH;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import data.DataReader;
import dataTypes.Challenge;
import dataTypes.Response;

public class RegisterGesturesActivity extends AppCompatActivity implements PufDrawView.ResponseListener{

    private long mSeed; // Seed for generating challenges
    private ArrayList<Point> mCurChallenge; // Current challenge
    private int mRemainingSwipes; // Remaining swipes until enrolled
    private String mode; // Either "enroll" or "authenticate"
    private String name; // Name of profile being generated/Authenticated against
    private char loadedProfile; //Either A or B
    private int strength; // How strong to make the profile
    private ChallengeGenerator mCg;
    private int response_counter; // Keep track of number of responses generated

    private TextView mUpdateView;
    private TextView mRemainingView;
    private TextView mPromptView;
    private PufDrawView mPdv;
    private ProgressBar mProgressBar;

    private Challenge mChallenge;
    private ArrayList<Response> mResponses;
    private ArrayList<dataTypes.Point> mChallengePoints;
    private boolean mChallengePointsAssigned;
    private CSVWriter csvWrite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_gestures);

        //Setup views
        mPdv = (PufDrawView) findViewById(R.id.pufDrawView);
        mUpdateView = (TextView) findViewById(R.id.updateView);
        mRemainingView = (TextView) findViewById(R.id.entriesRemainingView);
        mPromptView = (TextView) findViewById(R.id.prompt);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mPdv.setUpdateView(mUpdateView);


        //Grab pin from PinPatternGen Activity
        Intent i = getIntent();

        //Get mode of operation (enroll or authenticate) and profile working with
        mode = i.getStringExtra("mode");
        name = i.getStringExtra("name");
        loadedProfile = i.getCharExtra("profile", 'A');

        if(mode.equals("authenticate")) {
            seed.curseed = i.getLongExtra("pin", 11); //Default value is nada
            mSeed = seed.curseed;
            System.out.println("CurrentSeed = "+ mSeed);
            mRemainingView.setVisibility(View.INVISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
            mPromptView.setText("Authenticating " + name);
            setTitle("Authenticate");
        }
        //Set the seed for referential purposes
        else {
            mPromptView.setText(name + mPromptView.getText());
            seed.curseed = i.getIntExtra("pin", 0); //Default value is nada
            mSeed = seed.curseed;
            seed.curseed= mSeed;
            System.out.println("CurrentSeed = "+ mSeed);
            strength = i.getIntExtra("seek", 20);
            mRemainingSwipes = strength;
            mRemainingView.setText(mRemainingSwipes + " Left");
            setTitle("Enroll");
        }

        response_counter = 0;
        mCg = new ChallengeGenerator(mSeed);

        //Setup an initial challenge and give the challenge
        mCurChallenge = mCg.generateChallenge();
        mPdv.giveChallenge(mCurChallenge.toArray(new Point[mCurChallenge.size()]));
        mResponses = new ArrayList<>();
        mChallengePoints = new ArrayList<>();

        mChallengePointsAssigned = false;

        CreateChallengePoints();
    }

    /**
     * When an attempt is received, we save the response, and issue a new challenge.
     * TODO: Currently does this indefinitely
     * @param response
     */
    @Override
    public void onResponseAttempt(ArrayList<dataTypes.Point> response) {
        if (mode.equals("enroll")) {
            response_counter++;
            AddResponseToChallenge(response);
            if (--mRemainingSwipes == 0) {
                try {
                    csvWrite.close();
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
                Gson gson = new GsonBuilder()
                        .serializeNulls().serializeSpecialFloatingPointValues().create();
                String json = gson.toJson(mChallenge, mChallenge.getClass());

                Log.d("distance mu", mChallenge.getProfile().getPointDistanceMuSigmaValues().getMuValues().toString());
                Log.d("distance sigma", mChallenge.getProfile().getPointDistanceMuSigmaValues().getSigmaValues().toString());

                SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.profile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                if(loadedProfile == 'A') {
                    editor.putString(getString(R.string.profile_string_a), json);
                } else {
                    editor.putString(getString(R.string.profile_string_b), json);
                }
                editor.commit();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            mCurChallenge = mCg.generateChallenge();
            mRemainingView.setText(mRemainingSwipes + " Left");
            mProgressBar.setProgress(mProgressBar.getProgress() + (mProgressBar.getMax() / strength));
        }
        else if (mode.equals("authenticate")) {
            AddResponseToChallenge(response);
            Gson gson = new Gson();
            String json = gson.toJson(mResponses.get(0), mResponses.get(0).getClass());
            SharedPreferences sharedPref = this.getSharedPreferences("puf.iastate.edu.puf_enrollment.response", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.authenticate_response), json);
            editor.commit();

            Intent authenticate = new Intent(this, Authenticate.class);
            authenticate.putExtra("profile", loadedProfile);
            startActivity(authenticate);
            finish();
        }
    }

    public void CreateChallengePoints() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        String deviceName = prefs.getString("DeviceName", "");

        File baseDir = new File(Environment.getExternalStorageDirectory(), "UD_PUF");
        if (!baseDir.exists())
        {
            baseDir.mkdirs();
        }
        String fileName = name + "_" + mode + "_" + getCurrentLocalTime() + " " + seed.curseed + ".csv";
        File f = new File(baseDir, fileName);

        try {
            f.createNewFile();
            csvWrite = new CSVWriter(new FileWriter(f));

            String title[] = {mode + " profile for user " + name + " on device " + deviceName};
            csvWrite.writeNext(title);

            String[] points_header = {"Challenge Points"};
            csvWrite.writeNext(points_header);

            String[] challengeHeaders = {"X", "Y"};
            csvWrite.writeNext(challengeHeaders);
            for (int i = 0; i < mCurChallenge.size(); i++) {
                Point point = mCurChallenge.get(i);
                if (!mChallengePointsAssigned)
                    mChallengePoints.add(new dataTypes.Point(point.x, point.y, 0));
                String[] row = {Float.toString(point.x), Float.toString(point.y)};
                csvWrite.writeNext(row);
            }
            if (!mChallengePointsAssigned) {
                mChallenge = new Challenge(mChallengePoints, (int) mSeed);
                mChallengePointsAssigned = true;
            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

    }
    /**
     * Writes the response to a given challenge to a CSV file
     * @param response
     */
    public void AddResponseToChallenge(ArrayList<dataTypes.Point> response)
    {

        ArrayList<dataTypes.Point> points = new ArrayList<>();

        try {
            if(mode.equals("enroll")) {
                String[] response_header = {"Response " + response_counter};
                csvWrite.writeNext(response_header);
            }
            String[] headers = { "X", "Y", "PRESSURE", "DISTANCE", "TIME" };
            csvWrite.writeNext(headers);

            for( int i = 0; i < response.size(); i++)
            {
                dataTypes.Point point = response.get(i);
                points.add(new dataTypes.Point(point.getX(), point.getY(), point.getPressure(), point.getTime()));
                String[] row = { Double.toString(point.getX()), Double.toString(point.getY()),
                        Double.toString(point.getPressure()), Double.toString(point.getDistance()),
                        Double.toString(point.getTime()) };
                csvWrite.writeNext(row);
            }
            Response tempResponse = new Response(points);
            mResponses.add(new Response(tempResponse.getNormalizedResponse()));
            mChallenge.addResponse(new Response(response));

            if(mode.equals("authenticate")) {
                try {
                    csvWrite.close();
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public String getCurrentLocalTime() {
        Calendar c = Calendar.getInstance();
        String format = "yyyy-MM-dd hh:mm:ss aa";
        SimpleDateFormat localSdf = new SimpleDateFormat(format);
        return localSdf.format(c.getTime());
    }
}
