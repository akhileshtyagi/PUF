package components;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import computation.Confidence;
import computation.DistanceVector;
import trie.TrieList;

//TODO make the chain's compare_to method be able to update incrementally
//TODO make sure to use get_XXXXXX() instead of the instance variables
//TODO put windows into a Trie data structure for building model faster
//TODO anywhere where I need to compare windows, or Touches I need the option to do this with tokens
//there needs to be a way to set the distribution used for a chain. This is because the authentication chain is evaluated with the distribution of the base chain.
//compute the windows somewhere. This will be based on the threshold, window, token sizes. This may change distributions? if a touch is thrown out?
//this class represents the marcov chain. It contains a sequence of touches and a distribution. I avoid doing any processing on touch being added because eventually this will be called on key press in android. Setting it up this way is more flexible to in the sense that processing may be done at any time.
//caches the result of each computation so it does not have to be repeated.

/** Markov Chain built using keyboard tokens.
 * This class was designed to be used with the keyboard
 * of a mobile phone. The soft keyboard of this device
 * produces (key, pressure) values. These (key, pressure)
 * values become the tokens in our Marcov Chain.
 */
public class Chain{
	/* define enums for setting chain functions */
	// weighted or unweighted by frequency with which a window occurs
	public enum WindowAveraging {
		UNWEIGHTED,
		WEIGHTED
	}

	// weighted or unweighted by frequency with which a token occurs
	public enum TokenAveraging {
		UNWEIGHTED,
		WEIGHTED
	}

	/* set various model parameters here */
	private final WindowAveraging WINDOW_AVERAGING = WindowAveraging.WEIGHTED;
	private final TokenAveraging TOKEN_AVERAGING = TokenAveraging.WEIGHTED;

	/* set other variables here */
	private final Token.Type TOKEN_TYPE = Token.Type.keycode_mu; //Token.Type.linear;

	/* define instance variables */
	private Distribution distribution;
	private List<Distribution> key_distribution;
	
	private volatile List<Token> tokens; // tokens into which the range is split
	private volatile Map<Integer, List<Token>> token_map;
	private List<Touch> touches; // stores a list of all touch objects
	private volatile List<Window> windows; // this seems redundtant at first, but is necessary because a window is not necessarily touch[i,..,i+window]. Factored in are the timestamps associated with each touch.
	private volatile List<Touch> successor_touch; //keep a list of touches that come after windows at the same index

	private int window;
	private int token;
	private int threshold;
	private int model_size;

	private volatile boolean distribution_computed;
	private volatile boolean probability_computed;
	private volatile boolean key_distribution_computed;
	private volatile boolean windows_computed;
	private volatile boolean tokens_computed;

	public Chain(int window, int token, int threshold, int model_size){	
		this.key_distribution = new ArrayList<Distribution>();

		this.tokens = new ArrayList<Token>();
		this.token_map = new HashMap<>();
		this.touches = new ArrayList<Touch>();
		this.windows = new TrieList();
		this.successor_touch = new ArrayList<Touch>();

		this.window = window;
		this.token = token;
		this.threshold = threshold;
		this.model_size = model_size;

		on_model_update();
	}

	
	///copy constructor. New chain object should have the same state as the old with differant object references.
	public Chain(Chain c){
		this.key_distribution = new ArrayList<Distribution>(c.key_distribution);
		this.distribution = new Distribution(c.distribution);

		this.tokens = new ArrayList<Token>(c.tokens);
		this.touches = new ArrayList<Touch>(c.touches);
		this.windows = new TrieList((TrieList)c.windows);
		this.successor_touch = new ArrayList<Touch>(c.successor_touch);
		
		this.window = c.window;
		this.token = c.token;
		this.threshold = c.threshold;
		this.model_size = c.model_size;
	
		this.windows_computed = c.windows_computed;
		this.distribution_computed = c.distribution_computed;
		this.probability_computed = c.probability_computed;
		this.key_distribution_computed = c.key_distribution_computed;
		this.model_size = c.model_size;
		this.tokens_computed = c.tokens_computed;
	}


	public void add_touch(Touch touch){
		//TODO check for correctness		
		// handle sliding of the model if adding this touch brings us beyond model_size
		touches.add(touch);

		if(touches.size() > model_size){
			touches.remove(0);
		}

		on_model_update();
	}


	public void add_touch_list(List<Touch> t){
		//TODO check for correctness
		Iterator<Touch> touch_iterator = t.iterator();

		while(touch_iterator.hasNext()){		
			add_touch(touch_iterator.next());
		}
	}


