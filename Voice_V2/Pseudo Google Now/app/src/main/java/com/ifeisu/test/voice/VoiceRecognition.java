package com.ifeisu.test.voice;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Please install Voice_Search_2.1.4.apk on device before use this app.
 * @author yunxi
 */

public class VoiceRecognition extends Activity implements OnClickListener {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private ListView mList;
    private MediaRecorder mediaRecorder;
    private File file;
    private int order;
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        order = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button speakButton = (Button) findViewById(R.id.btn_speak);
        mList = (ListView) findViewById(R.id.list);
        // Check to see if a recognition activity is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0)
        {
            speakButton.setOnClickListener(this);
        } else
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
    }

    public void onClick(View v)
    {
        if (v.getId() == R.id.btn_speak)
        {
            startVoiceRecognitionActivity();
        }
    }

    private void startVoiceRecognitionActivity()
    {
        //voice recognition pattern
        Intent intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);//ACTION_WEB_SEARCH
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);


        // Ask user to start talking
        /*try {
            file = new File(Environment.getExternalStorageDirectory(), order + ".3gp");
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
            order++;
        }catch(IllegalStateException e){
        }catch (IOException e){
        }*/
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Please speak key word you want to search: ");


        //intent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        //intent.putExtra("android.speech.extra.GET_AUDIO", true);

        // Start voice recognition
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    // When recording ending, getting result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE
                && resultCode == RESULT_OK)
        {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mList.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, matches));
        }

        // the recording url is in getData:
        /*Bundle bundle = data.getExtras();
        ArrayList<String> matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        Uri audioUri = data.getData();
        ContentResolver contentResolver = getContentResolver();
        try {
            InputStream filestream = contentResolver.openInputStream(audioUri);
        }catch(FileNotFoundException f){

        }*/
        super.onActivityResult(requestCode, resultCode, data);
    }

}
