package puf;

import components.Chain;
import generator.Generator;
import puf_analysis.Variability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by element on 9/8/16.
 */
public class Challenge{
    /**
     * We will use 2^5 bits per character and only use
     *
     * abcd efgh ijkl
     * mnop qrst uvwx
     * yz
     *
     * [.]
     * [enter]
     * [del]
     * [space]
     *
     * [shift]
     * [symbol]
     *
     * The rationale is that
     * if every bit doens't map to something,
     * then we loose some ability to store useful information
     *
     * in other words, some bits will always be 0
     */
    /*
     * challenges are in the form
     * [BITS_PER_CHARACTER][BITS_PER_PRESSURE] .. [BITS_PER_CHARACTER][BITS_PER_PRESSURE]
     */
    /* represents the char part of each challenge bit */
    public final static int BITS_PER_CHARACTER = 5;

    /* represents the pressure part of each challenge bit */
    public final static int BITS_PER_PRESSURE = 11;

    private Bit[] challenge_bits;
    private String challenge_string;
    private List<Double> pressure_list;
    private UserInput user_input;

    /**
     * Bit[] provided
     */
    public Challenge(Bit[] challenge_bits){
        this.challenge_bits = challenge_bits;
        this.challenge_string = bit_array_to_string(challenge_bits);
        this.pressure_list = bit_array_to_pressure_list(challenge_bits);
    }

//    public Challenge(Bit[] challenge_bits, UserInput user_input){
//        this.challenge_bits = challenge_bits;
//        this.challenge_string = bit_array_to_string(challenge_bits);
//        this.user_input = user_input;
//    }

    /**
     * String provided
     */
//    public Challenge(String string){
//        this.challenge_bits = string_to_bit_array(string);
//
//        // necessary because multiple strings can map to the same bit array
//        // in other words, to ensure that character string is consistent
//        //this.challenge_string = bit_array_to_string(this.challenge_bits);
//        this.challenge_string = string;
//    }
//
//    public Challenge(String string, UserInput user_input){
//        this.challenge_bits = string_to_bit_array(string);
//
//        // necessary because multiple strings can map to the same bit array
//        // in other words, to ensure that character string is consistent
//        //this.challenge_string = bit_array_to_string(this.challenge_bits);
//        this.challenge_string = string;
//
//        this.user_input = user_input;
//    }

    /**
     * get methods
     */
    public String get_challenge_string(){
        return challenge_string;
    }

    public Bit[] get_challenge_bits(){
        return this.challenge_bits;
    }

    public UserInput get_user_input(){
        return this.user_input;
    }

    /**
     * set methods
     */
    /*
    public void set_user_input(UserInput user_input){
        this.user_input = user_input;
    }
    */

    /**
     * compute the userInput in response to this challenge string given a Chain
     *
     * return true if successful
     *          false if unsuccessful
     *
     * this could fail if an element of the userInput does not appear in the chain
     */
    public boolean compute_user_input(Chain chain, Generator generator){
        this.user_input = generator.generate(chain, this.challenge_string);

        return !(this.user_input == null);
    }

    /**
     * a Bit[] will always map to the same String
     * a String will always map to the same Bit[]
     *
     * however a Bit[] may not map back to a string
     */

    /**
     * converts from a bit[] to a string
     *
     * this is done by taking BITS_PER_CHARACTER bits of the array at a time
     * and converting this to an ascii character
     */
    //TODO I think I may have flipped the bits twice, neither time necessary
    protected String bit_array_to_string(Bit[] bit_array){
        String string = "";

        // move BITS_PER_CHARACTER at a time through bit_array
        // it is guarenteed that bit_array.length is a multiple of BITS_PER_CHARACTER
        for(int i=0; i<bit_array.length/BITS_PER_CHARACTER; i++){
            // make an array from i to i + BITS_PER_CHARACTER
            Bit[] array = new Bit[BITS_PER_CHARACTER];

            // begin from the end of the range
            // so as not to flip the max_value
            for(int j=0; j<BITS_PER_CHARACTER; j++){
                array[j] = bit_array[((i+1) * BITS_PER_CHARACTER)-j-1];
            }

            // get the corresponding character to the created array
            string += bit_array_to_character(array);
        }

        /***************************************/

        // if there are any remaining bits
        // in other words if bit_array.length%BITS_PER_CHARACTER > 0,
        // then pretend as though the missing bits are 0's
        int left_over_bits = bit_array.length % BITS_PER_CHARACTER;

        if(left_over_bits > 0) {
            Bit[] array = new Bit[BITS_PER_CHARACTER];

            // for the bits still in challenege bit array
            for (int i = 0; i < left_over_bits; i++) {
                // indexing will start [left_over_bits] from the end
                // build an array from these bits
                array[i] = bit_array[(bit_array.length - left_over_bits) + i];
            }

            // for any remaining bits which are not in challenge bit array
            for(int i=0; i< (BITS_PER_CHARACTER - left_over_bits); i++){
                // they are 0
                array[array.length - i - 1] = new Bit(Bit.Value.ZERO);
            }

            // get the response for the array, add it to string
            string += bit_array_to_character(array);
            System.out.println("added");
        }

        return string;
    }

