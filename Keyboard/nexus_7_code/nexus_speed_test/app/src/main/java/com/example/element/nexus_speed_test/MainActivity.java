package com.example.element.nexus_speed_test;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import components.Chain;
import components.Touch;
import runtime.CompareChains;


public class MainActivity extends ActionBarActivity {
    private EditText edit_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void buttonOnClick(View v) {
        // do something when the button is clicked
        Button button=(Button) v;
        String output = "";

        //contain the result time and the user, base model size
        ArrayList<Long> result_time = new ArrayList<Long>();
        ArrayList<Integer> base_size = new ArrayList<Integer>();
        ArrayList<Integer> auth_size = new ArrayList<Integer>();
        ArrayList<Integer> window_size = new ArrayList<Integer>();

        //times
        long time_taken;

        //here I want to run code for the speedtest.
        //when the speedtest has completed I will want to output code to the textbox
        //run the tests
        for(int i=8000;i<10001;i+=5000){     // base size
            for(int j=4000;j<10001;j+=5000){ // auth size
                for(int k=1;k<11;k++) {     // window size
                    //store the model sizes
                    base_size.add(i);
                    auth_size.add(j);
                    window_size.add(k);

                    //test the amount of time these model sizes take to build and authenticate
                    time_taken = time_overall_model_compare(i, j, k);

                    //store the result... the time it took to build and compare these models
                    result_time.add(time_taken);
                }
            }
        }

        //output to the textbox
        edit_text = (EditText) findViewById(R.id.editText);

        //construct the output string from the test resuls
        //print a header
        output = "base_size\tauth_size\twindow_size\ttime_taken\n";

        //print the results of all the tests
        for(int i =0;i<result_time.size();i++){
            output = output + base_size.get(i)+"\t"+auth_size.get(i)+"\t"+window_size.get(i)+"\t"+result_time.get(i)+"\n";
        }

        edit_text.setText(output);

        //write this same output to a file
        try {
            //figure out how to create files, this isn't working
            File output_directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            File output_file = new File(output_directory.getAbsolutePath()+"/nexus_speed_test.txt");

            //FileOutputStream fos = openFileOutput("nexus_speed_test",Context.MODE_MULTI_PROCESS);
            //BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            FileWriter writer = new FileWriter(output_file);

            writer.write(output);
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //constants used for speedtesting
    //final static int SPEED_TEST_WINDOW_SIZE = 3;
    final static int SPEED_TEST_TOKEN_SIZE = 7;
    final static int SPEED_TEST_THRESHOLD = 1000;

    //returns the time it takes to build and compare the models
    private static long time_overall_model_compare(int base_size, int auth_size, int window_size){
        Chain base_chain = create_chain(base_size, window_size);
        Chain auth_chain = create_chain(auth_size, window_size);

        CompareChains cc = new CompareChains(base_chain, auth_chain);
        Thread thread = new Thread(cc);

        long start_time = System.currentTimeMillis();
        //do the method
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end_time = System.currentTimeMillis();

        return end_time-start_time;
    }


    ///creates a chain of the specified size
    private static Chain create_chain(int chain_size, int window_size){
        Chain chain = new Chain(window_size, SPEED_TEST_TOKEN_SIZE, SPEED_TEST_THRESHOLD, chain_size);

        //add touches to the chain
        for(int i=0;i<chain_size;i++){
            chain.add_touch(new Touch('a', (i%11)*.1, 100));
        }

        return chain;
    }
}
