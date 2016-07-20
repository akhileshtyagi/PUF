package intent_visualizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import edu.isu.reu.intent_collection_service.R;
import intent_record.IntentData;
import intent_record.IntentRecord;

public class IntentVisualizer extends AppCompatActivity {
    /** update every 5 seconds */
    final long UPDATE_INTERVAL = 5000;

    private IntentDataGLSurfaceView gl_surface_view;

    IntentRecord intent_record;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_visualizer);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gl_surface_view = new IntentDataGLSurfaceView(this);
        setContentView(gl_surface_view);

        // Initialze service connections
        this.intent_record = new IntentRecord(this);

        // update the IntentData list of the IntentGraph
        // this could be done in a separate thread
        Thread update_data = new Thread(new Runnable(){
            @Override
            public void run(){
                // get IntentData based on service
                gl_surface_view.set_intent_data(get_intent_data());

                try { Thread.sleep(UPDATE_INTERVAL); } catch(Exception e) { e.printStackTrace(); }
            }
        }) ;

        update_data.start();
    }

    /**
     * uses IntentRecord to get intent_data_list
     * from the service
     */
    private ArrayList<IntentData> get_intent_data(){
        // TODO this causes the intent visualizer to freeze durring startup
        return intent_record.receive_intent_data();
//        return null;
    }
}
