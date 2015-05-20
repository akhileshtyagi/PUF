package runtime;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import components.Chain;
import components.Touch;

///TODO write a HashList class (most likely extends HashMap and implemetns list to store the hash of everything. Replace ArrayList with this class whereever arraylist is used. The other option is to use LinkedHashList.
/// handles building of the model based on input events. This may not be necessary in android framework, but it will allow a consistant way of building the model across platforms to allow for easier migration to android device.
/// whenever I add a touch, I take

public class ChainBuilder{
	final int USER_MODEL_SIZE = 10000;
	final int AUTH_MODEL_SIZE = 1000;
	final int COMPARE_INCREMENT = 1000; // compare the auth_model to the user_model every 1000 new touch inputs handled.
	final boolean INCREMENTAL_AUTHENTICATION_ON = false; // set to true causes the class to authenticate automatically. In some situations this may be preferred

	final int WINDOW = 3; //TODO find the best values for these
	final int TOKEN = 20;
	final int THRESHOLD = 1000;

	private Chain user_chain;
	private Chain auth_chain;	
	

	public ChainBuilder(){
		user_chain = new Chain(WINDOW, TOKEN, THRESHOLD, USER_MODEL_SIZE);
		auth_chain = new Chain(WINDOW, TOKEN, THRESHOLD, AUTH_MODEL_SIZE);
	}

	
	///this method should be called in some way whenever there is a touch event in android. There should be minimal amounts of processing done here so the input to the device doesn't lag.
	///I don't know by what method percicely this will need to be called in the android souce. It could be another class which simply handles touch events, or from the pre-existing android archetecture.
	public void handle_touch(Touch touch){		
		// add the touch to both chains
		static int count = 0;

		///need to ensure that each gets their own version of the same object
		user_chain.add_touch(new Touch(touch));
		auth_chain.add_touch(new Touch(touch));

		//every so often we want to trigger an authentication if this feature is enabled
		if((count == COMPARE_INCREMENT) && INCREMENTAL_AUTHENTICATION_ON){
			authenticate();
			count==0;
		}
		count++;
	}


	///allow forced authentication from outside of ChainBuilder. this involves starting the CompareChains
	public void authenticate(){
		//TODO check for correctness. Am i startign the thread correctly?
		CompareChains cc = new CompareChains(user_chain, auth_chain);
		Thread auth_thread = new Thread(cc);

		auth_thread.start();
	}


	///this code will NOT BE USEFULL ON ANDROID. It will build the model from a csv file in the current working directory. It will however utilize the handle_touch() method to add new touches to the chain. It is simply a matter of where the touches are coming from.
	///TODO move this method to another place. it is only by convience that it exists here now.
	public void build_chain_from_csv(File file){
		//TODO check for correctness
		// read in data from csv file and package them in touches. Then call handle_touch();
		List<Touch> touches = parse_csv(File file);
		
		//add each of the items to the chain one at a time
		Iterator<Touch> touch_iterator = touches.iterator();

		while(touch_iterator.hasNext(){
			handle_touch(touch_iterator.next());
		}
	}


	///parse the csv file NOT USEFULL ON ANDROID
	private List<Touch> parse_csv(File file){
		ArrayList<Touch> touches = new ArrayList<Touch>();

		//add everything in the arraylist to thouches
		Scanner scanner = new Scanner(file);

		while(scanner.hasNext()){
			//TODO parse the input. Need to know 1) where keycode values are  2) where touch pressure, timestamp are.
			
		}

		return touches;
	}
}
