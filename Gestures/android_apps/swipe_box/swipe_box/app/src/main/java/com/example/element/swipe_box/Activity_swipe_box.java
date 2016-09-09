package com.example.element.swipe_box;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Activity_swipe_box extends AppCompatActivity {
    public static final String TAG = "Activity Swipe Box";

    public enum ChallengeType {
        BOX, BIG_SQUIGGLE, CHECK, TRIANGLE, SHIFT_TRIANGLE,
        WORM, CIRCLE, HEXAGON, SPIRAL,
        LINE_HORIZONTAL, LINE_VERTICAL, LINE_315,
        SHIFT_LINE_HORIZONTAL, SHIFT_LINE_VERTICAL, SHIFT_LINE_315;
    }

    //public final ChallengeType CHALLENGE_TYPE= ChallengeType.CHECK;

    PufDrawView pufDrawView;
    private int box_width;
    private int box_height;
    private Point box_upper_left_corner;

    private ChallengeType challenge_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_swipe_box);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView tv = (TextView) findViewById(R.id.point_text_view);

        pufDrawView = (PufDrawView) findViewById(R.id.puf_draw_view);
        pufDrawView.setTV(tv);

        // get the box from the put_extra from the intent
        this.box_height = this.getIntent().getIntExtra("box_height", 0);
        this.box_width = this.getIntent().getIntExtra("box_width", 0);
        this.box_upper_left_corner = (Point)(this.getIntent().getExtras().getSerializable("box_upper_left_corner"));

        ArrayList<Point> challenge = new ArrayList<>();

        // defines how much the shift challenges are shifted
        float shift_x = 100.0f;
        float shift_y = 100.0f;

        // get the challenge type from the extra
        this.challenge_type = (ChallengeType)(this.getIntent().getExtras().getSerializable("challenge_type"));
        switch(this.challenge_type){
            case CHECK:
                challenge = generateCheckChallenge();
                break;
            case BIG_SQUIGGLE:
                challenge = generateSquiggleChallenge();
                break;
            case TRIANGLE:
                challenge = generateTriangleChallenge();
                break;
            case SHIFT_TRIANGLE:
                challenge = shift_point_list(generateTriangleChallenge(), shift_x, shift_y);
                break;
            case WORM:
                challenge = generateWormChallenge();
                break;
            case CIRCLE:
                challenge = generateCircleChallenge();
                break;
            case HEXAGON:
                challenge = generateHexagonChallenge();
                break;
            case SPIRAL:
                challenge = generateSpiralChallenge();
                break;
            case LINE_HORIZONTAL:
                challenge = generateLineHorizontalChallenge();
                break;
            case LINE_VERTICAL:
                challenge = generateLineVerticalChallenge();
                break;
            case LINE_315:
                challenge = generateLine315Challenge();
                break;
            case SHIFT_LINE_HORIZONTAL:
                challenge = shift_point_list(generateLineHorizontalChallenge(), shift_x, shift_y);
                break;
            case SHIFT_LINE_VERTICAL:
                challenge = shift_point_list(generateLineVerticalChallenge(), shift_x, shift_y);
                break;
            case SHIFT_LINE_315:
                challenge = shift_point_list(generateLine315Challenge(), shift_x, shift_y);
                break;
            default:
                challenge = generateChallenge();
                break;
        }

        Log.d("challenge: ", challenge.toString());
        pufDrawView.giveChallenge(challenge.toArray(new Point[challenge.size()]));
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        // test purposes
        test_responses();
        data.putExtra("response", pufDrawView.get_response());
        data.putExtra("challenge_type", this.challenge_type);
        setResult(RESULT_OK, data);

        super.finish();
    }

    /**
     * test the responses as they come back. This method is used for test purposes.
     */
    private void test_responses(){
        //collect all of the time and print
        ArrayList<Double> time_list = new ArrayList<Double>();

        for(Point response_point : pufDrawView.get_response()) {
            time_list.add(response_point.time);
        }

        Log.d("response", time_list.toString());
    }

    /**
     * never return null from this function.... it will cause PUF DRAW VIEW to break
     */
    private ArrayList<Point> generateChallenge(){
        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // width = 750, height = 300 seems to work fine
        challenge_list.addAll(create_box_challenge(box_width, box_height, (int) (box_upper_left_corner.x), (int) (box_upper_left_corner.y)));

        return challenge_list;
    }

    /**
     * Possibly a^2 / 16 to maximuze variance
     * a being the size of the screen
     *
     * create a box given the x_size, y_size, x_offset, and y_offset.
     * x,y offset begin at 0,0 in the upper left corner of the screen
     */
    private List<Point> create_box_challenge(int x_size, int y_size, int x_offset, int y_offset){
        ArrayList box_corner_list = new ArrayList<Point>();

        box_corner_list.add(new Point(x_offset,y_offset,0,0));
        box_corner_list.add(new Point(x_offset+x_size,y_offset,0,0));
        box_corner_list.add(new Point(x_offset+x_size,y_offset + y_size,0,0));
        box_corner_list.add(new Point(x_offset,y_offset + y_size,0,0));
        box_corner_list.add(new Point(x_offset, y_offset, 0, 0));

        return box_corner_list;
    }

    /**
     * generates a big squiggle. This is meant to emulate the way NG will use this.
     */
    private ArrayList<Point> generateSquiggleChallenge(){
        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int bounding_box_width = (int)(metrics.widthPixels * .7);
        int bounding_box_height = (int)(metrics.heightPixels * 6 / 8);

        // width = 750, height = 300 seems to work fine
        //challenge_list.addAll(create_box_challenge(box_width, box_height, (int) (box_upper_left_corner.x), (int) (box_upper_left_corner.y)));
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y, 0, 0));
        challenge_list.add(new Point(this.box_upper_left_corner.x + (bounding_box_width / 3), box_upper_left_corner.y + (bounding_box_height * 3 / 4), 0, 0));
        challenge_list.add(new Point(this.box_upper_left_corner.x+ (bounding_box_width * 2 / 3), box_upper_left_corner.y + (bounding_box_height / 4), 0, 0));
        challenge_list.add(new Point(this.box_upper_left_corner.x + bounding_box_width, box_upper_left_corner.y + bounding_box_height, 0, 0));

        return challenge_list;
    }

    private ArrayList<Point> generateCheckChallenge(){
        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int bounding_box_width = (int)(metrics.widthPixels / 4);
        int bounding_box_height = (int)(metrics.heightPixels / 4);

        // width = 750, height = 300 seems to work fine
        //challenge_list.addAll(create_box_challenge(box_width, box_height, (int) (box_upper_left_corner.x), (int) (box_upper_left_corner.y)));
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y, 0, 0));
        challenge_list.add(new Point(this.box_upper_left_corner.x + (bounding_box_width / 2), box_upper_left_corner.y + bounding_box_height , 0, 0));
        challenge_list.add(new Point(this.box_upper_left_corner.x+ bounding_box_width, box_upper_left_corner.y + (bounding_box_height * 2 / 3), 0, 0));
        challenge_list.add(new Point(this.box_upper_left_corner.x+ bounding_box_width, box_upper_left_corner.y + (bounding_box_height * 2 / 3), 0, 0));

        return challenge_list;
    }

    /**
     * generate challenges
     */
    private ArrayList<Point> generateTriangleChallenge(){
        // define some properties of the shape
        double angle_increment = 60;
        double angle = 270;

        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int bounding_box_width = (int)(metrics.widthPixels / 4);
        int bounding_box_height = (int)(metrics.heightPixels / 5);

        // create a list of challenge points
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y, 0, 0));

        // down
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_height, angle));

        // up and right
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_height, 30));

        // up and left
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_height, 150));

        // back to beginning
        //challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y, 0, 0));

        return challenge_list;
    }

    private ArrayList<Point> generateWormChallenge(){
        // define some properties of the shape
        double angle_increment = 90;
        double angle = 270;

        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int bounding_box_width = (int)(metrics.widthPixels / 2);
        int bounding_box_height = (int)(metrics.heightPixels / 4);

        // create a list of challenge points
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y, 0, 0));

        // down
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_height, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_width / 2, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // up
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_height, angle));
        angle -= angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // right
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_width / 2, angle));
        angle -= angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // down
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_height, angle));

        return challenge_list;
    }

    private ArrayList<Point> generateCircleChallenge(){
        double angle = 270;

        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int bounding_box_width = (int)(metrics.widthPixels / 4);
        int bounding_box_height = (int)(metrics.heightPixels);

        // create a list of challenge points
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y + bounding_box_height / 4, 0, 0));

        // build the circle in small increments
        int parts = 360;
        float angle_increment = 360.0f / parts;
        for(int i = 0; i < parts; i++) {
            challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size() - 1), (float)bounding_box_height / parts, angle));
            angle += angle_increment;
        }

        return challenge_list;
    }

    private ArrayList<Point> generateHexagonChallenge(){
        double angle = 270;

        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int bounding_box_width = (int)(metrics.widthPixels / 4);
        int bounding_box_height = (int)(metrics.heightPixels);

        // create a list of challenge points
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y + bounding_box_height / 4, 0, 0));

        // build the circle in small increments
        int parts = 6;
        float angle_increment = 360.0f / parts;
        for(int i = 0; i < parts; i++) {
            challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size() - 1), (float)bounding_box_height / parts, angle));
            angle += angle_increment;
        }

        return challenge_list;
    }

    private ArrayList<Point> generateSpiralChallenge(){
        // define some properties of the shape
        double angle_increment = 90;
        double angle = 270;

        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        float ratio = .6f;
        int bounding_box_width = (int)(metrics.widthPixels * ratio);
        int bounding_box_height = (int)(metrics.heightPixels * ratio);

        // create a list of challenge points
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y, 0, 0));

        // down
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_height, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // right
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_width, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // describe the amount the width, height shrink
        float shrink_ratio = .66f;

        float width = bounding_box_width * shrink_ratio;
        float height = bounding_box_height * shrink_ratio;

        // up
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), height, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // right
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), width, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // modify ratio
        width = width * shrink_ratio;
        height = height * shrink_ratio;

        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), height, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // right
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), width, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // modify ratio
        width = width * shrink_ratio;
        height = height * shrink_ratio;

        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), height, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        // right
        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), width, angle));
        angle += angle_increment;
        // ensure angle stays between 0 and 360
        angle = (angle > 360.0) ? (angle - 360.0) : angle;
        angle = (angle < 0.0) ? (angle + 360.0) : angle;

        return challenge_list;
    }

    private ArrayList<Point> generateLineHorizontalChallenge(){
        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int bounding_box_width = (int)(metrics.widthPixels / 2);
        int bounding_box_height = (int)(metrics.heightPixels / 2);

        // create a list of challenge points
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y, 0, 0));

        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_width, 0));

        return challenge_list;
    }

    private ArrayList<Point> generateLineVerticalChallenge(){
        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int bounding_box_width = (int)(metrics.widthPixels / 2);
        int bounding_box_height = (int)(metrics.heightPixels / 2);

        // create a list of challenge points
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y, 0, 0));

        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), bounding_box_height, 270));

        return challenge_list;
    }

    private ArrayList<Point> generateLine315Challenge(){
        ArrayList<Point> challenge_list = new ArrayList<Point>();

        // get some properties of the screen
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int bounding_box_width = (int)(metrics.widthPixels / 2);
        int bounding_box_height = (int)(metrics.heightPixels / 2);

        // create a list of challenge points
        challenge_list.add(new Point(this.box_upper_left_corner.x, box_upper_left_corner.y, 0, 0));

        challenge_list.add(generate_next_line_point(challenge_list.get(challenge_list.size()-1), (bounding_box_width + bounding_box_height) / 2, 315));

        return challenge_list;
    }

    /**
     * helper methods for generating challenges
     */
    /**
     * returns the next point in a line given beginning, length and angle
     */
    private Point generate_next_line_point(Point beginning, float distance, double angle){
        // use distance and angle to determine next point from beginning
        float x = beginning.x + (float)(distance * Math.cos(Math.toRadians(angle)));
        float y = beginning.y - (float)(distance * Math.sin(Math.toRadians(angle)));

        //Log.d(TAG, "sqrt x^2 + y^2: " + Math.sqrt((x - beginning.x)*(x - beginning.x) + (y - beginning.y)*(y - beginning.y)) + "\tdistance: " +distance);

        float pressure = 0.0f;
        float time = 0.0f;

        return new Point(x, y, pressure, time);
    }

    /**
     * returns an a set of points shifted by x, y from the given set of points
     */
    private ArrayList<Point> shift_point_list(ArrayList<Point> point_list, float shift_x, float shift_y){
        for(Point point : point_list){
            point.x += shift_x;
            point.y += shift_y;
        }

        return point_list;
    }
}
