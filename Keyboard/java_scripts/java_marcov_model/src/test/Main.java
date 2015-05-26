package test;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import runtime.ChainBuilder;

import components.Chain;
import components.Distribution;
import components.Token;
import components.Touch;
import components.Window;

///TODO generate a csv file for testing
///This class is used to test that the model is being built correctly. Also tested is the model compairason. and various classes used in model creating. The idea is to print out the tests which fail.
///This class should have to do no actual work if the program is designed well.
public class Main{
	final static String TOUCH_TEST_FILE_NAME = "test_touch.csv";
	final static int TOUCH_TEST_FILE_LENGTH = 1000;
	
	///enumerate the differant types of tests which may be run
	private enum TestTypes{
		SPEED ("Speed, output execution time of operations",0),
		CORRECTNESS ("Correctness, output pass / fail indicators of correctness",1);

		private final String description;
		private final int identifier;
		
		TestTypes(String description, int identifier){
			this.description = description;
			this.identifier = identifier;
		}

		public String toString(){
			return description;
		}
		
		public int get_identifier(){
			return identifier;
		}
	}

	///enumerate the different types of test files which may be built. Some inner enumerations are defined. The idea is that the test file may be any combination of these inner enumerations.
	private enum TestFiles{
		//TODO um.... check if this works... yeah
		PREEXISTING ("Preexisting file in working directory named: "+TOUCH_TEST_FILE_NAME, 0),
		SELECT ("Build a test file", 1);

		private final String description;
		private final int identifier;
		
		TestFiles(String description, int identifier){
			this.description = description;
			this.identifier = identifier;
		}

		public String toString(){
			return description;
		}
		
		public int get_identifier(){
			return identifier;
		}
		
		//enumerate properties of test files
		//the amount of pressure, this will determine the median pressure
		public enum PressureAmount{
			HIGH ("High pressure, 0.75", 0, .75),
			MEDIUM ("Medium Pressure, 0.5", 1, .5),
			LOW ("Low Pressure, 0.25", 2, .25);

			private final String description;
			private final int identifier;
			private final double value;
			
			PressureAmount(String description, int identifier, double value){
				this.description = description;
				this.identifier = identifier;
				this.value = value;
			}
			
			public String toString(){
				return description;
			}
		
			public int get_identifier(){
				return identifier;
			}

			public double get_value(){
				return value;
			}
		}

		//Defines what type of distribution will be written to the test file
		//TODO decide the value of double
		public enum Distribution{
			NORMAL ("Normal, centered about pressure median", 0, 0),
			ABNORMAL ("Abnormal, centered about pressure median, but inverted", 1, 0),
			RANDOM ("Random, completly and utterly random", 2, 0);

			private final String description;
			private final int identifier;
			private final double value;
			
			Distribution(String description, int identifier, double value){
				this.description = description;
				this.identifier = identifier;
				this.value = value;
			}
			
			public String toString(){
				return description;
			}
		
			public int get_identifier(){
				return identifier;
			}
		
			public double get_value(){
				return value;
			}
		}

		//this determines the width of the distribution. high concentration implies low standard deviation.
		//TODO decide the value
		public enum Concentration{
			HIGH ("High, [std deviation]", 0, 0),
			MEDIUM ("Medium, [std deviation]",1, 0),
			LOW ("Low, [std deviation]",2, 0);

			private final String description;
			private final int identifier;
			private final double value;
			
			Concentration(String description, int identifier, double value){
				this.description = description;
				this.identifier = identifier;
				this.value = value;
			}
			
			public String toString(){
				return description;
			}
		
			public int get_identifier(){
				return identifier;
			}
		
			public double get_value(){
				return value;
			}
		}
	}

	//all methods return true if they pass
	public static void main(String args[]){
		// allow the user to select between tests for correctness and tests for speed
		TestTypes type = get_user_input_test_type();

		//TODO construct the test file
		//build_touch_test_file(TOUCH_TEST_FILE_NAME);
		
		switch(type){
			case CORRECTNESS:
				correctness_test();
				break;
			case SPEED:
				speed_test();
				break;
		}
	}


