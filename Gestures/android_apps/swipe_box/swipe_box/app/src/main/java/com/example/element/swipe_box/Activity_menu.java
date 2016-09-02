package com.example.element.swipe_box;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import dataTypes.Challenge;
import dataTypes.Response;
import dataTypes.UserDevicePair;

public class Activity_menu extends AppCompatActivity {
    public static final String TAG = "SWIPE_BOX";

    /** constants for generating a data set */
    public static final int CHALLENGE_PER_SHAPE = 1;
    public static final String DATA_SET_FOLDER = "gestures";
    public static final String DATA_SET_FILE_NAME = "shape_response";

    /** constants for output files */
    public static final String SHARED_PREFERENCES_FILE = "swipe_box_preferences";
    public static final String ANALYSIS_FILENAME = "analysis_interpoint_time";
    public static final String RESPONSES_FILENAME = "response_profile";

    private Challenge challenge;
    private ArrayList<Response> responses;

    private int box_width;
    private int box_height;
    private Point box_upper_left_corner;

    public enum Result{
        RESPONSE(0),
        RESPONSE_TEST(1),
        AUTH_RESPONSE(2),
        DATA_SET_RESPONSE(3);

        private int int_value;
        public int get_int_value(){
            return int_value;
        }

        Result(int int_value){
            this.int_value = int_value;
        }
    }

    EditText output_console_edit_text;

    Map<Activity_swipe_box.ChallengeType, List<Response>> response_list_map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /**
         * initialize challenge variable with challenge pattern
         */
        // initialize the box variables which will be passed to the activity which draws the box.
        // set up the box as a percentage of the screen size
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        this.box_width = (int)(metrics.widthPixels * .7);
        this.box_height = metrics.heightPixels/8;

        int upper_left_x = metrics.widthPixels / 9;
        int upper_left_y = metrics.heightPixels / 16;
        this.box_upper_left_corner = new Point(upper_left_x, upper_left_y, 0, 0);

        // set up the challenge pattern
        ArrayList<dataTypes.Point> challenge_pattern_list = new ArrayList<dataTypes.Point>();
        challenge_pattern_list.add(new dataTypes.Point(box_upper_left_corner.x, box_upper_left_corner.y+box_height/2, 0));
        challenge_pattern_list.add(new dataTypes.Point(box_upper_left_corner.x+box_width, box_upper_left_corner.y+box_height/2, 0));

        this.challenge = new Challenge(challenge_pattern_list,0);

        this.responses = new ArrayList<Response>();

        /**
         * find the edit text for output
         */
        output_console_edit_text = (EditText)findViewById(R.id.output_console_edit_text);

        String instruction_text = "";
        instruction_text += "Test Swipe Box - test that the swipe-box activity is working\n";
        instruction_text += "Analyze Responses - write an analysis of responses collected with 'Collect Swipe Responses' button to this edit text\n";
        instruction_text += "Output Responses to Json - output responses collected with 'Collect Swipe Responses' button in Json format to the filesystem\n";
        instruction_text += "Save Responses - save responses collected with the 'Collect Swipe Responses' button to shared preferences\n";
        instruction_text += "Press Collect Swipe Responses - present several challenges which can be authenticated against(this does not generate a data set)\n";
        instruction_text += "Authenticate Against Responses - authenticate against the responses gathered using 'collect swipe responses' button\n";
        instruction_text += "Output analysis to CSV - after analysis has been run, output to csv on file system\n";
        instruction_text += "Load Responses - load previous responses from shared preferences\n";
        instruction_text += "Generate Data Set - press to generate a data set\n";
        output_console_edit_text.setText(instruction_text);