	///allows distribution to be set. If no distribution is set, the distribution for this chain of touches is computed.
	///NOTE the distribution is not maintained when new touches are added.
	public void set_distribution(Distribution distribution, List<Distribution> key_distribution){
		this.distribution = distribution;
		this.key_distribution = key_distribution;
		
		distribution_computed=true;
		key_distribution_computed=true;
		
		//will need to recompute the tokens because they depend on the distribution
		tokens_computed=false;
		
		//TODO is it necesary to recompute these as well
		windows_computed=false;
		probability_computed=false;
	}
	
	
	///returns the probability of a given touch (at the i'th index) based on the model. This will depend on the preceeding touches, in Window. A request for one probability will necessarily result in all of the probabilities being computed.
	public double get_touch_probability(Window w, Touch t){
		//TODO check for correctness
		//if the probability has not been computed, compute it
		if(!probability_computed){
			compute_probability();
			probability_computed=true;
		}
				
		if(touches.size() == 0){
			return 0;
		}
		
		if(t == null){
			return 0;
		}
		
		//find touch in the successors list
		Touch successor = null;
		Window predecessor = null;
		for(int i=0;i<successor_touch.size();i++){
			//System.out.println(successor_touch.get(i));
			//System.out.println(t);
			if((successor_touch.get(i).compare_with_token(get_tokens(), t)) &&
					(windows.get(i).compare_with_token(this.get_tokens(), w))){
				successor = successor_touch.get(i);
				predecessor = windows.get(i);
				break;
			}
		}
		
		if((successor == null) || (predecessor == null)){
			//System.out.println("zergling");
			return 0;
		}
		
		return successor.get_probability(this.get_tokens(), predecessor);
	}


	///returns the distribution of the data as a whole
	public Distribution get_distribution(){
		//if the distribution has not been computed, compute it
		if(!distribution_computed){
			distribution=new Distribution(touches);
			distribution_computed=true;
		}
		
		return distribution;
	}

	
	///returns a list of distributions for each key
	public List<Distribution> get_key_distribution(){
		//if the key_distribution has not been computed, compute it
		if(!key_distribution_computed){
			compute_key_distribution();
			key_distribution_computed=true;
		}
		
		return key_distribution;
	}


	public int get_window(){
		return window;
	}


	public int get_token(){
		return token;
	}
	
	
	public int get_model_size(){
		return model_size;
	}


	public int get_threshold(){
		return threshold;
	}

	
	///resets the object.. this is the same as constructing a new chain, but faster
	public void reset(){
		touches= new ArrayList<Touch>();
				
		on_model_update();
	}

	///computes all uncomputed aspects of the chain
	public void compute_uncomputed(){
		//TODO eventually I want to do this on multiple threads
		get_distribution();
		get_key_distribution();
		get_tokens();
		get_windows();
		get_touch_probability(null,null);
	}

	/**
	 * computes the confidence interval for this chain.
	 * this is defined as a number between 0.0 and 1.0
	 * 1.0 is maximally confident
	 * 0.0 is no confidence
	 *
	 * confidence is computed over data currently in the chain
	 * (NOT ACTUALLY A VALUE BETWEEN 0.0 AND 1.0)
     */
	public double get_confidence(){
		// invalidate all computations and re-compute
		on_model_update();
		compute_uncomputed();

		// use the confidence healper class to preform the computation
		return Confidence.compute_confidence(this);
	}

	/**
	 * compute the confidence interval for the difference
	 * between this chain and auth_chain.
	 *
	 * This will help to understand the variance in the difference
	 * between the data sets
     */
	public double get_distance_confidence(Chain auth_chain){
		// invalidate all computations and re-compute
		// this
		on_model_update();
		compute_uncomputed();

		// auth_chain ( this makes sure that both chains are using the same distribution ) => ( same windows and tokens )
		//set the distribution of the auth_chain based on the base chain
		auth_chain.set_distribution(this.get_distribution(), this.get_key_distribution());

		//begin the auth chain computations
		auth_chain.tokens_computed=false;
		auth_chain.windows_computed=false;
		auth_chain.probability_computed=false;

		// compute uncomputed values of auth chain
		auth_chain.compute_uncomputed();

		// compute the distance vector for the two chains
		DistanceVector distance_vector = new DistanceVector(this, auth_chain);

		// next, ask Confidence to compute the confidence for this distance vector
		return Confidence.compute_confidence(distance_vector);
	}

