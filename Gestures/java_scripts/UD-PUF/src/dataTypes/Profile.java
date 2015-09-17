package dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a profile containing Mu and Sigma values, along with a list of normalized challanges
 */
public class Profile {

    // List of mu values, which for our uses are average pressures
    private List<Double> muValues;

    // List of sigma values, the standard deviation for each mu value
    private List<Double> sigmaValues;

    // List of normalized Responses
    private List<Response> normalizedResponses;


    public Profile(List<Response> normalizedResponses) {
        this.normalizedResponses = normalizedResponses;

        // TODO
        // Calculate mu and sigma values for this profile
        // and assign them to muValues and sigmaValues
        // For now, just create blank ones
        muValues = new ArrayList<Double>();
        sigmaValues = new ArrayList<Double>();
    }

    // Constructor without normalized responses, for initially constructing a challenge
    public Profile() {
        normalizedResponses = new ArrayList<Response>();
        muValues = new ArrayList<Double>();
        sigmaValues = new ArrayList<Double>();
    }

    public void addNormalizedResponses(List<Response> normalizedResponses) {
        this.normalizedResponses = normalizedResponses;
    }

    public List<Double> getmuValues() {
        return muValues;
    }

    public List<Double> getsigmaValues() {
        return sigmaValues;
    }

    public List<Response> getNormalizedResponses() {
        return normalizedResponses;
    }

}
