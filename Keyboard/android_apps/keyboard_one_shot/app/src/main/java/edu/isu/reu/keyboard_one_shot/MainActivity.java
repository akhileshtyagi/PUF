package edu.isu.reu.keyboard_one_shot;

import android.app.Activity;
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

public class MainActivity extends Activity {
    public final static String AUTHENTICATION_STRING = "edu.isu.reu.AUTHENTICATION_STRING";

    Activity main_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_listener_word_authentication();
        add_listener_phrase_authentication();
        add_listener_paragraph_authentication();

        // keep track of the main activity so that it
        // can be used to build intents in the buttons
        main_activity = this;
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

    /**
     * define actions for the buttons in the list
     *
     * There is one activity which can be provided a string with which
     * to authenticate the user.
     *
     * The different authentication buttons call this activity with
     * different strings
     */
    private void add_listener_word_authentication(){
        Button button = (Button) findViewById(R.id.word_authentication_button);
        String message = "therefore";

        add_listener_button_string_authentication_activity(button, message);
    }

    private void add_listener_phrase_authentication(){
        Button button = (Button) findViewById(R.id.phrase_authentication_button);
        String message = "The cute brown fox chased the brown squirrel up a tree.";

        add_listener_button_string_authentication_activity(button, message);
    }

    private void add_listener_paragraph_authentication(){
        Button button = (Button) findViewById(R.id.paragraph_authentication_button);
        String message = "The cute brown fox chased the brown squirrel up a tree." +
                "The squirrel became enraged and began throwing acorns at the fox." +
                "The fox covers his face with his little claws." +
                "An acorn manages to evade the claws and smashes into the fox's nose." +
                "Best to run away from angry squirrels, acorns hurt.";

        add_listener_button_string_authentication_activity(button, message);
    }

    /**
     * this method adds a listener to a button
     *
     * when the button is pressed,
     * the given button calls StringAuthenticationActivity
     * with provided message
     */
    private void add_listener_button_string_authentication_activity(Button button, String message){
        //TODO .....
        final String s = message;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(main_activity, StringAuthenticationActivity.class);
                intent.putExtra(AUTHENTICATION_STRING, s);
                startActivity(intent);
            }
        });
    }
}
