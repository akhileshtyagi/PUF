///This class is used to test that the model is being built correctly. Also tested is the model compairason. and various classes used in model creating. The idea is to print out the tests which fail.
public class Main{
	//all methods return true if they pass
	public static void Main(String args[]){
		//make calls to all of the classes to be tested
		boolean test_model_compare_success = test_model_compare();
		boolean test_distribution_success = test_distribution();
		boolean test_model_building_success = test_model_building();
		boolean test_chain_success = test_chain();

		//print out errors in a readable format
		System.out.println("program overview error log:");

		if(test_model_compare_success &&
			test_distribution_success &&
			test_model_building_success &&
			test_chain_success)
		{
			System.out.println("\tall tests successful");
		}else{
			if(!test_model_compare_success){
				System.out.println("\tmodel_compare fails");
			}
		
			if(!test_distribution_success){
				System.out.println("\tdistribution fails");
			}
	
			if(!test_model_building_success){
				System.out.println("\tmodel_building fails");
			}

			if(!test_chain_success){
				System.out.println("\tchain fails");
			}
		}

		return;		
	}


	//### classes to be tested ###
	private static boolean test_model_compare(){
		//TODO test the model_compare.java file for correctness
	}
	

	private static boolean test_distribution(){
		boolean correct=true;

		//test functions in the distribution class
		boolean test_calc_min_success = test_calc_min();
		boolean test_calc_max_success = test_calc_max();
		boolean test_calc_average_success = test_calc_average();
		boolean test_calc_standard_deviation_success = test_calc_standard_deviation();
		
		//print out any errors in a readable way
		System.out.println("distribution error log:");

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


	private static boolean test_model_building(){
		//TODO test different aspects of building the model for correctness
	}

	
	///calls methods to test the chain class
	private static boolean test_chain(){
		//TODO call methods to test the functionality of the chain class
	}


	//### methods to be tested ###
	//# distribution class #
	private static boolean test_calc_min(){
		boolean correct = false;	
		ArrayList<Touch> touches = new ArrayList<Touch>();
		Distribution dist;

		//create a List of touches with a minimum value
		touches.add(new Touch('a',.1);
		touches.add(new Touch('a',.2);
		touches.add(new Touch('a',.3);
		touches.add(new Touch('a',.4);
		touches.add(new Touch('a',.5);
		touches.add(new Touch('a',.6);
		touches.add(new Touch('a',.7);
		touches.add(new Touch('a',.8);
		touches.add(new Touch('a',.9);

		
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
		touches.add(new Touch('a',.1);
		touches.add(new Touch('a',.2);
		touches.add(new Touch('a',.3);
		touches.add(new Touch('a',.4);
		touches.add(new Touch('a',.5);
		touches.add(new Touch('a',.6);
		touches.add(new Touch('a',.7);
		touches.add(new Touch('a',.8);
		touches.add(new Touch('a',.9);

		
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
		touches.add(new Touch('a',.1);
		touches.add(new Touch('a',.2);
		touches.add(new Touch('a',.3);
		touches.add(new Touch('a',.4);
		touches.add(new Touch('a',.5);
		touches.add(new Touch('a',.6);
		touches.add(new Touch('a',.7);
		touches.add(new Touch('a',.8);
		touches.add(new Touch('a',.9);

		
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
		touches.add(new Touch('a',.1);
		touches.add(new Touch('a',.2);
		touches.add(new Touch('a',.3);
		touches.add(new Touch('a',.4);
		touches.add(new Touch('a',.5);
		touches.add(new Touch('a',.6);
		touches.add(new Touch('a',.7);
		touches.add(new Touch('a',.8);
		touches.add(new Touch('a',.9);

		
		//test to see that calc_min finds this value correctly
		dist = new Distribution(touches);

		//TODO make sure this is correct
		//actual value should be around .2582
		if(dist.get_standard_deviation()>(.258)&&dist.get_standard_deviation()<(.2585)){
			correct=true;
		}
		
		return correct;
	}
	

	//TODO
	//# Model Compare #
	

	//TODO
	//# Model Building #


	//TODO
	//# Chain #
}
