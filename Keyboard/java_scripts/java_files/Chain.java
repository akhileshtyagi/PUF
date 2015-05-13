///this class represents the marcov chain. It contains a sequence of touches and a distribution
public class Chain{
	private Distribution distribution;
	private ArrayList<Touch> touches;
	private window;
	private token;
	private threshold;
	boolean updated;

	public Chain(int window, int token, int threshold){
		this.window = window;
		this.token = token;
		this.threshold = threshold;
		updated=true;
	}

	public void add_touch(Touch touch){
		touches.add(touch);
		updated=true;
	}

	public void add_touch_list(List<Touch> t){
		touches.addAll(t);
		updated=true;
	}

	public Distribution calculate_distribution(){
		//TODO
		if(updated){
			//TODO calculate distribution here
		}else{
			return distribution;
		}
		updated=false;
	}

	public int get_window(){
		return window;
	}

	public int get_token(){
		return token;
	}

	public int get threshold(){
		return threshold;
	}
	
	///returns a sort of percent difference between this model and the one passed in. The idea is that this may be used to authenticate
	public double compare(Chain auth_chain){
		//TODO
	}
}