        /**
         * find all of the buttons and assign them to variables
         *
         *      collect_swipe_responses_button - This button is used to collect a set of responses to the swipe
         */
        Button collect_swipe_responses_button = (Button)findViewById(R.id.collect_swipe_responses_button);
        Button test_swipe_box_button = (Button)findViewById(R.id.test_swipe_box_button);
        Button analyze_responses_button = (Button)findViewById(R.id.analyze_responses_button);
        Button output_responses_to_csv_button = (Button)findViewById(R.id.output_responses_to_csv_button);
        Button output_analysis_to_csv_button = (Button)findViewById(R.id.output_analysis_to_csv_button);
        Button save_responses_button = (Button)findViewById(R.id.save_responses_button);
        Button load_responses_button = (Button)findViewById(R.id.load_responses_button);
        Button authenticate_against_responses_button = (Button)findViewById(R.id.authenticate_against_responses);
        Button generate_data_set_button = (Button)findViewById(R.id.generate_data_set);

        /**
         * define the click listeners for each button
         */
        /**
         * active the collect swipe responses activity. Record somewhere the responses.
         */
        collect_swipe_responses_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //List<Response> responses = new ArrayList<Response>();

                // start the swipe box activity. Responses will be loged in onActivityResult()
                for(int i=0;i<10; i++){
                    start_activity_swipe_box(Activity_swipe_box.ChallengeType.CHECK, Result.RESPONSE);
                }