	///returns the percent difference between this chain and auth_chain.
	///the value returned will be between 0 and 1
	///0 indicates there is no difference
	///1 indicates there is a large difference
	/// compare should not return the same thing both directions
	/// this is due to the windows in auth chain being compared against base chain
	/// base chain may have more or different windows
	public double compare_to(Chain auth_chain){
		//TODO do this in a way that actually makes use of multipe threads
		double difference = 0;

		//recompute the distributions incase set_distribution has been called on this chain
		//call on_model_update() invalidate any previous calculations
		on_model_update();
		
		//calculate all uncalculated quantities
		compute_uncomputed();
		
		//set the distribution of the auth_chain based on the base chain
		auth_chain.set_distribution(this.get_distribution(), this.get_key_distribution());
		
		//begin the auth chain computations
		auth_chain.tokens_computed=false;
		auth_chain.windows_computed=false;
		auth_chain.probability_computed=false;

		auth_chain.compute_uncomputed();

		//windows depend on the distribution because tokens are created over the distribution
		//therefore if no windows were created, then the chains are very unequal... The distribution of the second chain does not intersect the first
		if(auth_chain.get_windows().size()==0){
			//furthest separation
			return 1;
		}

		// window is a TrieList, we need to use this property to lookup occurences of windows
		TrieList auth_window_list = (TrieList)auth_chain.get_windows();
		TrieList base_window_list = (TrieList)this.get_windows();
		double window_weight = 1;

		// create list of indexes of unique windows in auth_chain
		List<Integer> unique_auth_windows = compute_unique_windows(auth_chain.get_tokens(), auth_chain.get_windows());

		// decide to use window averaging or not
		if(WINDOW_AVERAGING == WindowAveraging.UNWEIGHTED) {
			//for every window in auth_chain
			double total_difference = 0;

			for (int i = 0; i<unique_auth_windows.size(); i++) {
				// find the difference between base_chain and auth_chain's corresponding window
				// multiply by number of successors, this will allow tokens to be weighted, without weighting windows
				//total_difference += auth_window_list.get_index_list(auth_window_list.get(unique_auth_windows.get(i))).size() *
				total_difference += get_window_difference(
						auth_window_list.get(unique_auth_windows.get(i)),
						base_window_list, auth_window_list,
						this.successor_touch, auth_chain.successor_touch);

//				System.out.println("window_difference: " +get_window_difference(
//						auth_window_list.get(unique_auth_windows.get(i)),
//						base_window_list, auth_window_list,
//						this.successor_touch, auth_chain.successor_touch));
			}

//			total_difference = 0;
//			for (int i = 0; i < auth_chain.get_windows().size(); i++) {
//				//TODO old method which works
//				total_difference += get_window_successor_difference(this.get_windows(), this.successor_touch, auth_chain.get_windows().get(i), auth_chain.successor_touch.get(i));
//			}

			difference = (total_difference==0) ? 0 : total_difference/((double)auth_chain.get_windows().size());
		}else if(WINDOW_AVERAGING == WindowAveraging.WEIGHTED){
			// compare the successor lists for each unique window
			double total_difference = 0;
			//double sum = 0.0;
			for(int i=0; i<unique_auth_windows.size(); i++){
				// compute the window weight, occurrences of window / total windows, in auth model
				window_weight = ((double) auth_window_list.occurrence_count(auth_window_list.get( unique_auth_windows.get(i) ))) / ((double)auth_window_list.size());

				// compute the window difference
				double window_difference = get_window_difference(
						auth_window_list.get(unique_auth_windows.get(i)),
						base_window_list, auth_window_list,
						this.successor_touch, auth_chain.successor_touch);

				//TODO
				//System.out.println("Chain Window difference: " + window_difference * window_weight);

				// add up the weighted difference between the windows \sigma(difference * weight of window)
				total_difference += window_weight * window_difference;

				//sum += window_weight;
			}

			//TODO
			//System.out.println("\t\tcount: " + unique_auth_windows.size());
			//System.out.println("\t\tchain window weight sum: " + sum);

			difference = total_difference;
		}

		//return the average of the window differences
		return difference;

		//System.out.println((difference==0) ? 0 : difference/((double)auth_chain.get_windows().size()));
		//System.out.println(auth_chain.get_windows().size());
		//System.out.println(this.get_windows().size());
	}

	/* compute the Indexes of unique windows in given List<Window>.
	 *
	 * requires a token list to know how to compare windows*/
	public static List<Integer> compute_unique_windows(List<Token> token_list, List<Window> window_list){
		// compute indexes for unique windows in list
		ArrayList<Integer> index_list = new ArrayList<>();

		// add a window only if it does not match any of the previous windows
		for(int i=0; i<window_list.size(); i++){
			// for all existing unique indexes, if none of the windows match, add new index
			boolean no_match = true;
			for(int j=0; j<index_list.size(); j++){
				if(window_list.get(i).compare_with_token(token_list, window_list.get(index_list.get(j)))){
					// window_i and window in list are equal, there is a match
					no_match = false;
					break;
				}
			}

			// if there was no match in the previous loop, add index to list, it is unique
			if(no_match) index_list.add(i);
		}

		// return the list of indexes
		return index_list;
	}

	/* compute the Indexes of unique windows in given List<Window>
	 *
	 * the windows pointed at by successor_list are unique,
	 * the goal is to find the indexes which point to
	 * unique successor touches
	 *
	 * requires a token list to know how to compare touches
	 *
	 * returns a list of indexes corresponding to
	 * windows in successor_list which have a unique successor touch*/
	public static List<Integer> compute_unique_successors(List<Token> token_list, List<Touch> successor_touch, List<Integer> successor_list){
		// compute indexes for unique successor touches in list
		ArrayList<Integer> index_list = new ArrayList<>();

		// add a successor only if it does not match any of the previous windows
		for(int i=0; i<successor_list.size(); i++){
			// for all existing unique indexes, if none of the windows match, add new index
			boolean no_match = true;
			for(int j=0; j<index_list.size(); j++){
				// compare current successor i to every index already in list
				if( successor_touch.get(successor_list.get(i))
						.compare_with_token(token_list, successor_touch.get(index_list.get(j)))){
					// touch_i and touch in list are equal, there is a match
					no_match = false;

					// no need to keep going
					break;
				}
			}

			// if there was no match in the previous loop, add index to list, it is unique
			if(no_match) index_list.add(successor_list.get(i));
		}

		// return the list of indexes
		return index_list;
	}

