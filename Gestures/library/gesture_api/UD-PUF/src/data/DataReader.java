package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dataTypes.Challenge;
import dataTypes.Point;
import dataTypes.Response;

/**
 * This class will have tools to retrieve the data from the storage device.
 * 
 * @author Tim Dee
 *
 */
public class DataReader {
    private File inputFile;

    /**
     * Takes a directory where the output from the app is stored and provides
     * methods to read in the data
     */
    public DataReader(File file) {
	this.inputFile = file;
    }

    /**
     * Returns a list of challenge response objects.
     */
    public Challenge readDataDirecotry() {
	ArrayList<File> files = new ArrayList<File>();
	ArrayList<ChallengeResponse> responses = new ArrayList<ChallengeResponse>();
	Challenge challenge = null;

	// create challengeResponse objects for every file in the input
	// directory
	// 1) create a list of files in the input directory
	File[] fileArray = this.inputFile.listFiles();

	for (File f : fileArray) {
	    files.add(f);
	}

	// 2) create the list of challengeResponse objects from the files
	for (File file : files) {
	    Scanner fileScanner;
	    String line = "";
	    String responseHeaderString = "\"X\",\"Y\",\"PRESSURE\"";
	    // ChallengeResponse response = null;

	    List<Point> challangePoints = new ArrayList<Point>();
	    List<Point> responsePoints = new ArrayList<Point>();

	    Response response = null;

	    String[] splitResponse = null;

	    try {
		fileScanner = new Scanner(file);

		// throw away the first line as it is a header
		fileScanner.nextLine();

		// grab the name from the first challenge line
		line = fileScanner.nextLine();
		splitResponse = line.split("\",\"");
		// response = new ChallengeResponse(splitResponse[2],
		// splitResponse[3]);

		// build the challengeResponse object from x,y challenge points
		// in the file
		// response.addChallengePoint(Double.valueOf(splitResponse[0].substring(1)),
		// Double.valueOf(splitResponse[1]));

		// challangePoints.add(new
		// Point(Double.parseDouble(splitResponse[2]),Double.parseDouble(splitResponse[3]),0));

		// iterate over the challenge points
		while (fileScanner.hasNextLine()) {
		    /*
		     * if the header to the response data is detected, I should
		     * break and start reading response data
		     */
		    if (line.equals(responseHeaderString)) {
			break;
		    } else {
			// add the line as a challenge point
			splitResponse = line.split("\",\"");
			// TODO this part really needs to be checked
			// response.addChallengePoint(Double.valueOf(splitResponse[0].substring(1)),
			// Double.valueOf(splitResponse[1]));
			challangePoints.add(new Point(Double.parseDouble(splitResponse[0].substring(1)),
				Double.parseDouble(splitResponse[1]), 0));
		    }
		    line = fileScanner.nextLine();
		}

		// Create challange from list of points
		// TODO Assign proper IDs to these
		challenge = new Challenge(challangePoints, 0);

		// iterate over the response points
		while (fileScanner.hasNextLine()) {
		    line = fileScanner.nextLine();

		    // add the line as a response point
		    splitResponse = line.split("\",\"");
		    // response.addResponsePoint(Double.valueOf(splitResponse[0].substring(1)),
		    // Double.valueOf(splitResponse[1]),
		    // Double.valueOf(splitResponse[2].substring(0,
		    // splitResponse[2].length() - 2)));
		    responsePoints.add(new Point(Double.parseDouble(splitResponse[0].substring(1)),
			    Double.parseDouble(splitResponse[1]),
			    Double.parseDouble(splitResponse[2].substring(0, splitResponse[2].length() - 2))));

		}

		// create response from list of points and add it to challenge
		response = new Response(responsePoints);
		challenge.addResponse(response);

		fileScanner.close();
	    } catch (FileNotFoundException e) {
		System.out.println("Input file cannot be opened");
		e.printStackTrace();
	    }

	}

	return challenge;
    }

    /**
     * Returns a list of challenge response objects. NOTE USED ANYMORE
     */
    public List<Challenge> readDataDirecotryChallenge() {
	List<ChallengeResponse> responses = null;

	// loop through all the challenges
	// create challenge objects for each unique challenge
	// add responses to the challenges
	List<Challenge> challenges = new ArrayList<Challenge>();

	for (ChallengeResponse response : responses) {
	    // if there is not a challenge object for the responses already,
	    // create it.
	    if (getChallengeIndex(challenges, response.getChallengeList()) == -1) {
		List<Point> challengePoints = new ArrayList<Point>();

		// populate the list of challenge points
		for (List<Double> point : response.getChallengeList()) {
		    challengePoints.add(new Point(point.get(0), point.get(1), 0.0));
		}

		// create a new challenge
		Challenge challenge = new Challenge(challengePoints, challenges.size());

		challenges.add(challenge);
	    }

	    // create response object from the list of points
	    List<Point> response_object_points = new ArrayList<Point>();

	    // convert the list of response points into a respoonse object
	    for (List<Double> response_point : response.getResponseList()) {
		response_object_points
			.add(new Point(response_point.get(0), response_point.get(1), response_point.get(2)));
	    }

	    Response response_object = new Response(response_object_points);

	    // add the response to the appropriate challenge
	    challenges.get(getChallengeIndex(challenges, response.getChallengeList())).addResponse(response_object);
	}

	return challenges;
    }

