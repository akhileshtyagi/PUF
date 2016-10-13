package data_analysis;

import java.util.ArrayList;
import java.util.List;

public class PerformanceMeasure{

    /**
     * enumerate all possible metrics we might want to know something about
     * tests for each of these will be provided in the run method
     */
    public enum Type{
        SAME_USER_SAME_DEVICE("SAME_USER_SAME_DEVICE"),
        SAME_USER_DIFFERENT_DEVICE("SAME_USER_DIFFERENT_DEVICE"),
        DIFFERENT_USER_SAME_DEVICE("DIFFERENT_USER_SAME_DEVICE"),
        DIFFERENT_USER_DIFFERENT_DEVICE("DIFFERENT_USER_DIFFERENT_DEVICE");

        String name;

        Type(String string){
            name = string;
        }

        String get_name(){
            return name;
        }
    }

    protected List<ParameterSet> parameter_set_list;

    protected double value;
    protected Type type;

    public PerformanceMeasure(Type type){
        parameter_set_list = new ArrayList<>();

        // create a set of parameters to test
        //TODO

        this.type = type;
        this.value = 0.0;
    }

    public Type get_type(){
        return this.type;
    }

    public double get_value(){
        return this.value;
    }

    /**
     * preforms some function based on the type of test
     * assigns a value to value based on the result
     *
     * for each test,
     * I want to find the maximal achievable performance with
     * any set of parameters
     */
    public void run(){
        // for each parameter set
        for(ParameterSet parameter_set : parameter_set_list) {
            double result = 0.0;

            // measure the performance of the test
            switch (this.type) {
                case SAME_USER_SAME_DEVICE:
                    // chose appropriate data files
                    // TODO run the test
                    result = compare(parameter_set, null, null);
                    break;
            }

            // record the result if it is greater than value
            value = result > value ? result : value;
        }

        // value will now be the maximal performance found
    }

    /**
     * returns the authentication accuracy of comparing
     * base_file vs. auth_file
     * using the given ParameterSet
     */
    private double compare(ParameterSet parameter_set, DataFile base_file, DataFile auth_file){
        // run ModelCompare
        //TODO

        // run Statistics
        //TODO

        //TODO
        return 0.0;
    }

    @Override
    public String toString(){
        StringBuilder string_builder = new StringBuilder();

        string_builder.append("test type | ");
        string_builder.append(this.type.get_name());

        string_builder.append("\t");

        string_builder.append("value | ");
        string_builder.append(this.value);

        return string_builder.toString();
    }
}