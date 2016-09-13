package puf;

/**
 * Created by element on 9/8/16.
 */
public class Response {
    protected Bit[] bit_array;

    public Response(Bit[] bit_array){
        this.bit_array = new Bit[bit_array.length];

        for(int i=0; i<bit_array.length; i++) {
            this.bit_array[i] = new Bit(bit_array[i].bit_value);
        }
    }

    /**
     * retun the size of the response
     */
    public int get_response_length() {
        return bit_array.length;
    }

    /**
     * return a representation of the response
     */
    public Bit[] get_response(){
        return bit_array;
    }

    /**
     * compute the hamming distance between
     * this response and the given response
     *
     * returns -1 on error,
     * else returns hamming distance
     */
    public int hamming_distance(Response response){
        //TODO test
        int hamming_distance = 0;

        if(bit_array.length == response.bit_array.length) {
            // compute the number of bits which are different between the byte arrays
            for (int i = 0; i < bit_array.length; i++) {
                hamming_distance += ( bit_array[i].get_value() == response.bit_array[i].get_value() )?(0):(1);
            }
        } else {
            hamming_distance = -1;
        }

        return hamming_distance;
    }
}
