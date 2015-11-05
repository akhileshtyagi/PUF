package com.example.element.swipe_box;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dataTypes.Challenge;
import dataTypes.Response;
import dataTypes.UserDevicePair;

public class Activity_menu extends AppCompatActivity {

    private Challenge challenge;

    private int box_width;
    private int box_height;
    private Point box_upper_left_corner;

    public enum Result{
        RESPONSE(0),
        RESPONSE_TEST(1),
        AUTH_RESPONSE(2);

        private int int_value;
        public int get_int_value(){
            return int_value;
        }

        Result(int int_value){
            this.int_value = int_value;
        }
    }

    EditText output_console_edit_text;

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
        this.box_width = 750;
        this.box_height = 300;
        this.box_upper_left_corner = new Point(100,100,0);

        // set up the challenge pattern
        ArrayList<dataTypes.Point> challenge_pattern_list = new ArrayList<dataTypes.Point>();
        challenge_pattern_list.add(new dataTypes.Point(box_upper_left_corner.x, box_upper_left_corner.y+box_height/2, 0));
        challenge_pattern_list.add(new dataTypes.Point(box_upper_left_corner.x+box_width, box_upper_left_corner.y+box_height/2, 0));

        this.challenge = new Challenge(challenge_pattern_list,0);

        /**
         * find the edit text for output
         */
        output_console_edit_text = (EditText)findViewById(R.id.output_console_edit_text);
        output_console_edit_text.setText("Press Collect Swipe Responses to Begin");

        /**
         * find all of the buttons and assign them to variables
         *
         *      collect_swipe_responses_button - This button is used to collect a set of responses to the swipe
         */
        Button collect_swipe_responses_button = (Button)findViewById(R.id.collect_swipe_responses_button);
        Button test_swipe_box_button = (Button)findViewById(R.id.test_swipe_box_button);
        Button analyze_responses_button = (Button)findViewById(R.id.analyze_responses_button);
        Button output_responses_to_csv_button = (Button)findViewById(R.id.output_responses_to_csv_button);
        Button save_responses_button = (Button)findViewById(R.id.save_responses_button);
        Button load_responses_button = (Button)findViewById(R.id.load_responses_button);
        Button authenticate_against_responses_button = (Button)findViewById(R.id.authenticate_against_responses);

        /**
         * define the click listeners for each button
         */
        /**
         * active the collect swipe responses activity. Record somewhere the responses.
         */
        collect_swipe_responses_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                List<Response> responses = new ArrayList<Response>();

                // start the swipe box activity. Responses will be loged in onActivityResult()
                for(int i=0;i<10; i++){
                    start_activity_swipe_box();
                }

                // set the challenge equal to the most resent set of responses
                for(Response r: responses) {
                    challenge.addResponse(r);
                }
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
                // perform analysis of the Responses Will use the UD-PUF library here
                // build various functions which generate different results
                // TODO normalize the responses using the UD_PUF library
                //Challenge challenge = new Challenge();

                // write functions to analyze the thing and print out the results
                int number_responses = challenge.getProfile().getNormalizedResponses().size();
                List<Double> mu_responses = challenge.getProfile().getPressureMuSigmaValues().getMuValues(); // compute_mu_list(responses);
                List<Double> sigma_responses = challenge.getProfile().getPressureMuSigmaValues().getSigmaValues(); // compute_sigma_list(responses, mu_responses);
                List<Double> variance_by_mean_responses = compute_variance_by_mean_list(mu_responses, sigma_responses);

                // turn the results into an output string
                String console_output_0 = "";
                console_output_0 += "number of responses: " + number_responses + "\n";
                console_output_0 += "average of responses: " + mu_responses + "\n";
                console_output_0 += "sigma of responses: " + sigma_responses + "\n";
                console_output_0 += "variance by mean of responses: " + variance_by_mean_responses + "\n";

                // a neater way of displaying the information
                String console_output_1 = new String();

                // header for information
                console_output_1 += "number of responses: " + number_responses + "\n";
                console_output_1 += "avg : sigma : variance/mean\n";

                // for each point, print out average, std deviation, and variance by mean
                for(int i = 0; i < mu_responses.size(); i++){
                    console_output_1 += String.format("%.5f", mu_responses.get(i)) + " : " + String.format("%.5f", sigma_responses.get(i)) + " : " + String.format("%.5f", variance_by_mean_responses.get(i)) + "\n";
                }

                // update the edit text console with the results
                output_console_edit_text.setText(console_output_1);
            }//End onClick
        });

        /**
         * output the current response set to .csv files
         */
        output_responses_to_csv_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                //startActivityForResult(i, 0);
            }//End onClick
        });

        /**
         * save the current responses to shared preferences
         */
        save_responses_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                //startActivityForResult(i, 0);
            }//End onClick
        });

        /**
         * load into current responses the response set from shared preferences
         */
        load_responses_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Intent i = new Intent(getActivity(), CrimePagerActivity.class);
                //startActivityForResult(i, 0);
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

            // put the responses into the challenge object
            challenge.addResponse(response);
        }else if ((requestCode == Result.AUTH_RESPONSE.get_int_value()) && (resultCode == RESULT_OK)){
            // here the response is ment to be authenticated against the current responses
            ArrayList<Point> array_response = (ArrayList<Point>)data.getExtras().getSerializable("response");

            //convert the arraylist of points into a ud_puf response object
            Response response = array_to_response(array_response);

            // call a method to compare this response against the responses gathered previously.
            authenticate_against_responses(response);
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
        boolean authenticated = ud_pair.authenticate(response.getResponse(), challenge.getProfile());

        // print out information about the authentication to the console
        String console_output = "";
        console_output += "authenticated: " + authenticated + "\n";
        console_output += "failed_points_ratio: " + String.format("%.5f", (ud_pair.failedPointRatio()));

        // output to the console
        output_console_edit_text = (EditText)findViewById(R.id.output_console_edit_text);
        output_console_edit_text.setText(console_output);
    }

    /**
     * takes an ArrayList<Point> and turns it into a UD_PUF response
     */
    private Response array_to_response(ArrayList<Point> array_response){
        ArrayList<dataTypes.Point> points = new ArrayList<dataTypes.Point>();

        //for each point in the array, add it to the response
        for(Point array_point : array_response){
            points.add(new dataTypes.Point(array_point.x, array_point.y, array_point.pressure));
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
    private void start_activity_swipe_box(){
        Intent intent = new Intent(this, Activity_swipe_box.class);

        // add the box challenge parameters to the intent
        intent.putExtra("box_width", this.box_width);
        intent.putExtra("box_height", this.box_height);
        intent.putExtra("box_upper_left_corner", this.box_upper_left_corner);

        startActivityForResult(intent, Result.RESPONSE.get_int_value());
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
        for(int i=0;i<response_list.get(0).getResponse().size();i++){
            //for each response create a list of pressure values
            pressure_list = new ArrayList<Double>();

            for(dataTypes.Point point : response_list.get(i).getResponse()){
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
        for(int i=0;i<response_list.get(i).getResponse().size();i++){
            //for each response create a list of pressure values
            pressure_list = new ArrayList<Double>();

            for(dataTypes.Point point : response_list.get(i).getResponse()){
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
}
