package roc_curve_generation;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * goal is to test hamming functions in
 * comparevaluegenerator.java
 */
public class hamming_test {
    public static void main(String[] args){
        test_hamming_distance();
        test_map_access();
    }

    public static void test_hamming_distance(){
        BitSet bitset0 = new BitSet(128);
        BitSet bitset1 = new BitSet(128);

        int hamming_distance = CompareValueGenerator.hamming_distance(bitset0, bitset1);

        System.out.println("hamming_distance: " + hamming_distance);
    }

    /**
     * an attempt to answer the question:
     * can I access a map with a boolean array and
     * have the values in the boolean array be what matters
     * for choosing the value from the key, value pair
     *
     * yes ?
     * no ?
     */
    public static void test_map_access(){
        Map<String, Integer> condition_map = new HashMap<>();
        boolean[] condition0 = new boolean[3];
        boolean[] condition1 = new boolean[3];
        boolean[] condition2 = new boolean[3];
        boolean[] condition3 = new boolean[3];

        condition0[0] = true;
        condition0[1] = true;
        condition0[2] = true;

        condition1[0] = true;
        condition1[1] = true;
        condition1[2] = true;

        condition2[0] = true;
        condition2[1] = true;
        condition2[2] = false;

        condition_map.put(condition_to_string(condition0), 10);

        System.out.println("should be 10: " + condition_map.get(condition_to_string(condition1)));
        System.out.println("should be null: " + condition_map.get(condition_to_string(condition2)));

        condition3 = condition2;
        condition3[2] = true;

        System.out.println("should be 10: " + condition_map.get(condition_to_string(condition3)));
    }

    public static String condition_to_string(boolean[] condition){
        String s = "";

        for(boolean b : condition){
            s += b ? "1" : "0";
        }

        return s;
    }
}