	/* compute the difference between two windows.
	 * This can be done based on the successor lists of the windows.
	 * Need some way of computing (occur(touch_i) / total(touch)) in auth model
	 *
	 * The weighted version weights by the ratio of
	 * (occurences/total) in the auth list.*/
	private double get_window_difference(Window window, TrieList base_window_list, TrieList auth_window_list, List<Touch> successor_list_base, List<Touch> successor_list_auth){
		double difference = 0;

		// get a List<Integer> of all occurrences of the given unique window
		// this list also gives the index of the successor touch stored in the parallel arraylist
		List<Integer> index_list_auth = compute_unique_successors(get_tokens(), successor_list_auth, auth_window_list.get_index_list(window));

		// to get the successors for the same window in base model,
		// need to first find an instance of this window,
		// then the successors of the found instance may be gotten
		// actually, using the same window from the auth model should work because
		// both base and auth model used the same distribution to compute their tokens
		List<Integer> index_list_base = compute_unique_successors(get_tokens(), successor_list_base, base_window_list.get_index_list(window));

		// handle the case when the window does not exist in the base model
		if(index_list_base.size() == 0){
			// the window does not exist in the base model
			// furthest possible difference
			return 1;
		}

		// compute the difference between w_base and w_auth
		// do token weighting here
		if (TOKEN_AVERAGING == TokenAveraging.UNWEIGHTED) {
			// unweighted version of token averaging
			// difference is simply for all tokens: |p_i - p`_i| where
			// p_i is base model probability
			double total_difference = 0;
			// for all successor touches of this window in the auth model
			for(int i=0; i<index_list_auth.size(); i++) {
				// get base and auth probabilities
				// base tokens should be okay to use here because distributions and thus tokens are set to be the same
				// between the base and the auth models
				double auth_probability = successor_list_auth.get(index_list_auth.get(i)).get_probability(this.get_tokens(), window);

				// get base probability
				double base_probability = 0;
				// getting base_probability is incorrect, the index_lists don't necessarily correspond on i
				// want the probability of getting the same successor touch in the base model
				// for all successor touches of base
				int base_touch_index = -1;
				for(int j=0; j<index_list_base.size(); j++){
					// determine if there is a touch which matches the auth touch
					if( successor_list_base.get(index_list_base.get(j))
							.compare_with_token(this.get_tokens(), successor_list_auth.get(index_list_auth.get(i))) ){
						// they do match, this the index in index_list_base which corresponds to the index in index_list_auth
						base_touch_index = j;
						break;
					}
				}

				// if there is no such touch, base_probability is 0
				if(base_touch_index == -1){
					// no touch was found
					base_probability = 0;
				}else{
					// matching touch was found
					base_probability = successor_list_base.get(index_list_base.get(base_touch_index)).get_probability(this.get_tokens(), window);
				}

				//TODO these test print statements print out probabilities for checking
				//System.out.println("base_probability: " + base_probability + "\tauth_probability: " + auth_probability);

				// compute absolute difference
				//TODO potentially do token weighting here
				//TODO the current problem with token weighting is that the sum of the weights doesn't equal 1
				total_difference += Math.abs(base_probability - auth_probability);
			}

			//TODO which one sould it be?
			//return total_difference;
			difference = total_difference / ((double)index_list_auth.size());

			//difference = get_corresponding_successor_difference(window, index_list_base, index_list_auth, successor_list_base, successor_list_auth) / ((double)index_list_auth.size());
		} else if(TOKEN_AVERAGING == TokenAveraging.WEIGHTED) {
			//TODO test print statement
			//System.out.println("new window");

			// weighted version of token averaging
			// tokens are weighted by their occurrence in auth model
			double token_weight = 1.0;
			for(int i=0; i<index_list_auth.size(); i++) {
				// get auth probability
				double auth_probability = successor_list_auth.get(index_list_auth.get(i)).get_probability(this.get_tokens(), window);

				// get base probability
				double base_probability = 0;
				// getting base_probability is incorrect, the index_lists don't necessarily correspond on i
				// want the probability of getting the same successor touch in the base model
				// for all successor touches of base
				int base_touch_index = -1;
				for(int j=0; j<index_list_base.size(); j++){
					// determine if there is a touch which matches the auth touch
					if( successor_list_base.get(index_list_base.get(j))
							.compare_with_token(this.get_tokens(), successor_list_auth.get(index_list_auth.get(i))) ){
						// they do match, this the index in index_list_base which corresponds to the index in index_list_auth
						base_touch_index = j;
						break;
					}
				}

				// if there is no such touch, base_probability is 0
				if(base_touch_index == -1){
					// no touch was found
					base_probability = 0;
				}else{
					// matching touch was found
					base_probability = successor_list_base.get(index_list_base.get(base_touch_index)).get_probability(this.get_tokens(), window);
				}

				// token weight is simply the probability in the auth model
				// this is because we are weighting by occurrences and auth_probability represents
				// the fractional amount of time the token occurred
				token_weight = successor_list_auth.get(index_list_auth.get(i)).get_probability(this.get_tokens(), window);

				//TODO ah this unveils the problem. Token weights do not sum to 1. They should within a given window though
				//TODO weight should be [number of tokens / total tokens coming after window]
				//TODO perhaps this is a problem with the probability computation then? (THat would suck_)
				//System.out.println("token weight: " + token_weight);

				// compute absolute difference
				difference += token_weight * Math.abs(base_probability - auth_probability);
				//difference += token_weight * get_corresponding_successor_difference(window, index_list_base, index_list_auth, successor_list_base, successor_list_auth);
			}
		}

		//TODO this should not be greater than 1 but is greater than 1
		//TODO only shows up under WEIGHTED condition
		if(difference > 1){ System.out.println("Should not be here: Chain, window_difference()"); }

		return difference;
	}

