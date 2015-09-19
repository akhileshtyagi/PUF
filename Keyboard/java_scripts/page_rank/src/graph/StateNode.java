package graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a state in the model.
 * 
 * @author element
 *
 */
public class StateNode {
	private List<StateEdge> edges;
	private int identifier;

	public StateNode(int identifier) {
		edges = new ArrayList<StateEdge>();

		this.identifier = identifier;
	}

	/**
	 * adds an edge from this node to the destination node
	 * 
	 * @param destination
	 * @param probability
	 */
	public void addEdge(StateEdge edge) {
		edges.add(edge);
	}

	public List<StateEdge> getEdges() {
		return edges;
	}

	public int get_identifier() {
		return identifier;
	}
}
