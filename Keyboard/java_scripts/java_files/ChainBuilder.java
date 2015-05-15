/// handles building of the model based on input events. This may not be necessary in android framework, but it will allow a consistant way of building the model across platforms to allow for easier migration to android device.
/// whenever I add a touch, I take

public class ChainBuilder{
	final int USER_MODEL_SIZE = 10000;
	final int AUTH_MODEL_SIZE = 1000;
	final int COMPARE_INCREMENT = 1000; // compare the auth_model to the user_model every 1000 new touch inputs handled.

	final int WINDOW = 3; //TODO find the best values for these
	final int TOKEN = 20;
	final int THRESHOLD = 1000;

	private Chain user_chain;
	private Chain auth_chain;	
	

	public ChainBuilder(){
		user_chain = new Chain(WINDOW, TOKEN, THRESHOLD);
		auth_chain = new Chain(WINDOW, TOKEN, THRESHOLD);
	}

	
	///this method should be called in some way whenever there is a touch event in android. There should be minimal amounts of processing done here so the input to the device doesn't lag.
	public void handle_touch(Touch touch){
		//TODO add the touch to both chains, sliding if necessary.
	}


	///this code will NOT BE USEFULL ON ANDROID. It will build the model from a csv file in the current working directory. It will however utilize the handle_touch() method to add new touches to the chain. It is simply a matter of where the touches are coming from.
	///TODO move this method to another place. it is only by convience that it exists here now.
	public void build_chain_from_csv(){
		//TODO read in data from csv file and package them in touches. Then call handle_touch();
	}


	///starts the CompareChains thread
	private void compare_chains(){
		CompareChains compare_thread = new CompareChains(user_chain, auth_chain);
		compare_thread.run();
	}
}
