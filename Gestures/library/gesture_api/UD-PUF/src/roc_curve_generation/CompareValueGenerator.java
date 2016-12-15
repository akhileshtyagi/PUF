package roc_curve_generation;

import data.DataReader;
import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Response;
import dataTypes.UserDevicePair;
import test.graph_points;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import static java.lang.Boolean.TRUE;

/**
 * Generate a file with all the compare values
 * format:
 * "compare data", "profile data", "compare value"
 * "data_user", "data_device", "profile_user", "profile_device", "compare_value"
 *
 *
 */
public class CompareValueGenerator {
    /* output a list of [user, device, challenge, response] to this file */
    public static String UDC_OUTPUT_FILE_NAME = "src/roc_curve_generation/raw_data.csv";
    /* outputs a list of [should authenticate, compare value] to this file */
    public static String OUTPUT_FILE_NAME = "src/roc_curve_generation/compare_data.csv";
    public static String DATA_FOLDER_NAME = "src/roc_curve_generation/data/";

    /* maximum challengeID number. Given by the first integer in the file name */
    public static int MAXIMUM_SEED_NUMBER = 8;
    /* minimumchallengeID number. Given by the first integer in the file name */
    public static int MINIMUM_SEED_NUMBER = 1;
    /* minimum profile size in number of responses */
    public static int MINIMUM_PROFILE_SIZE = 10;
    /* minimum response size for authenticating against a profile in number of points */
    public static int MINIMUM_RESPONSE_SIZE = 10;
    /* display a visual representation of the authentications taking place */
    public static boolean DISPLAY_VISUAL_AUTHENTICATION = false;

    public static void main(String[] args){
        ArrayList<Boolean> positive_list = new ArrayList<>();
        ArrayList<Double> compare_value_list = new ArrayList<>();

        // generate dummy results
        //dummy_results_for_testing_r_script(positive_list, compare_value_list);

        // generate real results
        //compare_data(positive_list, compare_value_list);

        // output results to file
        //output_results(positive_list, compare_value_list);

        // read in all files from the data director and output them
        // in the format used by the r scripts
        output_data_directory();

        System.out.println("data from: " + DATA_FOLDER_NAME);
        System.out.println("output to: " + OUTPUT_FILE_NAME);
    }

