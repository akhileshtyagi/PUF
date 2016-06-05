package test;

import java.io.File;
import java.util.List;

import runtime.ChainBuilder;

import components.Chain;
import components.Touch;
import trie.TrieList;

/** This class will print out the model constructed form the designated file */
public class Print_model {
	final static String data_path = "data_sets/t_tim_d_tim.csv";
	final static int window = 2;
	final static int token = 1;
	final static int threshold = 5000;
	final static int model_size = 500;

	public static void main(String[] args){
		Chain chain = new Chain(window, token, threshold, model_size);
		
		// cosntruct the model
		List<Touch> touches = ChainBuilder.parse_csv(new File(data_path));
		chain.add_touch_list(touches);
		
		//cause computations to happen
		chain.get_distribution();
		chain.get_key_distribution();
		chain.get_touch_probability(null, null);

		// print out the chain window by window
		chain.output_by_window("print_model_output.txt");
		
		// print out the model
		//chain.output_to_csv("print_model_output.txt");
	}
}
