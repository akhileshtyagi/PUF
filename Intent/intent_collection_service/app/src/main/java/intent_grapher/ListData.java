package intent_grapher;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.List;

import edu.isu.reu.intent_collection_service.R;
import intent_analysis.IntentAnalytics;
import intent_record.IntentData;
import intent_record.IntentRecord;

/**
 * The goals of this class are as follows:
 * 1. retrieve the IntentData information from the service
 *
 * 2. Give a String which puts:
 *
 * deffonatly -> IntentData
 * mabe -> transition matrix
 * mabe -> intent graph
 *
 * into a readable format
 *
 * 3. Display this data in a user friendly way
 */
public class ListData extends AppCompatActivity {
    final static boolean IS_NUMBERED = true;

    private EditText edit_text;
    private IntentRecord intent_record;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set the xml layout
        setContentView(R.layout.layout_settings_activity);

        // set the toolbar
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        //setSupportActionBar(myToolbar);

        // add elements to the layout
        add_elements();

        // create intent record for getting intents from service
        this.intent_record = new IntentRecord(this);
    }

    /**
     * adds elements to the layout
     */
    private void add_elements(){
        // get the layout to add buttons to
        LinearLayout linear_layout = (LinearLayout) findViewById(R.id.master_layout);

        // create a horizontal layout
        LinearLayout button_layout = new LinearLayout(getApplicationContext());
        button_layout.setOrientation(LinearLayout.HORIZONTAL);

        // add buttons to button layout
        // print intents
        button_layout.addView(create_button("intent list",
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        // request Intents from the intent collection service
                        // use IntentRecord
                        intent_list();
                    }
                }));

        // print a number of stats about intents
        button_layout.addView(create_button("statistics",
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // Perform action on click
                        // request Intents from the intent collection service
                        // use IntentRecord
                        display_statistics();
                    }
                }));

        // add the update buttons
        linear_layout.addView(button_layout);

        // add an edit text
        edit_text = create_edit_text();
        linear_layout.addView(edit_text);
    }

    /**
     * update the edit_text
     */
    private void intent_list(){
        // get intents from service
        List<IntentData> intent_data_list = this.intent_record.receive_intent_data();

        // create a string from the intents
        StringBuilder sb = new StringBuilder();

        // add each intent data to the list
        int count = 0;
        for(IntentData intent_data : intent_data_list){
            // provide a numbering
            if(IS_NUMBERED){
                sb.append(count);
                sb.append(": ");
            }

            // add the intent data
            sb.append(intent_data.toString());
            sb.append('\n');

            count++;
        }

        // set the string as the edittext text
        this.edit_text.setText(sb.toString());
    }

    /**
     * display statistics about the intents
     */
    private void display_statistics(){
        // get intents from service
        List<IntentData> intent_data_list = this.intent_record.receive_intent_data();

        // create a string from the intents
        StringBuilder sb = new StringBuilder();

        // Compute all stats offered about intent_list
        // for each enum value
        for(IntentAnalytics.Statistic statistic : IntentAnalytics.Statistic.values()){
            sb.append(IntentAnalytics.string_value(statistic));
            sb.append(": ");

            sb.append(IntentAnalytics.analyze(intent_data_list, statistic));
            sb.append('\n');
        }

        // set the string as the edittext text
        this.edit_text.setText(sb.toString());
    }

    /**
     * build a button with the given String as text
     */
    private Button create_button(String button_text, View.OnClickListener on_click_listener){
        Button button = new Button(this);

        // set button properties
        button.setText(button_text);
        button.setOnClickListener(on_click_listener);

        return button;
    }

    /**
     * create an edit text with all parameters reset
     */
    private EditText create_edit_text(){
        EditText e = new EditText(getApplicationContext());

        // set properties of e
        e.setText("press update to request intents from intent collection service.");
        e.setBackgroundColor(Color.BLACK);
        e.setKeyListener(null);

        return e;
    }
}
