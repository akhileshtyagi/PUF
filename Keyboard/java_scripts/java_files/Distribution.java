///this class knows how to calculate the distribution of a list of touches
public class Distribution{
	private double min;
	private double max;
	private double average;
	private double standard_deviation;
	
	public Distribution(List<Touch> touches){
		calc_min(touches);
		calc_max(touches);
		calc_average(touches);
		calc_standard_deviation(touches);
	}

	///updates the distribution using a list of touches. This update has nothing to do with the old values in the distribution. It is synonomous to creating a new Distribution object with this list of touches.
	public void update(List<Touch> touches){
		calc_min(touches);
		calc_max(touches);
		calc_average(touches);
		calc_standard_deviation(touches);
	}

	///calculate min pressure value in touches
	private void calc_min(List<Touch> touches){
		//TODO
		min=0;
	}

	///calculate max pressure value in touches
	private void calc_max(List<Touch> touches){
		//TODO
		max=1;
	}

	///calcualte average pressure value in touches
	private void calc_average(List<Touch> touches){
		//TODO
		average=.5;
	}

	///calculate standard deviation of pressure values in touches
	private void calc_standard_deviation(List<Touch> touches){
		//TODO
		standard_deviation=.1;
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
