package isu.edu.keyboardauthenticationservice.keyboardAuthenticationInterface;

/**
 * Created by element on 7/1/16.
 */

import android.view.MotionEvent;

/**
 * it may seem excessive to wrap the MotionEvent in another class
 *
 * The following is the rationale:
 * It is possible that in the future more than the MotionEvent may be needed
 * If this is the case,
 * then it may simply be added to this class.
 *
 * The alternative would be to add new methods to the interface which
 * is messy because new code must be added beyond
 * code for the addition.
 * new method calls need to be made and so forth.
 *
 * In short,
 * doing it this way where all the data needed for our service is packaged
 * together minimizes the amount of effort needed to add things.
 */
public class Data {
    /** holds the result of Chain.compareTo() */
    public double compare_result;

    /** confidence in compare_result */
    public double confidence;

    public Data(){}
}
