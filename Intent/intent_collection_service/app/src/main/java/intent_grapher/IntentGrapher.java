package intent_grapher;

import java.util.ArrayList;
import java.util.List;

import intent_record.IntentData;

/**
 * The purpose of this class is to graph
 * intents visually.
 *
 * Each entity which can send intents (activities and services)
 * will be represented as a node
 *
 * Every entent will be represented by a number of edges
 * (could also use an edge with a number over it.)
 *
 * Edges are directed with
 * an arrow pointing from sender to receiver
 */
public class IntentGrapher {
    private List<IntentData> intent_data_list;

    public IntentGrapher(){
        intent_data_list = new ArrayList<>();

        // initialize graphics
        //TODO
    }

    /**
     * set intent data list,
     * graphical depiction will be updated
     */
    public void set_intent_data_list(List<IntentData> intent_data_list){
        this.intent_data_list = intent_data_list;

        graphical_update();
    }

    /**
     * allow adding of single intents for incremental update
     */
    public void add_intent_data(IntentData intent_data){
        this.intent_data_list.add(intent_data);

        graphical_update();
    }

    /**
     * allow adding of a list of intents for incremental update
     */
    public void add_intent_data_list(List<IntentData> intent_data_list){
        this.intent_data_list.addAll(intent_data_list);

        graphical_update();
    }

    /**
     * preform graphical update operations
     *
     * this happens after the intent_data_list is changed
     */
    private void graphical_update(){
        // update graphical display
        //TODO
    }
}