    /**
     * use the data in data folder to create compare results
     * compare each data set
     *  - build a list of profiles with all but one Response trace
     *  - keep a list of this "left out" Response trace
     *  - compare every response trace against every profile
     *  - response traces compared against data generated
     *      by the same user on the same device should authenticate
     *      => positive = true
     *  - response traces compared against data generated
     *      by a different user or different device should not authenticate
     *      => positive = false
     */
    public static void compare_data(
            ArrayList<Boolean> positive_list, ArrayList<Double> compare_value_list){
        // response_list contains one response from each udc
        ArrayList<UDC> udc_list = new ArrayList<>();

        try{
            File data_folder = new File(DATA_FOLDER_NAME);

            // for each device folder
            for(File device_folder : data_folder.listFiles()){
                String device_name = device_folder.getName();

                // for each user of the device
                for(File user_folder : device_folder.listFiles()){
                    String user_name = user_folder.getName();

                    HashMap<Integer, ArrayList<Response>> response_map = new HashMap<>();
                    HashMap<Integer, List<Point>> challenge_pattern_map = new HashMap<>();
                    for(File data_file : user_folder.listFiles()) {
                        // Each file is a response to a challenge
                        // given a user_folder, create a set of
                        // challenges corresponding to that user, device
                        //
                        // ONLY FOR THINGS IN CHALLENGE_SET
                        //
                        // create a response from the data file
                        Challenge challenge = DataReader.getChallenge(data_file);

                        // add the challenge pattern to the map
                        if(challenge_pattern_map.get((int)challenge.getChallengeID()) == null){
                            // then this challenge has not been encountered yet,
                            // put the challenge points into the map
                            challenge_pattern_map.put((int)challenge.getChallengeID(),
                                    challenge.getChallengePattern());
                        }

                        // ask data reader to get the response from the file
                        Response response = DataReader.getResponse(data_file);

                        // add this response to the map
                        ArrayList<Response> response_list = response_map.get((int)challenge.getChallengeID());
                        if(response_list == null){
                            // null if no mapping for key
                            response_list = new ArrayList<>();

                            //TODO make sure getChallengeID is returning the int from the file name
                            response_map.put((int)challenge.getChallengeID(), response_list);
                        }

                        //System.out.println(response);
                        //System.out.println(response_list);

                        // in any case, I want to add the response to the response_list
                        response_list.add(response);
                    }

                    //System.out.println(response_map.entrySet().size()); //TODO

                    // for each challenge(integer) in the map, create a UDC
                    for(Map.Entry<Integer, ArrayList<Response>> response_map_entry : response_map.entrySet()){
                        //System.out.println(response_map_entry.getValue().size() == 1); //TODO

                        int challenge_name = response_map_entry.getKey();

                        // for each response to a challenge, create the UDC object
                        // with one response left out
                        //TODO should not be comparing UDC which came from the same response data
                        //TODO this code is doing that. using this mechanism does it make sense to compare
                        //TODO each UDC to itself?
                        for(int j=0; j<response_map_entry.getValue().size(); j++) {
                            // create UCD and define known quantities
                            UDC udc = new UDC();
                            udc.user = user_name;
                            udc.device = device_name;
                            udc.challenge = challenge_name;

                            // guarenteed not to be size 0, othersise the map entry would not have
                            // been created
                            // response is the current j index
                            udc.response = response_map_entry.getValue().get(j);

                            // for each response to this challenge,
                            // except for response index j
                            Challenge challenge = new Challenge(
                                    challenge_pattern_map.get(challenge_name), challenge_name);
                            for (int i = 1; i < response_map_entry.getValue().size(); i++) {
                                // start at j+1 and wrap around the list
                                // should pull n-1 responses
                                challenge.addResponse(response_map_entry.getValue()
                                        .get( (j + i) % response_map_entry.getValue().size()));
                            }

                            //System.out.println(response_map_entry.getValue().size()); //TODO

                            // add the challenge to the UserDevicePair
                            udc.ud_pair = new UserDevicePair((int) (Math.random() * 10000000));
                            udc.ud_pair.addChallenge(challenge);

                            udc_list.add(udc);
                        }
                    }
                }
            }
        }catch(Exception e){ e.printStackTrace(); }

        //System.out.println(udc_list.size()); //TODO

        // for each thing in udc_list
        // compare against all OTHER things in udc_list
        for(UDC profile_udc : udc_list){
            for(UDC response_udc : udc_list) {
                // I only want to compare profiles/ responses of the same challenge
                // and only if there were enough responses to create a profile
                if(profile_udc.challenge == response_udc.challenge &&
                        profile_udc.ud_pair.getChallenges().get(0).getResponsePattern().size() >= MINIMUM_PROFILE_SIZE &&
                        profile_udc.challenge <= MAXIMUM_SEED_NUMBER &&
                        profile_udc.challenge >= MINIMUM_SEED_NUMBER &&
                        response_udc.response.getOrigionalResponse().size() >= MINIMUM_RESPONSE_SIZE
                        ){
                    //System.out.println(String.format("profile: %s\t\tresponse: %s",
                    //        profile_udc.challenge, response_udc.challenge));

                    // are the user, device, challenge strings equal?
                    positive_list.add(profile_udc.equals(response_udc));

                    System.out.println("challenge value: " + profile_udc.ud_pair.getChallenges().get(0).getChallengeID()
                        + "\npositive: " + profile_udc.equals(response_udc)); //TODO

                    // what is the compare value
                    //TODO why is profile NormalizingPoints size == 1 sometimes??????
                    compare_value_list.add(profile_udc.ud_pair.compare(response_udc.response));

                    // display a visual representation of the authentication
                    if(DISPLAY_VISUAL_AUTHENTICATION && Math.random() < 0.1) {
                        // print other important information
                        //
                        //TODO these should be equal, why are they not?
                        //System.out.println("# normalizing points: " + profile_udc.ud_pair.getChallenges().get(0).getNormalizingPoints().size());
                        //response_udc.response.normalize(profile_udc.ud_pair.getChallenges().get(0).getNormalizingPoints());
                        //System.out.println("# normalized response points: " + response_udc.response.getNormalizedResponse().size());
                        System.out.println("profile_mu: " + profile_udc.ud_pair.getChallenges().get(0).getProfile().getMuSigmaValues(Point.Metrics.PRESSURE).getMuValues());
                        System.out.println("profile_sigma: " + profile_udc.ud_pair.getChallenges().get(0).getProfile().getMuSigmaValues(Point.Metrics.PRESSURE).getSigmaValues());

                        // display the visual
                        create_visual_authentication(profile_udc.ud_pair.getChallenges().get(0), response_udc.response);
                        //create_visual_authentication(profile_udc.ud_pair.getChallenges().get(0),
                        //        profile_udc.ud_pair.getChallenges().get(0).getResponsePattern().get(0));
                    }
                }
            }
        }
    }

