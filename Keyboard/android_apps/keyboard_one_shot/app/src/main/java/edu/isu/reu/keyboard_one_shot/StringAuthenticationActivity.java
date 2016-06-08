package edu.isu.reu.keyboard_one_shot;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO list
 * [ ] test encode methods for correctness
 * [ ] test decode methods for correctness
 * [ ] figure out why the application is dieing after implementing encode, decode functionality
 * [ ] implement the get_keys() method
 * [ ] test the get_keys() method
 */

/**
 * mabe eventually the user has a security word that they type
 * this is ment to emulate how that might go
 * <p>
 * could we generate a key-vault type of thing from this security word?
 */
public class StringAuthenticationActivity extends AppCompatActivity {
    // defines value of intent extra
    public final static String TOUCH_POINT_STRING = "touch_point_string";
    // defines value of break character between string elements in encoded string
    public final static String TOUCH_POINT_BREAK_CHARACTER = "~";
    // defines the value of break character between x,y,p in touch string
    public final static String INTER_TOUCH_POINT_BREAK_CHARACTER = "_";

    // this stores a programatic representation of the keyboard
    Keyboard keyboard;

    String display_text;
    int bolded_index;
    List<List<Float>> touch_point_list;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_string_authentication);

        // initialize the keyboard
        initialize_keyboard();

        // get the intent string
        this.display_text = get_intent_string();
        this.bolded_index = 0;

        // get the edittext display and edittext edittext
        EditText display_text_edit_text = (EditText) findViewById(R.id.display_text_edit_text);
        EditText enter_text_edit_text = (EditText) findViewById(R.id.enter_text_edit_text);

        // make the edit text uneditable
        display_text_edit_text.setKeyListener(null);
        enter_text_edit_text.setKeyListener(null);

        // display the intent string
        display_intent_string(this.display_text, this.bolded_index);

        // init variables
        this.touch_point_list = new ArrayList<List<Float>>();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * this method initializes the keyboard
     */
    private void initialize_keyboard() {
        // acquire the imageview that displays the keyboard
        ImageView keyboard_image_view = (ImageView) findViewById(R.id.keyboard_image_view);

        // get the display size
        // get the bottom of the screen in px
        WindowManager windowManager = (WindowManager) this.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        // create a listener to wait for keyboard to be drawn
        keyboard_image_view.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Layout has happened here.
                        if (keyboard == null) {
                            // if this is the first time this listener has run,
                            setup_keyboard_instance();
                        }

                        // the x value is simply x (probabally 0)
                        keyboard.x_offset = (int) keyboard_image_view.getX();

                        // the y offset of the keyboard is more complicated
                        // the imageview does not necessarily display
                        // the entire keyboard.
                        // to get this value,
                        // 1. get the bottom of the keyboard
                        // 2. add the keyboard height to this
                        Point display_size = new Point();
                        display.getSize(display_size);
                        float keyboard_bottom = display_size.y;

                        keyboard.y_offset = (int)(keyboard_bottom - keyboard.get_height());

                        //TODO testing to figure out why height is incorrect
                        //Log.d("offset", "x: " + (keyboard.x_offset) + ", y: " + (keyboard.y_offset));
                        //Log.d("keyboard bottom", "" + (keyboard_bottom));
                        //Log.d("keyboard height", "" + (keyboard.get_height()));
                        //Log.d("keyboard copyBounds", "" + (keyboard_image_view.getDrawable().copyBounds()));
                        //Log.d("image view y", "" + (keyboard_image_view.getY()));
                        //Log.d("image view height", "" + (keyboard_image_view.getHeight()));


                        // the next line of code removes the listener
                        // don't want to do this because above the keyboard is updated with text
                        // this will push the keyboard down.
                        // When the layout is redone, we need to be informed
                        //keyboard_image_view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    /**
     * set up the keyboard instance variable
     * this is useful because it only needs to be done once
     * <p>
     * also this needs to be done after the layout has been created
     */
    private void setup_keyboard_instance() {
        // acquire the imageview that displays the keyboard
        ImageView keyboard_image_view = (ImageView) findViewById(R.id.keyboard_image_view);

        // initialize the keyboard
        // get other attributes and instanciate keyboard
        int width = keyboard_image_view.getWidth();

        // height is height of the keyboard graphic,
        // not the height of the imageview
        // acquire the actual height of the drawn image
        float keyboard_image_actual_height = keyboard_image_view.getDrawable().getIntrinsicHeight();
        float keyboard_image_actual_width = keyboard_image_view.getDrawable().getIntrinsicWidth();

        // use the width to determine the scale factor
        float scale_factor = keyboard_image_actual_width / (float) width;

        // the actual height of the scaled keyboard will be the
        // scaling factor * intrinsic height
        //int height = (int)(keyboard_image_actual_height * scale_factor);
        int height = (int) keyboard_image_view.getHeight();

        // create keyboard instance
        int separation = 0;
        this.keyboard = new Keyboard(width, height, separation, separation);

        //TODO testing to figure out why height is incorrect
        //Log.d("keyboard width, height",
          //      "width: " + (this.keyboard.get_width()) + ", height: " + this.keyboard.get_height());
    }

    /**
     * get the authentication string from the intent
     */
    private String get_intent_string() {
        Intent intent = getIntent();

        return intent.getStringExtra(MainActivity.AUTHENTICATION_STRING);
    }

    /**
     * display the string in the text_to_type_textbox
     * <p>
     * the bolded letter index refers to which character will be bolded
     */
    private void display_intent_string(String text, int bolded_index) {
        // get the edittext display
        EditText display_text_edit_text = (EditText) findViewById(R.id.display_text_edit_text);

        // go though the text, set one character to bold indicated by bolded_index
        char[] text_char_list = new char[text.length()];
        text.getChars(0, text.length(), text_char_list, 0);

        // go the the string, building a new one which is bolded at the specified index
        String bolded_char_string = "";
        for (int i = 0; i < text_char_list.length; i++) {
            boolean bold_char = i == bolded_index;
            char character = text_char_list[i];

            // add bold tag
            if (bold_char) {
                bolded_char_string += "<b>";
            }

            bolded_char_string += character;

            // add bold end tag
            if (bold_char) {
                bolded_char_string += "</b>";
            }
        }

        // set the text value of the edittext
        display_text_edit_text.setText(Html.fromHtml(bolded_char_string), TextView.BufferType.EDITABLE);
    }

    /**
     * this method manages the entry of text by the keyboard
     * <p>
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
     * <p>
     * the method is called every time the user presses a key
     */
    private void manage_text_entry(float x, float y, float pressure) {
        Log.d("event information", String.format("x:%.4f, y:%.4f, p:%.4f", x, y, pressure));

        // go though the text, set one character to bold indicated by bolded_index
        char[] text_char_list = new char[this.display_text.length()];
        this.display_text.getChars(0, this.display_text.length(), text_char_list, 0);

        // grab the edit text box
        EditText enter_text_edit_text = (EditText) findViewById(R.id.enter_text_edit_text);

        // decide what key was pressed
        // this is based on the location of the key on the screen
        char key = get_key((int) x, (int) y);

        // decide if the key is correct
        // does the key equal the bolded key?
        //TODO right now I don't have shift implemented, so check for lower case
        boolean correct_key = (key == Character.toLowerCase(text_char_list[this.bolded_index]));

        // preform actions based on whether the key is correct or not
        if (correct_key) {
            // advance the bolded index
            this.bolded_index++;

            // add the character to edittext
            enter_text_edit_text.setText(enter_text_edit_text.getText().append(key), TextView.BufferType.EDITABLE);

            // check if this is the last character in the entire string
            // this happens if bolded index equals the length of the string
            if (bolded_index == text_char_list.length) {
                // we are done!
                // show the authentication view
                display_authentication_results();

                //TODO potentially call finish() here,
                //TODO i want to do this if it will finish on return
                finish();
            }

            // check if it is a space
            if (key == ' ') {
                // delete all characters in edit text
                enter_text_edit_text.setText("", TextView.BufferType.EDITABLE);
            }

            // redisplay the display string
            display_intent_string(this.display_text, this.bolded_index);
        }//else {
        //TODO could do somehting like turn edittext red and put bad character there until backspace
        //TODO currently backspace is not implemnted
        //}
    }

    /**
     * get the keyboard key which coresponds to
     * the given x, y coordinate
     */
    private char get_key(int x, int y) {
        //TODO test code always returns correct key
        //EditText enter_text_edit_text = (EditText)findViewById(R.id.enter_text_edit_text);

        //char[] text_char_list = new char[this.display_text.length()];
        //this.display_text.getChars(0, this.display_text.length(), text_char_list, 0);

        //char key = Character.toLowerCase(text_char_list[this.bolded_index]);

        //return key;

        //TODo seems to be returning the incorrect character
        Log.d("key", "" + this.keyboard.get_character(x - this.keyboard.x_offset, y - this.keyboard.y_offset));
        Log.d("x, y", "x: " + (x - this.keyboard.x_offset) + ", y: " + (y - this.keyboard.y_offset));

        //TODO this returns the real key according to the keyboard
        return this.keyboard.get_character(x - this.keyboard.x_offset, y - this.keyboard.y_offset);
    }

    /**
     * this method creates an intent to another activity
     * <p>
     * this other activity will
     * compute the result of the authentication
     * <p>
     * display the results of the authentication
     */
    private void display_authentication_results() {
        // create intent for display activity
        Intent intent = new Intent(this, KeyboardAuthenticationResult.class);

        // put extra string generated by keyboard data
        intent.putExtra(TOUCH_POINT_STRING, encode_touch_point_list(touch_point_list));

        // start the display activity
        startActivity(intent);
    }

    /**
     * encode list<list<>> as a string given list<list<>>
     */
    public static String encode_touch_point_list(List<List<Float>> touch_point_list) {
        String touch_point_list_string = "";

        // for each touch point in the array
        for (List<Float> touch_point : touch_point_list) {
            // add the encoded point to the string
            touch_point_list_string += encode_touch_point(touch_point);

            // after encoding the point, add the break character
            touch_point_list_string += TOUCH_POINT_BREAK_CHARACTER;
        }

        return touch_point_list_string;
    }

    /**
     * encode a single touch point
     */
    private static String encode_touch_point(List<Float> touch_point) {
        String touch_point_string = "";

        // for the first two elements in touch_point
        for (int i = 0; i < 2; i++) {
            touch_point_string += Math.round(touch_point.get(i));

            // add the break character
            touch_point_string += INTER_TOUCH_POINT_BREAK_CHARACTER;
        }

        // for the last (3) element in touch _point (the pressure)
        touch_point_string += touch_point.get(2);

        // add the break character
        touch_point_string += INTER_TOUCH_POINT_BREAK_CHARACTER;

        return touch_point_string;
    }

    /**
     * decode list<list<>> as a list<list<>> given string
     */
    public static List<List<Float>> decode_touch_point_list(String touch_point_list_string) {
        List<List<Float>> touch_point_list = new ArrayList<>();

        // separate out the elements in the string strTok
        String[] touch_point_string_array = touch_point_list_string.split(TOUCH_POINT_BREAK_CHARACTER);

        // parse through the elements one at a time
        for (String touch_point : touch_point_string_array) {
            // decode and add the value to touch_point_list
            touch_point_list.add(decode_touch_point(touch_point));
        }

        // return the decoded list of touch point values
        return touch_point_list;
    }

    /**
     * decode a single touch point
     */
    private static List<Float> decode_touch_point(String touch_point_string) {
        List<Float> touch_point = new ArrayList<>();

        // separate the elements
        String[] touch_point_string_array = touch_point_string.split(INTER_TOUCH_POINT_BREAK_CHARACTER);

        // parse the elements
        for (int i = 0; i < 3; i++) {
            // get the value of the element
            float value = Float.valueOf(touch_point_string_array[i]);

            // add the value to the list
            touch_point.add(value);
        }

        return touch_point;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        float pressure = e.getPressure();

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                // add this point to the touch_list
                List<Float> touch_point = new ArrayList<>();
                touch_point.add(x);
                touch_point.add(y);
                touch_point.add(pressure);
                this.touch_point_list.add(touch_point);

                // manage the text for this key press
                manage_text_entry(x, y, pressure);
                break;
        }

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "StringAuthentication Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.isu.reu.keyboard_one_shot/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "StringAuthentication Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://edu.isu.reu.keyboard_one_shot/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
