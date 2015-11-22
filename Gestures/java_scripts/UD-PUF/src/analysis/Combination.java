package analysis;

/**
 * holds a combination of model parameters. Computes the Effectiveness of this
 * combination.
 * 
 * @author element
 *
 */
public class Combination {
    public double false_positive;
    public double false_negative;
    public double accuracy;

    // test parameters which may be varied.
    public double pressure_allowed_deviations;
    public double distance_allowed_deviations;
    public double time_allowed_deviations;
    public double authentication_threshold;
    public double time_length_allowed_deviations;

    public Combination(double pressure_allowed_deviations, double distance_allowed_deviations,
	    double time_allowed_deviations, double time_length_allowed_deviations, double authentication_threshold) {
	this.pressure_allowed_deviations = pressure_allowed_deviations;
	this.distance_allowed_deviations = distance_allowed_deviations;
	this.time_allowed_deviations = time_allowed_deviations;
	this.authentication_threshold = authentication_threshold;
	this.time_length_allowed_deviations = time_length_allowed_deviations;

	// compute the effectiveness
	compute_effectiveness();
    }

    /**
     * use effectiveness to compute effectiveness store in instance variables.
     */
    private void compute_effectiveness() {
	Effectiveness stats = new Effectiveness(this);

	this.false_positive = stats.false_positive;
	this.false_negative = stats.false_negative;
	this.accuracy = stats.accuracy;
    }

    @Override
    public String toString() {
	// print out the results
	StringBuilder output = new StringBuilder();

	output.append("false positive: " + this.false_positive + "\n");
	output.append("false negative: " + this.false_negative + "\n");
	output.append("accuracy: " + this.accuracy + "\n");

	output.append("\n");
	output.append("pressure_allowed_deviations: " + this.pressure_allowed_deviations + "\n");
	output.append("distance_allowed_deviations: " + this.distance_allowed_deviations + "\n");
	output.append("time_allowed_deviations: " + this.time_allowed_deviations + "\n");
	output.append("time_length_allowed_deviations: " + this.time_length_allowed_deviations + "\n");
	output.append("authentication_threshold: " + this.authentication_threshold + "\n");

	return output.toString();
    }
}
