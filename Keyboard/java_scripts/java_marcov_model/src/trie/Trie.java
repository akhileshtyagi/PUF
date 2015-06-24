package trie;

///Credit for portions of this implementation to:
///https://sites.google.com/site/indy256/algo/trie
public class Trie {
   class TrieNode {
	   TrieNode[] children;
	   boolean leaf;
	   int occurrences; // used to indicated how many times root -> leaf occurs
	   
	   public TrieNode(){
		   children = new TrieNode[128];
		   leaf=false;
		   occurrences=0;
	   }
	   
	   
	   public TrieNode(TrieNode t){
		   this.children = new TrieNode[128];
		   this.leaf = t.leaf;
		   
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
  
  
  public  void insertString(String s){
	  insertString(root,s);
  }

  ///inserts a string into the trie
  private  void insertString(TrieNode root, String s) {
    TrieNode v = root;
    for (char ch : s.toCharArray()) {
      TrieNode next = v.children[ch];
      if (next == null)
        v.children[ch] = next = new TrieNode();
      v = next;
    }
    
    v.leaf = true;
    v.occurrences++;
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