	///TODO builds a test csv file containing touches. This will be read in to build the marcov model. Make this easy to modify. The goal is to be able to test how different types of data will affect the model.
	private static void build_touch_test_file(String file_name){
		//TODO prompt the user, asking which type of test file they would like to generate.
		Scanner input = new Scanner(System.in);
		Scanner output = null;
		boolean invalid = true;
		TestFiles type = null;
		int choice;
		
		while(invalid){
			prompt_test_file_type();
			
			while(!input.hasNextLine());
			choice = Integer.valueOf(input.nextLine());
			
			for(TestFiles t_f : TestFiles.values()){
				if(t_f.get_identifier()==choice){
					invalid = false;
					type = t_f;
					break;
				}
			}
		}

		switch(type){
			case PREEXISTING:
				break;
			case SELECT:
				//ask for differant properties of the file
				double file_distribution = get_file_distribution();
				double file_concentration = get_file_concentration();
				double file_pressure_amount = get_file_pressure_amount();

				//construct the file based on these values
				try {
					output = new Scanner(new File(file_name));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for(int i=0;i<TOUCH_TEST_FILE_LENGTH;i++){
					//TODO generate touch information
					//TODO write touch information to output
				}

				break;
		}
		
		output.close();
		input.close();
	}


	// get user input from std in
	private static TestTypes get_user_input_test_type(){
		Scanner input = new Scanner(System.in);
		boolean invalid = true;
		TestTypes type = null;
		int choice;
		
		while(invalid){
			prompt_test_type();

			choice = Integer.valueOf(input.nextLine());
			for(TestTypes t_t : TestTypes.values()){
				if(t_t.get_identifier()==choice){
					invalid = false;
					type = t_t;
					break;
				}
			}
		}

		input.close();
		return type;
	}


	// display user options for types of tests
	private static void prompt_test_type(){
		System.out.println("");		
		System.out.println("Which type of test would you like to run:");

		//for all enum values		
		for (TestTypes t_t : TestTypes.values()) {
 			System.out.println(t_t.get_identifier()+") "+t_t.toString());			
		}
	}

	
	// display user options for types of files
	private static void prompt_test_file_type(){
		System.out.println("");		
		System.out.println("Which type of test file would you like to use:");

		//for all enum values		
		for (TestFiles t_f : TestFiles.values()) {
 			System.out.println(t_f.get_identifier()+") "+t_f.toString());			
		}
	}


	// display user options for file distribution, return the value associated with the selection
	private static double get_file_distribution(){
		Scanner input = new Scanner(System.in);
		boolean invalid = true;
		TestFiles.Distribution type = null;
		int choice;
		
		while(invalid){
			display_file_distribution_options();

			choice = Integer.valueOf(input.nextLine());
			for(TestFiles.Distribution t_f : TestFiles.Distribution.values()){
				if(t_f.get_identifier()==choice){
					invalid = false;
					type = t_f;
					break;
				}
			}
		}

		input.close();
		return type.get_value();
	}


	// display user options for file concentration, return the value associated with the selection
	private static double get_file_concentration(){
		Scanner input = new Scanner(System.in);
		boolean invalid = true;
		TestFiles.Concentration type = null;
		int choice = 0;
		
		while(invalid){
			display_file_concentration_options();

			choice = Integer.valueOf(input.nextLine());
			for(TestFiles.Concentration t_f : TestFiles.Concentration.values()){
				if(t_f.get_identifier()==choice){
					invalid = false;
					type=t_f;
					break;
				}
			}
		}

		input.close();
		return type.get_value();
	}


	// display user options for file pressure amount, return the value associated with the selection
	private static double get_file_pressure_amount(){
		Scanner input = new Scanner(System.in);
		boolean invalid = true;
		TestFiles.PressureAmount type = null;
		int choice;
		
		while(invalid){
			display_file_pressure_amount_options();

			choice = Integer.valueOf(input.nextLine());
			for(TestFiles.PressureAmount t_f : TestFiles.PressureAmount.values()){
				if(t_f.get_identifier()==choice){
					invalid = false;
					type = t_f;
					break;
				}
			}
		}

		input.close();
		return type.get_value();
	}

	// display distribution options
	private static void display_file_distribution_options(){
		System.out.println("");		
		System.out.println("What should be the file distribution:");

		//for all enum values		
		for (TestFiles.Distribution t_f : TestFiles.Distribution.values()) {
 			System.out.println(t_f.get_identifier()+") "+t_f.toString());			
		}
	}


	// display concentration options
	private static void display_file_concentration_options(){
		System.out.println("");		
		System.out.println("What should be the file distribution:");

		//for all enum values		
		for (TestFiles.Concentration t_f : TestFiles.Concentration.values()) {
 			System.out.println(t_f.get_identifier()+") "+t_f.toString());			
		}
	}


	// display pressure amount options
	private static void display_file_pressure_amount_options(){
		System.out.println("");		
		System.out.println("What should be the file distribution:");

		//for all enum values		
		for (TestFiles.PressureAmount t_f : TestFiles.PressureAmount.values()) {
 			System.out.println(t_f.get_identifier()+") "+t_f.toString());			
		}
	}
	
	
	//############################
	//##### BEGIN TEST CALLS #####
	//############################
	//TODO call tests for speed. Print out results
	private static void speed_test(){
		//make calls to all of the speed test methods which return a long indicating the amount of time the action took. These actions will allow me to isolate slow points in processing and attempt to fix them.
		//this will call individual methods instead of grouped together class methods. It will time everything and print the results.
		
		long total_time = 0;

		//output the results
		System.out.println("");
		System.out.println("total time taken: " + total_time);
	}


	//call tests for correctness. Print out results
	private static void correctness_test(){
		//make calls to all of the classes to be tested
		boolean test_chain_success = test_chain();
		boolean test_distribution_success = test_distribution();
		boolean test_token_success = test_token();
		boolean test_touch_success = test_touch();
		boolean test_window_success = test_window();
		boolean test_chain_builder_success = test_chain_builder();
		boolean test_compare_chains_success = test_compare_chains();

		//print out errors in a readable format
		System.out.println("\nprogram overview error log:");

			if(!test_chain_success){
				System.out.println("\tchain fails");
			}
		
			if(!test_distribution_success){
				System.out.println("\tdistribution fails");
			}
	
			if(!test_token_success){
				System.out.println("\ttoken fails");
			}

			if(!test_touch_success){
				System.out.println("\ttouch fails");
			}
			
			if(!test_window_success){
				System.out.println("\twindow fails");
			}
			
			if(!test_chain_builder_success){
				System.out.println("\tchain_builder fails");
			}
			
			if(!test_compare_chains_success){
				System.out.println("\tcompare_chains fails");
			}
	}

	//###############################
	//###############################
	//#### Tests for correctness ####
	//###############################
	//###############################
	
	//############################
	//### classes to be tested ###
	//############################

	//# Chain class #
	private static boolean test_chain(){
		// call methods to test the functionality of the chain class
		boolean correct=true;

		//test functions in the distribution class
		boolean test_add_touch_success = test_add_touch();
		boolean test_get_touch_probability_success = test_get_touch_probability();
		boolean test_get_distribution_success = test_get_distribution();
		boolean test_get_key_distribution_success = test_get_key_distribution();
		boolean test_chain_compare_to_success = test_chain_compare_to();
		test_output_to_csv();
		
		//print out any errors in a readable way
		System.out.println("\nchain error log:");

		if(!test_add_touch_success){
			System.out.println("\tadd_touch fails");
			correct=false;
		}
		
		if(!test_get_touch_probability_success){
			System.out.println("\tget_touch_probability fails");
			correct=false;
		}

		if(!test_get_distribution_success){
			System.out.println("\tget_distribution fails");
			correct=false;
		}

		if(!test_get_key_distribution_success){
			System.out.println("\tget_key_distribution fails");
			correct=false;
		}
		
		if(!test_chain_compare_to_success){
			System.out.println("\tcompare_to fails");
			correct=false;
		}
		
		return correct;
	}

	
	//# Distribution class #
	private static boolean test_distribution(){
		boolean correct=true;

		//test functions in the distribution class
		boolean test_calc_min_success = test_calc_min();
		boolean test_calc_max_success = test_calc_max();
		boolean test_calc_average_success = test_calc_average();
		boolean test_calc_standard_deviation_success = test_calc_standard_deviation();
		
		//print out any errors in a readable way
		System.out.println("\ndistribution error log:");

		if(!test_calc_min_success){
			System.out.println("\tcalc_min fails");
			correct=false;
		}
		
		if(!test_calc_max_success){
			System.out.println("\tcalc_max fails");
			correct=false;
		}

		if(!test_calc_average_success){
			System.out.println("\tcalc_average fails");
			correct=false;
		}

		if(!test_calc_standard_deviation_success){
			System.out.println("\tcalc_standard_deviation fails");
			correct=false;
		}
		
		return correct;
	}

	
	//# Token class #
	private static boolean test_token(){
		boolean correct=true;

		//test functions in the token class
		boolean test_constructors_success = test_token_constructors();
		boolean test_contains_success = test_token_contains();
		boolean test_get_min_max_success = test_get_min_max();
		boolean test_token_equals_success = test_token_equals();
		
		//print out any errors in a readable way
		System.out.println("\ntoken error log:");

		if(!test_constructors_success){
			System.out.println("\ttoken constructors fails");
			correct=false;
		}
		
		if(!test_contains_success){
			System.out.println("\ttoken_contains fails");
			correct=false;
		}

		if(!test_get_min_max_success){
			System.out.println("\tget_min_max fails");
			correct=false;
		}

		if(!test_token_equals_success){
			System.out.println("\ttoken_equals fails");
			correct=false;
		}
		
		return correct;
	}

	
	//# Touch class #
	private static boolean test_touch(){
		// call methods to test the functionality of the chain class
		boolean correct=true;

		//test functions in the distribution class
		boolean test_set_probability_success = test_set_probability();
		boolean test_get_probability_success = test_get_probability();
		boolean test_touch_hashCode_success = test_touch_hashCode();
		boolean test_touch_compareTo_success = test_touch_compareTo();
		
		//print out any errors in a readable way
		System.out.println("\ntouch error log:");

		if(!test_set_probability_success){
			System.out.println("\tset_probability fails");
			correct=false;
		}
		
		if(!test_get_probability_success){
			System.out.println("\tget_probability fails");
			correct=false;
		}
		
		if(!test_touch_hashCode_success){
			System.out.println("\thashCode fails");
			correct=false;
		}
		
		if(!test_touch_compareTo_success){
			System.out.println("\tcompareTo fails");
			correct=false;
		}
		
		return correct;
	}


	//# Window class #
	private static boolean test_window(){
		//call methods to test the functionality of the chain class
		boolean correct=true;

		//test functions in the distribution class
		boolean test_window_compareTo_success = test_window_compareTo();
		boolean test_window_hashCode_success = test_window_hashCode();
		
		//print out any errors in a readable way
		System.out.println("\nwindow error log:");

		if(!test_window_compareTo_success){
			System.out.println("\tcompareTo fails");
			correct=false;
		}
		
		if(!test_window_hashCode_success){
			System.out.println("\thashCode fails");
			correct=false;
		}
		
		return correct;
	}
	
	
	//# ChainBuilder class #
	private static boolean test_chain_builder(){
		// call methods to test the functionality of the chain class
		boolean correct=true;

		//test functions in the distribution class
		boolean test_handle_touch_success = test_handle_touch();
		boolean test_authenticate_success = test_authenticate();
		boolean test_build_chain_from_csv_success = test_build_chain_from_csv();
		
		//print out any errors in a readable way
		System.out.println("\nchainBuilder error log:");

		if(!test_handle_touch_success){
			System.out.println("\thandle_touch fails");
			correct=false;
		}
		
		if(!test_authenticate_success){
			System.out.println("\tauthenticate fails");
			correct=false;
		}
		
		if(!test_build_chain_from_csv_success){
			System.out.println("\tbuild_chain_from_csv fails");
			correct=false;
		}
		
		return correct;
	}
	
	
	//# CompareChains #
	private static boolean test_compare_chains(){
		//TODO call methods to test the functionality of the chain class
		boolean correct=true;

		//test functions in the distribution class
		boolean test_compare_chains_run_success = test_compare_chains_run();
		
		//print out any errors in a readable way
		System.out.println("\ncompareChains error log:");

		if(!test_compare_chains_run_success){
			System.out.println("\tcalc_min fails");
			correct=false;
		}
		
		return correct;
	}
	
	
	//############################
	//### methods to be tested ###
	//############################
	
	//TODO
	//# Chain class#
	private static boolean test_add_touch(){
		boolean correct;
		
		// window 2, tokens 10, threshold 500, size 5
		Chain chain = new Chain(2,10,500,5);
		
		//model size is 5. Add 10 touches to the model and see if the sliding is working correctly. The most rescent 5 touches should be retained.
		for(int i=0;i<10;i++){
			chain.add_touch(new Touch('a',.1*i,100*i));
		}
		
		Distribution dist = chain.get_distribution();
		// most rescent 5 touches are  pressure .9, .8, .7, .6, .5
		correct = (dist.get_max() == .9) && (dist.get_min()== .5);
		
		return correct;
	}
	
	
	private static boolean test_get_touch_probability(){
		//TODO
		boolean correct;
		
		// window 2, tokens 10, threshold 500, size 5
		Chain chain = new Chain(2,10,500,10);
		
		//model size is 5. Add 10 touches to the model and see if the sliding is working correctly. The most rescent 5 touches should be retained.
		for(int i=0;i<10;i++){
			chain.add_touch(new Touch('a',.1*i,100*i));
		}
		//TODO create a window and tests that the correct probability is retrieved afterward
		//double probability = chain.get_touch_probability();
		
		//correct = (probability==1);
		
		return correct = false;
	}
	
	
	private static boolean test_get_distribution(){
		// works per test_add_touch();
		return true;
	}
	
	
	private static boolean test_get_key_distribution(){
		//TODO
		boolean correct = false;
		
		// window 2, tokens 10, threshold 500, size 9
		Chain chain = new Chain(2,10,500,9);
		
		//model size is 5. Add 10 touches to the model and see if the sliding is working correctly. The most rescent 5 touches should be retained.
		for(int i=0;i<9;i++){
			//.1 though .9 pressures.... average is .5.... keycode is half a, half b
			chain.add_touch(new Touch('a'+(i%2),.1*(i+1),100*i));
		}
		
		List<Distribution> key_dist = chain.get_key_distribution();
		// TODO determine whether key_dist is correct
		//correct = ;
		
		return correct;
	}
	
	
	private static boolean test_chain_compare_to(){
		//TODO
		return false;
	}
	
	
	private static boolean test_output_to_csv(){
		// window 2, tokens 10, threshold 500, size 9
		Chain chain = new Chain(2,10,500,10000);
		
		//model size is 5. Add 10 touches to the model and see if the sliding is working correctly. The most rescent 5 touches should be retained.
		for(int i=0;i<10000;i++){
			//.1 though .9 pressures.... average is .5.... keycode is half a, half b
			Touch touch =new Touch('a'+(i%2),.1*((i+1)%11),100);
			//System.out.println(touch);
			
			chain.add_touch(touch);
		}
		
		//cause all computations to happen
		chain.get_distribution();
		chain.get_key_distribution();
		chain.get_touch_probability(null, 0);
		
		//output the chain to a csv file
		chain.output_to_csv();
		
		return true;
	}
	
	
	//# Distribution class #
	private static boolean test_calc_min(){
		boolean correct = false;	
		ArrayList<Touch> touches = new ArrayList<Touch>();
		Distribution dist;

		//create a List of touches with a minimum value
		touches.add(new Touch('a',.1, 100));
		touches.add(new Touch('a',.2, 200));
		touches.add(new Touch('a',.3, 300));
		touches.add(new Touch('a',.4, 400));
		touches.add(new Touch('a',.5, 500));
		touches.add(new Touch('a',.6, 600));
		touches.add(new Touch('a',.7, 700));
		touches.add(new Touch('a',.8, 800));
		touches.add(new Touch('a',.9, 900));

		
		//test to see that calc_min finds this value correctly
		dist = new Distribution(touches);

		if(dist.get_min()==.1){
			correct=true;
		}
		
		return correct;
	}


	private static boolean test_calc_max(){
		boolean correct = false;	
		ArrayList<Touch> touches = new ArrayList<Touch>();
		Distribution dist;

		//create a List of touches with a minimum value
		touches.add(new Touch('a',.1, 100));
		touches.add(new Touch('a',.2, 200));
		touches.add(new Touch('a',.3, 300));
		touches.add(new Touch('a',.4, 400));
		touches.add(new Touch('a',.5, 500));
		touches.add(new Touch('a',.6, 600));
		touches.add(new Touch('a',.7, 700));
		touches.add(new Touch('a',.8, 800));
		touches.add(new Touch('a',.9, 900));
		
		//test to see that calc_min finds this value correctly
		dist = new Distribution(touches);

		if(dist.get_max()==.9){
			correct=true;
		}
		
		return correct;
	}


	private static boolean test_calc_average(){
		boolean correct = false;	
		ArrayList<Touch> touches = new ArrayList<Touch>();
		Distribution dist;

		//create a List of touches with a minimum value
		touches.add(new Touch('a',.1, 100));
		touches.add(new Touch('a',.2, 200));
		touches.add(new Touch('a',.3, 300));
		touches.add(new Touch('a',.4, 400));
		touches.add(new Touch('a',.5, 500));
		touches.add(new Touch('a',.6, 600));
		touches.add(new Touch('a',.7, 700));
		touches.add(new Touch('a',.8, 800));
		touches.add(new Touch('a',.9, 900));

		
		//test to see that calc_min finds this value correctly
		dist = new Distribution(touches);

		if(dist.get_average()==.5){
			correct=true;
		}
		
		return correct;
	}


	private static boolean test_calc_standard_deviation(){
		boolean correct = false;	
		ArrayList<Touch> touches = new ArrayList<Touch>();
		Distribution dist;

		//create a List of touches with a minimum value
		touches.add(new Touch('a',.1, 100));
		touches.add(new Touch('a',.2, 200));
		touches.add(new Touch('a',.3, 300));
		touches.add(new Touch('a',.4, 400));
		touches.add(new Touch('a',.5, 500));
		touches.add(new Touch('a',.6, 600));
		touches.add(new Touch('a',.7, 700));
		touches.add(new Touch('a',.8, 800));
		touches.add(new Touch('a',.9, 900));

		
		//test to see that calc_min finds this value correctly
		dist = new Distribution(touches);

		//actual value should be around .2582
		if(dist.get_standard_deviation()>(.258)&&dist.get_standard_deviation()<(.2585)){
			correct=true;
		}
		
		return correct;
	}
	

	//# Token class #
	private static boolean test_token_constructors(){
		boolean correct = true;
		
		//create a distribution to be used in the construction of tokens
		ArrayList<Touch> touches = new ArrayList<Touch>();
		Distribution dist;

		//create a List of touches with a minimum value
		touches.add(new Touch('a', 0, 0));
		touches.add(new Touch('a',.1, 100));
		touches.add(new Touch('a',.2, 200));
		touches.add(new Touch('a',.3, 300));
		touches.add(new Touch('a',.4, 400));
		touches.add(new Touch('a',.5, 500));
		touches.add(new Touch('a',.6, 600));
		touches.add(new Touch('a',.7, 700));
		touches.add(new Touch('a',.8, 800));
		touches.add(new Touch('a',.9, 900));
		touches.add(new Touch('a', 1, 1000));

		
		//test to see that calc_min finds this value correctly
		dist = new Distribution(touches);
		
		//make a call to Token() constructors. Test one of them for correctness. Test the rest for equality to the fist.
		ArrayList<Token> tokens_0 = new ArrayList<Token>();
		ArrayList<Token> tokens_1 = new ArrayList<Token>();
		ArrayList<Token> tokens_2 = new ArrayList<Token>();
		
		//create 10 tokens over the range 0 to 1 using the three different constructors.
		for(int i=0;i<10;i++){
			tokens_0.add(new Token(dist, 10, i, 2)); // this one should necessarily be differant from the other two
			tokens_1.add(new Token(dist, 10, i));
			tokens_2.add(new Token(0, 1, 10, i));
		}
		
		//check for the correctness of each of the token sets
		boolean token_0_incorrect = false;
		boolean token_1_incorrect;
		boolean token_2_incorrect;
		double sigma = dist.get_standard_deviation();
		
		for(int i=0;i<10;i++){
			//test each of the token sets for correctness
			//System.out.print(((.4*sigma*i)+(dist.get_average()-2*sigma)));
			//System.out.println("\t"+tokens_0.get(i).get_min());
			
			//TODO make sure this is a valid test
			token_0_incorrect = !within_episilon(tokens_0.get(i).get_min(), ((.4*sigma*(i+1))+dist.get_average()-2*sigma), .01);
			token_1_incorrect = !(tokens_1.get(i).get_min()==(.1*i));
			token_2_incorrect = !(tokens_2.get(i).get_min()==(.1*i));
			
			if(	token_0_incorrect ||
				token_1_incorrect ||
				token_2_incorrect){
				correct = false;
			}
		}
		
		return correct;
	}
	
	
	private static boolean test_token_contains(){
		boolean correct = true;
		
		//create a distribution to be used in the construction of tokens
		ArrayList<Touch> touches = new ArrayList<Touch>();
		Distribution dist;

		//create a List of touches with a minimum value
		touches.add(new Touch('a', 0, 0));
		touches.add(new Touch('a',.1, 100));
		touches.add(new Touch('a',.2, 200));
		touches.add(new Touch('a',.3, 300));
		touches.add(new Touch('a',.4, 400));
		touches.add(new Touch('a',.5, 500));
		touches.add(new Touch('a',.6, 600));
		touches.add(new Touch('a',.7, 700));
		touches.add(new Touch('a',.8, 800));
		touches.add(new Touch('a',.9, 900));
		touches.add(new Touch('a', 1, 1000));

		
		//test to see that calc_min finds this value correctly
		dist = new Distribution(touches);
		
		//make a call to Token() constructors. Test one of them for correctness. Test the rest for equality to the fist.
		ArrayList<Token> tokens_2 = new ArrayList<Token>();
		
		//create 10 tokens over the range 0 to 1 using the three different constructors.
		for(int i=0;i<10;i++){
			tokens_2.add(new Token(0, 1, 10, i));
		}
		
		//check for the correctness of each of the token sets
		boolean token_2_incorrect;
		
		for(int i=0;i<10;i++){
			//test each of the token sets for correctness
			Touch t = new Touch('a',.5+i, 0);
			
			token_2_incorrect = (tokens_2.get(i).contains(t));
			
			if(token_2_incorrect){
				correct = false;
			}
		}
		
		return correct;
	}
	
	
	private static boolean test_get_min_max(){
		boolean correct;
		
		Token t = new Token(0, 1, 10, 0);
		
		correct = t.get_max() == .1;
		correct = correct && t.get_min()==0;
		
		return correct;
	}
	
	
	private static boolean test_token_equals(){
		boolean correct;
		
		Token t_1 = new Token(0, 1, 10, 0);
		Token t_2 = new Token(0, 1, 10, 0);
		Token t_3 = new Token(0, 1, 10, 5);
		
		correct = t_1.equals(t_2);
		correct = correct && !t_3.equals(t_2);
		
		
		return correct;
	}

	
	//# Touch class #
	private static boolean test_set_probability(){
		boolean correct;
		
		Touch touch = new Touch('a', .5, 100);
		Window window = new Window(new ArrayList<Touch>());
		
		touch.set_probability(window, .2);
		correct = touch.get_probability(window)==.2;
		
		return correct;
	}
	
	
	private static boolean test_get_probability(){
		boolean correct;
		
		Touch touch = new Touch('a', .5, 100);
		Window window = new Window(new ArrayList<Touch>());
		
		List<Touch> touches = new ArrayList<Touch>();
		touches.add(touch);
		
		touch.set_probability(window, .2);
		correct = touch.get_probability(window)==.2;
		correct = correct && (0==touch.get_probability(new Window(touches)));
		
		return correct;
	}
	
	
	private static boolean test_touch_hashCode(){
		//TODO
		boolean correct;
		
		Touch touch = new Touch('a', .5, 100);
		Window window = new Window(new ArrayList<Touch>());
		
		touch.set_probability(window, .2);
		correct = (touch.hashCode() > 0);
		
		return correct;
	}
	
	
	private static boolean test_touch_compareTo(){
		boolean correct;
		
		Touch touch_0 = new Touch('a', .5, 100);
		Touch touch_1 = new Touch('a', .5, 100);
		Touch touch_2 = new Touch('b', .5, 100);
		Touch touch_3 = new Touch('a', .6, 100);
		Touch touch_4 = new Touch('a', .5, 200);
		
		Window window = new Window(new ArrayList<Touch>());
		
		List<Touch> touches = new ArrayList<Touch>();
		touches.add(touch_0);
		
		//get the touches for equality
		correct = touch_0.compareTo(touch_1) == 0; // are they equal?
		
		//change a touch
		touch_0.set_probability(window, .2);
		
		//are they still equal? they shouldn't be.
		correct = correct && (touch_0.compareTo(touch_1) > 0);
	
		//are the touches origionally not equal, still not equal?
		correct = correct && (touch_1.compareTo(touch_2) > 0);
		correct = correct && (touch_1.compareTo(touch_3) > 0);
		correct = correct && (touch_1.compareTo(touch_4) > 0);

		return correct;
	}
	
	
	//# Window class #
	private static boolean test_window_compareTo(){
		boolean correct;
		
		ArrayList<Touch> touches_0 = new ArrayList<Touch>();
		ArrayList<Touch> touches_1 = new ArrayList<Touch>();
		ArrayList<Touch> touches_2 = new ArrayList<Touch>();
		
		Touch touch_1 = new Touch('a', 0, 0);
		Touch touch_2 = new Touch('a',.1, 100);
		Touch touch_3 = new Touch('a',.2, 200);
		Touch touch_4 = new Touch('b',.1, 300);
		
		touches_0.add(touch_1);
		touches_0.add(touch_2);
		touches_0.add(touch_3);
		
		touches_1.add(touch_1);
		touches_1.add(touch_2);
		touches_1.add(touch_3);
		
		touches_2.add(touch_1);
		touches_2.add(touch_2);
		touches_2.add(touch_4);
		
		Window window_0 = new Window(touches_0);
		Window window_1 = new Window(touches_0); 
		Window window_2 = new Window(touches_1);
		Window window_3 = new Window(touches_2);
		Window window_4 = new Window(new ArrayList<Touch>());
		
		//compare two windows created with the same touch list
		correct = window_0.compareTo(window_1) == 0;
		//compare two windows created with equal, but different touch lists. Should still be the same
		correct = correct && window_0.compareTo(window_2) == 0;
		//compare two windows created with unequal touch lists. They should be unequal
		correct = correct && window_0.compareTo(window_3) > 0;
		//compare two windows of differing size. They should not be equal
		correct = correct && window_0.compareTo(window_4) > 0;
		
		return correct;
	}
	
	
	private static boolean test_window_hashCode(){
		//TODO
		return false;
	}
	

	//# ChainBuilder class
	private static boolean test_handle_touch(){
		boolean correct = true;
		
		ChainBuilder chain_builder = new ChainBuilder();
		
		try{
			for(int i=0;i<10000;i++){
				chain_builder.handle_touch(new Touch('a',1.0/i,100));
			}
		}catch(Exception e){
			e.printStackTrace();
			correct=false;
		}
		
		return correct;
	}
	
	
	private static boolean test_authenticate(){
		//TODO none of these will actually work yet because compare is not implemented. Right now I am more testing whether this causes an error that needs to be taken care of.
		boolean correct;
		
		ChainBuilder chain_builder = new ChainBuilder();
		
		//try to authenticate two chains which are the same
		for(int i=0;i<1000;i++){
			chain_builder.handle_touch(new Touch('a',1.0/i,100));
		}
		
		chain_builder.authenticate();
		//wait for the authentication to finish
		while(chain_builder.get_authenticate_state()==ChainBuilder.State.IN_PROGRESS){
			
		}
		
		correct = (chain_builder.get_authenticate_state()==ChainBuilder.State.SUCCESS);
		
		//TODO try to authenticate two chains which are different
		
		
		//TODO try to authenticate two chains who's differance should amount to just above the authentication threshold
		
		
		//TODO try to authenticate two chains who's differance should amount to just below the authentication threshold
		
		
		return correct;
	}
	
	
	private static boolean test_build_chain_from_csv(){
		//TODO
		return false;
	}
	
	
	//# CompareChains class #
	private static boolean test_compare_chains_run(){
		//TODO in testing chain_builder, this method is also tested in authenticate
		
		return true;
	}

	
	//#########################
	//#########################
	//#### Tests for speed ####
	//#########################
	//#########################
	// call all of the same methods as above, but return the time they take
	
	//TODO
	//# Chain class#
	
	
	//TODO
	//# Distribution class #
	
	
	//TODO
	//# Token class #
		

	//TODO
	//# Touch class #

		
	//TODO
	//# Window class #


	//TODO
	//# ChainBuilder class
		
		
	//TODO
	//# CompareChains class #	
	
	
	//This is a generic method for testing the time of something
	private static long test_time(){
		//initialize things
		ChainBuilder chain_builder = new ChainBuilder();

		//initialize
		//chain_builder

		long start_time = System.currentTimeMillis();
		//do the method
		long end_time = System.currentTimeMillis();

		return end_time-start_time;
	}


	//###################
	//###################
	//#### Utilities ####
	//###################
	//###################
	//there are useful across all test cases
	
	///determines whether a is within episilon of b. true if a is within episilon of b.
	private static boolean within_episilon(double a, double b, double episilon){
		return ((a>(b-episilon)) && (a<(b+episilon)));
	}
}