package roc_curve_generation;

import data.DataReader;
import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Response;
import dataTypes.UserDevicePair;
import org.python.core.PyArray;
import org.python.core.PyList;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;
import test.graph_points;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static dataTypes.Response.PYTHON_UTIL_DIRECTORY;
import static dataTypes.Response.setDefaultPythonPath;
import static java.lang.Boolean.TRUE;
import static java.lang.String.valueOf;

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
    /* output a list of [user, device, challenge, response] which has been normalized */
    public static String NORMALIZED_UCD_OUTPUT_FILE_NAME = "src/roc_curve_generation/normalized_data.csv";
    /* output quantized version */
    public static String QUANTIZED_UCD_OUTPUT_FILE_NAME = "src/roc_curve_generation/quantized_data.csv";
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
        //TODO uncomment if necessary
        output_data_directory(UDC_OUTPUT_FILE_NAME, false, false);
        output_data_directory(NORMALIZED_UCD_OUTPUT_FILE_NAME, true, false);
        output_data_directory(QUANTIZED_UCD_OUTPUT_FILE_NAME , true, true);

        System.out.println("data from: " + DATA_FOLDER_NAME);
        System.out.println("output to: " + UDC_OUTPUT_FILE_NAME);
        System.out.println("output to: " + NORMALIZED_UCD_OUTPUT_FILE_NAME);
        System.out.println("output to: " + QUANTIZED_UCD_OUTPUT_FILE_NAME );
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

        // the challenge points associated with challenge
        public List<Point> challenge_point_list;

        @Override
        public boolean equals(Object o){
            UDC other = (UDC)o;

            return  (this.user.equals(other.user)) &&
                    (this.device.equals(other.device)) &&
                    (this.challenge == other.challenge);
        }

        @Override
        public String toString(){
            String s = "";

            s += user + "_";
            s += device + "_";
            s += challenge;

            return s;
        }
    }

    /**
     * output data directory in the format
     * user, device, challenge, response
     */
    private static void output_data_directory(
            String output_file_name, boolean normalize_udc, boolean quantized_output){
        // acquire the list of udc
        List<UDC> udc_list = get_udc_list();

        // print the list of udc
        String header = "\"user\", \"device\", \"challenge\", \"response\"";

        // normalize the udc if necessary
        //TODO make sure udc.challenge_point_list is valid
        //TODO check that there are 72 challenges created
        //TODO check that Challenge.computeNormalizingPoints still works
        if(normalize_udc){
            // for UDC == UDC,
            // normalize all responses using the same challenge
            //
            // it matters what is the first one added to the challenge
            // give the longest udc as the first in a challenge
            //
            // 1. create a challenge for each udc type
            // 2. keep track of the longest udc for each challenge
            HashMap<String, Challenge> challenge_map = new HashMap<>();
            HashMap<Challenge, UDC> longest_response_map = new HashMap<>();
            for(int i=0; i<udc_list.size(); i++) {
                // create a challenge for this udc
                // if one does not already exist
                Challenge challenge = challenge_map.get(udc_list.get(i).toString());

                // if there is not a challenge
                if(challenge == null){
                    // create a challenge
                    challenge_map.put(udc_list.get(i).toString(),
                            new Challenge(udc_list.get(i).challenge_point_list,
                                    udc_list.get(i).challenge));
                }

                // if this udc is the longest for this
                // challenge, set it in the longest_response_map
                UDC current_longest_udc = longest_response_map.get(challenge);
                if(current_longest_udc == null ||
                        udc_list.get(i).response.getOrigionalResponse().size() >
                        current_longest_udc.response.getOrigionalResponse().size()){
                    // then this udc is the longest
                    longest_response_map.put(challenge, udc_list.get(i));
                }
            }

            // the longest udc will get added to each challenge
            // twice. I think this is fine.
            //
            // add the longest udc's of each challenge type
            // to their corresponding challenge first
            for(Challenge challenge: challenge_map.values()){
                challenge.addResponse(longest_response_map.get(challenge).response);
            }

            // add all responses to the corresponding challenge
            for(int i=0; i<udc_list.size(); i++) {
                Challenge challenge = challenge_map.
                        get(udc_list.get(i).toString());

                //add udc.response to the appropriate challenge
                challenge.addResponse(udc_list.get(i).response);
            }
        }

        try{
            PrintWriter file = new PrintWriter(output_file_name, "UTF-8");

            file.println(header);

            for(int i=0; i<udc_list.size(); i++){
                String user = udc_list.get(i).user;
                String device = udc_list.get(i).device;
                String challenge = valueOf(udc_list.get(i).challenge);

                // there is a UDC for each response to a challenge
                String response;
                if(!normalize_udc) {
                    // origional response is being output
                    response = udc_list.get(i).response.toRString();
                }else if(quantized_output) {
                    // quantized response is being output
                    // only happens if response is also normalized
                    response = bit_set_to_string(udc_list.get(i).response.quantize());
                }else{
                    // ask for the normalized R string
                    // normalized response is being output
                    response = udc_list.get(i).response.toNormalizedRString();
                }

                file.println(user + ", " + device +
                        ", " + challenge + ", \"" + response + "\"");
            }

            file.close();
        }catch(Exception e){ e.printStackTrace(); }

        //TODO analyze hamming distance
        //TODO analyze PRG properties with TESTU01
        // print the average hamming distance if responses are quantized
        if(normalize_udc && quantized_output){
            print_results(udc_list);
            //print_average_hamming(udc_list);
            //print_testu01_results(udc_list);
        }
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

                            // record the challenge points in the UDC
                            udc.challenge_point_list = challenge_pattern_map.get(challenge_name);

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
                String compare_value_string = valueOf(compare_value_list.get(i));

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

    /**
     * turn a BitSet into a csv String of bits
     */
    public static String bit_set_to_string(BitSet bit_set){
        String s = "";

        for(int i=0; i<bit_set.length(); i++){
            if(bit_set.get(i)){
                s += "1";
            }else{
                s += "0";
            }

            // on the last iteraction, I don't want a ,
            if(bit_set.length()-1 != i){
                s += ",";
            }
        }

        return s;
    }

    /**
     * master method
     * print results from UDC list
     */
    public static void print_results(List<UDC> udc_list){
        long start_time = System.nanoTime();

        //TODO uncomment
        print_testu01_results(udc_list);
        print_average_hamming(udc_list);

        long end_time = System.nanoTime();
        long duration_micros = (end_time - start_time) / 1000;

        System.out.println("total microseconds: " + duration_micros);
    }

    /**
     * print the results of running the respones through
     * the TESTU01 suite
     */
    //TODO make sure this method actually works
    public static void print_testu01_results(List<UDC> udc_list){
        //TODO change this method to run
        //TODO TESTU01_output for each (user,device,challenge)
        //TODO each one should put one file in the input directory

        // create a map of <"user_device_challenge", List<response_string>>
        Map<String, List<String>> response_map = new HashMap<>();

        for(int i=0; i<udc_list.size(); i++){
            // good, responses are 128 bit
            //System.out.println(udc_list.get(i).response.quantize());

            String response_string = bit_set_to_string_no_comma(
                    udc_list.get(i).response.quantize());

            // add response to list in map
            // this will create a new list if one does not exist
            List<String> list = response_map.getOrDefault(udc_list.get(i).toString(), new ArrayList<>());
            list.add(response_string);
            response_map.putIfAbsent(udc_list.get(i).toString(), list);
        }

        // iterate over the map
        // this will make one file for each (user, device, challenge)
        for(Map.Entry<String, List<String>> entry : response_map.entrySet()) {
            // now run TestU01 on the response set
            // this is done by calling a python script
            //
            // create properties to change the system path to python scripts director
            Properties properties = setDefaultPythonPath(System.getProperties(), PYTHON_UTIL_DIRECTORY);

            // create a Python Intrepeter for running python functions in util.py
            PythonInterpreter interpreter = new PythonInterpreter();
            interpreter.initialize(System.getProperties(), properties, new String[0]);
            interpreter.exec("from test1_util import TESTU01_output");

            // call the hamming function to compute average
            // string_array should be List<String> with
            // each element of the list one response by a (user, device, challenge)
            PyObject function = interpreter.get("TESTU01_output");
            function.__call__(new PyList(entry.getValue()), new PyString(entry.getKey()));
        }
    }

    /**
     * print the average hamming diatance
     */
    //TODO make sure this method actually works
    public static void print_average_hamming(List<UDC> udc_list){
        ExecutorService cached_pool = Executors.newCachedThreadPool();
        Collection<Future<?>> task_list = new LinkedList<>();

        // create condition_map which
        // maps a condition of user,device,challenge same to
        // a map from udc.toString to hamming distance for that udc
        Map<String, Map<String, List<Integer>>> condition_map =
                Collections.synchronizedMap(new HashMap<>());

        // for each quantized response, compute average hamming distance
        // compared to all other responses which meet a condition
        for(int i=0; i<udc_list.size(); i++){
            // specify a final variable to be accessed by thread
            final int k = i;

            // spawn a new thread to handle each value of i
            task_list.add(cached_pool.submit(new Runnable(){
                @Override
                public void run() {
                    for(int j = k + 1; j < udc_list.size(); j++){
                        // user,device,challenge conditions
                        UDC udc0 = udc_list.get(k);
                        UDC udc1 = udc_list.get(j);

                        // determine the condition of the current compairason
                        boolean[] condition = new boolean[3];
                        condition[0] = udc0.user.equals(udc1.user);
                        condition[1] = udc0.device.equals(udc1.device);
                        condition[2] = udc0.challenge == udc1.challenge;

                        // do all the work outside of the synchronized access
                        String condition_string = condition_to_string(condition);
                        int hamming_distance = hamming_distance(udc0.response.quantize(), udc1.response.quantize());

                        synchronized (condition_map) {
                            // compute the hamming distance and add it to the list of hamming
                            // distances for the current condition
                            Map<String, List<Integer>> map = condition_map.getOrDefault(condition_string,
                                    Collections.synchronizedMap(new HashMap<>()));

                            // depending on the condition, I construct the key to the map
                            // differently
                            // in the same user, same challenge, shame device case,
                            // I key to be "user_device_challenge"
                            // in same user, same challenge, different device
                            // i need "user_device"
                            //
                            // I need this so I can average for each "key"
                            // and then average over all keys
                            String key = "";
                            key += condition[0] ? udc0.user + "_" : "";
                            key += condition[1] ? udc0.device + "_" : "";
                            key += condition[2] ? udc0.challenge : "";

                            synchronized (map) {
                                List<Integer> list = map.getOrDefault(key,
                                        Collections.synchronizedList(new ArrayList<>()));

                                synchronized (list){
                                    list.add(hamming_distance);
                                }

                                map.putIfAbsent(key, list);
                            }

                            // need to put the list on to condition map if the list is new
                            condition_map.putIfAbsent(condition_string, map);
                        }
                    }
                }
            }));
        }

        // wait on all task completion
        for(Future<?> task : task_list){
            try{ task.get(); } catch(Exception e){ e.printStackTrace(); }
        }

        // remove all tasks up until this point from the list
        task_list.clear();

        // compute the average for each condition
        System.out.println("same [user, device, challenge] average_hamming_distance");
        for(Map.Entry<String, Map<String, List<Integer>>> entry : condition_map.entrySet()){
            // use 8 threads to take the average
            task_list.add(cached_pool.submit(new Runnable() {
                @Override
                public void run() {
                    String key = entry.getKey();

                    // compute the average hamming distance for each
                    // list in the value map
                    // the ultimate value will be the average of the average_list
                    List<Double> average_list = new ArrayList<>();
                    for(Map.Entry<String, List<Integer>> subentry : entry.getValue().entrySet()){
                        double hamming_sum = 0;
                        for (Integer integer : subentry.getValue()) {
                            hamming_sum += integer;
                        }

                        average_list.add(hamming_sum / subentry.getValue().size());
                    }

                    // average the average_list
                    double average_sum = 0;
                    for(Double d : average_list){
                        average_sum += d;
                    }

                    String value = String.valueOf(average_sum / average_list.size());

                    System.out.println(key + " : " + value);
                }
            }));
        }

        // wait on all task completion
        for(Future<?> task : task_list){
            try{ task.get(); } catch(Exception e){ e.printStackTrace(); }
        }

        // shut down the thread pool
        cached_pool.shutdown();
    }

    /**
     * print the average hamming diatance
     */
    //TODO make sure this method actually works
    public static void print_average_hamming_old(List<UDC> udc_list){
        ExecutorService cached_pool = Executors.newCachedThreadPool();
        Collection<Future<?>> task_list = new LinkedList<>();

        // create condition_map which
        // maps a condition of user,device,challenge same to hamming distance list
        Map<String, List<Integer>> condition_map = Collections.synchronizedMap(new HashMap<>());

        // for each quantized response, compute average hamming distance
        // compared to all other responses which meet a condition
        for(int i=0; i<udc_list.size(); i++){
            // specify a final variable to be accessed by thread
            final int k = i;

            // spawn a new thread to handle each value of i
            task_list.add(cached_pool.submit(new Runnable(){
                @Override
                public void run() {
                    for(int j = k + 1; j < udc_list.size(); j++){
                        // user,device,challenge conditions
                        UDC udc0 = udc_list.get(k);
                        UDC udc1 = udc_list.get(j);

                        // determine the condition of the current compairason
                        boolean[] condition = new boolean[3];
                        condition[0] = udc0.user.equals(udc1.user);
                        condition[1] = udc0.device.equals(udc1.device);
                        condition[2] = udc0.challenge == udc1.challenge;

                        // do all the work outside of the synchronized access
                        String condition_string = condition_to_string(condition);
                        int hamming_distance = hamming_distance(udc0.response.quantize(), udc1.response.quantize());

                        synchronized (condition_map) {
                            // compute the hamming distance and add it to the list of hamming
                            // distances for the current condition
                            List<Integer> list = condition_map.getOrDefault(condition_string,
                                    Collections.synchronizedList(new ArrayList<>()));

                            synchronized (list) {
                                list.add(hamming_distance);
                            }

                            // need to put the list on to condition map if the list is new
                            condition_map.putIfAbsent(condition_string, list);
                        }
                    }
                }
            }));
        }

        // wait on all task completion
        for(Future<?> task : task_list){
            try{ task.get(); } catch(Exception e){ e.printStackTrace(); }
        }

        // remove all tasks up until this point from the list
        task_list.clear();

        // compute the average for each condition
        System.out.println("same [user, device, challenge] average_hamming_distance");
        for(Map.Entry<String, List<Integer>> entry : condition_map.entrySet()){
            // use 8 threads to take the average
            task_list.add(cached_pool.submit(new Runnable() {
                @Override
                public void run() {
                    String key = entry.getKey();

                    double hamming_sum = 0;
                    for (Integer integer : entry.getValue()) {
                        hamming_sum += integer;
                    }

                    String value = String.valueOf(hamming_sum / entry.getValue().size());

                    System.out.println(key + " : " + value);
                }
            }));
        }

        // wait on all task completion
        for(Future<?> task : task_list){
            try{ task.get(); } catch(Exception e){ e.printStackTrace(); }
        }

        // shut down the thread pool
        cached_pool.shutdown();
    }

    /**
     * compute hamming distance
     */
    public static int hamming_distance(BitSet bs_0, BitSet bs_1){
        BitSet bs_10 = (BitSet)bs_0.clone();
        BitSet bs_11 = (BitSet)bs_1.clone();

        // different bits will be set to 1
        bs_10.xor(bs_11);

        // returns the number of bits set to 1
        return bs_10.cardinality();
    }

    /**
     * print the average hamming distance of the
     */
    //TODO I need to figure out how to make sure responses are 128 bit
    public static void print_average_hamming_python(List<UDC> udc_list){
        // create a python object as argument
        List<int[]> response_list = new ArrayList<>();

        for(UDC udc : udc_list){
            // get the quantized response
            BitSet quantized_response = udc.response.quantize();

            // turn the quantized response into a bit[]
            int[] array = new int[quantized_response.size()];

            for(int i=0; i<quantized_response.size(); i++){
                array[i] = quantized_response.get(i) ? 1 : 0;
            }

            response_list.add(array);
        }

        // call python script functions
        PyList argument = new PyList(response_list);
        System.out.println("average hamming different device, user same challenge: " +
            call_hamming_function("average_hamming_d_device_d_user_s_challenge", argument));
        System.out.println("average hamming same device, user different challenge: " +
                call_hamming_function("average_hamming_d_device_d_user_s_challenge", argument));
        System.out.println("average hamming same user, device, challenge: " +
                call_hamming_function("average_hamming_s_device_s_user_s_challenge", argument));
    }

    /**
     * call the function on the byte array given
     * calls a python script to do the work
     */
    public static double call_hamming_function(String function_name, PyList argument){
        // create properties to change the system path to python scripts director
        Properties properties = setDefaultPythonPath(System.getProperties(), PYTHON_UTIL_DIRECTORY);

        // create a Python Intrepeter for running python functions in util.py
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.initialize(System.getProperties(), properties, new String[0]);
        interpreter.exec("from hamming_util import " + function_name);

        // disable printing
        interpreter.exec("from hamming_util import " + "disable_print");
        PyObject disable_printing = interpreter.get("disable_print");
        disable_printing.__call__();

        // call the hamming function to compute average
        PyObject hamming_function = interpreter.get(function_name);
        double average_hamming = (double)(hamming_function.__call__(argument).__tojava__(double.class));

        return average_hamming;
    }

    /**
     * convert a condition into a string for accessing a map
     */
    public static String condition_to_string(boolean[] condition){
        String s = "";

        for(boolean b : condition){
            s += b ? "1" : "0";
        }

        return s;
    }

    /**
     * convert a BitSet into a String
     * this one has no cammas
     */
    public static String bit_set_to_string_no_comma(BitSet bit_set){
        String s = "";

        for(int i=0; i<bit_set.size(); i++){
            s += bit_set.get(i) ? "1" : "0";
        }

        return s;
    }
}
