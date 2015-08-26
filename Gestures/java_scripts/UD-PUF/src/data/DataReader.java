package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class will have tools to retrieve the data from the storage device.
 * 
 * @author Tim Dee
 *
 */
public class DataReader {
    private final File inputFile;

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
    public List<ChallengeResponse> readDataDirecotry() {
	ArrayList<File> files = new ArrayList<File>();
	ArrayList<ChallengeResponse> responses = new ArrayList<ChallengeResponse>();

	// create challengeResponse objects for every file in the input
	// directory
	// 1) create a list of files in the input directory
	// TODO

	// 2) create the list of challengeResponse objects from the files
	for (File file : files) {
	    Scanner fileScanner;
	    String line = "";
	    String responseHeaderString = "\"X\",\"Y\",\"PRESSURE\"";
	    ChallengeResponse response = null;
	    String[] splitResponse = null;

	    try {
		fileScanner = new Scanner(file);

		// throw away the first line as it is a header
		fileScanner.nextLine();

		// grab the name from the first challenge line
		line = fileScanner.nextLine();
		splitResponse = line.split("\",\"");
		response = new ChallengeResponse(splitResponse[2], splitResponse[3]);
		// TODO this part really needs to be checked
		response.addChallengePoint(Double.valueOf(splitResponse[0]), Double.valueOf(splitResponse[1]));

		// iterate over the challenge points
		while (fileScanner.hasNextLine()) {
		    line = fileScanner.nextLine();
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
			response.addChallengePoint(Double.valueOf(splitResponse[0]), Double.valueOf(splitResponse[1]));

		    }
		}

		// iterate over the response points
		while (fileScanner.hasNextLine()) {
		    line = fileScanner.nextLine();

		    // add the line as a response point
		    splitResponse = line.split("\",\"");
		    // TODO this part really needs to be checked. there is an extra " on these values
		    response.addResponsePoint(Double.valueOf(splitResponse[0]), Double.valueOf(splitResponse[1]),
			    Double.valueOf(splitResponse[2]));
		}

		fileScanner.close();
	    } catch (FileNotFoundException e) {
		System.out.println("Input file cannot be opened");
		e.printStackTrace();
	    }

	}

	return responses;
    }
}
