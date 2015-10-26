package puf.iastate.edu.puf_enrollment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void enroll_pressed(View v) {

        Intent intent = new Intent(this, PinPatternGen.class);
        startActivity(intent);

    }


    public void authenticate_pressed(View v) {
        Toast.makeText(this, "Launching Authentication", Toast.LENGTH_SHORT).show();
    }

    public void pin_pressed(View v) {
        RelativeLayout layoutToAdd = (RelativeLayout) findViewById(R.id.main_view);
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        View view = inflater.inflate(R.layout.pin_enter, null);
        layoutToAdd.addView(view);
    }

}
