package data;

import graph.StateGraph;

/**
 * provides tools for allowing the user of this library to create a transition
 * matrix. Allows creation of Transition matrix from a graph.
 * 
 * This class also knows how to modify itself for one iteration of the pagerank
 * algorithm. Provides to option to iterate until the algorithm has completed
 * and return the eigenvector.
 * 
 * @author element
 *
 */
public class TransitionMatrix {
	final double EPISILON = 100.0;

	private Matrix<Double> transition_matrix;
	private ProbabilityVector vector;

	/**
	 * creates a transition matrix from the state graph
	 * 
	 * @param graph
	 */
	public TransitionMatrix(StateGraph graph) {
		// TODO populate transition_matrix from state graph
		
		
		// TODO create the initial vector (1/n) based on how many states there
		// are in the graph
	}

	/**
	 * multiplies the vector by the transition matrix.
	 */
	public void multiply_vector() {
		vector.multiply_vector(transition_matrix);
	}

	/**
	 * preforms the multiply_vector() until the results converge on a value.
	 */
	public void iterate_multiply() {
		ProbabilityVector prev_vector;

		// preform the multiply until the vectors are within EPISILON
		do {
			prev_vector = new ProbabilityVector(vector);
			vector.multiply_vector(transition_matrix);
		} while (vector.difference(prev_vector) > EPISILON);
	}

	/**
	 * returns the current probability vector. Need to run iterate_multiply()
	 * for this to contain accurate probabilities.
	 * 
	 * @return
	 */
	public ProbabilityVector get_vector() {
		return vector;
	}
}