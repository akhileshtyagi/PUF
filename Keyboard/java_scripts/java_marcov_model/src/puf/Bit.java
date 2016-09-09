package puf;

/**
 * Created by element on 9/9/16.
 *
 * represents one bit of the Response from the PUF
 */
public class Bit {
    /**
     * represents the values which can be taken by the Bit
     */
    public enum Value{
        ZERO(0), ONE(1);

        private int value;

        Value(int value){
            this.value = value;
        }
    }

    protected Value bit_value;

    public Bit(Value bit_value){
        this.bit_value = bit_value;
    }

    public int get_int_value(){
        return this.bit_value.value;
    }

    public Value get_value(){
        return this.bit_value;
    }
}
