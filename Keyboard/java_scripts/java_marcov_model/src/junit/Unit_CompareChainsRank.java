package junit;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import components.Chain;
import components.Touch;
import rank.CompareChainsRank;

/**
 * goal is to test compare chains rank functionality
 *
 */
public class Unit_CompareChainsRank {
	private Chain user_chain;
	private Chain auth_chain;

	@Before
	public void init() {
		// selected these values based on best results... window parameter does
		// not matter
		int window = 3;
		int token = 7;
		int threshold = 5000;
		int user_model_size = 8000;
		int auth_model_size = 4000;

		// create the chains
		this.user_chain = new Chain(window, token, threshold, user_model_size);
		this.auth_chain = new Chain(window, token, threshold, auth_model_size);

		// create user and auth chains which will have known probability
		for(int i=0; i<user_model_size; i++){
			user_chain.add_touch(new Touch(i%4, .3, i*100L));
		}
		
		for(int i=0; i<auth_model_size; i++){
			auth_chain.add_touch(new Touch(i%4, .3, i*100L));
		}
	}

	@Test
	public void test_authentication_probability() {
		CompareChainsRank cc = new CompareChainsRank(user_chain, auth_chain);
		Thread auth_thread = new Thread(cc);
		auth_thread.start();

		while (cc.get_auth_complete() == false) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		System.out.println("authentication_probability: " + cc.get_auth_probability());

		fail("Compute by hand value of authentication probability");
	}
}