                // set the challenge equal to the most resent set of responses
                //for(Response r: responses) {
                //    challenge.addResponse(r);
                //}
            }//End onClick
        });

        /**
         * activate the collect swipe responses activity. Log something for testing? Do not record the responses.
         */
        test_swipe_box_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_activity_swipe_box_test();
            }//End onClick
        });

        /**
         * output information about the current response set.
         */
        analyze_responses_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // a neater way of displaying the information
                String console_output_1 = new String();

                // get the analysis text
                console_output_1 = create_analysis_string();

                // update the edit text console with the results
                output_console_edit_text.setText(console_output_1);
            }//End onClick
        });

        /**
         * output the current responses list to file
         */
        output_responses_to_csv_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // write a file in json format which contains the responses
                Gson gson = new Gson();
                String json = gson.toJson(responses.toArray(), responses.toArray().getClass());

                Log.d("json", json);

                write_responses_to_file(RESPONSES_FILENAME, json);
            }//End onClick
        });

        /**
         * output the analysis of current response set to .csv files
         */
        output_analysis_to_csv_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                analysis_to_csv(ANALYSIS_FILENAME);
            }//End onClick
        });

        /**
         * save the current responses to shared preferences
         */
        save_responses_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // get editor
                SharedPreferences.Editor editor = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit();

                Gson gson = new Gson();
                String json = gson.toJson(challenge, challenge.getClass());

                editor.putString("NONE", json);
                editor.commit();
            }//End onClick
        });

        /**
         * load into current responses the response set from shared preferences
         */
        load_responses_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences(SHARED_PREFERENCES_FILE, MODE_PRIVATE);
                Gson gson = new Gson();

                String default_value = "NONE";
                String json = preferences.getString("NONE", default_value);
                challenge = gson.fromJson(json, Challenge.class);
            }//End onClick
        });

        /**
         * authenticate against previously gathered responses
         */
        authenticate_against_responses_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_activity_swipe_box_authenticate();
            }//End onClick
        });

        /**
         * generate a data set and write it out to disk
         */
        generate_data_set_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                start_activity_generate_data_set();
            }//End onClick
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            // reset objects
            // set up the challenge pattern
            ArrayList<dataTypes.Point> challenge_pattern_list = new ArrayList<dataTypes.Point>();
            challenge_pattern_list.add(new dataTypes.Point(box_upper_left_corner.x, box_upper_left_corner.y+box_height/2, 0));
            challenge_pattern_list.add(new dataTypes.Point(box_upper_left_corner.x+box_width, box_upper_left_corner.y+box_height/2, 0));

            this.challenge = new Challenge(challenge_pattern_list,0);

            this.responses = new ArrayList<Response>();

            // reset the edit text
            output_console_edit_text = (EditText)findViewById(R.id.output_console_edit_text);
            output_console_edit_text.setText("Reset Successfull");

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == Result.RESPONSE_TEST.get_int_value()) && (resultCode == RESULT_OK)){
            //String response = data.getStringExtra("response");
            ArrayList<Point> response = (ArrayList<Point>)data.getExtras().getSerializable("response");

            // Have retrieved the response from
            Log.d("return", response.toString());
        }else if ((requestCode == Result.RESPONSE.get_int_value()) && (resultCode == RESULT_OK)){
            //String response = data.getStringExtra("response");
            ArrayList<Point> array_response = (ArrayList<Point>)data.getExtras().getSerializable("response");

            //convert the arraylist of points into a ud_puf response object
            Response response = array_to_response(array_response);

            // add the response to the list of responses (must be done in a way that will cause it not to be changed)
            responses.add(new Response(response.getOrigionalResponse()));

            // put the responses into the challenge object
            challenge.addResponse(response);
        }else if ((requestCode == Result.AUTH_RESPONSE.get_int_value()) && (resultCode == RESULT_OK)){
            // here the response is ment to be authenticated against the current responses
            ArrayList<Point> array_response = (ArrayList<Point>)data.getExtras().getSerializable("response");

            //convert the arraylist of points into a ud_puf response object
            Response response = array_to_response(array_response);

            // call a method to compare this response against the responses gathered previously.
            authenticate_against_responses(response);
        }else if((requestCode == Result.DATA_SET_RESPONSE.get_int_value()) && (resultCode == RESULT_OK)){
            ArrayList<Point> array_response = (ArrayList<Point>)data.getExtras().getSerializable("response");

            //convert the arraylist of points into a ud_puf response object
            Response response = array_to_response(array_response);

            // get the challenge_type from the result
            Activity_swipe_box.ChallengeType challenge_type = (Activity_swipe_box.ChallengeType)data.getExtras().getSerializable("challenge_type");

            // add the response to the list of responses (must be done in a way that will cause it not to be changed)
            // retrieve the response (it will be the most resently added one to the list)
            this.response_list_map.get(challenge_type).add(new Response(response.getOrigionalResponse()));

            // if this is the last response, output all responses to file system
            int total_responses = 0;
            for(Collection response_list : this.response_list_map.values()){
                total_responses += response_list.size();
            }

            // check if it is last response
            int number_of_shapes = this.response_list_map.size();

            if(number_of_shapes * CHALLENGE_PER_SHAPE == total_responses) {
                write_responses_generate_data_set();
            }
        }
    }

    /**
     * takes in a response and authenticates it against the responses stored in challenge.
     * Also prints out data associated with the authentication to the console.
     * @param response
     */
    private void authenticate_against_responses(Response response){
        // construct a new user device pair object to analyze the data
        UserDevicePair ud_pair = new UserDevicePair(0);

        ud_pair.addChallenge(challenge);

        // use ud_pair to authenticate
        boolean authenticated = ud_pair.authenticate(response.getOrigionalResponse(), challenge);

        // print out information about the authentication to the console
        String console_output = "";
        console_output += "authenticated: " + authenticated + "\n";
        console_output += "failed_pressure_points_ratio: " + String.format("%.5f", (ud_pair.failedPointRatio(UserDevicePair.RatioType.PRESSURE))) + "\n";
        console_output += "failed_distance_points_ratio: " + String.format("%.5f", (ud_pair.failedPointRatio(UserDevicePair.RatioType.DISTANCE))) + "\n";
        console_output += "failed_time_points_ratio: " + String.format("%.5f", (ud_pair.failedPointRatio(UserDevicePair.RatioType.TIME)));

        // output to the console
        output_console_edit_text = (EditText)findViewById(R.id.output_console_edit_text);
        output_console_edit_text.setText(console_output);
    }

    /**
     * takes an ArrayList<Point> and turns it into a UD_PUF response. Somewhat confusing, there are two kinds of points.
     * The first kind of point is Points generated by swipe_box.
     * The second kind of point is Points used by UD_PUF.
     * This method servers as a conversion between the two.
     */
    private Response array_to_response(ArrayList<Point> array_response){
        ArrayList<dataTypes.Point> points = new ArrayList<dataTypes.Point>();

        //for each point in the array, add it to the response
        for(Point array_point : array_response){
            points.add(new dataTypes.Point(array_point.x, array_point.y, array_point.pressure, 0, array_point.time));
        }

        return new Response(points);
    }

    /**
     * starts the swipe box activity to test its functionality
     */
    private void start_activity_swipe_box_test(){
        Intent intent = new Intent(this, Activity_swipe_box.class);

        // add the box challenge parameters to the intent
        intent.putExtra("box_width", this.box_width);
        intent.putExtra("box_height", this.box_height);
        intent.putExtra("box_upper_left_corner", this.box_upper_left_corner);

        startActivityForResult(intent, Result.RESPONSE_TEST.get_int_value());
    }

    /**
     * starts the swipe box activity to gather a response to be added to challenge
     */
    private void start_activity_swipe_box(Activity_swipe_box.ChallengeType challenge_type, Result result){
        Intent intent = new Intent(this, Activity_swipe_box.class);

        // add the box challenge parameters to the intent
        intent.putExtra("box_width", this.box_width);
        intent.putExtra("box_height", this.box_height);
        intent.putExtra("box_upper_left_corner", this.box_upper_left_corner);

        // add the challenge as an extra to the intent
        intent.putExtra("challenge_type", challenge_type);

        startActivityForResult(intent, result.get_int_value());
    }

    /**
     * starts the swipe box activity to gather an authentication response
     */
    private void start_activity_swipe_box_authenticate(){
        Intent intent = new Intent(this, Activity_swipe_box.class);

        // add the box challenge parameters to the intent
        intent.putExtra("box_width", this.box_width);
        intent.putExtra("box_height", this.box_height);
        intent.putExtra("box_upper_left_corner", this.box_upper_left_corner);

        startActivityForResult(intent, Result.AUTH_RESPONSE.get_int_value());
    }

    /**
     * starts the swipe box activity to gather a data set
     */
    private void start_activity_generate_data_set(){
        // define a set of shapes for which responses should be created for
        Set<Activity_swipe_box.ChallengeType> challenge_shape_set = new HashSet<>();

        // for now take all shapes defined in swipe box.
        // this construct is here so that we can be more selective later
        for(Activity_swipe_box.ChallengeType c_t : Activity_swipe_box.ChallengeType.values()){
            challenge_shape_set.add(c_t);
        }

        // create a response list for each of the challenge types
        this.response_list_map = new HashMap<>();

        for(Activity_swipe_box.ChallengeType challenge_type : challenge_shape_set){
            this.response_list_map.put(challenge_type, new ArrayList<Response>());
        }

        // call start_activity_swipe_box many times with different arguments
        //
        // the response can be retrieved by getting the last element of this.response
        // each time a call returns,
        // sort the response into the appropriate list based on the argument
        for(int i=0; i<CHALLENGE_PER_SHAPE; i++) {
            for (Activity_swipe_box.ChallengeType challenge_type : challenge_shape_set) {
                // provide the challenge
                start_activity_swipe_box(challenge_type, Result.DATA_SET_RESPONSE);
            }
        }
    }

    /**
     * writes responses collected for data set generation out to the file system
     */
    private void write_responses_generate_data_set(){
        // write response_list_map out to a file
        // write each shape individually
        //
        // csv format
        //TODO

        // json format
        //TODO

        Log.d(TAG, "writing data set to file system");
    }

    /**
     * computes variance by mean for the responses
     * @param mu_list
     * @param sigma_list
     * @return
     */
    private List<Double> compute_variance_by_mean_list(List<Double> mu_list, List<Double> sigma_list){
        List<Double> variance_by_mean_list = new ArrayList<Double>();

        // compute variance by mean
        // variance = sigma^2 / mu
        for(int i = 0; i<mu_list.size();i++){
            variance_by_mean_list.add(sigma_list.get(i)*sigma_list.get(i) / mu_list.get(i));
        }

        return variance_by_mean_list;
    }

    /**
     * computes the average for each point in the list
     * returns the average of each point in a list
     * @param response_list
     * @return
     */
    private List<Double> compute_mu_list(List<Response> response_list){
        List<Double> sigma_list = new ArrayList<Double>();
        List<Double> pressure_list;

        // compute mu for each point in the list
        for(int i=0;i<response_list.get(0).getNormalizedResponse().size();i++){
            //for each response create a list of pressure values
            pressure_list = new ArrayList<Double>();

            for(dataTypes.Point point : response_list.get(i).getNormalizedResponse()){
                pressure_list.add(point.getPressure());
            }

            // compute mu for the point
            sigma_list.add(compute_mu(pressure_list));
        }

        return sigma_list;
    }

    /**
     * compute the std deviation given an number of responses. this will return a list of std deviation for each point i nthe list.
     * @param response_list
     * @return
     */
    private List<Double> compute_sigma_list(List<Response> response_list, List<Double> mu_list){
        List<Double> sigma_list = new ArrayList<Double>();
        List<Double> pressure_list;

        // compute sigma for each point in the list
        for(int i=0;i<response_list.get(i).getNormalizedResponse().size();i++){
            //for each response create a list of pressure values
            pressure_list = new ArrayList<Double>();

            for(dataTypes.Point point : response_list.get(i).getNormalizedResponse()){
                pressure_list.add(point.getPressure());
            }

            // compute sigma for the point
            sigma_list.add(compute_sigma(pressure_list, mu_list.get(i)));
        }

        return sigma_list;
    }

    /**
     * compute the standard deviation for the list of points
     *
     * @return
     */
    private double compute_sigma(List<Double> list, double mu) {
        double std = 0;

        // 1. Work out the Mean (the simple average of the numbers)
        // 2. Then for each number: subtract the Mean and square the result
        // 3. Then work out the mean of those squared differences.
        // 4. Take the square root of that and we are done!
        Iterator<Double> iterator = list.iterator();
        int count = 0;
        double total_subtract_mean_squared = 0;

        while (iterator.hasNext()) {
            Double t = iterator.next();

            total_subtract_mean_squared += Math.pow(t - mu, 2);
            count++;
        }

        // std is the square root of the average of these numbers
        std = Math.sqrt(total_subtract_mean_squared / count);

        return std;
    }

    /**
     * compute average of the list of points
     */
    private double compute_mu(List<Double> list) {
        Iterator<Double> iterator = list.iterator();
        double average = 0;
        double total = 0;

        while (iterator.hasNext()) {
            Double t = iterator.next();

            total += t;

        }

        average = total / list.size();

        return average;
    }

    private String create_analysis_string(){
        // perform analysis of the Responses Will use the UD-PUF library here
        // write functions to analyze the thing and print out the results
        int number_responses = challenge.getProfile().getNormalizedResponses().size();
        List<Double> pressure_mu_responses = challenge.getProfile().getPressureMuSigmaValues().getMuValues(); // compute_mu_list(responses);
        List<Double> pressure_sigma_responses = challenge.getProfile().getPressureMuSigmaValues().getSigmaValues(); // compute_sigma_list(responses, mu_responses);
        List<Double> pressure_variance_by_mean_responses = compute_variance_by_mean_list(pressure_mu_responses, pressure_sigma_responses);

        List<Double> distance_mu_responses = challenge.getProfile().getPointDistanceMuSigmaValues().getMuValues(); // compute_mu_list(responses);
        List<Double> distance_sigma_responses = challenge.getProfile().getPointDistanceMuSigmaValues().getSigmaValues(); // compute_sigma_list(responses, mu_responses);
        List<Double> distance_variance_by_mean_responses = compute_variance_by_mean_list(distance_mu_responses, distance_sigma_responses);

        List<Double> time_mu_responses = challenge.getProfile().getTimeDistanceMuSigmaValues().getMuValues(); // compute_mu_list(responses);
        List<Double> time_sigma_responses = challenge.getProfile().getTimeDistanceMuSigmaValues().getSigmaValues(); // compute_sigma_list(responses, mu_responses);
        List<Double> time_variance_by_mean_responses = compute_variance_by_mean_list(time_mu_responses, time_sigma_responses);

        // a neater way of displaying the information
        String console_output_1 = new String();

        // header for information
        console_output_1 += "number of responses: " + number_responses + "\n";
        console_output_1 += "PRESSURE\n";
        console_output_1 += "avg : sigma : variance/mean\n";

        // for each point, print out average, std deviation, and variance by mean
        for(int i = 0; i < pressure_mu_responses.size(); i++){
            console_output_1 += String.format("%.5f", pressure_mu_responses.get(i)) + " : " + String.format("%.5f", pressure_sigma_responses.get(i)) + " : " + String.format("%.5f", pressure_variance_by_mean_responses.get(i)) + "\n";
        }

        console_output_1 += "\nDISTANCE\n";
        console_output_1 += "avg : sigma : variance/mean\n";

        // for each point, print out average, std deviation, and variance by mean
        for(int i = 0; i < distance_mu_responses.size(); i++){
            console_output_1 += String.format("%.5f", distance_mu_responses.get(i)) + " : " + String.format("%.5f", distance_sigma_responses.get(i)) + " : " + String.format("%.5f", distance_variance_by_mean_responses.get(i)) + "\n";
        }

        console_output_1 += "\nTIME_POINT_TO_POINT\n";
        console_output_1 += "avg : sigma : variance/mean\n";

        // for each point, print out average, std deviation, and variance by mean
        for(int i = 0; i < time_mu_responses.size(); i++){
            console_output_1 += String.format("%.5f", time_mu_responses.get(i)) + " : " + String.format("%.5f", time_sigma_responses.get(i)) + " : " + String.format("%.5f", time_variance_by_mean_responses.get(i)) + "\n";
        }

        console_output_1 += "\nTIME_END_TO_END\n";
        console_output_1 += "avg : sigma : variance/mean\n";

        // for all responses, print out average, std deviation, and variance by mean
        double time_end_to_end_variance_by_mean = (challenge.getProfile().getTimeLengthSigma() * challenge.getProfile().getTimeLengthSigma())/challenge.getProfile().getTimeLengthMu();
        console_output_1 += String.format("%.5f", challenge.getProfile().getTimeLengthMu()) + " : " + String.format("%.5f", challenge.getProfile().getTimeLengthSigma()) + " : " + String.format("%.5f", time_end_to_end_variance_by_mean) + "\n";

        return console_output_1;
    }

    /**
     * print the same analysis which is printed to the console to a csv file
     */
    private void analysis_to_csv(String filename){
        // get the analysis information
        String output = create_analysis_string();

        // print the analysis information to a csv file
        write_string_to_csv(filename, output);
    }

    /**
     * Writes the response to a given challenge to a CSV file
     */
    public void write_string_to_csv(String filename, String output)
    {
        File baseDir = new File(Environment.getExternalStorageDirectory(), "puf_swipe_box");

        if (!baseDir.exists())
        {
            baseDir.mkdirs();
        }

        File f = new File(baseDir, filename);

        try
        {
            f.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(f));

            String[] output_array = {output};
            csvWrite.writeNext(output_array);

            csvWrite.close();

            Toast.makeText(this, "Written to CSV.", Toast.LENGTH_SHORT).show();
        }
        catch( Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Writes the responses list to a text file
     */
    public void write_responses_to_file(String filename, String output)
    {
        //File baseDir = new File(this.getFilesDir(), "puf_swipe_box");
        File baseDir = new File(Environment.getExternalStorageDirectory(), "puf_swipe_box");

        if (!baseDir.exists())
        {
            baseDir.mkdirs();
        }

        File f = new File(baseDir, filename);

        try
        {
            Log.d("filename", f.getName());

            FileWriter outputWriter = new FileWriter(f);

            outputWriter.write(output);
            outputWriter.close();

            Toast.makeText(this, "File saved successfully.", Toast.LENGTH_SHORT).show();
        }
        catch( Exception e)
        {
            e.printStackTrace();
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
