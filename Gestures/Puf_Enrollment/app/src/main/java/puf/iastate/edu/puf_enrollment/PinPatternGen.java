package puf.iastate.edu.puf_enrollment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


/**
 * Created by nmont on 10/8/15.
 */
public class PinPatternGen  extends AppCompatActivity {

    public PinPatternGen() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pin_enter);
    }

    public void begin_pressed(View v) {
        Toast.makeText(this, "Beginning Enrollment", Toast.LENGTH_SHORT).show();
    }

}
