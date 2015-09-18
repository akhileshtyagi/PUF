package compute;

import data.ProbabilityVector;
import data.TransitionMatrix;
import graph.StateGraph;

/**
 * preforms all the necessary computations. Given a graph this class has methods
 * which will produce a vector representing the probability.
 * 
 * This is simply a utility class which helps to make this library easier to
 * use.
 * 
 * @author element
 *
 */
public class Rank {
	/**
	 * computes a probability vector given a state graph.
	 * 
	 * @param graph
	 * @return
	 */
	public static ProbabilityVector compute_vector(StateGraph graph) {
		TransitionMatrix matrix = new TransitionMatrix(graph);

		matrix.iterate_multiply();

		return matrix.get_vector();
	}
}
