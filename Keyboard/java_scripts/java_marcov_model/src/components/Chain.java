package components;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

///TODO compute the windows somewhere. This will be based on the threshold, window, token sizes. This may change distributions? if a touch is thrown out?
///this class represents the marcov chain. It contains a sequence of touches and a distribution. I avoid doing any processing on touch being added because eventually this will be called on key press in android. Setting it up this way is more flexible to in the sense that processing may be done at any time.
///caches the result of each computation so it does not have to be repeated.
public class Chain{
	private Distribution distribution;
	private List<Distribution> key_distribution;
	
	private List<Token> tokens; // tokens into which the range is split
	private List<Touch> touches; // stores a list of all touch objects
	private List<Window> windows; // this seems redundtant at first, but is necessary because a window is not necessarily touch[i,..,i+window]. Factored in are the timestamps associated with each touch.
	private List<Touch> successor_touch; //keep a list of touches that come after windows at the same index

	private int window;
	private int token;
	private int threshold;
	private int model_size;

	private boolean distribution_computed;
	private boolean probability_computed;
	private boolean key_distribution_computed;
	private boolean windows_computed;
	private boolean tokens_computed;

	public Chain(int window, int token, int threshold, int model_size){	
		this.key_distribution = new ArrayList<Distribution>();

		this.tokens = new ArrayList<Token>();
		this.touches = new ArrayList<Touch>();
		this.windows = new ArrayList<Window>();
		this.successor_touch = new ArrayList<Touch>();

		this.window = window;
		this.token = token;
		this.threshold = threshold;
		this.model_size = model_size;

		on_model_update();
	}

	
	///copy constructor. New chain object should have the same state as the old with differant object references.
	//TODO check copy constructors for correctness
	public Chain(Chain c){
		this.key_distribution = new ArrayList<Distribution>(c.key_distribution);
		this.distribution = new Distribution(c.distribution);

		this.touches = new ArrayList<Touch>(c.touches);
		this.windows = new ArrayList<Window>(c.windows);
		
		this.window = c.window;
		this.token = c.token;
		this.threshold = c.threshold;
	
		this.distribution_computed = c.distribution_computed;
		this.probability_computed = c.probability_computed;
		this.key_distribution_computed = c.key_distribution_computed;
		this.model_size = c.model_size;
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


	///returns the probability of a given touch (at the i'th index) based on the model. This will depend on the preceeding touches, in Window. A request for one probability will necessarily result in all of the probabilities being computed.
	public double get_touch_probability(Window w, int i){
		//TODO check for correctness
		//if the probability has not been computed, compute it
		if(!probability_computed){
			compute_probability();
			probability_computed=true;
		}

		return touches.get(i).get_probability(w);
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


	public int get_threshold(){
		return threshold;
	}
	

	///returns a sort of percent difference between this model and the one passed in. The idea is that this may be used to authenticate. Most of this code should come from Model_Compare.py
	//TODO consider doing this on multiple threads if preformance is an issue
	public double compare_to(Chain auth_chain){
		//TODO compare two chains and return the percent difference between them
		//make sure to use the get_x() methods here instead of just using the instance variables. This will guarentee that the values have been calculated by the time they are used.
		double differance = 0;

		return differance;
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
	//TODO consider splitting this up across multiple threads if preformance is an issue. I'm fairly certain this will be the main performance concern.
	private void compute_probability(){
		//TODO
		//assign the appropriate probability to each of the touch objects
		//TODO consider computing windows on a separate thread, and joining this thread before windows are needed.
		//TODO implementing hashing of the windows later
		// basic process
		// for a given window, I want to store in the next touch the probability of that touch coming after this window. This will depend on the other touches which have succeeded this sequence and the number of times the window occurrs. 
		// 1) get a list of windows
		// 2) determine how many times each of the windows occurrs
		// 3) assign a probability to the successor touch based on 1,2
		List<Window> window_list = get_windows();
		int occurrences_of_window;
		double number_successions;
		double probability;

		for(int i=0;i<window_list.size();i++){
			//get the number of occurrences of this window
			occurrences_of_window = occurrence_count(window_list, window_list.get(i));
			
			//get the number of times a touch has succeeded this window. We can use the old probability following this window to figure this out. TODO if this method turns out not be correct, this would be a good place to begin looking for mistakes.
			number_successions = 1 + (successor_touch.get(i).get_probability(window_list.get(i)) * occurrences_of_window);

			//compute the probability
			probability = (number_successions) / ((double)occurrences_of_window);

			//TODO update the probability of the other touches with this successor as well?
			//set the probability of the successor touch. To do this, I need to know how many times this touch succeeds this window
			successor_touch.get(i).set_probability(window_list.get(i), probability);
		}
	}

	
	///return the number of occurrences of w in window_list
	private int occurrence_count(List<Window> window_list, Window w){
		//TODO check for correctness
		int occurrences=0;
		
		for(int i=0;i<window_list.size();i++){
			//determine if the windows are equal
			if(window_list.get(i).compareTo(w)==0){
				occurrences++;
			}
		}

		return occurrences;
	}


	///compute the windows. This will also fill the successor_touch list
	private void compute_windows(){
		//TODO check for correctness
		// this takes into account the time delay between touches when adding them to windows. There may be fewer (windows*window_size) than the total number of touches. This is because if there is too long a delay between touches, the window is simply thrown out.
		// 1) normalize the data based on the distribution (this is done already. can call tokens.get(i).contains(touch) to determine if a touch is within a given token.
		// 2) throw out anything outside of 2 sigma ( these will have -1 returned when they are normalized
		// 3) throw out any window where the gap in touches is greater than threshold
		windows = new ArrayList<Window>();
		successor_touch = new ArrayList<Touch>();
		List<Touch> touch_list = new ArrayList<Touch>();

		//for each of the touches (they are in order)
		for(int i=0; i<touches.size(); i++){
			//TODO take into account that touches are also not good if they fall outside of their keycode distribution, or the overall distribution
			//if the touch is good, add it to the touch list. A touch is good if it is within threshold time and it is contained in one of the tokens.
			if(	(get_token_index(touches.get(i)) >=0 ) && 
				((touch_list.size()==0) || ((touches.get(i).get_timestamp()-touch_list.get(touch_list.size()-1).get_timestamp()) <= threshold)))
			{
				//the touch is good
				touch_list.add(touches.get(i));
			}else{
				//the touch is no good. Reset the touch list
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


	///returns the index corresponding to the token which contains touch. returns -1 if no token contains touch
	private int get_token_index(Touch touch){
		//TODO check for correctness
		List<Token> token_list = get_tokens();

		for(int i=0;i<token_list.size();i++){
			if(token_list.get(i).contains(touch)){
				return i;
			}
		}
		
		return -1;
	}


	///handle requests for windows
	private List<Window> get_windows(){
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

		for(int i=0; i<token; i++){
			tokens.add(new Token(get_distribution(),token,i));
		}
	}


	///handle requests for tokens
	private List<Token> get_tokens(){
		//if tokens have not been computed, compute them
		if(!tokens_computed){
			compute_tokens();
			tokens_computed = true;
		}

		return tokens;
	}
	
	
	///NOT USEFUL IN ANDROID. This is used for debugging purposes. Outputs the model to a csv file in a readable format.
	public void output_to_csv(){
		PrintWriter output=null;
		
		try {
			output = new PrintWriter("test_model_construction.txt", "UTF-8");
			
			output.println("[preceeding sequence] [touch pressure, probability]");
			for(int i=0;i<windows.size();i++){
				String predecessor_window = windows.get(i).toString();
				double touch_probability = successor_touch.get(i).get_probability(windows.get(i));
				double touch_pressure = successor_touch.get(i).get_pressure();
				
				//output.print("-");
				output.println("["+predecessor_window+"] ["+touch_pressure+", "+touch_probability+"]");
			}
			
			output.close();
		} catch (Exception e) {
			System.out.println("Failed to open output file");
			e.printStackTrace();
		}
	}
}