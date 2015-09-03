package test;

import java.io.File;
import java.util.List;

import data.ChallengeResponse;
import data.DataReader;
import muSigmaModel.NormalizedModel;

/**
 * Tests the ability to read data from the storage device. This is an ad-hoc
 * test.
 * 
 * @author element
 *
 */
public class ReadData {
    private static String inputDirectory = "data_link_tim_2000";

    /**
     * Read in data and check to make sure its correct
     * 
     * @param args
     */
    public static void main(String[] args) {
	File inputFile = new File(inputDirectory);
	DataReader reader = new DataReader(inputFile);

	// check that the file exists
	System.out.println(inputFile.exists());

	// read in the data
	List<ChallengeResponse> responses = reader.readDataDirecotry();

	// check to see if the data is correct in the challenge response object
	System.out.println(responses.get(0).getResponseList().get(0));
	System.out.println(responses.get(0).getChallengeList().get(0));

	// construct the list of normalized models for each response
	NormalizedModel normalizedResponse = new NormalizedModel(responses, 1);

	// TODO check the normalized model for correctness
	List<List<Double>> normalizedModel = normalizedResponse.getModel();
	// System.out.println(normalizedModel);
    }
}
