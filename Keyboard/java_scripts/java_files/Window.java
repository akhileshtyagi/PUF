///This class will store and provide functions for a single window within the model

public class Window{
	List<Touch> window;
	int window_size;
	int hash; // stores the hash so it doesn't have to be recomputed, unless update

	boolean hash_computed;

	public Window(List<Touch> touches, int window_size){
		window = new ArrayList<Touch>();
		window.addAll(touches);

		this.window_size=window_size;
		hash_computed=false;
	}


	///copy constructor
	public Window(Window w){
		//TODO
	}


	//implement a has function which returns the hash of the current window
	@Override
	public int hashCode(){
		if(!hash_computed){
			compute_hashcode();
			hash_computed=true;
		}
		
		return hash;
	}


	//compute the hashcode based on the touches currently in the window
	private int compute_hashcode(){
		//TODO incorportate hash function in here
	}
}
