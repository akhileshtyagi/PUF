package puf.iastate.edu.puf_enrollment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Created by nmont on 10/8/15.
 */
public class PinPatternGen  extends AppCompatActivity {
    private TextView mPinView;
    private int pin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_enter);

        mPinView = (TextView) findViewById(R.id.pin_edit_text);

    }

    /**
     * Button press to begin gesture training
     * @param v
     */
    public void begin_pressed(View v) {

        try {
            //Grab pin
            pin = Integer.valueOf(mPinView.getText().toString());

            //Pass pin to gesture training activity
            Intent authenticate = new Intent(this, RegisterGesturesActivity.class);
            authenticate.putExtra("pin", pin);
            authenticate.putExtra("mode", "enroll");
            startActivity(authenticate);

            Toast.makeText(this, "Beginning Enrollment", Toast.LENGTH_SHORT).show();
        } catch ( NumberFormatException nfe ) {
            Toast.makeText(this, "Pin is not a number", Toast.LENGTH_SHORT).show();
        }
    }

}
