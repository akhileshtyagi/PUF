package generator;

import components.Chain;
import puf.UserInput;

/**
 * Created by element on 9/10/16.
 *
 * describes how to go from having:
 * 1. Chain
 * 2. Challenge String
 *
 * to having
 * 0. a UserInput coming from chain which
 *      correspond to the challenge String
 */
public interface Generator {
    /**
     * produce a UserInput given a Chain and a challenge String
     *
     * returns null if this is not possible
     */
    public UserInput generate(Chain chain, String string);
}