    /**
     * this class represents a
     * (user, device, challenge, UserDevicePair set
     *
     * this is useful because I only want to compare
     * within the same challenge
     */
    private static class UDC{
        public String user;
        public String device;
        public int challenge;

        // profile for user, device, challenge
        public UserDevicePair ud_pair;

        // response not included in the profile
        public Response response;

        @Override
        public boolean equals(Object o){
            UDC other = (UDC)o;

            return  (this.user.equals(other.user)) &&
                    (this.device.equals(other.device)) &&
                    (this.challenge == other.challenge);
        }
    }

    /**
     * output data directory in the format
     * user, device, challenge, response
     */
    private static void output_data_directory(){
        // acquire the list of udc
        List<UDC> udc_list = get_udc_list();

        // print the list of udc
        String header = "\"user\", \"device\", \"challenge\", \"response\"";

        try{
            PrintWriter file = new PrintWriter(UDC_OUTPUT_FILE_NAME, "UTF-8");

            file.println(header);

            for(int i=0; i<udc_list.size(); i++){
                String user = udc_list.get(i).user;
                String device = udc_list.get(i).device;
                String challenge = String.valueOf(udc_list.get(i).challenge);

                // there is a UDC for each response to a challenge
                String response = udc_list.get(i).response.toRString();

                file.println(user + ", " + device +
                        ", " + challenge + ", \"" + response + "\"");
            }

            file.close();
        }catch(Exception e){ e.printStackTrace(); }
    }

    /**
     * return a list of UDC given the data folder name
     */
    private static List<UDC> get_udc_list(){
        ArrayList<UDC> udc_list = new ArrayList<>();

        try{
            File data_folder = new File(DATA_FOLDER_NAME);

            // for each device folder
            for(File device_folder : data_folder.listFiles()){
                String device_name = device_folder.getName();

                // for each user of the device
                for(File user_folder : device_folder.listFiles()){
                    String user_name = user_folder.getName();

                    HashMap<Integer, ArrayList<Response>> response_map = new HashMap<>();
                    HashMap<Integer, List<Point>> challenge_pattern_map = new HashMap<>();
                    for(File data_file : user_folder.listFiles()) {
                        // Each file is a response to a challenge
                        // given a user_folder, create a set of
                        // challenges corresponding to that user, device
                        //
                        // ONLY FOR THINGS IN CHALLENGE_SET
                        //
                        // create a response from the data file
                        Challenge challenge = DataReader.getChallenge(data_file);

                        // add the challenge pattern to the map
                        if(challenge_pattern_map.get((int)challenge.getChallengeID()) == null){
                            // then this challenge has not been encountered yet,
                            // put the challenge points into the map
                            challenge_pattern_map.put((int)challenge.getChallengeID(),
                                    challenge.getChallengePattern());
                        }

                        // ask data reader to get the response from the file
                        Response response = DataReader.getResponse(data_file);

                        // add this response to the map
                        ArrayList<Response> response_list = response_map.get((int)challenge.getChallengeID());
                        if(response_list == null){
                            // null if no mapping for key
                            response_list = new ArrayList<>();

                            //TODO make sure getChallengeID is returning the int from the file name
                            response_map.put((int)challenge.getChallengeID(), response_list);
                        }

                        //System.out.println(response);
                        //System.out.println(response_list);

                        // in any case, I want to add the response to the response_list
                        response_list.add(response);
                    }

                    //System.out.println(response_map.entrySet().size()); //TODO

                    // for each challenge(integer) in the map, create a UDC
                    for(Map.Entry<Integer, ArrayList<Response>> response_map_entry : response_map.entrySet()){
                        //System.out.println(response_map_entry.getValue().size() == 1); //TODO

                        int challenge_name = response_map_entry.getKey();

                        // for each response to a challenge, create the UDC object
                        // with one response left out
                        //
                        //TODO should not be comparing UDC which came from the same response data
                        //TODO this code is doing that. using this mechanism does it make sense to compare
                        //TODO each UDC to itself?
                        //
                        // outputting to a data file uses the fact that there
                        // is a UDC for each response
                        for(int j=0; j<response_map_entry.getValue().size(); j++) {
                            // create UCD and define known quantities
                            UDC udc = new UDC();
                            udc.user = user_name;
                            udc.device = device_name;
                            udc.challenge = challenge_name;

                            // guarenteed not to be size 0, othersise the map entry would not have
                            // been created
                            // response is the current j index
                            udc.response = response_map_entry.getValue().get(j);

                            // no challenge in this one
                            // I don't want the responses getting normalized, just in case
                            // there are any deep copy issues in play
                            //
                            // for each response to this challenge,
                            // except for response index j
                            /*
                            Challenge challenge = new Challenge(
                                    challenge_pattern_map.get(challenge_name), challenge_name);
                            for (int i = 1; i < response_map_entry.getValue().size(); i++) {
                                // start at j+1 and wrap around the list
                                // should pull n-1 responses
                                challenge.addResponse(response_map_entry.getValue()
                                        .get( (j + i) % response_map_entry.getValue().size()));
                            }

                            //System.out.println(response_map_entry.getValue().size()); //TODO

                            // add the challenge to the UserDevicePair
                            udc.ud_pair = new UserDevicePair((int) (Math.random() * 10000000));
                            udc.ud_pair.addChallenge(challenge);
                            */

                            udc_list.add(udc);
                        }
                    }
                }
            }
        }catch(Exception e){ e.printStackTrace(); }

        return udc_list;
    }

