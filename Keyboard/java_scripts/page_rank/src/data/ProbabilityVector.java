package data;

import java.util.ArrayList;
import java.util.List;

/**
 * this class describes the probability of a state. The identifiers represent a
 * state, and the probability corresponds with the identifier by having the same
 * index.
 * 
 * @author element
 *
 */
public class ProbabilityVector {
	List<Integer> identifiers;
	List<Double> probabilities;

	/**
	 * creates a probability vector and initializes the probabilities.
	 * 
	 * @param identifiers
	 */
	public ProbabilityVector(List<Integer> identifiers) {
		// the starting values are the number of identifiers passed in divided
		// by 1
		this.identifiers = new ArrayList<Integer>(identifiers);

		probabilities = new ArrayList<Double>();
		double starting_value = 1.0 / identifiers.size();

		// set the starting values
		for (int i = 0; i < identifiers.size(); i++) {
			probabilities.add(starting_value);
		}
	}

	/**
	 * copy constructor
	 */
	public ProbabilityVector(ProbabilityVector p) {
		this.identifiers = new ArrayList<Integer>(p.identifiers);
		this.probabilities = new ArrayList<Double>(p.probabilities);
	}

	/**
	 * multiplies the vector by the matrix provided.
	 */
	public void multiply_vector(Matrix<Double> matrix) {
		// each spot in the new vector is the result of:
		// row corresponding to this spot in the vector
		// multiplied by the entire vector
		List<Double> new_probabilities = new ArrayList<Double>();
		double row_probability;
		List<Double> current_row;

		// compute new probabilities
		for (int i = 0; i < matrix.get_rows(); i++) {
			row_probability = 0;
			current_row = matrix.get_row(i);

			// iterate over each row, multiply by the current vector to find
			// probabilities
			for (int j = 0; j < current_row.size(); j++) {
				row_probability += current_row.get(j) * probabilities.get(j);
			}

			new_probabilities.add(row_probability);
		}

		// update the probabilities of this vector
		probabilities = new_probabilities;
	}

	/**
	 * returns a scaler representing the difference between this vector and the
	 * one passed in.
	 */
	public double difference(ProbabilityVector other_vector) {
		// 1) calculate the absolute difference between each of the
		// probabilities of this vector, and the corresponding probabilitioes in
		// other_vector.
		// 2) sum the absolute differences.
		double sum = 0;

		for (int i = 0; i < probabilities.size(); i++) {
			sum += Math.abs(probabilities.get(i) - other_vector.probabilities.get(i));
		}

		return sum;
	}

	@Override
	public String toString() {
		String s_rep = "";

		// for each row, create a line
		for (int i = 0; i < identifiers.size(); i++) {
			s_rep += "[ ";

			s_rep += identifiers.get(i) + ", ";
			s_rep += probabilities.get(i) + " ";

			s_rep += "]\n";
		}

		return s_rep;
	}
}
