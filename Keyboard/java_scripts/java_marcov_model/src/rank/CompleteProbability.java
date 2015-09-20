package rank;

import components.Chain;

/**
 * This class computes probability in a different way from what is contained in
 * the Chain class. This class looks at all of the touches to try to determine
 * the probability that from any given touch, it transitions to another.
 * 
 * this is similar to having a window size of 1?
 * 
 * @author element
 *
 */
public class CompleteProbability {
	private Chain chain;

	public CompleteProbability(Chain chain) {
		this.chain = chain;
	}

	/**
	 * make a replica of the chain with a window size of 1 and compute the
	 * probability.
	 * 
	 * @return replica chain
	 */
	public Chain compute_probability() {
		Chain replica_chain = new Chain(1, chain.get_token(), chain.get_threshold(), chain.get_model_size());

		replica_chain.compute_uncomputed();

		return replica_chain;
	}
}
