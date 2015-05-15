///This thread will call the compare method of chain class. The goal is to compare user chain and auth chain and make the result, pass/fail known. Or do something based on pass/fail such as cause the phone to lock.

public class CompareChains implements runnable{
	final double AUTHENTICATION_THRESHOLD = .7; //TODO find a reasonable / justifiable value for the authentication threshold	

	private Chain user_chain;
	private Chain auth_chain;
	
	/// will need to make copies of the chains passed in so they do not get updated by something else during the comparason
	public CompareChains(Chain user_chain, Chain auth_chain){
		user_chain = new Chain(user_chain);
		auth_chain = new Chain(auth_chain);
	}

	
	///compare user_chain and auth_chain and choose what to do with the result
	@Override
	public void run(){
		double differance;
	
		differance = user_chain.compare_to(auth_chain);
		
		if(!is_user_authentic(differance)){
			//TODO determine what to do when the user fails the authentication
			//user fails the authentication
			System.out.println("User fails to authenticate");
		}
	}

	
	///based on the value passed in, determine whether the user should be authenticaed or not. This is split out into a method because it will probably be more complex than this.
	private boolean is_user_authentic(double differance){
		return differance > AUTHENTICATION_THRESHOLD;
	}
}
