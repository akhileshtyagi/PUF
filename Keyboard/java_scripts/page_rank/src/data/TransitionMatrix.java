package data;

import java.util.ArrayList;
import java.util.List;

import graph.StateEdge;
import graph.StateGraph;
import graph.StateNode;

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
		List<Integer> identifiers = new ArrayList<Integer>();
		List<StateNode> nodes = graph.get_nodes();

		// initialize the matrix to zero
		for (int i = 0; i < nodes.size(); i++) {
			List<Double> row = new ArrayList<Double>();

			for (int j = 0; j < nodes.size(); j++) {
				row.add(0.0);
			}
			transition_matrix.add_row(row);
		}

		// populate transition_matrix from state graph
		for (StateNode node : nodes) {
			// for each edge coming out of a node, I need to update its
			// probability into the matrix
			int column = node.get_identifier();
			for (StateEdge edge : node.getEdges()) {
				int row = edge.get_destination().get_identifier();
				double value = edge.get_probability();

				transition_matrix.set(row, column, value);
			}
		}

		// create the initial vector (1/n) based on how many states there
		// are in the graph
		vector = new ProbabilityVector(identifiers);
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