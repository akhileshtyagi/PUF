package junit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import components.Chain;
import components.Distribution;
import components.Touch;
import rank.CompleteProbability;

/** unit test demonstrating how to compute probility */
public class Unit_CompleteProbability {
	private Chain replica_chain;

	@Before
	public void init() {
		// selected these values based on best results... window parameter does
		// not matter
		int window = 3;
		int token = 7;
		int threshold = 5000;
		int user_model_size = 8000;

		// create the chain
		Chain user_chain = new Chain(window, token, threshold, user_model_size);

		// create user and auth chains which will have known probability
		for (int i = 0; i < user_model_size; i++) {
			user_chain.add_touch(new Touch(i % 4, .3, i * 100L));
		}

		CompleteProbability complete_probability = new CompleteProbability(user_chain);

		this.replica_chain = complete_probability.compute_probability();
	}

	/**
	 * test different properties of replica chain to see if this works as
	 * expected. Replica chain should contain the probabilities for when window
	 * is equal to 1.
	 */
	@Test
	public void test_replica_distribution() {
		Distribution distribution = this.replica_chain.get_distribution();
		
		// TODO this deffonately shouldn't be NaN, figure out what is going on here
		System.out.println(distribution.get_average());
		System.out.println(distribution.get_min());
		System.out.println(distribution.get_max());
		
		assertTrue(distribution.get_average() > .29 && distribution.get_average() < .31);
	}

}
