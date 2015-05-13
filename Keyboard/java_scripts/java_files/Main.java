///This class is used to test that the model is being built correctly. Also tested is the model compairason.
public class Main{
	//all methods return true if they pass
	public static void Main(String args[]){
		//make calls to all of the classes to be tested
		test_model_compare();
		test_distribution();
		test_model_building();
	}


	//### classes to be tested ###
	private static boolean test_model_compare(){
		//TODO test the model_compare.java file for correctness
	}
	

	private static boolean test_distribution(){
		//test functions in the distribution class
		if(!(	test_calc_min() &&
			test_calc_max() &&
			test_calc_average() &&
			test_calc_standard_deviation()))
		{
			System.out.println("distribution failed a test");
		}
	}


	private static boolean test_model_building(){
		//TODO test different aspects of building the model for correctness
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
	

	//# Model Compare #
	//TODO

	//TODO
	//# Model Building #
}
