package rank;

import java.util.ArrayList;
import java.util.List;

import components.Chain;
import components.Token;
import components.Touch;
import components.Window;
import compute.Rank;
import data.ProbabilityVector;
import graph.StateGraph;
import runtime.CompareChains;

public class CompareChainsRank extends CompareChains {
	private Chain user_replica;
	private Chain auth_replica;

	public CompareChainsRank(Chain user_chain, Chain auth_chain) {
		super(user_chain, auth_chain);

		user_replica = (new CompleteProbability(user_chain)).compute_probability();
		auth_replica = (new CompleteProbability(auth_chain)).compute_probability();
	}

	/**
	 * overrides the run method to implement the authentication with a page-rank
	 * style algorithm.
	 */
	@Override
	public void run() {
		// need to update complete, authentication_probability, and
		// is_authentic
		// variables

		// to use the page_rank library to compare the chains, we first need a
		// graphical representation of the chains.
		StateGraph user_graph = chain_to_graph(user_replica);
		StateGraph auth_graph = chain_to_graph(auth_replica);

		// now we can ask for a ranking of each one
		ProbabilityVector user_vector = Rank.compute_vector(user_graph);
		ProbabilityVector auth_vector = Rank.compute_vector(auth_graph);

		// now we can compare the ranking vectors for their difference
		// TODO
		double difference = user_vector.difference(auth_vector);
		authentication_probability = difference;

		// TODO
		is_authentic = difference < 1.0;

		complete = true;
	}

	/**
	 * converts a chain to a page_rank graph
	 */
	private StateGraph chain_to_graph(Chain chain) {
		// need to use the same states for each graph. States must also have the
		// same index in each graph, otherwise the vector compairason will not
		// work as expected.
		StateGraph graph = new StateGraph();
		List<Touch> touches = chain.get_touches();
		List<Token> tokens = chain.get_tokens();

		List<Integer> nodes = new ArrayList<Integer>();

		int destination;
		double probability;

		// create a node for each possible state => number of tokens per state *
		// number of states
		for (int i = 0; i < chain.get_token() * chain.get_key_distribution().size(); i++) {
			nodes.add(graph.add_node());
		}

		// create an edge for each transition. Look at each touch and
		// figure out source, destination, and probability
		int previous_touch_index = -1;
		Touch previous_touch = null;

		for (Touch touch : touches) {
			// for the first iteration, do something different
			if (previous_touch_index == -1) {
				previous_touch_index = touch_index(touch, chain);
				previous_touch = touch;
				continue;
			}

			// find destination
			destination = touch_index(touch, chain);

			// find probability, this works because chain is a replica
			// chain with window size of 1. This means that previous_touch ==
			// window.
			// This touch occurrs with probability [number of this touch which
			// succeeds previous_touch]/[total number of touches which succeed
			// previous_touch]

			// find the window which contains previous touch.
			Window previous_window = touch_window(previous_touch, chain);
			probability = chain.get_touch_probability(previous_window, touch);

			graph.add_edge(previous_touch_index, destination, probability);

			previous_touch_index = touch_index(touch, chain);
			previous_touch = touch;
		}

		return graph;
	}

	/**
	 * takes in a Touch, and returns the index in the list to which the Touch
	 * corresponds.
	 * 
	 * The basic formula for correspondance is: [key_distribution index]*[number
	 * of tokens] + [token_index]
	 */
	private int touch_index(Touch touch, Chain chain) {
		int key_distribution_index = -1;
		int token_index = -1;

		// key_distribution_index
		for (int i = 0; i < chain.get_key_distribution().size(); i++) {
			if (touch.get_key() == chain.get_key_distribution().get(i).get_keycode()) {
				key_distribution_index = i;
				break;
			}
		}

		// token_index
		for (int i = 0; i < chain.get_tokens().size(); i++) {
			if (chain.get_tokens().get(i).contains(touch)) {
				token_index = i;
				break;
			}
		}

		// check to make sure both were actually found
		if ((token_index == -1) || (key_distribution_index == -1)) {
			return -1;
		}

		return key_distribution_index * chain.get_token() + token_index;
	}

	/**
	 * finds the window which contains the touch
	 */
	private Window touch_window(Touch touch, Chain chain) {
		// windows are of size 1 for the replica chain. Therefore all I need to
		// do is find the window containing the touch i'm interested in.
		for (Window window : chain.get_windows()) {
			// determines if the touches are the same.... This depends on more
			// than just the token
			if (window.get_touch_list().get(0).compare_with_token(chain.get_tokens(), touch)) {
				return window;
			}
		}

		return null;
	}
}
