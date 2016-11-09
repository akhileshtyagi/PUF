package data_analysis;

import java.io.File;
import java.util.*;

public class PerformanceMeasure {
    private final static String input_folder_name = "data_sets";

    private final static int[] window_sizes = {1,2,3};
    private final static int[] token_sizes = {1,2,3};
    private final static int[] thresholds = {5000};
    private static int[] user_model_sizes = {400};
    private static int[] auth_model_sizes = {400};

    /**
     * enumerate all possible metrics we might want to know something about
     * tests for each of these will be provided in the run method
     *
     * Tests can be defined as:
     * how accurately can we differentiate TEST from
     * the same user, same device case
     */
    public enum Type {
        SAME_USER_SAME_DEVICE("SAME_USER_SAME_DEVICE"),
        SAME_USER_DIFFERENT_DEVICE("SAME_USER_DIFFERENT_DEVICE"),
        DIFFERENT_USER_SAME_DEVICE("DIFFERENT_USER_SAME_DEVICE"),
        DIFFERENT_USER_DIFFERENT_DEVICE("DIFFERENT_USER_DIFFERENT_DEVICE");

        String name;

        Type(String string) {
            name = string;
        }

        String get_name() {
            return name;
        }
    }

    /**
     * represents a User, Device pairing
     */
    private class UD{
        String user;
        String device;

        public UD(String user, String device){
            this.user = user;
            this.device = device;
        }

        public boolean is_same_user(UD other_UD){
            return user.equals(other_UD.user);
        }

        public boolean is_same_device(UD other_UD){
            return device.equals(other_UD.device);
        }

        // UD's are equal if both user and device are equal
        public boolean equals(Object other_object){
            if(other_object == null){
                return false;
            }

            UD other_UD = (UD)other_object;

            boolean user_equal = (other_UD.user.equals(user));
            boolean device_equal = (other_UD.device.equals(device));

            return user_equal && device_equal;
        }
    }

    protected List<ParameterSet> parameter_set_list;
    protected Map<UD, DataFile> data_file_map;

    protected ParameterSet max_parameter_set;
    protected double max_value;
    protected Type type;

    public PerformanceMeasure(Type type) {
        parameter_set_list = new ArrayList<>();
        data_file_map = new HashMap<>();

        // create a set of parameters to test
        // these are defined by the constants
        for (int a = 0; a < window_sizes.length; a++) {
            for (int b = 0; b < token_sizes.length; b++) {
                for (int c = 0; c < thresholds.length; c++) {
                    for (int d = 0; d < user_model_sizes.length; d++) {
                        for (int e = 0; e < auth_model_sizes.length; e++) {
                            parameter_set_list.add(new ParameterSet(
                                    window_sizes[a],
                                    token_sizes[b],
                                    thresholds[c],
                                    user_model_sizes[d],
                                    auth_model_sizes[e]));
                        }
                    }
                }
            }
        }

        // create a map of the datafiles which will be used
        // maps a user, device combination to a List of data file
        // for that user, device
        File[] files = new File(input_folder_name).listFiles();
        for(File file : files) {
            if (file.isFile()) {
                DataFile data_file = new DataFile(file.getAbsolutePath());

                // user_devcie should be parced from file_name
                // files are named in the form t_[user]_d_[device].csv
                String[] split_file_name_array = file.getName().split("_");

                // make sure the correct parts of the file name are being grabbed
                //System.out.println("user | " + split_file_name_array[1]);

                String[] device_name_array = split_file_name_array[3].split("\\.");
                //System.out.println("device | " + device_name_array[0]);

                UD user_device = new UD(split_file_name_array[1], device_name_array[0]);

                data_file_map.put(user_device, data_file);
            }
        }

        this.type = type;
        this.max_value = -1.0;
    }

    public Type get_type() {
        return this.type;
    }

    public double get_value() {
        return this.max_value;
    }

    public ParameterSet get_parameter_set() {
        return this.max_parameter_set;
    }

