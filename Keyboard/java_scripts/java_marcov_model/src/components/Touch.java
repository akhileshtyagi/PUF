package components;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** This class represents a touch event.
 * Touch evens have an associated key, pressure, and time from the raw data.
 * From the Markov Chain we derive the probability and predecessor_window attributes.
 * The probability is a value between 0 and 1 representing the percent change this
 * token follows the predecessor window.
 */
public class Touch implements Comparable<Touch>{
	private int key;
	private double pressure;
	private long timestamp;

	private List<Double> probability; //TODO move parallel arraw to better data structure
	private List<Window> predecessor_window;


	public Touch(int keycode, double pressure, long timestamp){
		this.key=keycode;
		this.pressure=pressure;
		this.timestamp=timestamp;

		probability=new ArrayList<Double>();
		predecessor_window=new ArrayList<Window>();
	}


	///copy constructor
	public Touch(Touch t){
		this.key = t.key;
		this.pressure = t.pressure;
		this.timestamp = t.timestamp;

		this.probability = new ArrayList<Double>(t.probability);
		this.predecessor_window = new ArrayList<Window>(t.predecessor_window);
	}


	///sets the probability that this touch succeeds a given sequence. Reccord the sequence and the probability
	public void set_probability(List<Token> tokens, Window preceeding_window, double p){
		//TODO check for correctness
		//add them at their respective locations
		//if predecessor_window already exists, update the pressure value. If predecessor_window does not exist, then add both predecessor_window and pressure
		int index = 0;

		//search for window in predecessor_window
		for(index=0;index<predecessor_window.size();index++){
			//TODO should this also use compare_with_tokens?
			//TODO the effect of this should be that windows which are considered equal
			//TODO appear only once. This is what I want?
			if(predecessor_window.get(index).compare_with_token(tokens, preceeding_window)){
				break;
			}

			// does this window equal the current window?
			// old method makes exact comparison
			//TODO perhaps it shouldn't be .compare_with_token() because predecessor windows aren't unique?
			//TODO or mabe it it should be ..... the predecessor windows should be unique with .compare_to_token().
//			if(predecessor_window.get(index).compareTo(preceeding_window)==0){
//				break;
//			}
		}

		if(index < predecessor_window.size()){
			//predecessor_window already exists, updateing probability
			probability.set(index, p);
		}else{
			predecessor_window.add(preceeding_window);
			probability.add(p);
		}
	}


	///returns the probability of the touch occurring after a given window w. If the window does not exist return (TODO) currently returning 0
	public double get_probability(List<Token> tokens, Window preceeding_window){
		//TODO check for correctness
		//take in a window and return the probability of this touch coming after that window
		//search for window in predecessor_window
		int index;
		
		for(index=0;index<predecessor_window.size();index++){
			// does this window equal the current window?
			//TODO should this be using compare with token
			if(predecessor_window.get(index).compare_with_token(tokens, preceeding_window)){
				break;
			}

			// old method makes exact comparison
//			if(predecessor_window.get(index).compareTo(preceeding_window)==0){
//				break;
//			}
		}

		if(index < predecessor_window.size()){
			//predecessor_window already exists, updateing probability
			return probability.get(index);
		}else{
			//window does not exist in predecessor window
			//TODO determine what to do. is this correct?
			return 0;
		}
	}


	public double get_pressure(){
		return pressure;
	}


	public int get_key(){
		return key;
	}


	public long get_timestamp(){
		return timestamp;
	}
	
	
	///compares the touches with the given token list.
	///this function will return true if the touches are contained within the smae token
	/* and if the touches have the same keycode */
	public boolean compare_with_token(List<Token> tokens, Touch other_touch){
		Token this_touch_token = null;
		Token other_touch_token = null;
		int i = 0;
		
		while(((this_touch_token == null) || (other_touch_token==null)) && (i< tokens.size())){
			if(tokens.get(i).contains(this)){
				this_touch_token = tokens.get(i);
			}
			
			if(tokens.get(i).contains(other_touch)){
				other_touch_token = tokens.get(i);
			}
			
			i++;
		}

		// check that tokens are the same
		boolean token_equal = this_touch_token.equals(other_touch_token);

		// check that keycodes are the same
		boolean keycode_equal = (this.key == other_touch.key);

		//TODO what would be the effect of removing the keycode check
		return token_equal && keycode_equal;
	}


	///implement hash function for the touch class
	@Override
	public int hashCode(){
		//TODO make this better.
		return (int)(pressure*37);
	}


	//TODO I may have used this to compare only pressure in other parts of the code. If this is the case, I could change this method to only represent differences in pressure.
	///compare touches to one another. return negative if this touch is less than other_touch
	@Override
	public int compareTo(Touch other_touch){
		//TODO check for correctness
		int differance = 0;

		if(this.key != other_touch.key){
			differance++;
		}

		if(this.timestamp != other_touch.timestamp){
			differance++;
		}

		if(!are_lists_equal(this, other_touch)){
			differance++;
		}

		//do this check last so if they are indeed unequal, the differance in the pressure value will be returned (most likly)
		if(this.pressure != other_touch.pressure){
			differance ++;
		}

		return differance;
	}


	///compare the lists contained within two touches for equality. return true if they are equal. Cannot compare predecessor_window here because it will create an infinite loop in calling the compareTo functions back and forth.
	private boolean are_lists_equal(Touch t1, Touch t2){
			//TODO check for correctness, also REWORK THIS FOR EFFICIENCY. THIS IS NOT THE IDEAL WAY TO ACCOMPLISH WHAT I WANT
			int differance = 0;			

			Iterator<Double> this_iterator = t1.probability.iterator();
			Iterator<Double> other_iterator = t2.probability.iterator();

			while(this_iterator.hasNext() && other_iterator.hasNext()){
				double this_touch = this_iterator.next();
				double other_touch = other_iterator.next();

				//compare the touches
				if((this_touch-other_touch) != 0){
					//incrementing differance indicates they are not equal
					differance++;
					break;
				}
			}
			
			differance += (this_iterator.hasNext() || other_iterator.hasNext())?1:0;

			return differance==0;
	}
	
	
	@Override
	public String toString(){
		String string="";
		
		string += String.format("%.4f", pressure);
		
		return string;
	}
}
