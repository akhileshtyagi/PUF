package runtime;

import components.Chain;

//TODO cause something that makes sence to happen when the user fails the authentication
//This thread will call the compare method of chain class. The goal is to compare user chain and auth chain and make the result, pass/fail known. Or do something based on pass/fail such as cause the phone to lock.

/** Use the compare method of the Chain class to determine an authetnication probability between 0 and 1.
 * This class was designed to allow for comparing chains to happen on a different thread.
 */
public class CompareChains implements Runnable {
	final double AUTHENTICATION_THRESHOLD = .7; // TODO find a reasonable /
												// justifiable value for the
												// authentication threshold

	protected volatile boolean is_authentic;
	protected volatile boolean complete; // indicates is_authentic contains the
											// result

	protected Chain user_chain;
	protected Chain auth_chain;

	protected volatile double authentication_probability;

	/// will need to make copies of the chains passed in so they do not get
	/// updated by something else during the comparason
	public CompareChains(Chain user_chain, Chain auth_chain) {
		this.user_chain = new Chain(user_chain);
		this.auth_chain = new Chain(auth_chain);

		is_authentic = false;
		complete = false;
	}

	/// compare user_chain and auth_chain and choose what to do with the result
	@Override
	public void run() {
		/// perform the comparison now that the values are cached in the Chain's
		authentication_probability = 1 - user_chain.compare_to(auth_chain);

		if (!is_user_authentic(authentication_probability)) {
			// TODO determine what to do when the user fails the authentication
			// user fails the authentication
			// possibly cause the lock screen to come up
			is_authentic = false;
			// System.out.println("User fails to authenticate");
		} else {
			is_authentic = true;
		}
		complete = true;
	}

	/// returns the probability with which the
	public double get_auth_probability() {
		return authentication_probability;
	}

	/// returns the result of the authentication. This method does not provide
	/// any guarentees that the compairason has finsihed yet. If the compairason
	/// has not yet finished it will return false;
	public boolean get_auth_result() {
		return is_authentic;
	}

	// returns true if the authentication has completed and is_authentic holds
	// the result.
	public boolean get_auth_complete() {
		return complete;
	}

	/// based on the value passed in, determine whether the user should be
	/// authenticaed or not. This is split out into a method because it will
	/// probably be more complex than this.
	private boolean is_user_authentic(double differance) {
		return differance > AUTHENTICATION_THRESHOLD;
	}
}