	/* return the difference between
	 * the probabilities for the corresponding touch in base_window_list and auth_window_list.
	// index_list_base gives all indexes in successor_list_base which are successors of the corresponding window
	// index_list_auth gives all indexes in successor_list_auth which are successors of the corresponding window
	// successor_list_base gives all seccessor touches for all windows in base model
	// successor_list_auth gives all successor touches for all windows in auth model
	 *
	 * the absolute difference between the probabilities will be returned.
	  * the returned value is 0<= x <= 1*/
	//TODO change this method to be done for a single successor
	private double get_corresponding_successor_difference(Window window, List<Integer> index_list_base, List<Integer> index_list_auth, List<Touch> successor_list_base, List<Touch> successor_list_auth){
		//TODO there is duplicated code above which could go here, I will do this simplification later
		return 0;

		//TODO these test print statements print out probabilities for checking
		//TODO print out all necessary information to know if the difference was taken correctly
		//TODO difference or total_difference, window, successor touch, successor touch probability
//		System.out.println("Auth Window Set:");
//		for(int k=0; k<index_list_auth.size(); k++){
//			//System.out.print("\t" + "window: " + auth_window_list.get(index_list_auth.get(k)).toString() );
//
//			System.out.print("\t[");
//			System.out.print("successor: " +successor_list_auth.get(index_list_auth.get(k)).toString());
//			System.out.print(", " + "probability: " +successor_list_auth.get(index_list_auth.get(k)).get_probability(this.get_tokens(), window));
//			System.out.print("]");
//
//			System.out.println();
//		}
//
//		System.out.println("Base Window Set:");
//		for(int k=0; k<index_list_base.size(); k++){
//			//System.out.print("\t" + "window: " + base_window_list.get(index_list_base.get(k)).toString() );
//
//			System.out.print("\t[");
//			System.out.print("successor: " +successor_list_base.get(index_list_base.get(k)).toString());
//			System.out.print(", " + "probability: " +successor_list_base.get(index_list_base.get(k)).get_probability(this.get_tokens(), window));
//			System.out.print("]");
//
//			System.out.println();
//		}

		//TODO test print
		//System.out.println("difference: " + total_difference ); // / ((double)index_list_auth.size()));
	}

	///return the difference between two given windows with following successor touch
	///@param base window successor touch is the touch coming after base_window in the base model
	///@param auth window_touches are the touches which succeeds auth_window
	/// base_window_successor and auth_window_successor should be equivilent. This method simply returns the difference in their probabilities.
	/// the reason this method is broken out is because this is likely to be modified to refine the model
	private double get_window_successor_difference(List<Window> base_window_list, List<Touch> base_successor_touch_list, Window auth_window, Touch auth_window_successor_touch){
		//TODO this can deffonatly be made more effecient
		//TODO this can be done by using get_index_list() function of base_window_list
		//we want to know the differences in touch probability for the touches which come after these windows
		double difference = 0;

		// What it sould be doing is:
		//  find the auth window in base_window_list
		//  compare the probability of the successor touches
		//  windows and successor touches need to match
		int index = -1;
		for(int i=0;i<base_window_list.size();i++){
			if((base_window_list.get(i).compare_with_token(this.get_tokens(), auth_window)) && (base_successor_touch_list.get(i).compare_with_token(this.get_tokens(), auth_window_successor_touch))){
				index=i;
				break;
			}
		}

		double base_probability;
		if(index==-1){
			// this stands for the probability in the base window
			//auth window not found in base_window; hense the difference is maximum
			base_probability = 0;
		}else{
			//found it! now determine the probability of the same touch coming after
			base_probability = base_successor_touch_list.get(index).get_probability(this.get_tokens(), base_window_list.get(index));

			//TODO might just be able to use auth window
			//base_probability = base_successor_touch_list
		}
		
		double auth_probability = auth_window_successor_touch.get_probability(this.get_tokens(), auth_window);
		
		//System.out.println("base_p:"+base_probability+" auth_p:"+auth_probability);
		
		// take the absolute value because we are adding up
		// the total difference between the edges of both models
		difference = Math.abs(base_probability - auth_probability);
		
		return difference;
	}
	
