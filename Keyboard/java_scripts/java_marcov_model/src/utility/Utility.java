package utility;

/**
 * Created by element on 9/9/16.
 */
public class Utility {
    /**
     * take the average of the given array
     */
    public static double average(Double[] array){
        Double sum = 0.0;
        for(Double d : array){
            sum += d;
        }

        return sum / array.length;
    }
}
