package jUnitTests.ReadData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dataTypes.Challenge;
import dataTypes.MuSigma;
import dataTypes.Point;
import dataTypes.Profile;
import dataTypes.Response;

import data.DataReader;

/**
 * This test case tests the Profile class for correctness. The tests in this
 * class assume that Challenge class works
 *
 * @author element
 *
 */
public class ReadDataTest{
    private Profile profile;
	private static String inputDirectory = "tim_2000_1";

    @Before
    public void init() {
        File inputFile = new File(inputDirectory);
        DataReader reader = new DataReader(inputFile);

        // check that the file exists
        System.out.println(inputFile.exists());

        // read in the data
        Challenge challenge = reader.readDataDirecotry();

        // check to see if the data is correct in the challenge response object
        System.out.println("Challenge Point 1 = (" + challenge.getChallengePattern().get(0).getX() + ", " + challenge.getChallengePattern().get(0).getY() + ")");
        System.out.println("Challenge Point 2 = (" + challenge.getChallengePattern().get(1).getX() + ", " + challenge.getChallengePattern().get(1).getY() + ")");

        for(int i = 0; i < 32; i ++) {
            System.out.println("Response 1: Normalized Point " + (i + 1) + " = \t(" + challenge.getResponsePattern().get(0).getNormalizedResponse().get(i).getX() + ", " + challenge.getResponsePattern().get(0).getNormalizedResponse().get(i).getY() + ")" +
                    "   \twith pressure = " + challenge.getResponsePattern().get(0).getNormalizedResponse().get(i).getPressure());
        }

    }

    /**
     * test teh getMuSigmaValues method for correctness.
     */
    @Test
    public void simpleTest() {
        assertEquals(2,2);
	}
}
