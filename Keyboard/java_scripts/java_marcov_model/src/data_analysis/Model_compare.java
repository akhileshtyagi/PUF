package data_analysis;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


///The purpose of this class is to test out the model compare process on data that has been collected
///The data to used will be contained in the data_sets folder
/// input: 	data_sets folder
/// output: model_compare_output.txt
public class Model_compare {
	//setting this to true will print out the probability of each test instead of the min,max,average
	final static boolean PRINT_ALL_PROBABILITY = true;
	
	//specify the input, output locations
	private final static String output_file_name = "model_compare_output.txt";
	private final static String statistics_output_file_name = "model_compare_output_statistics.txt";
	private final static String input_folder_name = "data_sets";
	
	//specify different model sizes and 
	private final static int[] window_sizes = {3};
	private final static int[] token_sizes = {7};
	private final static int[] thresholds = {5000};
	private static int[] user_model_sizes = {10000};//new int[10]; //5000
	private static int[] auth_model_sizes = {500,1000};//new int[10]; //4000
	
	public static void main(String[] args){
		ArrayList<Model_compare_thread> test_models = new ArrayList<Model_compare_thread>();
		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<String> data_sets = new ArrayList<String>();
		
//		//enumerate the user, auth model sizes
//		for(int i=0;i<user_model_sizes.length;i++){
//			user_model_sizes[i]=(i+1)*1000;
//			auth_model_sizes[i]=(i+1)*1000;
//		}
		
		//read in all data set paths into data_sets
		File[] files = new File(input_folder_name).listFiles();
		for(File file : files){
		  if(file.isFile()){
		    data_sets.add(file.getAbsolutePath());
		  }
		}
		
		///create a number of tests with different parameters
		for(int a=0;a<window_sizes.length;a++){
			for(int b=0;b<token_sizes.length;b++){
				for(int c=0;c<thresholds.length;c++){
					for(int d=0;d<user_model_sizes.length;d++){
						for(int e=0;e<auth_model_sizes.length;e++){
							for(int f=0;f<data_sets.size();f++){
								for(int g=0;g<data_sets.size();g++){
									test_models.add(
											new Model_compare_thread(
													data_sets.get(f),
													data_sets.get(g),
													user_model_sizes[d],
													auth_model_sizes[e],
													window_sizes[a],
													token_sizes[b],
													thresholds[c]));
								}
							}
						}
					}
				}
			}
		}
		
		//run all threads
		for(int i=0;i<test_models.size();i++){
			threads.add(new Thread(test_models.get(i)));
			threads.get(i).start();
		}
		
		boolean finished;
		int offset;
		
		// join all threads
		for(int i=0;i<threads.size();i++){
			finished = false;
			offset = 0;
			
			try {
				threads.get(i).join();
				//TODO make this more functional version of a progress bar work
//				while(!finished){
//					if(threads.get(i+offset).isAlive()){
//						threads.get(i+offset).join(100);
//						
//						//if the thread has been joined in the time
//						if(threads.get(i+offset).isAlive()==false){
//							finished=true;
//						}
//					}
//					
//					offset++;
//					if((offset+i+1)>=threads.size()){
//						offset=0;
//					}
//				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			print_progress(i*(100.0/threads.size()));
		}
		
		
		//print the results to a txt file
		print_results(test_models);
		print_statistics(test_models);
	}
	
	
	//TODO print out the useful statistics like false_positive_percentage
	private static void print_statistics(List<Model_compare_thread> test_models){
		//I want to print the following: 
		//best authentication percentage
		//false postive percentage (at the authentication percentage)
		//false negative percentage (at the authentication percentage)
		PrintWriter output=null;
		
		double best_authentication_percentage = 1;
		double false_positive_percentage = 1;
		double false_negative_percentage = 1;
		double authentication_accuracy = 0;
		
		ArrayList<Double> should_authenticate_percentages = new ArrayList<Double>();
		ArrayList<Double> should_not_authenticate_percentages = new ArrayList<Double>();
		
		// first build a list of the result percentages from the test_models
		for(int i=0;i<test_models.size();i++){
			//they should authenticate if the touches came from different parts of the same data set
			if(test_models.get(i).get_base_data_path().equals(test_models.get(i).get_auth_data_path())){
				should_authenticate_percentages.addAll(test_models.get(i).get_auth_probability_list());
			}else{
				should_not_authenticate_percentages.addAll(test_models.get(i).get_auth_probability_list());
			}
		}
		
		// use Statistics class to calculate the false positive and false negative percentages
		best_authentication_percentage = Statistics.best_authentication_percentage(should_authenticate_percentages, should_not_authenticate_percentages);
		false_positive_percentage = Statistics.false_positive_percentage(best_authentication_percentage, should_authenticate_percentages, should_not_authenticate_percentages);
		false_negative_percentage = Statistics.false_negative_percentage(best_authentication_percentage, should_authenticate_percentages, should_not_authenticate_percentages);
		authentication_accuracy = Statistics.authentication_accuracy(best_authentication_percentage, should_authenticate_percentages, should_not_authenticate_percentages);
		
		try {
			output = new PrintWriter(statistics_output_file_name, "UTF-8");
			
			//TODO print out the probability for each individual compairason
			output.println("best_authentication_percentage: "+best_authentication_percentage+"\n"
					+ "false_positive_percentage: "+false_positive_percentage+"\n"
					+ "false_negative_percentage: "+false_negative_percentage+"\n"
					+ "authentication_accuracy: "+authentication_accuracy+"\n"
					+ "number_of_tests_conducted: "+(should_authenticate_percentages.size()+should_not_authenticate_percentages.size()));
			
			output.close();
		} catch (Exception e) {
			System.out.println("Failed to open output file");
			e.printStackTrace();
		}
	}
	
	
	//prints out a progress bar
	private static void print_progress(double percent){
		char character='=';
		
		//make sure percent is between 0 and 100
		percent = (percent>100)?(100):(percent);
		percent = (percent<0)?(0):(percent);
		
		System.out.print("progress: 0:");
		
		for(int i=0;i<10;i++){
			System.out.print((percent>=i*10)?(character):(' '));
		}
		
		System.out.println(":100");
	}
	
	
	///output the results to a text file, these are the results for the individual compairasons
	private static void print_results(List<Model_compare_thread> test_models){
		//do things based on PRINT_ALL_PROBABILITY
		//things I want to print
		//1) probability with which the data sets authenticated [min, max, average]
		//2) base data set used
		//3) auth data set used
		//4) size of base model
		//5) size of auth model
		PrintWriter output=null;
		
		try {
			output = new PrintWriter(output_file_name, "UTF-8");
			
			if(!PRINT_ALL_PROBABILITY){
				output.println("base_data_set\t"
						+ "auth_data_set\t"
						+ "window_size\t"
						+ "token_size\t"
						+ "threshold\t"
						+ "base_model_size\t"
						+ "auth_model_size\t"
						+ "min\t"
						+ "max\t"
						+ "average");
				for(int i=0;i<test_models.size();i++){	
					String auth_data_name = test_models.get(i).get_auth_data_path();
					String base_data_name = test_models.get(i).get_base_data_path();
					
					//put the data paths in a more read-able format
					String[] split_auth_string = auth_data_name.split("/");
					String[] split_base_string = base_data_name.split("/");
					
					auth_data_name = split_auth_string[split_auth_string.length-1];
					base_data_name = split_base_string[split_base_string.length-1];
					
					//output.print("-");
					output.println(base_data_name + "\t"
							+ auth_data_name + "\t"
							+ test_models.get(i).get_window_size() + "\t"
							+ test_models.get(i).get_token_size() + "\t"
							+ test_models.get(i).get_threshold() + "\t"
							+ test_models.get(i).get_base_model_size() + "\t"
							+ test_models.get(i).get_auth_model_size() + "\t"
							+ test_models.get(i).min_authentication_probability + "\t"
							+ test_models.get(i).max_authentication_probability + "\t"
							+ test_models.get(i).average_authentication_probability);
				}
			}else{
				//TODO print out the probability for each individual compairason
				output.println("base_data_set\t"
						+ "auth_data_set\t"
						+ "window_size\t"
						+ "token_size\t"
						+ "threshold\t"
						+ "base_model_size\t"
						+ "auth_model_size\t"
						+ "probability");
				for(int i=0;i<test_models.size();i++){	
					String auth_data_name = test_models.get(i).get_auth_data_path();
					String base_data_name = test_models.get(i).get_base_data_path();
					
					//put the data paths in a more read-able format
					String[] split_auth_string = auth_data_name.split("/");
					String[] split_base_string = base_data_name.split("/");
					
					auth_data_name = split_auth_string[split_auth_string.length-1];
					base_data_name = split_base_string[split_base_string.length-1];
					
					//output.print("-");
					for(int k=0;k<test_models.get(i).get_auth_probability_list().size();k++){
						output.println(base_data_name + "\t"
								+ auth_data_name + "\t"
								+ test_models.get(i).get_window_size() + "\t"
								+ test_models.get(i).get_token_size() + "\t"
								+ test_models.get(i).get_threshold() + "\t"
								+ test_models.get(i).get_base_model_size() + "\t"
								+ test_models.get(i).get_auth_model_size() + "\t"
								+ test_models.get(i).get_auth_probability_list().get(k));
					}
				}
			}
			
			output.close();
		} catch (Exception e) {
			System.out.println("Failed to open output file");
			e.printStackTrace();
		}
	}
}
