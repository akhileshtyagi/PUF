package data_analysis;

import java.util.ArrayList;
import java.util.List;

/**
 * Designed as a test suite to
 * aggregate various performance measures
 *
 * uses Model_compare and Statistics
 *
 * generates data about comparisons between
 * same user - same device
 * same user - different device
 * different user - same device
 * different user - different device
 */
public class OverallPerformance {

    public static void main(String[] args){
        List<PerformanceMeasure> performance_measure_list = new ArrayList<>();

        // create a set of PerformanceMeasure,
        // one performance measure for each test defined
        for(PerformanceMeasure.Type measure_type : PerformanceMeasure.Type.values()) {
            performance_measure_list.add(new PerformanceMeasure(measure_type));
        }

        // run each performance measure
        for(PerformanceMeasure performance_measure : performance_measure_list){
            performance_measure.run();
        }

        // print out the results
        for(PerformanceMeasure performance_measure : performance_measure_list){
            System.out.println(performance_measure);
        }
    }
}
