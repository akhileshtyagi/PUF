package statistics;

import java.io.File;
import java.util.Scanner;

/**
 * Provides a basic menu for outputting statistics information
 * 
 * @author element
 *
 */
public class StatisticsMenu {
    // directory containing data output from gestures app
    public static final String inputFileName = "direcotry";
    public static final String outputFileName = "statistics";

    private enum Stats {
	averageHammering(1), consistencyHammering(2), genFigures(3), normalDistribution(4);

	public final int selectNumber;

	Stats(int num) {
	    this.selectNumber = num;
	}
    }

    public static void main(String[] args) {
	File file = new File(inputFileName);
	Hamming hammer = new Hamming(file);
	GeneralStatistics stats = new GeneralStatistics(file);

	int selection = getUserChoice();
	double output = 0.0;

	if (Stats.averageHammering.selectNumber == selection) {
	    output = hammer.computeAverageHammering();
	} else if (Stats.consistencyHammering.selectNumber == selection) {
	    output = hammer.computeConsistencyHammering();
	} else if (Stats.genFigures.selectNumber == selection) {
	    // TODO split this into several options
	} else if (Stats.normalDistribution.selectNumber == selection) {
	    output = stats.computeNormalDistribution();
	}

	// TODO find something better to do with the output (like print it to a
	// file)
	System.out.println(output);
    }

    /**
     * Reads the command line to get input from the user.
     * 
     * @return user's choice
     */
    private static int getUserChoice() {
	String choices = "";
	int selection = 0;
	Scanner input = new Scanner(System.in);

	choices += "1 ) averageHammering\n";
	choices += "2 ) consistencyHammering\n";
	choices += "3 ) genFigures\n";
	choices += "4 ) normalDistribution\n";

	while (!((selection > 0) && (selection < 5))) {
	    System.out.print(choices);
	    System.out.print("Please enter your selection: ");

	    selection = input.nextInt();
	}

	return selection;
    }

    /**
     * prints data to the output folder a file containing information about the
     * tests that were run and their results
     */
    private static void printOutputFile(double output) {
	// TODO
    }
}