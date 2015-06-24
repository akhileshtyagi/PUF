package trie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import components.Token;
import components.Touch;
import components.Window;

///TODO Eventually this will be implemented as a prefix tree. This will greatly speed up many of the operations causing the calculation of the probabilities to be slow.
/// right now it is fine to have the backing be an arraylist
/// overrided methods are any that remove, modify, or add to the arraylist.
/// these methods will also change the prefix tree
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
		this.add_to_trie(arg0);
		
		return super.add(arg0);
	}

	
	@Override
	public void add(int arg0, Window arg1) {
		this.add_to_trie(arg1);
		
		super.add(arg0, arg1);
	}

	
	@Override
	public boolean addAll(Collection<? extends Window> arg0) {
		for(Window w : arg0){
			this.add_to_trie(w);
		}
		
		return super.addAll(arg0);
	}

	
	@Override
	public boolean addAll(int arg0, Collection<? extends Window> arg1) {
		for(Window w : arg1){
			this.add_to_trie(w);
		}
		
		return super.addAll(arg0, arg1);
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
		this.add_to_trie(arg1);
		
		return super.set(arg0, arg1);
	}
	
	
	///counts the number of times a given touch comes after a given window. in the given window, succesors list
	public int successor_count(List<Touch> successor_list, Window window, Touch touch){
		int count = 0;
		
		for(int i=0;i<this.size();i++){
			//for every occurrence of window, successor match, increment count
			if((this.get(i).compare_with_token(tokens, window)) && (successor_list.get(i).compare_with_token(tokens,touch))){
				count++;
			}
		}
		
		return count;
	}

	
	///return the number of occurrences of w in window_list
	///TODO I think this method needs to be faster. Storing windows in a prefix tree would allow for this
	public int occurrence_count(Window w){
		//TODO check for correctness
		int occurrences=0;
		
		for(int i=0;i<this.size();i++){
			//determine if the windows are equal
			if(this.get(i).compare_with_token(tokens,w)){
				occurrences++;
			}
		}

		return occurrences;
	}

	
	///sets the tokens that will be used when encoding the window
	public void set_tokens(List<Token> tokens){
		this.tokens = tokens;
		this.are_tokens_set = true;
	}
	
	
	///adds and element to the trie
	private void add_to_trie(Window element){
		if(!are_tokens_set){
			//tokens have not been set
			return;
		}
		
		//otherwise the tokens have been say and we are okay to continue
		String encoding = encode(element);
		
		trie.insertString(encoding);
	}
	
	
	///removes an element from trie
	private void remove_from_trie(Window element){
		//TODO, possibly:
		//remove everything from trie and re add all non-removed elements
		
	}
	
	
	//encodes the window into a string. Each character c at index i is given by: the index of window.get(i) in tokens
	private String encode(Window window){
		//TODO
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
