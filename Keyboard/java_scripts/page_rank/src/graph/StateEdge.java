package graph;

/**
 * Represents an edge from one node to another.
 * 
 * @author element
 *
 */
public class StateEdge {
	private StateNode to_node;

	private double probability;

	public StateEdge(StateNode to_node, double probability) {
		this.to_node = to_node;

		this.probability = probability;
	}

	public StateNode get_destination() {
		return to_node;
	}

	public double get_probability() {
		return probability;
	}
}
