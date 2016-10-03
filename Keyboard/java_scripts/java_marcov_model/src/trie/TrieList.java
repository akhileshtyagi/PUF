package trie;

import java.util.*;

import components.Chain;
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
	private Map<Integer, List<Token>> token_map;
	
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
		//TODO unimplemented
		
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
//		int count = 0;
//		for(int i=0;i<this.size();i++){
//			//for every occurrence of window, successor match, increment count
//			if((this.get(i).compare_with_token(tokens, window)) && (successor_list.get(i).compare_with_token(tokens,touch))){
//				count++;
//			}
//		}

		//1) get a list of the indexes which contain the window
		//2) for each item in this list, test to see if the successor==touch
		List<Integer> index_list = trie.get_index_list(encode(window));

		// retrieve the tokens for this touch
		//TODO ? need to think through what is happening here
		//TODO tokens being used are incorrect, but i need to consider,
		//TODO I can either make the change here or i can change the way .compare_with_token works

		int count2 = 0;
		for(int i=0;i<index_list.size();i++){
			//for every occurrence of window, successor match, increment count
			if(Chain.TOKEN_TYPE == Token.Type.keycode_mu) {
				if (successor_list.get(index_list.get(i)).compare_with_token(
						token_map.get(successor_list.get(index_list.get(i)).get_key()),
						touch)) {
					count2++;
				}
			}else{
				if (successor_list.get(index_list.get(i)).compare_with_token(tokens, touch)) {
					count2++;
				}
			}
		}

		//TODO test determine if htey reutnr hte same vlaue, they do
//		if(count!=count2) {
//			System.out.println("count:" + count + "   count2:" + count2);
//			System.out.println(count == count2);
//		}
		
		return count2;
	}

	/* return the indexes of a window's occurrence in window_list */
	public List<Integer> get_index_list(Window window) {
		return trie.get_index_list(encode(window));
	}
	
	///return the number of occurrences of w in window_list
	public int occurrence_count(Window w){
		// use prefix tree to do this
		//int occurrences=0;

		// inefficient
//		for(int i=0;i<this.size();i++){
//			//determine if the windows are equal
//			if(this.get(i).compare_with_token(tokens,w)){
//				occurrences++;
//			}
//		}

		//TODO efficient but possibly broken, returns the same results as inefficient mehtod
		int occurrences = trie.occurrence_count(encode(w));

		return occurrences;
	}

	/**
	 * The idea is that only one of the next two methods
	 * to set_tokens will be used
	 *
	 * List<Token> will the the argument when
	 * the overall distribution is being used
	 * to generate all hte tokens</Token>
	 *
	 * Map<Integer, List<Token>> will be used when the
	 * key distributions are being used to generated
	 * a list of tokens for each key</Token>
	 */
	//TODO uncomment
	///sets the tokens that will be used when encoding the window
	public void set_tokens(List<Token> tokens){
		this.tokens = tokens;
		this.are_tokens_set = true;
	}

	///sets the tokens that will be used when encoding the window
	public void set_tokens(Map<Integer, List<Token>> token_map){
		this.token_map = token_map;
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
		//TODO remove functionality is not currently utilized
		//TODO therefore this mehtod is uncessary for now
		//TODO, possibly:
		//remove everything from trie and re add all non-removed elements
		
	}
	
	
	//encodes the window into a string. Each character c at index i is given by: the index of window.get(i) in tokens
	private String encode(Window window){
		String encoding = "";
		List<Touch> touches = window.get_touch_list();

		//TODO make sure this a correct way to encode things to the trie
		// 'k' and 't' are here to ensure there is no overlap in windows
		// though rare, this overlap could occur if
		// part of the token encoding mistakenly represented a key
		// this arises from the alloance for arbitrary length strings
		// to represent touches and tokens
		for(int i=0;i<touches.size();i++){
			// location of key
			encoding += 'k';
			encoding += touches.get(i).get_key();

			// pressure at key
			encoding += 't';
			encoding += 'a' + get_token_index(touches.get(i));
		}

		//System.out.println("encoding: " + encoding);
		
		return encoding;
	}
	
	
	///returns the index corresponding to the token which contains touch. returns -1 if no token contains touch
	private int get_token_index(Touch touch){
		List<Token> token_list;
		if(Chain.TOKEN_TYPE == Token.Type.keycode_mu){
			token_list = token_map.get(touch.get_key());
		}else {
			token_list = tokens;
		}

		//take the first token to return true
		for(int i=0;i<token_list.size();i++){
			if(token_list.get(i).contains(touch)){
				return i;
			}
		}
		
		return -1;
	}
}