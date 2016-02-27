package runtime;

import components.Chain;

/** UNUSED.
 * The Intent of this class was to run specific computations on a differant thread.
 * It is unused in the current implementation because there is never a need to run
 * one computation independent from the others.
 */
public class Operation_thread implements Runnable{
	Chain chain;
	Computation computation;
	
	public enum Computation{
		DISTRIBUTION,
		KEY_DISTRIBUTION,
		WINDOW,
		TOKEN,
		PROBABILITY
	}
	
	public Operation_thread(Chain chain, Computation computation){
		this.chain=chain;
		this.computation = computation;
	}
	
	
	@Override
	public void run(){
		switch(computation){
			case DISTRIBUTION:
				chain.get_distribution();
				break;
				
			case KEY_DISTRIBUTION:
				chain.get_key_distribution();
				break;
				
			case WINDOW:
				chain.get_windows();
				break;
				
			case TOKEN:
				chain.get_tokens();
				break;
				
			case PROBABILITY:
				chain.get_touch_probability(null, null);
				break;
		}
		
	}
}
