package jUnitTests.dataTypes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;

import dataTypes.*;
import org.junit.Before;
import org.junit.Test;

import data.DataReader;

/**
 * Unit tests for the Profile
 */
public class ProfileUnit2 {
    private static String inputDirectory = "tim_2000_1";

    private File inputFile;
    private DataReader reader;

    private ArrayList<Response> defaultResponses;
    private ArrayList<ArrayList<Point>> defaultPointSets;
    private Profile defaultProfile;

    private ArrayList<Response> linearResponses;
    private ArrayList<ArrayList<Point>> linearPointSets;
    private Profile linearProfile;

    public void init() {

        // initialize default sets
        defaultPointSets = new ArrayList<>();
        defaultResponses = new ArrayList<>();
        inputFile = new File(inputDirectory);
        reader = new DataReader(inputFile);

        // initialize linear sets
        linearPointSets = new ArrayList<>();
        linearResponses = new ArrayList<>();
        inputFile = new File(inputDirectory);
        reader = new DataReader(inputFile);


        // Creates a list of identical defaultResponses, each at an
        // ascending x and y coordinate and with .5 pressure

        // outer loop to loop through each response
        for(int i = 0; i < 20; i++) {
            defaultPointSets.add(new ArrayList<Point>());
            linearPointSets.add(new ArrayList<Point>());
            // inner loop to assign point values to each response
            for(int j = 0; j < 32; j++) {
                defaultPointSets.get(i).add(new Point(j, j, .5));
                linearPointSets.get(i).add(new Point(j, j, (double)j / 32));
            }
            defaultResponses.add(new Response(defaultPointSets.get(i)));
            linearResponses.add(new Response(linearPointSets.get(i)));
        }

        defaultProfile = new Profile(defaultResponses);
        linearProfile = new Profile(linearResponses);

    }

    // Tests the default profile to assure the correct mu and sigma values
    // are being created
    @Test
    public void testMuSigmaValues() {
        for(int i = 0; i < 32; i++) {
//            assertTrue(.5 == defaultProfile.getMuSigmaValues().getMuValues().get(i));
//            assertTrue(0 == defaultProfile.getMuSigmaValues().getSigmaValues().get(i));
//            assertTrue((double)i/32== linearProfile.getMuSigmaValues().getMuValues().get(i));
//            assertTrue(0 == linearProfile.getMuSigmaValues().getSigmaValues().get(i));
        }
    }


}
