///this class represents the marcov chain. It contains a sequence of touches and a distribution. I avoid doing any processing on touch being added because eventually this will be called on key press in android. Setting it up this way is more flexible to in the sense that processing may be done at any time.
public class Chain{
	private Distribution distribution;
	private List<Distribution> key_distribution;
	
	private List<Touch> touches; // stores a list of all touch objects
	private List<Window> windows; // this seems redundtant at first, but is necessary because a window is not necessarily touch[i,..,i+window]. Factored in are the timestamps associated with each touch.

	private int window;
	private int token;
	private int threshold;

	private boolean distribution_computed;
	private boolean probability_computed;
	private boolean key_distribution_computed;

	public Chain(int window, int token, int threshold){	
		this.key_distribution = new ArrayList<Distribution>();
		this.touches = new ArrayList<Touch>();
		this.windows = new ArrayList<Window>();
		this.window = window;
		this.token = token;
		this.threshold = threshold;
		on_model_update();
	}

	
	///copy constructor. New chain object should have the same state as the old with differant object references.
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
		this.key_distribution_coputed = c.key_distribution_computed
	}


	public void add_touch(Touch touch){
		touches.add(touch);
		on_model_update();
	}


	public void add_touch_list(List<Touch> t){
		touches.addAll(t);
		on_model_update();
	}


	///returns the probability of a given touch based on the model
	public Double get_touch_probability(int i){
		//if the probability has not been computed, compute it
		if(!probability_computed){
			compute_probability();
			probability_computed=true;
		}

		return touches.get(i).get_probability();
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
	public List<Distribution> get_distribution(){
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
	public double compare_to(Chain auth_chain){
		//TODO compare two chains and return the percent difference between them
		double differance = 0;

		return differance;
	}


	///called when the model is updated. keeps track of whether a given attribute needs to be recalculated
	private void on_model_update(){
		distribution_computed=false;
		probability_computed=false;
		key_distribution_computed=false;
	}


	///compute the key distribution
	private void compute_key_distribution(){
		//TODO
		//this involves creating a distribution object for each key
		//1. create a list for each keycode
		//2. create a distribution object from each of these lists
		
	}


	///compute the probability
	private void compute_probability(){
		//TODO
		//assign the appropriate probability to each of the touch objects
	}
}