    /**
     * convert a bit[] into a character
     */
    protected char bit_array_to_character(Bit[] bit_array){
//        if(bit_array[0] == null){
//            System.out.println("bit_array is null");
//        }else{
//            //System.out.println("good");
//        }

        char value = 0;

        // add up the values depending on the position of the bit
        // 0 is MSB
        int place_value = (0b1 << (BITS_PER_CHARACTER-1));
        for(int i=0; i<bit_array.length; i++){
            // add max_value*place_value
            value += bit_array[i].get_int_value() * place_value;

            // increase place max_value
            place_value = place_value >> 1;
        }

        return (char)(value + 'a');
    }

    /**
     * convert a bit[] into a pressure list
     */
    protected List<Double> bit_array_to_pressure_list(Bit[] challenge_bits){
        List<Double> double_list = new ArrayList<>();
        Bit[] bit_array = new Bit[BITS_PER_PRESSURE];

        // for each set of bits in the string
        //TODO think through whether or not this is getting all the bits
        for(int i=0; i<challenge_bits.length/(BITS_PER_PRESSURE + BITS_PER_CHARACTER) - 1; i++) {
            // build the bit[]
            for(int j=0; j<bit_array.length; j++){
                bit_array[j] = challenge_bits[(i+1) * (BITS_PER_PRESSURE + BITS_PER_CHARACTER) - (bit_array.length - j - 1)];
            }

            double_list.add(bit_array_to_pressure(bit_array));
        }

        /************************************/

        // for any remaining bits
        //TODO


        return double_list;
    }

    /**
     * convert a single bit array into the corresponding pressure max_value
     *
     * normalize the values as if pressure is between 0 and 1.0
     * to retrieve the pressure at runtime,
     *
     * the distribution of the pressure should have its max max_value
     * multiplied by this number
     */
    //TODO check that this is correct
    protected double bit_array_to_pressure(Bit[] bit_array){
        double value = 0;

        // add up the values depending on the position of the bit
        // 0 is MSB
        int place_value = (0b1 << (BITS_PER_CHARACTER-1));
        for(int i=0; i<bit_array.length; i++){
            // add max_value*place_value
            value += bit_array[i].get_int_value() * place_value;

            // increase place max_value
            place_value = place_value >> 1;
        }

        return 1.0 / value;
    }

    /**
     * converts a challenge string to its equivilent bit[]
     *
     * each character becomes 4 bits
     * this is determined by their ASCII code mod 2^[BITS_PER_CHARACTER]
     */
    protected Bit[] string_to_bit_array(String string){
        int array_size = string.length() * BITS_PER_CHARACTER;
        Bit[] bit_array = new Bit[array_size];

        // for each character of the string
        for(int i=0; i<string.length(); i++){
            // convert that character to a bit array
            Bit[] character_bit_array = character_to_bit_array(string.charAt(i), BITS_PER_CHARACTER);

            // for each element in the array for the character,
            // add it to the correct spot in the array
            for(int j=0; j<character_bit_array.length; j++) {
                bit_array[(i*BITS_PER_CHARACTER)+j] = character_bit_array[j];
            }
        }

        return bit_array;
    }

    /**
     * convert a single character into a bit[]
     */
    protected static Bit[] character_to_bit_array(char character, int bits){
        Bit[] bit_array = new Bit[bits];

        // take character mod 2^bits to get something within the requested array size
        int mod_character = character-'a' % (0b1 << bits);

        // convert character into bits number of bits
        // this is easier in reverse order because going
        // from msb to lsb I wouldn't know where to start the mask
        int mask = 0b1;
        for(int i=bits-1; i>=0; i--){
            // mask out the bits I want from mod_character
            // if there is a 1 at this bit position, create Bit(Bit.ONE)
            //
            // bit_array[0] should get the LSB
            bit_array[bits-1-i] = ((mod_character & mask) == mask) ?
                    (new Bit(Bit.Value.ONE)) :
                    (new Bit(Bit.Value.ZERO));

            // shift the mask one to the left so as to get the
            // next iteration will get the next most significant thing
            mask = (mask << 1);
        }

        return bit_array;
    }

    @Override
    public String toString(){
        String format = "bit[] | %s \nchallenge string | %s\n";

        String bit_string = "";
        for(int i=0; i<this.challenge_bits.length; i++) {
            bit_string += this.challenge_bits[i];
            if(i % 5 ==4) bit_string += " ";
        }

        return String.format(format, bit_string, this.challenge_string);
    }
}
