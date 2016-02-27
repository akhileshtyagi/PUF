package trie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import components.Token;
import components.Touch;
import components.Window;

//TODO Eventually this will be implemented as a prefix tree. This will greatly speed up many of the operations causing the calculation of the probabilities to be slow.
// right now it is fine to have the backing be an arraylist
// overrided methods are any that remove, modify, or add to the arraylist.
// these methods will also change the prefix tree
//TODO eventaully I want this class to only implement the List<Window> interface. The fact that it must rely on an arraylist backing means it is taking up more memory than need be because all of the information can be gotten from the prefix tree.

/** Wrapper around Trie used to maintain an ordering among the stored elements.
 * This class uses some additional space to store elements in both an ArrayList
 * and the prefix tree.
 * NOTE: This was done for speed of implementation.
 * It would be good if in the future only a prefix tree was used.
 */
public class TrieList extends ArrayList<Window>{
	private Trie trie;
	private List<Token> tokens;	
	
	private boolean are_tokens_set;
	
	public TrieList(){
		trie = new Trie();
		tokens = null;
		
		are_tokens_set=false;
	}
	
	
	public TrieList(TrieList t){
		trie = new Trie(t.trie);
		
		if(t.tokens!=null){
			tokens = new ArrayList<Token>(t.tokens);
		}else{
			tokens=null;
		}
			
		are_tokens_set=t.are_tokens_set;
	}
	
	
	@Override
	public boolean add(Window arg0) {
		this.add_to_trie(arg0, this.size());
		
		return super.add(arg0);
	}

	
	@Override
	public void add(int arg0, Window arg1) {
		this.add_to_trie(arg1, arg0);
		//TODO update the indexes in the trie
		
		super.add(arg0, arg1);
	}

	
	@Override
	public boolean addAll(Collection<? extends Window> arg0) {
		for(Window w : arg0){
			this.add(w);
		}
		
		return true;
	}

	
	@Override
	public boolean addAll(int arg0, Collection<? extends Window> arg1) {
		for(int i=arg1.size()-1;i>=0;i--){
			//TODO this is an unsafe cast
			this.add(arg0, ((ArrayList<Window>) arg1).get(i));
		}
		
		return true;
	}

	
	@Override
	public void clear() {
		super.clear();
		trie.clear();
	}

	
	@Override
	public boolean remove(Object arg0) {
		this.remove_from_trie((Window) arg0);		
		
		return super.remove(arg0);
	}

	
	@Override
	public Window remove(int arg0) {
		this.remove_from_trie(this.get(arg0));
		
		return super.remove(arg0);
	}

	
	@Override
	public boolean removeAll(Collection<?> arg0) {
		for(Object object : arg0){
			this.remove_from_trie((Window)object);
		}
		
		return super.removeAll(arg0);
	}

	
	@Override
	public boolean retainAll(Collection<?> arg0) {
		clear();
		
		for(Object object : arg0){
			//this.add_to_trie((Window)object);
			this.add((Window)arg0);
		}
		
		return true;
	}

	
	@Override
	public Window set(int arg0, Window arg1) {
		this.remove_from_trie(this.get(arg0));
		this.add_to_trie(arg1, arg0);
		
		return super.set(arg0, arg1);
	}
	
	
	///counts the number of times a given touch comes after a given window. in the given window, succesors list
	public int successor_count(List<Touch> successor_list, Window window, Touch touch){
		//int count = 0;
		int count2 = 0;
		
//		for(int i=0;i<this.size();i++){
//			//for every occurrence of window, successor match, increment count
//			if((this.get(i).compare_with_token(tokens, window)) && (successor_list.get(i).compare_with_token(tokens,touch))){
//				count++;
//			}
//		}
		
		//TODO
		//1) get a list of the indexes which contain the window
		//2) for each item in this list, test to see if the successor==touch
		List<Integer> index_list = trie.get_index_list(encode(window));
		
		for(int i=0;i<index_list.size();i++){
			//for every occurrence of window, successor match, increment count
			if(successor_list.get(index_list.get(i)).compare_with_token(tokens,touch)){
				count2++;
			}
		}
		
		//System.out.println("count:"+count+"   count2:"+count2);
		//System.out.println(count==count2);
		
		return count2;
	}

	
	///return the number of occurrences of w in window_list
	///TODO I think this method needs to be faster. Storing windows in a prefix tree would allow for this
	public int occurrence_count(Window w){
		//TODO use prefix tree to do this
		int occurrences=0;
		
//		for(int i=0;i<this.size();i++){
//			//determine if the windows are equal
//			if(this.get(i).compare_with_token(tokens,w)){
//				occurrences++;
//			}
//		}
		
		occurrences=trie.occurrence_count(encode(w));

		return occurrences;
	}

	
	///sets the tokens that will be used when encoding the window
	public void set_tokens(List<Token> tokens){
		this.tokens = tokens;
		this.are_tokens_set = true;
	}
	
	
	///adds and element to the trie
	private void add_to_trie(Window element, int index){
		if(!are_tokens_set){
			//tokens have not been set
			return;
		}
		
		//otherwise the tokens have been say and we are okay to continue
		String encoding = encode(element);
		
		trie.insertString(encoding, index);
	}
	
	
	///removes an element from trie
	private void remove_from_trie(Window element){
		//TODO, possibly:
		//remove everything from trie and re add all non-removed elements
		
	}
	
	
	//encodes the window into a string. Each character c at index i is given by: the index of window.get(i) in tokens
	private String encode(Window window){
		String encoding = "";
		List<Touch> touches = window.get_touch_list();
				
		for(int i=0;i<touches.size();i++){
			encoding += 'a' + get_token_index(touches.get(i));
		}
		
		return encoding;
	}
	
	
	///returns the index corresponding to the token which contains touch. returns -1 if no token contains touch
	private int get_token_index(Touch touch){
		//TODO check for correctness
		List<Token> token_list = tokens;

		//take the first token to return true
		for(int i=0;i<token_list.size();i++){
			if(token_list.get(i).contains(touch)){
				return i;
			}
		}
		
		return -1;
	}
}