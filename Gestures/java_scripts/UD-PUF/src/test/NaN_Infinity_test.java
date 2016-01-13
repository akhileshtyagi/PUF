package test;

import java.io.*;

/**
 * Created by element on 1/13/16.
 */
public class NaN_Infinity_test {
    public static void main(String[] args) {
        // basic numberator denominator tests
        double numerator = 10;
        double denominator = 0;
        System.out.println(numerator / denominator);

        numerator = 0;
        denominator = 0;
        System.out.println(numerator / denominator);

        numerator = 0;
        denominator = 10;
        System.out.println(numerator / denominator);

        // line break
        System.out.println();

        // numerator, demoninator inf or nan
        numerator = Double.POSITIVE_INFINITY;
        denominator = 10;
        System.out.println(numerator / denominator);

        numerator = Double.POSITIVE_INFINITY;
        denominator = 0;
        System.out.println(numerator / denominator);

        numerator = 10;
        denominator = Double.POSITIVE_INFINITY;
        System.out.println(numerator / denominator);

        numerator = 0;
        denominator = Double.POSITIVE_INFINITY;
        System.out.println(numerator / denominator);

        numerator = Double.POSITIVE_INFINITY;
        denominator = Double.POSITIVE_INFINITY;
        System.out.println(numerator / denominator);

        // line break
        System.out.println();

        double negative = -10;
        System.out.println(Math.sqrt(negative));


    }
}
