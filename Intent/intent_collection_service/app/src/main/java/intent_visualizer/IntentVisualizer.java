package intent_visualizer;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.isu.reu.intent_collection_service.R;

public class IntentVisualizer extends AppCompatActivity {
    private GLSurfaceView gl_surface_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_visualizer);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        gl_surface_view = new IntentDataGLSurfaceView(this);
        setContentView(gl_surface_view);
    }
}
