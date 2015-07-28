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

        //models
        //TODO Chain base_chain;
        //TODo Chain auth_chain;

        //times
        long start_time;
        long time_taken;

        //here I want to run code for the speedtest.
        //when the speedtest has completed I will want to output code to the textbox
        //TODO run the tests
        for(int i=1000;i<10001;i+=500){
            for(int j=1000;j<10001;j+=500){
                //store the model sizes
                base_size.add(i);
                auth_size.add(j);

                //create the base and auth models
                //TODO base_chain = ;
                //TODO auth_chain = ;

                //add the touches to the chains, try to create the worst case running time

                //begin the timer
                start_time = System.currentTimeMillis();
                // TODO base_chain.compare_to(auth_chain);

                //stop the timer, reccord the time
                time_taken = System.currentTimeMillis() - start_time;

                //store the result... the time it took to build and compare these models
                result_time.add(time_taken);
            }
        }

        //output to the textbox
        edit_text = (EditText) findViewById(R.id.editText);

        //construct the output string from the test resuls
        //print a header
        output = "base_size\tauth_size\ttime_taken\n";

        //print the results of all the tests
        for(int i =0;i<result_time.size();i++){
            output = output + base_size.get(i)+"\t"+auth_size.get(i)+"\t"+result_time.get(i)+"\n";
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
}
