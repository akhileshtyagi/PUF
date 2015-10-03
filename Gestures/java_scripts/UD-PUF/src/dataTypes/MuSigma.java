package dataTypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a MuSigma pair
 */
public class MuSigma {

    // List of mu values, which for our uses are average pressures
    private List<Double> muValues;

    // List of sigma values, the standard deviation for each mu value
    private List<Double> sigmaValues;

    // Empty constructor
    public MuSigma() {
        this.muValues = new ArrayList<Double>();
        this.sigmaValues = new ArrayList<Double>();
    }

    // Constructor for including lists of values
    public MuSigma(List<Double> muValues, List<Double> sigmaValues) {
        this.muValues = muValues;
        this.sigmaValues = sigmaValues;
    }

    public List<Double> getMuValues() {
        return muValues;
    }

    public List<Double> getSigmaValues() {
        return sigmaValues;
    }

    public void addMuSigma(double mu, double sigma)
    {
        this.muValues.add(mu);
        this.sigmaValues.add(sigma);

    }
}
