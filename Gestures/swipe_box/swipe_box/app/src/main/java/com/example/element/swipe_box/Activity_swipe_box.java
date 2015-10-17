package com.example.element.swipe_box;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Activity_swipe_box extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_swipe_box);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tv = (TextView) findViewById(R.id.point_text_view);

        PufDrawView pufDrawView = (PufDrawView) findViewById(R.id.puf_draw_view);
        pufDrawView.setTV(tv);

        ArrayList<Point> challenge = generateChallenge();
        pufDrawView.giveChallenge(challenge.toArray(new Point[challenge.size()]));
    }

    /**
     * never return null from this function.... it will cause PUF DRAW VIEW to break
     */
    private ArrayList<Point> generateChallenge(){
        ArrayList<Point> challenge_list = new ArrayList<Point>();

        challenge_list.addAll(create_box_challenge(750, 300, 100, 100));

        return challenge_list;
    }

    /**
     * create a box given the x_size, y_size, x_offset, and y_offset.
     * x,y offset begin at 0,0 in the upper left corner of the screen
     */
    private List<Point> create_box_challenge(int x_size, int y_size, int x_offset, int y_offset){
        ArrayList box_corner_list = new ArrayList<Point>();

        box_corner_list.add(new Point(x_offset,y_offset,0));
        box_corner_list.add(new Point(x_offset+x_size,y_offset,0));
        box_corner_list.add(new Point(x_offset+x_size,y_offset + y_size,0));
        box_corner_list.add(new Point(x_offset,y_offset + y_size,0));
        box_corner_list.add(new Point(x_offset,y_offset,0));

        return box_corner_list;
    }

}