	//returns non -1 only if successor touches are the same for the given window
	//returns the index of auth_window if it is contained in base window
	//will only return the index if the successor touches are equivilent
	//returns -1 if not contained
	///this function will search between base start index, and base end index
	private int get_base_window_index(Chain base_chain, Window auth_window, Touch auth_successor_touch, int base_start_index, int base_end_index){
		//compre with tokens
		int i=base_start_index;
		
		List<Window> base_windows = base_chain.get_windows();
		List<Touch> base_successor_touch = base_chain.successor_touch;
		
		for(i=base_start_index;(i<base_windows.size()) && (i<base_end_index);i++){
			//if the successor touches are the same 
			if(base_successor_touch.get(i).compare_with_token(this.tokens, auth_successor_touch)){
				//also if the windows are equal
				if(base_windows.get(i).compare_with_token(this.get_tokens(), auth_window)){
					break;
				}
			}

		}
		
		return (i==base_windows.size())?(-1):(i);
	}


	///called when the model is updated. keeps track of whether a given attribute needs to be recalculated
	private void on_model_update(){
		distribution_computed=false;
		probability_computed=false;
		key_distribution_computed=false;
		windows_computed=false;
		tokens_computed=false;
	}


	///compute the key distribution
	private void compute_key_distribution(){
		//TODO check for correctness
		//this involves creating a distribution object for each key
		//1. create a list for each keycode
		//2. create a distribution object from each of these lists
		//reset key_distribution to an empty array
		key_distribution = new ArrayList<Distribution>();
		ArrayList<List<Touch>> list_of_keycodes = new ArrayList<List<Touch>>();

		//for the entire list of touches. After this loop, all keycodes should be placed in a separate list in list_of_keycodes
		for(int i=0;i<touches.size();i++){
			//get the index of the current keycode
			int keycode_index = keycode_index(list_of_keycodes, touches.get(i).get_key());

			if(keycode_index==-1){
				//the keycode does not already exist in list of keycodes, add it
				ArrayList<Touch> single_touch_list = new ArrayList<Touch>();
				single_touch_list.add(touches.get(i));
				
				list_of_keycodes.add(single_touch_list);
			}else{
				//the keycode already exits. Simply add it to the array at keycode_index
				list_of_keycodes.get(keycode_index).add(touches.get(i));
			}
		}
		
		//create a distribution object for each of the lists in list of keycodes, add this to key distribution
		for(int i=0;i<list_of_keycodes.size();i++){
			key_distribution.add(new Distribution(list_of_keycodes.get(i), list_of_keycodes.get(i).get(0).get_key()));
		}

		//denote that the distribution has been computed
		key_distribution_computed=true;
	}


	///returns the index of a given keycode in the list containing lists of touches corresponding to a given key. Returns -1 if the keycode does not exist in the list.
	//TODO this seems like it could be done in less work, could save the indexes in some sort of data structure and retrieve them when needed
	//TODO turn this into a list of size of unique keycodes, then take keycode % keycodes in order to find the index ? this doesn't work but its close
	private int keycode_index(ArrayList<List<Touch>> list_of_keycodes, int keycode){
		//TODO check for correctness
		int index;		
		
		for(index=0;index<list_of_keycodes.size();index++){
			if(list_of_keycodes.get(index).get(0).get_key() == keycode){
				break;
			}
		}

		return (index==list_of_keycodes.size())? -1: index;
	}


	///compute the probability
	private void compute_probability(){
		//ensure windows are computed
		//if there are now windows, probabality is undefined, simply return
		if(this.get_windows().size()==0){
			return;
		}
		
		//create threads which will preform the probability computation
		ArrayList<Thread> threads = new ArrayList<Thread>();
		ExecutorService executor = Executors.newCachedThreadPool();
		//TODO write a program to determine the optimal number of loopse per thread
		int thread_responsibility = 100;
		
		// create all the threads and begin executing them
		// each thread has responsibility fro some of the loops
		for(int i=0;i<windows.size();i+=thread_responsibility){
			int end_index = (i+thread_responsibility)-1;
			
			if(end_index >= windows.size()){
				//System.out.println("here");
				end_index = windows.size()-1;
			}
			
			Runnable compute_partial = new Compute_partial_probability(i, end_index);
			Thread partial_thread = new Thread(compute_partial);
			
			executor.execute(partial_thread);
		}
		
		executor.shutdown();
		while(!executor.isTerminated()){}
	}
	
	
	///Thread used to compute the probabilitys
	private class Compute_partial_probability implements Runnable {
		int begin_index;
		int end_index;
		
