package puf_test;

import puf.Bit;
import puf.Challenge;

/**
 * Created by element on 9/19/16.
 */
public class ChallengeTest extends Challenge {
//    public ChallengeTest(String string){
//        // convert the string to
//        super(string);
//    }

//    public ChallengeTest(){
//
//    }

    public ChallengeTest(Bit[] bit_array){
        super(bit_array);
    }

    public void test_conversion(){
        /* assume string passed in */
        // translate into a bit
        //Bit[] bit_array_0 = string_to_bit_array(get_challenge_string());
//        Bit[] bit_array_0 = string_to_bit_array("ABCD");
//
//        System.out.print("bit array  | ");
//        for(int i=0; i<bit_array_0.length; i++) {
//            System.out.print(bit_array_0[i]);
//            if(i % 5 ==4) System.out.print(" ");
//        }
//        System.out.println();
//
//        // translate back to a string
//        String string_0 = bit_array_to_string(bit_array_0);
//
//        System.out.println("string_0 | " + string_0);
//
//        // compare are they the same?
//        System.out.println("pass {string -> bit[] -> string}? " + (get_challenge_string().equals(string_0)));

        //////

        /* assume bit array passed in */

        System.out.println(this);

        // translate back to a string
        String string_1 = bit_array_to_string(get_challenge_bits());

        System.out.println(string_1);
        System.out.println(this);

        // translate into a bit[]
        Bit[] bit_array_1 = string_to_bit_array(string_1);

        System.out.println(this);

        // compare are they the same?
        boolean same = true;
        for(int i=0; i<bit_array_1.length; i++){
            if(get_challenge_bits()[i].equals(bit_array_1[i])){
                same = false;
            }
        }

        System.out.println("pass{bit[] -> string -> bit[]}? " + same);
    }

    /**
     * test
     * 1. bits to string functions
     * 2. string to bits functions
     */
    public static void main(String[] args){
        // begin with a string
        //String string_0 = "andk";
        //ChallengeTest challenge_test = new ChallengeTest(string_0);

        //challenge_test.test_conversion();

        // test the challenge with a bit array
        ChallengeTest challenge_test = new ChallengeTest(create_dummy_bit_array());
        challenge_test.test_conversion();
    }

    private static Bit[] create_dummy_bit_array(){
        int array_length = 10;
        int bits_per_character = 5;
        Bit[] bit_array = new Bit[array_length];
        char[] character_array = new char[(int)Math.ceil(((double)array_length)/((double)bits_per_character))];

        // populate the character array ( this will be the alphabet in order
        for(int i=0; i< array_length/bits_per_character; i++) {
            character_array[i] = (char)('A' + i);
        }

        // for each bit
        for(int i=0; i<array_length; i++){
            char character = character_array[(int)Math.floor( ((double)i)/((double)bits_per_character) )];

            // determine bit value based on the location within the character
            char mask = (char)(0b1 << (i%5));
            Bit.Value bit_value = ( ((character-'A') & mask) == 1 ) ? Bit.Value.ONE : Bit.Value.ZERO;

            bit_array[i] = new Bit(bit_value);
        }

        return bit_array;
    }
}
