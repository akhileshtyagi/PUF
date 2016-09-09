package arbiter;

import components.Chain;
import components.Touch;
import puf.Bit;
import puf.Challenge;

/**
 * Created by element on 9/9/16.
 *
 * defines the functions needed by a PUF arbiter
 */
public interface Arbiter {
    /**
     * returns 0 or 1 based on quantization
     */
    public Bit quantize_bit(Chain chain, Challenge challenge, Touch touch);
}