		public Compute_partial_probability(int begin_index, int end_index){
			this.begin_index = begin_index;
			this.end_index = end_index;
		}
		
		
		public void run(){
			//assign the appropriate probability to each of the touch objects
			// basic process
			// for a given window, I want to store in the next touch the probability of that touch coming after this window. This will depend on the other touches which have succeeded this sequence and the number of times the window occurrs. 
			// 1) get a list of windows
			// 2) determine how many times each of the windows occurrs
			// 3) assign a probability to the successor touch based on 1,2
			TrieList window_list = (TrieList)windows;
			int occurrences_of_window;
			int number_successions;
			double probability;

			for(int i=begin_index;i<=end_index;i++){
				//get the number of occurrences of this window
				
				//occurrences_of_window = occurrence_count(window_list, window_list.get(i));
				occurrences_of_window = window_list.occurrence_count(window_list.get(i));
				
				//get the number of times a touch has succeeded this window. We can use the old probability following this window to figure this out.
				// old method
				//number_successions = 1 + (successor_touch.get(i).get_probability(window_list.get(i)) * occurrences_of_window);
				
				//number_successions=successor_count(window_list, successor_touch, window_list.get(i), successor_touch.get(i));
				number_successions=window_list.successor_count(successor_touch, window_list.get(i), successor_touch.get(i));
				
				//compute the probability
				probability = ((double)number_successions) / ((double)occurrences_of_window);
				//System.out.println("number_successions:"+number_successions+" occurrences_of_windows:"+occurrences_of_window);
				
				//set the probability of the successor touch. To do this, I need to know how many times this touch succeeds this window
				successor_touch.get(i).set_probability(get_tokens(), window_list.get(i), probability);
			}
		}
	}

	///compute the windows. This will also fill the successor_touch list
	///this does not taken the token rules into account. this is done later in computing the probability
	private void compute_windows(){
		//TODO check for correctness
		// this takes into account the time delay between touches when adding them to windows. There may be fewer (windows*window_size) than the total number of touches. This is because if there is too long a delay between touches, the window is simply thrown out.
		// 1) normalize the data based on the distribution (this is done already. can call tokens.get(i).contains(touch) to determine if a touch is within a given token.
		// 2) throw out anything outside of 2 sigma ( these will have -1 returned when they are normalized
		// 3) throw out any window where the gap in touches is greater than threshold
		windows = new TrieList();
		successor_touch = new ArrayList<Touch>();
		List<Touch> touch_list = new ArrayList<Touch>();
		
		((TrieList)windows).set_tokens(this.get_tokens());
		
		//System.out.println(touches.size());
		//for each of the touches (they are in order)
		for(int i=0; i<touches.size(); i++){
			//TODO, decided against this, take into account that touches are also not good if they fall outside of their keycode distribution, or the overall distribution
			//if the touch is good, add it to the touch list. A touch is good if it is within threshold time and it is contained in one of the tokens.
			if(	(get_token_index(touches.get(i)) >=0 ) && 
					((touch_list.size()==0) || 
						((touches.get(i).get_timestamp()-touch_list.get(touch_list.size()-1).get_timestamp()) <= threshold)) &&
					is_touch_in_key_distribution(touches.get(i)))
			{
				//the touch is good
				touch_list.add(touches.get(i));
			}else{
				//the touch is no good. Reset the touch list
				//System.out.println("marine");
				touch_list= new ArrayList<Touch>();
			}

			//if touch list has grown big enoguh to fill a window, plus 1 for the successor touch, add it to windows
			if(touch_list.size()==window+1){
				//update windows, successor touch	
				//add_list will contain all but the last touch in touch_list
				List<Touch> add_list;
				add_list = new ArrayList<Touch>(touch_list);
				add_list.remove(add_list.size()-1);
			
				windows.add(new Window(add_list));
				successor_touch.add(touch_list.get(touch_list.size()-1));

				touch_list.remove(0);
			}
		}
	}

	
	///returns true if a touch is within 2 sigma for it's key distribution
	public boolean is_touch_in_key_distribution(Touch touch){
		int sigma = 2;
		boolean is_touch_in = false;
		Distribution key_dist = null;
		List<Distribution> key_distributions = get_key_distribution();
		
		//determine if the touch is within 2 sigma for the mu of its key distribution
		//1) get the distribution object for this keycode
		if(key_distributions != null){
			for(int i=0;i<key_distributions.size();i++){
				if(key_distributions.get(i).get_keycode()==touch.get_key()){
					key_dist=key_distributions.get(i);
				}
			}
		}
		
		//2) check to see if the pressure value for this touch is within 2 sigma of mu for this distribution
		if(key_dist != null){
			if((touch.get_pressure() <= (key_dist.get_average()+sigma*key_dist.get_standard_deviation())) &&
					(touch.get_pressure() >= (key_dist.get_average()-sigma*key_dist.get_standard_deviation()))){
				is_touch_in=true;
			}
		}
		
		return is_touch_in;
	}
	

	///returns the index corresponding to the token which contains touch. returns -1 if no token contains touch
	private int get_token_index(Touch touch){
		//TODO check for correctness
		List<Token> token_list = get_tokens();

		//take the first token to return true
		for(int i=0;i<token_list.size();i++){
			if(token_list.get(i).contains(touch)){
				return i;
			}
		}
		
		return -1;
	}


