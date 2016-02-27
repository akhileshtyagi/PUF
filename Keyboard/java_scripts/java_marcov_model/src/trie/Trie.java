package trie;

import java.util.ArrayList;
import java.util.List;

import components.Window;

/** Implementation of Prefix Tree.
 * This benefits the efficiency of the program.
 * This class is used primarily to figure out information about windows needed in the probability computation.
 */
public class Trie {
   class TrieNode {
	   TrieNode[] children;
	   List<Integer> index_list;
	   
	   boolean leaf;
	   int occurrences; // used to indicated how many times root -> leaf occurs
	   
	   public TrieNode(){
		   children = new TrieNode[128];
		   leaf=false;
		   occurrences=0;
		   index_list=new ArrayList<Integer>();
	   }
	   
	   
	   public TrieNode(TrieNode t){
		   this.children = new TrieNode[128];
		   this.leaf = t.leaf;
		   this.index_list = new ArrayList<Integer>(t.index_list);
		   
		   for(int i=0;i<t.children.length;i++){
			   if(t.children[i] != null){
				   this.children[i] = new TrieNode(t.children[i]);
			   } 
		   }
	   }
  }
  
  private  TrieNode root;
  
  ///sets up the tree so that everything will be added to trienode root?
  public Trie(){
	  root = new TrieNode();
  }
  
  
  ///creates a copy trie
  public Trie(Trie t){
	  root= new TrieNode(t.root);
  }
  
  
  ///removes all elements from the trie
  public void clear(){
	  root = new TrieNode();
  }
  
  
  public  void insertString(String s, int index){
	  insertString(root,s,index);
  }

  ///inserts a string into the trie
  private  void insertString(TrieNode root, String s, int index) {
    TrieNode v = root;
    for (char ch : s.toCharArray()) {
      TrieNode next = v.children[ch];
      if (next == null)
        v.children[ch] = next = new TrieNode();
      v = next;
    }
    
    v.leaf = true;
    v.occurrences++;
    v.index_list.add(index);
  }
  
  
  ///retrieves the number of occurrences of a given string in the tree
  public int occurrence_count(String s){
	    TrieNode v = root;
	    for (char ch : s.toCharArray()) {
	      TrieNode next = v.children[ch];
	      if (next == null)
	        break;
	      v = next;
	    }
	    
	    return v.occurrences;
  }
  
  
  ///returns a list of indexes containing the given window
  public List<Integer> get_index_list(String s){
	  	TrieNode v = root;
	    for (char ch : s.toCharArray()) {
	    	TrieNode next = v.children[ch];
	    	if (next == null)
	    		break;
	     		v = next;
	    }
	    
	    return v.index_list;
  }

  
  ///prints the elements in a sorted order
  public  void printSorted(TrieNode node, String s) {
    for (char ch = 0; ch < node.children.length; ch++) {
      TrieNode child = node.children[ch];
      if (child != null)
        printSorted(child, s + ch);
    }
    if (node.leaf) {
      System.out.println(s);
    }
  }

  
  // Usage example
//  public static void main(String[] args) {
//    TrieNode root = new TrieNode();
//    insertString(root, "hello");
//    insertString(root, "world");
//    insertString(root, "hi");
//    printSorted(root, "");
//  }
}