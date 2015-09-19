package graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * allows for the creation of a state graph. Information about the edges is
 * stored on the nodes from which the edges originate.
 * 
 * @author element
 *
 */
public class StateGraph {
	List<StateNode> nodes;

	public StateGraph() {
		nodes = new ArrayList<StateNode>();
	}

	/**
	 * adds a node and returns an identifier to that node
	 * 
	 * @return
	 */
	public int add_node() {
		// make the identifier the index to the node in the list
		StateNode node = new StateNode(nodes.size());

		nodes.add(node);

		return node.get_identifier();
	}

	/**
	 * adds an edge into the graph
	 * 
	 * @param source
	 * @param destination
	 * @param probability
	 */
	public void add_edge(int source_identifier, int destination_identifier, double probability) {
		StateNode source = nodes.get(source_identifier);
		StateNode destination = nodes.get(destination_identifier);

		StateEdge edge = new StateEdge(destination, probability);

		source.addEdge(edge);
	}

	/**
	 * gets the edges for the specified node. Edges stored on a node describe an
	 * edge from that node to a different node.
	 */
	public Set<StateEdge> get_edges(int identifier) {
		return nodes.get(identifier).getEdges();
	}

	/**
	 * returns all of the nodes in the graph
	 */
	public List<StateNode> get_nodes() {
		return nodes;
	}
}