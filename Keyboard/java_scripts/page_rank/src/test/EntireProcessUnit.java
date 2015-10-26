package test;

import static org.junit.Assert.fail;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import compute.Rank;
import data.ProbabilityVector;
import data.TransitionMatrix;
import graph.StateGraph;

public class EntireProcessUnit {
	StateGraph graph;
	TransitionMatrix matrix;
	ProbabilityVector vector;

	/**
	 * creates all objects necessary for the tests. If nothing else this gives
	 * consistent naming to all of them.
	 */
	@Before
	public void before() {
		graph = new StateGraph();

		// populate the graph.
		// These are the same nodes and edges as the webfig example, p 166
		// UllmanDataMiningBook
		ArrayList<Integer> nodes = new ArrayList<Integer>();

		for (int i = 0; i < 4; i++) {
			nodes.add(graph.add_node());
		}

		// node 0 = A
		graph.add_edge(0, 1, 1.0 / 3.0);
		graph.add_edge(0, 2, 1.0 / 3.0);
		graph.add_edge(0, 3, 1.0 / 3.0);

		// node 1 = B
		graph.add_edge(1, 0, 1.0 / 2.0);
		graph.add_edge(1, 3, 1.0 / 2.0);

		// node 2 = C
		graph.add_edge(2, 0, 1.0);

		// node 3 = D
		graph.add_edge(3, 1, 1.0 / 2.0);
		graph.add_edge(3, 2, 1.0 / 2.0);

		// create the transition matrix from the graph
		matrix = new TransitionMatrix(graph);

		matrix.iterate_multiply();

		vector = matrix.get_vector();
	}

	/**
	 * test construction of the graph.
	 */
	@Test
	public void test_graph_construction() {

		fail("Not yet implemented");
	}

	/**
	 * test building Transition Matrix from graph.
	 */
	@Test
	public void test_transition_matrix_construction() {
		TransitionMatrix test_matrix = new TransitionMatrix(graph);

		// verified by looking at it
		// System.out.println(test_matrix);
	}

	/**
	 * test Rank
	 */
	@Test
	public void test_rank() {
		// TODO verify this is correct
		ProbabilityVector result_vector = Rank.compute_vector(graph);

		// verified by looking at them
		// System.out.println(result_vector);
		// System.out.println(vector);
	}

	/**
	 * test construction of the graph.
	 */
	@Test
	public void test() {

		fail("Not yet implemented");
	}
}