    /**
     * given a challenge, response
     * create a visual depicting the
     * origional point, challenge points, normalization points
     */
    private static void create_visual_authentication(Challenge challenge_p, Response response_p){
        graph_points graph_frame = new graph_points();

        // create response point list
        List<Point> response_points = response_p.getOrigionalResponse();

        // get the list of normalizing points
        List<Point> np_list = challenge_p.getNormalizingPoints();

        // create a response and normalize it
        Response response = new Response(response_points);

        graph_frame.addPointList(response_points, "origional_response_points");
        response.normalize(np_list);
        graph_frame.addPointList(np_list, "normalizing_points");
        graph_frame.addPointList(response.getNormalizedResponse(), "normalized_response_points");

        // print out response and normalized response
        System.out.println("origional_response:\t" + response.getOrigionalResponse());
        System.out.println("normalizing_points:\t" + np_list);
        System.out.println("normalized_response:\t" + response.getNormalizedResponse());

        // wait for the user to press "ENTER"
        try{ System.in.read(); }catch(Exception e){ e.printStackTrace(); }

        // close the frame
        graph_frame.dispose();
    }

    /**
     * output the results to file
     */
    public static void output_results(
            ArrayList<Boolean> positive_list, ArrayList<Double> compare_value_list){
        // positive is 1 if the compare value came from a user which should authenticate
        String header = "\"positive\", \"compare_value\"";

        try{
            PrintWriter file = new PrintWriter(OUTPUT_FILE_NAME, "UTF-8");

            file.println(header);

            for(int i=0; i<positive_list.size(); i++){
                String positive_string = positive_list.get(i) == TRUE ? "1" : "0";
                String compare_value_string = String.valueOf(compare_value_list.get(i));

                file.println(positive_string + ", " + compare_value_string);
            }

            file.close();
        }catch(Exception e){ e.printStackTrace(); }
    }

    /**
     * generate some dummy results
     */
    public static void dummy_results_for_testing_r_script(
            ArrayList<Boolean> positive_list, ArrayList<Double> compare_value_list){
        int test_data_size = 10;
        Random random = new Random();

        for(int i=0; i<test_data_size; i++){
            positive_list.add(random.nextInt()%2 == 0);
            compare_value_list.add(random.nextDouble());
        }
    }
}
