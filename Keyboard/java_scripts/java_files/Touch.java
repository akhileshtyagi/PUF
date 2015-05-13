///This class represents a touch event.
public class Touch{
	private char key;
	private double pressure;

	public Touch(int keycode, double pressure){
		this.keycode=keycode;
		this.pressure=pressure;
	}

	public double touch.get_pressure(){
		return pressure;
	}

	public double touch.get_key(){
		return key;
	}
}
