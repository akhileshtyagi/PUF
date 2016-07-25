package intent_analysis;

import intent_record.IntentData;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides many
 * public static methods
 * which allow the user to pass in a
 * List<IntentData> and receive some analysis about it
 */
public class IntentAnalytics {
    public enum Statistic {
        COUNT,
        UNIQUE_ENTITIES,
        UNIQUE_SENDERS,
        UNIQUE_RECEIVERS
    }

    public static double analyze(List<IntentData> intent_data_list, Statistic statistic){
        double value;

        switch(statistic){
            case COUNT:
                value = intent_data_list.size();
                break;
            case UNIQUE_ENTITIES:
                value = unique_entities(intent_data_list).size();
                break;
            case UNIQUE_SENDERS:
                value = unique_senders(intent_data_list).size();
                break;
            case UNIQUE_RECEIVERS:
                value = unique_receivers(intent_data_list).size();
                break;
            default:
                value = -1;
        }

        return value;
    }

    public static String string_value(Statistic statistic){
        String value;

        switch(statistic){
            case COUNT:
                value = "number of intents";
                break;
            case UNIQUE_ENTITIES:
                value = "unique entities";
                break;
            case UNIQUE_SENDERS:
                value = "unique senders";
                break;
            case UNIQUE_RECEIVERS:
                value = "unique receivers";

                break;
            default:
                value = "statistic not found";
        }

        return value;
    }

    /**
     * return a list of unique senders in the input list
     */
    public static List<String> unique_senders(List<IntentData> intent_data_list){
        ArrayList<String> string_list = new ArrayList<>();

        // create a list of senders
        for(IntentData intent_data : intent_data_list){
            string_list.add(intent_data.get_sender());
        }

        // return the unique values from this list
        return unique_string(string_list);
    }

    /**
     * return a list of unique receivers in the input list
     */
    public static List<String> unique_receivers(List<IntentData> intent_data_list){
        ArrayList<String> string_list = new ArrayList<>();

        // create a list of receivers
        for(IntentData intent_data : intent_data_list){
            string_list.add(intent_data.get_receiver());
        }

        // return the unique values from this list
        return unique_string(string_list);
    }

    /**
     * return a list of unique entities (sender or receiver) in the input list
     */
    public static List<String> unique_entities(List<IntentData> intent_data_list){
        ArrayList<String> unique_entity_list = new ArrayList<>();

        // get the unique senders, unique_receivers
        unique_entity_list.addAll(unique_senders(intent_data_list));
        unique_entity_list.addAll(unique_receivers(intent_data_list));

        // now take the unique values between those
        return unique_string(unique_entity_list);
    }

    /**
     * return a list of uniuqe strings in the list passed in
     */
    private static List<String> unique_string(List<String> string_list){
        // stuff is only added to this list if it is not in the list
        List<String> unique_string_list = new ArrayList<>();

        for(String string : string_list){
            // if the unique string list doesn't already contain this string,
            if(!list_contains(unique_string_list, string)){
                // add the string to the unique string list
                unique_string_list.add(string);
            }
        }

        return unique_string_list;
    }

    /**
     * returns true if the list contains the given string
     */
    private static boolean list_contains(List<String> string_list, String string){
        // if the string is within the string list return true
        for(String s : string_list){
            if(s.equals(string)){
                return true;
            }
        }

        // if the string was not found in the string list return false
        return false;
    }

    /**
     * determine the number of intents directed
     * from sender
     * to receiver
     */
    public static int intent_count(List<IntentData> intent_data_list, String sender, String receiver){
        List<IntentData> sender_intent_data_list = new ArrayList<>();

        // create a list of IntentsData sent by sender to receiver
        for(IntentData intent_data : intent_data_list) {
            // filter out things not sent by sender
            if(intent_data.get_sender().equals(sender)) {
                // filter out things not received by receiver
                if(intent_data.get_receiver().equals(receiver)) {
                    sender_intent_data_list.add(intent_data);
                }
            }
        }

        // this list now contains all intents directed from sender to receiver
        return sender_intent_data_list.size();
    }
}