    /**
     * gets the index of the list of points in the challenges list. Returns -1
     * if it doesn't exist
     * 
     * @return
     */
    private int getChallengeIndex(List<Challenge> challenges, List<List<Double>> list_of_points) {
	int index = -1;

	// search though challenges for list_of_points
	for (int i = 0; i < challenges.size(); i++) {
	    boolean list_equals_challenge = true;

	    // test each of the points to make sure list_of_points is the same
	    // as challenge points
	    for (int j = 0; j < challenges.get(i).getChallengePattern().size(); j++) {
		Point challenge_pattern_point = challenges.get(i).getChallengePattern().get(j);

		// test x,y values equal
		list_equals_challenge = list_equals_challenge
			&& (challenge_pattern_point.getX() == list_of_points.get(j).get(0));
		list_equals_challenge = list_equals_challenge
			&& (challenge_pattern_point.getY() == list_of_points.get(j).get(1));
	    }

	    if (list_equals_challenge) {
		index = i;
		break;
	    }
	}

	return index;
    }

	/**
	 * return the challenge data contained within a data file
	 *
	 * be sure to get the challengeID from the file name
	 */
	public static Challenge getChallenge(File data_file){
		Challenge challenge = null;

		Scanner fileScanner;
		String line = "";
		String responseHeaderString = "\"X\",\"Y\",\"PRESSURE\"";

		List<Point> challangePoints = new ArrayList<Point>();

		String[] splitResponse = null;

		try {
			fileScanner = new Scanner(data_file);

			// throw away the first line as it is a header
			fileScanner.nextLine();

			// grab the challenge points
			line = fileScanner.nextLine();

			// iterate over the challenge points
			while (fileScanner.hasNextLine()) {
				// if the header to the response data is detected, I should
				// break and start reading response data
				if (line.equals(responseHeaderString)) {
					break;
				} else {
					// add the line as a challenge point
					splitResponse = line.split("\",\"");

					// add the challenge point to the list
					challangePoints.add(new Point(Double.parseDouble(splitResponse[0].substring(1)),
							Double.parseDouble(splitResponse[1]), 0));
				}

				line = fileScanner.nextLine();
			}

			// get the challengeID from the file name
			String challenge_string_name = data_file.getName().split(":| ")[0];
			//System.out.println(challenge_string_name);
			//System.out.flush();

			long challenge_name = Long.valueOf(challenge_string_name);

			// Create challange from list of points and id
			challenge = new Challenge(challangePoints, challenge_name);

			fileScanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("Input file cannot be opened");
			e.printStackTrace();
		}

		return challenge;
	}

	/**
	 * return the response data contained within a data file
	 */
	public static Response getResponse(File data_file){
		Response response = null;

		List<Point> responsePoints = new ArrayList<Point>();
		Scanner fileScanner;

		String line = "";
		String responseHeaderString = "\"X\",\"Y\",\"PRESSURE\"";
		String[] splitResponse = null;

		try {
			fileScanner = new Scanner(data_file);

			// grab the challenge points
			line = fileScanner.nextLine();

			// iterate over the challenge points
			while (fileScanner.hasNextLine()) {
				// if the header to the response data is detected, I should
				// break and start reading response data
				if (line.equals(responseHeaderString)) {
					break;
				}

				line = fileScanner.nextLine();
			}

			// iterate over the response points
			while (fileScanner.hasNextLine()) {
				line = fileScanner.nextLine();

				// add the line as a response point
				splitResponse = line.split("\",\"");

				// get the response point values
				responsePoints.add(new Point(Double.parseDouble(splitResponse[0].substring(1)),
						Double.parseDouble(splitResponse[1]),
						Double.parseDouble(splitResponse[2].substring(0, splitResponse[2].length() - 2))));

			}

			// create response from list of points and add it to challenge
			response = new Response(responsePoints);
		}catch( Exception e){ e.printStackTrace(); }

		//System.out.println(response);

		return response;
	}
}
