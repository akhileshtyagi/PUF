package opengl_artifacts;

import java.util.ArrayList;

import intent_record.IntentData;

/**
 * Visual representation of all the intents
 * capable of drawing itself
 */
public class IntentGraph implements Drawable {
    /** constants to control the look and feel */
    final static float ARROW_WIDTH = 0.5f;

    final static String TAG = "IntentGraph";

    /** contains all the components */
    private ArrayList<Drawable> component_list;

    /** contains intent data to be graphed */
    public volatile ArrayList<IntentData> intent_data_list;

    /**
     * initialize all shapes needed to draw the graph
     *
     * (there may be none if they are
     * initialized a run time based on IntentData)
     */
    public IntentGraph(){
        this.component_list = new ArrayList<>();

        // create an Arrow
        float height = 0.05f;
        float width = 0.5f;
        float center_x = 0.0f;
        float center_y = 0.0f;
        //this.component_list.add(new Arrow(height, width, center_x, center_y));

        // create arrow with two vertexes
        this.component_list.add(new Arrow(ARROW_WIDTH,
                new Vertex(0.0f, 0.0f, 0.0f),
                new Vertex(1.0f, 0.0f, 0.0f)
        ));
    }

    /**
     * Add new necessary shapes to the component_list
     *
     * Call the draw method of all the component shapes
     */
    public void draw(float[] mvpMatrix) {
        // draw all components
        for(Drawable component : component_list){
            component.draw(mvpMatrix);
        }

        //if(intent_data_list != null) Log.d(TAG, intent_data_list.toString());
    }

    /**
     * set the intent data which will be graphed
     */
    public void set_intent_data(ArrayList<IntentData> intent_data_list){
        this.intent_data_list = intent_data_list;

        update_component_list();
    }

    /**
     * update the component_list based on intent_data_list
     */
    private void update_component_list(){
        this.component_list = new ArrayList<>();

        for(IntentData intent_data : intent_data_list){
            this.component_list.add(create_component_from_intent_data(intent_data));
        }
    }

    /**
     * create a component representation from the provided intent data.
     */
    private Drawable create_component_from_intent_data(IntentData intent_data){
        // it might be useful to associate a location with each intent data
        //TODO
        return null;
    }
}
