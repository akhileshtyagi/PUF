package graph;

import java.util.Set;
import java.util.TreeSet;

/**
 * Represents a state in the model.
 * 
 * @author element
 *
 */
public class StateNode {
	private Set<StateEdge> edges;
	private int identifier;

	public StateNode(int identifier) {
		edges = new TreeSet<StateEdge>();

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

	public Set<StateEdge> getEdges() {
		return edges;
	}

	public int get_identifier() {
		return identifier;
	}
}