	///handle requests for windows
	public List<Window> get_windows(){
		//if windows have not been computed, compute them
		if(!windows_computed){
			compute_windows();
			windows_computed = true;
		}

		return windows;
	}

	
	///compute the tokens
	private void compute_tokens(){
		tokens = new ArrayList<Token>();
		
		if(TOKEN_TYPE == Token.Type.linear){
			//create a set of tokens which is linear across the distribution within 2 sigma of the center
			for(int i=0; i<token; i++){
				tokens.add(new Token(get_distribution(),token,i, 2,Token.Type.linear));
			}
		}else{
			//create tokens over the keycode set
			for(int i=0;i<get_key_distribution().size();i++){
				//[token] for each keycode fouch touches within 2 sigma of that keycode
				// build a list of tokens for this keycode
				ArrayList<Token> token_list = new ArrayList<>();
				for(int j=0; j<token; j++) {
					token_list.add(new Token(get_key_distribution().get(i), token, i, 2, Token.Type.keycode_mu));
				}

				// add the list of tokens for this keycode to the token map
				token_map.put(get_key_distribution().get(i).get_keycode(), token_list);
			}
		}
	}


	///handle requests for tokens
	public List<Token> get_tokens(){
		//if tokens have not been computed, compute them
		if(!tokens_computed){
			compute_tokens();
			tokens_computed = true;
		}

		return tokens;
	}
	

	///get a list of all touches in the chain
	public List<Touch> get_touches(){
		return touches;
	}

	/// return the successor touch list
	public List<Touch> get_successors() {
		// make sure successor touch have been computed
		if(!windows_computed) {
			get_windows();
		}

		return this.successor_touch;
	}
	
	///prints out all of the touches in order
	@Override
	public String toString(){
		String s = "";
		
		s+= "[";
		for(int i=0;i<touches.size();i++){
			s+=touches.get(i).toString();
			
			if(i<touches.size()-1){
				s+=", ";
			}
		}
		
		s+="]";
		
		return s;
	}
	
	
	///NOT USEFUL IN ANDROID. This is used for debugging purposes. Outputs the model to a csv file in a readable format.
	public void output_to_csv(String file_name){
		PrintWriter output=null;
		
		try {
			output = new PrintWriter(file_name, "UTF-8");
			
			output.println("[preceeding sequence] [touch pressure, probability]");
			for(int i=0;i<windows.size();i++){
				String predecessor_window = windows.get(i).toString();
				double touch_probability = successor_touch.get(i).get_probability(this.get_tokens(), windows.get(i));
				double touch_pressure = successor_touch.get(i).get_pressure();
				
				//output.print("-");
				output.println("["+ predecessor_window+"] ["+String.format("%.4f", touch_pressure)+", "+String.format("%.4f", touch_probability)+"]");
			}
			
			output.close();
		} catch (Exception e) {
			System.out.println("Failed to open output file");
			e.printStackTrace();
		}
	}

	public void output_by_window(String file_name){
		PrintWriter output=null;

		try {
			output = new PrintWriter(file_name, "UTF-8");

			// print out the confidence in this data
			output.println(String.format("%20s: %.4f", "confidence", this.get_confidence()));

			// print out the model window-by-window
			output.println("[preceeding sequence] [touch key, touch pressure, probability] [token occurrences, preceeding sequence occurrences]");

			List<Integer> unique_windows = compute_unique_windows(this.get_tokens(), this.get_windows());
			TrieList window_list = (TrieList)this.get_windows();

			// for every unique window
			for(int i=0;i<unique_windows.size();i++){
				// get a list of successor touches for this same window
				List<Integer> successor_list = compute_unique_successors(get_tokens(), this.successor_touch, window_list.get_index_list(window_list.get(unique_windows.get(i))));

				output.println("----- window_" + i + " -----");

				// for each successor
				for(int j=0; j<successor_list.size(); j++) {
					String predecessor_window = windows.get(successor_list.get(j)).toString();
					double touch_probability = successor_touch.get(successor_list.get(j)).get_probability(this.get_tokens(), windows.get(successor_list.get(j)));
					double touch_pressure = successor_touch.get(successor_list.get(j)).get_pressure();
					int touch_key = successor_touch.get(successor_list.get(j)).get_key();
					//TODO i think these are correct, but they could not be... think though what these are actually doing
					int preceeding_sequence_occurrences = window_list.occurrence_count(windows.get(successor_list.get(j)));
					int token_occurrences = window_list.successor_count(successor_touch, windows.get(successor_list.get(j)), successor_touch.get(successor_list.get(j)));

					//output.print("-");
					output.print("["+ predecessor_window+"] ["+String.format("%3d", touch_key)+", "+String.format("%.4f", touch_pressure)+", "+String.format("%.4f", touch_probability)+"]");
					output.println(" [" + token_occurrences + ", " + preceeding_sequence_occurrences + "]");
				}
			}

			output.close();
		} catch (Exception e) {
			System.out.println("Failed to open output file");
			e.printStackTrace();
		}
	}
}