    /**
     * preforms some function based on the type of test
     * assigns a max_value to max_value based on the result
     * <p>
     * for each test,
     * I want to find the maximal achievable performance with
     * any set of parameters
     */
    public void run() {
        // for each parameter set
        for (ParameterSet parameter_set : parameter_set_list) {
            double result = 0.0;

            // measure the performance of the test
            // essentially this switch statement chooses the appropriate data files
            //TODO user the switch statement to decide what user_data_file and what auth_data_file should be used
            DataFile user_data_file = null;
            DataFile auth_data_file = null;
            UD user = null;
            UD auth = null;
            switch (this.type) {
                case SAME_USER_SAME_DEVICE:
                    // chose appropriate data files
                    // find two sets with same user, same device
                    for(UD user_device : this.data_file_map.keySet()) {
                        user_data_file = this.data_file_map.get(user_device);
                        auth_data_file = this.data_file_map.get(user_device);
                        break;
                    }

                    break;
                case SAME_USER_DIFFERENT_DEVICE:
                    // chose appropriate data files
                    for(UD user_device : this.data_file_map.keySet()) {
                        // the first iteration, set the user_data_file
                        if (user == null){
                            user = user_device;
                            continue;
                        }

                        // choose an auth data file with a different device but same user
                        if(user.is_same_user(user_device) && !user.is_same_device(user_device)){
                            auth = user_device;
                            break;
                        }
                    }

                    user_data_file = this.data_file_map.get(user);
                    auth_data_file = this.data_file_map.get(auth);
                    break;
                case DIFFERENT_USER_SAME_DEVICE:
                    // chose appropriate data files
                    for(UD user_device : this.data_file_map.keySet()) {
                        // the first iteration, set the user_data_file
                        if (user == null){
                            user = user_device;
                            continue;
                        }

                        // choose an auth data file
                        if(!user.is_same_user(user_device) && user.is_same_device(user_device)){
                            auth = user_device;
                            break;
                        }
                    }

                    user_data_file = this.data_file_map.get(user);
                    auth_data_file = this.data_file_map.get(auth);
                    break;
                case DIFFERENT_USER_DIFFERENT_DEVICE:
                    // chose appropriate data files
                    // chose appropriate data files
                    for(UD user_device : this.data_file_map.keySet()) {
                        // the first iteration, set the user_data_file
                        if (user == null){
                            user = user_device;
                            continue;
                        }

                        // choose an auth data file
                        if(!user.is_same_user(user_device) && !user.is_same_device(user_device)){
                            auth = user_device;
                            break;
                        }
                    }

                    user_data_file = this.data_file_map.get(user);
                    auth_data_file = this.data_file_map.get(auth);
                    break;
            }

            // result of comparing the data sets
            result = compare(parameter_set, user_data_file, auth_data_file);

            // record the result if it is greater than max_value
            if (result > this.max_value) {
                this.max_value = result;
                this.max_parameter_set = parameter_set;
            }
        }

        // max_value will now be the maximal performance found
    }

    /**
     * returns the authentication accuracy of comparing
     * base_file vs. auth_file
     * using the given ParameterSet
     */
    private double compare(ParameterSet parameter_set, DataFile base_file, DataFile auth_file) {
        double result = 0.0;

        // run ModelCompare ( or perhaps use Model_compare_thread )
        // this will output information to files used by Statistics
        Model_compare.compare(parameter_set, base_file, auth_file);

        // run Statistics
        String[] args = new String[1];
        args[0] = "PerformanceMeasure";

        Statistics.main(args);

        //TODO make sure this works
        // read authentication accuracy from a file generated by statistics
        try {
            Scanner input = new Scanner(new File(Statistics.AUTHENTICATION_ACCURACY_OUTPUT_FILE_NAME));

            // value we are concerned about is on line 1, col 3 and is a double
            input.nextLine();
            String line = input.nextLine();

            result = Double.valueOf(line.split("\t")[2]);
        }catch(Exception e){ e.printStackTrace(); }

        // return authentication accuracy
        return result;
    }

    @Override
    public String toString() {
        StringBuilder string_builder = new StringBuilder();

        string_builder.append("test type | ");
        string_builder.append(this.type.get_name());

        string_builder.append("\t");

        string_builder.append("max_value | ");
        string_builder.append(this.max_value);

        string_builder.append("\t\t");

        string_builder.append("parameters | ");
        string_builder.append(this.max_parameter_set);

        return string_builder.toString();
    }
}