package test;

import com.sun.org.apache.xpath.internal.SourceTree;

/**
 * Created by element on 1/20/16.
 */
public class trig_test {
    public static void main(String[] args) {
        // theta is in radians
        double theta = Math.atan(1);
        System.out.println("theta:" + theta);

        // negative results in negative if
        theta = Math.atan(-1);
        System.out.println("theta:" + theta);

        // negative results in negative if
        theta = Math.atan(-2);
        System.out.println("theta:" + theta);

        // as n gets large, theta approachs pi/2
        theta = Math.atan(100);
        System.out.println("theta:" + theta);

        /**
         * theta takes on the same sign as the argument
         */

        theta = Math.atan(-1);
        double sin = Math.sin(theta);
        double cos = Math.cos(theta);

        System.out.println("sin:" + sin);
        System.out.println("cos:" + cos);
    }
}
