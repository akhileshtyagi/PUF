package edu.isu.reu.keyboard_one_shot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

/**
 * mabe eventually the user has a security word that they type
 * this is ment to emulate how that might go
 *
 * could we generate a key-vault type of thing from this security word?
 */
public class StringAuthenticationActivity extends AppCompatActivity {
    String display_text;
    int bolded_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_authentication);

        // get the intent string
        this.display_text = get_intent_string();
        this.bolded_index = 0;

        // get the edittext display and edittext edittext
        EditText display_text_edit_text = (EditText)findViewById(R.id.display_text_edit_text);
        EditText enter_text_edit_text = (EditText)findViewById(R.id.enter_text_edit_text);

        // make the edit text uneditable
        display_text_edit_text.setKeyListener(null);
        enter_text_edit_text.setKeyListener(null);

        // display the intent string
        display_intent_string(this.display_text, this.bolded_index);

        // display the keyboard
    }

    /**
     * get the authentication string from the intent
     */
    private String get_intent_string(){
        Intent intent = getIntent();

        return intent.getStringExtra(MainActivity.AUTHENTICATION_STRING);
    }

    /**
     * display the string in the text_to_type_textbox
     *
     * the bolded letter index refers to which character will be bolded
     */
    private void display_intent_string(String text, int bolded_index){
        // get the edittext display
        EditText display_text_edit_text = (EditText)findViewById(R.id.display_text_edit_text);

        // go though the text, set one character to bold indicated by bolded_index
        char[] text_char_list = new char[text.length()];
        text.getChars(0, text.length(), text_char_list, 0);

        // go the the string, building a new one which is bolded at the specified index
        String bolded_char_string = "";
        for(int i=0; i<text_char_list.length; i++){
            boolean bold_char = i == bolded_index;
            char character = text_char_list[i];

            // add bold tag
            if(bold_char){
                bolded_char_string += "<b>";
            }

            bolded_char_string += character;

            // add bold end tag
            if(bold_char){
                bolded_char_string += "</b>";
            }
        }

        // set the text value of the edittext
        display_text_edit_text.setText(Html.fromHtml(bolded_char_string), TextView.BufferType.EDITABLE);
    }

    /**
     * this method manages the entry of text by the keyboard
     *
     * every time the user enters a key
     * 0. start with the first letter in display_text bolded
     * 1. if (the typed letter is correct)
     * 1.1 advance the bolded letter in the display_text
     * 2. if (the character is space) && (the typed word is correct)
     * 2.1 then delete the text in edit_text
     * 3. if the word in edit_text is incorrect
     * 3.1 turn the word in edit_text red
     * 4. if the entire phrase has been entered
     * 4.1 then display authentication results
     * 4.2 when authentication results finishes
     * 4.2.1 then finish this activity
     *
     * the method is called every time the user presses a key
     */
    private void manage_text_entry(float x, float y, float pressure){
        //TODO
        Log.d("event information", String.format("x:%.4f, y:%.4f, p:%.4f", x, y, pressure));

        // go though the text, set one character to bold indicated by bolded_index
        char[] text_char_list = new char[this.display_text.length()];
        this.display_text.getChars(0, this.display_text.length(), text_char_list, 0);

        // decide what key was pressed
        // this is based on the location of the key on the screen
        char key = get_key((int)x, (int)y);

        // decide if the key is correct
        // does the key equal the bolded key?
        //TODO right now I don't have shift implemented, so check for lower case
        boolean correct_key = (key == Character.toLowerCase(text_char_list[this.bolded_index]));

        // preform actions based on whether the key is correct or not
        if(correct_key){
            // advance the bolded index
            this.bolded_index++;

            // check if this is the last character

            // check if it is a space
            //TODO
        }else {
            //TODO could do somehting like turn edittext red and put bad character there until backspace
            //TODO currently backspace is not implemnted
        }
    }

    /**
     * get the keyboard key which coresponds to
     * the given x, y coordinate
     */
    private char get_key(int x, int y){
        //TODO
        return 't';
    }

    /**
     * this method creates an intent to another activity
     *
     * this other activity will
     * compute the result of the authentication
     *
     * display the results of the authentication
     */
    private void display_authentication_results(){
        // create intent for display activity
        //TODO

        // put extra string generated by keyboard data
        //TODO

        // start the display activity
        //TODO
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        float pressure = e.getPressure();

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                manage_text_entry(x, y, pressure);
                break;
        }

        return true;
    }
}
