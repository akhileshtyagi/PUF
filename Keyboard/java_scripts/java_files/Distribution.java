///this class knows how to calculate the distribution of a list of touches
public class Distribution{
	private double min;
	private double max;
	private double average;
	private double standard_deviation;
	
	public Distribution(List<Touch> touches){
		min = calc_min(touches);
		max = calc_max(touches);
		average = calc_average(touches);
		standard_deviation = calc_standard_deviation(touches);
	}


	///copy constructor. This exists because computations are done in the constructor. Copying in this way avoids recomputation.
	public Distribution(Distribution d){
		
	}


	///updates the distribution using a list of touches. This update has nothing to do with the old values in the distribution. It is synonomous to creating a new Distribution object with this list of touches.
	public void update(List<Touch> touches){
		min = calc_min(touches);
		max = calc_max(touches);
		average = calc_average(touches);
		standard_deviation = calc_standard_deviation(touches);
	}


	///calculate min pressure value in touches. Returns 1 if  no touches in list
	private double calc_min(List<Touch> touches){
		//TODO check for correctness
		Iterator<Touch> touches_iterator = touches.iterator();
		double min_pressure = 1;

		while(touches_iterator.hasNext()){
			Touch t = touches_iterator.next();
			
			if(t.get_pressure() < min_pressure){
				min_pressure = t.get_pressure();
			}
		}

		return min_pressure;
	}

	///calculate max pressure value in touches. returns 0 on no touches
	private double calc_max(List<Touch> touches){
		//TODO check for correctness
		Iterator<Touch> touches_iterator = touches.iterator();
		double max_pressure = 0;

		while(touches_iterator.hasNext()){
			Touch t = touches_iterator.next();
			
			if(t.get_pressure() > max_pressure){
				max_pressure = t.get_pressure();
			}
		}

		return max_pressure;
	}


	///calcualte average pressure value in touches. returns 0 on no touches
	private double calc_average(List<Touch> touches){
		//TODO check for correctness
		Iterator<Touch> touches_iterator = touches.iterator();
		double average = 0;
		double total_pressure = 0;

		while(touches_iterator.hasNext()){
			Touch t = touches_iterator.next();
			
			total_pressure += t.get_pressure()
		}

		average = total_pressure/touches.size();

		return average;
	}


	///calculate standard deviation of pressure values in touches
	private double calc_standard_deviation(List<Touch> touches){
		//TODO
		double std = 0;


		return std;
	}


	public double get_min(){
		return min;
	}


	public double get_max(){
		return max;
	}


	public double get_average(){
		return average;
	}


	public double get_standard_deviation(){
		return standard_deviation;
	}
}
