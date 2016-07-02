/**
 * this class exists to ensure the compare value and confidence value
 * are from the same computation.
 */
public class Result {
    public double confidence;
    public double value;

    public Result(){}

//    /** force both value and confidence to be set at the same time */
//    public void setValue(double value, double confidence){
//        this.value = value;
//        this.confidence = confidence;
//    }
//
//    public double getConfidence(){
//        return this.confidence;
//    }
//
//    public double getValue(){
//        return this.value;
//    }
}
