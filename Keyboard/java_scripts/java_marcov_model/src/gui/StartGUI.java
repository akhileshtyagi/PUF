package gui;

//The purpose of this class is to consoladate all of the information about the program, and tests into one place.
//features
// 1) allows browsing of the readme file
// 2) allows tests to be run
// 	a) speed tests
//  b) correctness tests
//  c) model compare tests
// 3) allows printing of model
// 4) allows browsing of test output files

/** GUI useful for testing.
 * Contains functionality to run test code and view results.
 */
public class StartGUI {
	static Marcov_frame frame;
	
	public static void main(String[] args) {
		//TODO create a thread for the frame to run on
		// TODO or start the frame on its own thread
		frame = new Marcov_frame();
	}

	
	///causes the frame to close
	public static void exit(){
		frame.dispose();
	}
}
