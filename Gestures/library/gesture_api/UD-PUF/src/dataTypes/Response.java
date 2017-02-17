package dataTypes;

import java.io.Serializable;
import java.util.*;

import org.python.core.*;
import org.python.util.PythonInterpreter;

/**
 * Represents one response created by a user
 */
public class Response implements Serializable {
    public enum QuantizationType{
        FLAT_AVERAGE,
        N_5_MOVING_AVERAGE,
        N_10_MOVING_AVERAGE,
        CUMULATIVE_MOVING_AVERAGE
    }

    //TODO change this to test different quantization types
    public static QuantizationType QTYPE = QuantizationType.FLAT_AVERAGE;
    public static int RESPONSE_BITS = 128;

    public static boolean TRANSFORM_RESPONSE = false;

    private static final long serialVersionUID = -292775056595225846L;
    public static final String PYTHON_UTIL_DIRECTORY =
            "/home/element/PUF/Gestures/library/python_scripts";
    public static final int NORMALIZED_ONE_AXIS_POINTS = 32;

    // List of points which the user swiped
    // always maintains the origional response pattern
    private ArrayList<Point> responsePattern;

    // normalized list of points. This will be overridden every time normalize
    // is called
    private ArrayList<Point> normalizedResponsePattern;

    // Count of motion events this response originally had
    private int motionEvenCount;

    public Response(List<Point> responsePattern) {
        this.responsePattern = new ArrayList<Point>(responsePattern);
        motionEvenCount = responsePattern.size();

        this.normalizedResponsePattern = this.responsePattern;
        // ArrayList<Point>(responsePattern);
    }

    /**
     * master calls other master algor
     */
    public void normalize(List<Point> normalizingPoints) {
        //TODO use this true,false to toggle the type of normalization preformed
        normalize(normalizingPoints, false);
    }

    /**
     * master normalization algorithm
     * chooses which algorithm to use
     */
    public void normalize(List<Point> normalizingPoints, boolean two_axis_normalization) {
        if(two_axis_normalization) {
            // this is the origional algorithm
            normalize_8(normalizingPoints);
        }else{
            normalize_one_axis(normalizingPoints);
        }
    }

    /**
     * return the normalized response pattern
     */
    public List<Point> getOrigionalResponse() {
        return this.responsePattern;
        // return this.normalizedResponsePattern;
    }

    /**
     * return the normalized response pattern
     */
    public List<Point> getNormalizedResponse() {
        return this.normalizedResponsePattern;
    }

    public int getMotionEvenCount() {
        return motionEvenCount;
    }

    /**
     * returns the time of the last point minus the time of the first point.
     * This method preforms the function literally. the time mig variable will
     * be changed during normalization. After the point is normalized this will
     * most likely return a different value compared to before the response
     * being normalized.
     */
    public double getTimeLength() {
        //TODO error no response points added
        if(responsePattern.size() == 0){ return -1; }

        return responsePattern.get(responsePattern.size() - 1).getTime() - responsePattern.get(0).getTime();
    }

    /**
     * remove all duplicate points from the response pattern.
     * Points will only be compared in terms of x and y.
     * <p>
     * return the number of duplicates removed
     */
    public int remove_duplicates() {
        int removed_count = 0;

        // filter response_points
        if (this.responsePattern.size() > 1) {
            Point prev_point;
            Point current_point;
            int i = 1;

            // go though list of points checking that neighbors are equal
            while (i < responsePattern.size()) {
                prev_point = responsePattern.get(i - 1);
                current_point = responsePattern.get(i);

                // check equality
                if (locations_equal(current_point, prev_point)) {
                    // points are equal, throw current point out
                    responsePattern.remove(i);
                    removed_count++;
                } else {
                    i++;
                }
            }
        }

        return removed_count;
    }

    /**
     * determines if x1 == x2 and y1 == y2
     */
    private boolean locations_equal(Point p1, Point p2) {
        double episilon = .001;
        return (Math.abs(p2.getX() - p1.getX()) < episilon) && (Math.abs(p2.getY() - p1.getY()) < episilon);
    }

    /**
     * quantize the response.
     * This can only be done after the response is normalized
     *
     * returns a 128 BitSet
     */
    public BitSet quantize(){
        BitSet bit_set = new BitSet(RESPONSE_BITS);
        bit_set.clear();

        // ensure the response has been normalized
        // objects are initialized to be equal
        if(this.normalizedResponsePattern == responsePattern){
            return bit_set;
        }

        // create properties to change the system path to python scripts director
        Properties properties = setDefaultPythonPath(System.getProperties(), PYTHON_UTIL_DIRECTORY);

        // create a Python Intrepeter for running python functions in util.py
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.initialize(System.getProperties(), properties, new String[0]);
        interpreter.exec("from util " +
                "import simpleMovingAverage, cumulativeMovingAverage");

        // create a python dataList object
        List<Double> pressure_list = new ArrayList<>();

        for(Point p : this.getNormalizedResponse()){
            pressure_list.add(p.getPressure());
        }

        PyObject quantization;
        PyObject quantized_list = null;
        int n = 5;

        // choose quantization method
        if(QTYPE == QuantizationType.FLAT_AVERAGE){
            bit_set = flat_average_quantization(pressure_list);
        }else{
            if(QTYPE == QuantizationType.CUMULATIVE_MOVING_AVERAGE) {
                quantization = interpreter.get("cumulativeMovingAverage");
                quantized_list = quantization.__call__(new PyList(pressure_list));
            }else{
                quantization = interpreter.get("simpleMovingAverage");

                if(QTYPE == QuantizationType.N_5_MOVING_AVERAGE){
                    n = 5;
                }else {
                    n = 10;
                }

                quantized_list = quantization.__call__(new PyList(pressure_list), new PyInteger(n));
            }

            // turn the normalized pressure list into a java object
            Object npl_o = quantized_list.__tojava__(Collection.class);
            List<Boolean> npl = (List<Boolean>)npl_o;

            // set the appropriate bits in bit set based on list returned
            for(int i=0; i<npl.size(); i++){
                if(npl.get(i)) bit_set.set(i);
            }
        }

//        System.out.println("bits: " + bit_set);

        return bit_set;
    }

    /**
     * given a list of pressure values,
     * return a sequence of bits equal to the length of the list
     * 1 := if p_i >= average
     * 2 := if p_i < average
     */
    //TODO test this method
    private BitSet flat_average_quantization(List<Double> pressure_list){
        BitSet bit_set = new BitSet(RESPONSE_BITS);
        double average_pressure = 0;
        double sum = 0;

        for(Double d : pressure_list){
            sum += d;
        }

        average_pressure = sum / pressure_list.size();

        // set all bits to 0
        bit_set.clear();
        for(int i=0; i<pressure_list.size(); i++){
            Double d = pressure_list.get(i);
            if(d >= average_pressure) bit_set.set(i);
        }

        return bit_set;
    }

    /**
     * Adds user.dir into python.path to make Jython look for python modules in working directory in all cases
     * (both standalone and not standalone modes)
     * @param props
     * @return props
     */
    public static Properties setDefaultPythonPath(Properties props, String directory) {
        String pythonPathProp = props.getProperty("python.path");
        String new_value;
        if (pythonPathProp==null)
        {
            new_value  = directory;
        } else {
            new_value = pythonPathProp +java.io.File.pathSeparator + directory + java.io.File.pathSeparator;
        }
        props.setProperty("python.path",new_value);
        return props;
    }

    /**
     * normalize response along one axis
     */
    private void normalize_one_axis(List<Point> normalizingPoints){
        // create properties to change the system path to python scripts director
        Properties properties = setDefaultPythonPath(System.getProperties(), PYTHON_UTIL_DIRECTORY);

        // create a Python Intrepeter for running python functions in util.py
        PythonInterpreter interpreter = new PythonInterpreter();
        interpreter.initialize(System.getProperties(), properties, new String[0]);
        interpreter.exec("from util " + //+ PYTHON_UTIL_SCRIPT +
                "import interpolatedPressure, DataList");//+ PYTHON_UTIL_SCRIPT);

        // create a python dataList object
        //TODO replace the lists with thetypes that they need to be in
        //TODO order to make the function work
        List<List<Double>> challenge_list = new ArrayList<>();
        List<List<Double>> response_list = new ArrayList<>();
        List<Double> pressure_list = new ArrayList<>();

        for(Point p : this.getOrigionalResponse()){
            List<Double> list = new ArrayList<>();
            list.add(p.getX());
            list.add(p.getY());

            response_list.add(list);
            pressure_list.add(p.getPressure());
        }

        for(Point p : normalizingPoints){
            List<Double> list = new ArrayList<>();
            list.add(p.getX());
            list.add(p.getY());

            challenge_list.add(list);
        }

        interpreter.set("file_name", new PyString("file"));
        interpreter.set("tester_name", new PyString("tester"));
        interpreter.set("device_name", new PyString("device"));
        interpreter.set("seed", new PyInteger(0));
        interpreter.set("challenge_list", new PyList(challenge_list));
        interpreter.set("response_list", new PyList(response_list));
        interpreter.set("pressure_list", new PyList(pressure_list));

        PyObject data_list = interpreter.eval(
                "DataList(file_name, tester_name, device_name, seed, challenge_list, response_list, pressure_list)"
        );

        PyObject interpolatedPressure = interpreter.get("interpolatedPressure");
        PyObject normalized_pressure_list = interpolatedPressure.__call__(new PyInteger(NORMALIZED_ONE_AXIS_POINTS), data_list);

        // turn the normalized pressure list into a java object
        Object npl_o = normalized_pressure_list.__tojava__(Collection.class);
        List<Double> npl = (List<Double>)npl_o;

        ArrayList<Point> normalized_point_list = new ArrayList<>();
        for(int i=0; i<npl.size(); i++) {
            normalized_point_list.add(new Point(normalizingPoints.get(i).getX(),
                    normalizingPoints.get(i).getY(), npl.get(i), 0, 0));
        }

        this.normalizedResponsePattern = normalized_point_list;

//        System.out.println("challenge: " + challenge_list);
//        System.out.println("response: " + response_list);
//        System.out.println("pressure: " + pressure_list);
//        System.out.println("normalized_pressure: " + npl);
    }

    /**
     * find the closest point on the x axis or y axis to a given normalization point
     * assign the value of the response point to this normalization point
     */
    public void normalize_0(List<Point> normalizingPoints) {
        // responses of size <= 1 cannot be normalized
        if(responsePattern.size() <= 1){ System.out.println("error: 42"); return; }

        ArrayList<Point> newNormalizedList = new ArrayList<>();

        for(Point np : normalizingPoints){
            Point minimum_point = responsePattern.get(0);
            double minimum_distance = Double.POSITIVE_INFINITY;

            for(Point response_point : this.responsePattern){
                double current_distance =
                        Math.abs(np.getX() - response_point.getX()) < Math.abs(np.getY() - response_point.getY()) ?
                                Math.abs(np.getX() - response_point.getX()) :
                                Math.abs(np.getY() - response_point.getY());

                if(current_distance < minimum_distance){
                    minimum_point = response_point;
                    minimum_distance = current_distance;
                }
            }

            // minimum point is now the closest geographical point
            // to this normalization point
            // set the values of the normalization point to the
            double x,y,pressure,distance,time;
            x = minimum_point.getX();
            y = minimum_point.getY();
            pressure = minimum_point.getPressure();
            distance = Challenge.computeEuclideanDistance(minimum_point, np);
            time = minimum_point.getTime();
            newNormalizedList.add(new Point(x, y, pressure, distance, time));
        }

        this.normalizedResponsePattern = newNormalizedList;
    }

    /**
     * find the geographically closest response point for any given
     * normalization point.
     *
     * Set the value of the normalization point to that of the response point
     */
    public void normalize_1(List<Point> normalizingPoints) {
        // responses of size <= 1 cannot be normalized
        if(responsePattern.size() <= 1){ System.out.println("error: 42"); return; }

        ArrayList<Point> newNormalizedList = new ArrayList<>();

        for(Point np : normalizingPoints){
            Point minimum_point = responsePattern.get(0);
            double minimum_distance = Double.POSITIVE_INFINITY;

            for(Point response_point : this.responsePattern){
                double current_distance = Challenge.computeEuclideanDistance(np, response_point);

                if(current_distance < minimum_distance){
                    minimum_point = response_point;
                    minimum_distance = current_distance;
                }
            }

            // minimum point is now the closest geographical point
            // to this normalization point
            // set the values of the normalization point to the
            double x,y,pressure,distance,time;
            x = minimum_point.getX();
            y = minimum_point.getY();
            pressure = minimum_point.getPressure();
            distance = Challenge.computeEuclideanDistance(minimum_point, np);
            time = minimum_point.getTime();
            newNormalizedList.add(new Point(x, y, pressure, distance, time));
        }

        this.normalizedResponsePattern = newNormalizedList;
    }

    /**
     * find the geographically closest  and second closest
     * response point for any given normalization point.
     *
     * Set the value of the normalization point to that of the response point
     */
    public void normalize_6(List<Point> normalizingPoints) {
        // responses of size <= 1 cannot be normalized
        if(responsePattern.size() <= 1){ System.out.println("error: 42"); return; }

        ArrayList<Point> newNormalizedList = new ArrayList<>();

        for(Point np : normalizingPoints){
            Point minimum_point = responsePattern.get(0);
            double minimum_distance = Double.POSITIVE_INFINITY;

            // find the minimum point
            for(Point response_point : this.responsePattern){
                double current_distance = Challenge.computeEuclideanDistance(np, response_point);

                if(current_distance < minimum_distance){
                    minimum_point = response_point;
                    minimum_distance = current_distance;
                }
            }

            // set the minimum point to before neighbor
            Point before_neighbor = minimum_point;

            // find the minimum distance to all points not equal to before neighbor
            minimum_distance = Double.POSITIVE_INFINITY;
            for(Point response_point : this.responsePattern){
                // want to compare objects
                if(response_point != before_neighbor) {
                    double current_distance = Challenge.computeEuclideanDistance(np, response_point);

                    if (current_distance < minimum_distance) {
                        minimum_point = response_point;
                        minimum_distance = current_distance;
                    }
                }
            }

            // set the second minimum point to after neighbor
            Point after_neighbor = minimum_point;

            // compute a weight for each point based on their distance to the np
            double before_distance = Challenge.computeEuclideanDistance(before_neighbor, np);
            double after_distance = Challenge.computeEuclideanDistance(after_neighbor, np);

            double before_weight =  (before_distance > after_distance) ?
                    1.0 - (after_distance / before_distance) :
                    (before_distance / after_distance);

            double after_weight = 1.0 - before_weight;

            // multiply the values by the weight
            double x,y,pressure,distance,time;
            x = before_neighbor.getX()* before_weight + after_neighbor.getX() * after_weight;
            y = before_neighbor.getY()* before_weight + after_neighbor.getY() * after_weight;
            pressure = before_neighbor.getPressure()* before_weight+ after_neighbor.getPressure() * after_weight;
            distance = before_distance * before_weight + after_distance * after_weight;
            time = before_neighbor.getTime() * before_weight + after_neighbor.getTime() * after_weight;
            newNormalizedList.add(new Point(x, y, pressure, distance, time));
        }

        this.normalizedResponsePattern = newNormalizedList;
    }

    /**
     * this one tries to compute what would have been the value
     * had the measurement been taken at the normalizing point.
     *
     * This can be done by computing the points at
     * the same ratio distance along the response as the normalizing points
     *     in other words, if the response is half as long
     *     then the normalizing points distance will be measured at half the distance
     *
     * This can also be done by taking the closest euclidean distance point to be
     * one of the neighbors
     *
     * Might also be viewed as mapping pairs of response points
     * onto normalization points
     */
    public void normalize_2(List<Point> normalizingPoints) {
        // responses of size <= 1 cannot be normalized
        if(responsePattern.size() <= 1) return;

        ArrayList<Point> newNormalizedList = new ArrayList<>();

        // np stands for normalizing point
        double np_length = Challenge.computeResponseLength(normalizingPoints);
        double response_length = Challenge.computeResponseLength(this.responsePattern);

        // determine the distance along the response at which each normalized point should be measured
        //double measure_ratio = Challenge.computeEuclideanDistance(normalizingPoints.get(0), normalizingPoints.get(1)) / (np_length - 2);
        //double measure_ratio = 0;
        //double response_measure_distance = measure_ratio * response_length;
        // m` = l`l/m
        double response_measure_distance = response_length * Challenge.computeEuclideanDistance(normalizingPoints.get(0), normalizingPoints.get(1)) / np_length;

        // the first point is exactly the values of the first point in the response
        double x,y,pressure,distance,time;
        x = this.responsePattern.get(0).getX();
        y = this.responsePattern.get(0).getY();
        pressure = this.responsePattern.get(0).getPressure();
        distance = Challenge.computeEuclideanDistance(
                this.responsePattern.get(0), normalizingPoints.get(0));
        time = this.responsePattern.get(0).getTime();
        newNormalizedList.add(new Point(x, y, pressure, distance, time));

        // response measure distance tells me that
        // i*response_measure_distance will map to the i'th normalization point
        for(int i=1; i<normalizingPoints.size()-1; i++){
            double response_point_distance = i*response_measure_distance;

            // find the left and right neighbors at this distance
            // determine the left neighbor and
            // the distance along the trace at which left neighbor lies
            int before_neighbor_index = 0;
            double before_neighbor_distance = 0.0;
            double after_neighbor_distance = 0.0;
            for (int j = 1; j < this.responsePattern.size(); j++) {
                before_neighbor_index= j-1;
                before_neighbor_distance = after_neighbor_distance;

                after_neighbor_distance += computeEuclideanDistance(
                        this.responsePattern.get(j), this.responsePattern.get(j - 1));

                // if this last distance has pushed me over np_distance, exit
                // before_neighbor_index should be the left neighbor
                // before_neighbor_distance is the distance along the response of left neighbor
                if(after_neighbor_distance > response_point_distance) break;
            }

            // place the NP on the line between before_neighbor and after_neighbor
            // distance greater than before_neighbor
            //
            // determine the distance of the NP along the line (guarenteed to be positive)
            Point before_neighbor = this.responsePattern.get(before_neighbor_index);
            Point after_neighbor = this.responsePattern.get(before_neighbor_index+1);
            double np_line_distance = response_point_distance - before_neighbor_distance;

            // move np_line_distance in the direction from before_neighbor to after_neighbor
            double neighbor_euclidean_distance = Challenge.computeEuclideanDistance(before_neighbor, after_neighbor);
            double neighbor_x_distance = after_neighbor.getX() - before_neighbor.getX();
            double neighbor_y_distance = after_neighbor.getY() - before_neighbor.getY();
            double neighbor_pressure_distance = after_neighbor.getPressure() - before_neighbor.getPressure();
            double neighbor_time_distance = after_neighbor.getTime() - before_neighbor.getTime(); //TODO

            // compute the ratio of the total distance between the points
            // the normalization point covers
            double np_distance_coverage_ratio = np_line_distance / neighbor_euclidean_distance;

            // now compute the x,y distance the normalization point should be from the left neighbor
            double np_x_distance = np_distance_coverage_ratio * neighbor_x_distance;
            double np_y_distance = np_distance_coverage_ratio * neighbor_y_distance;
            double np_pressure_distance = np_distance_coverage_ratio * neighbor_pressure_distance;
            double np_time_distance = np_distance_coverage_ratio * neighbor_time_distance;

            // compute the absolute x,y points of the normalization point
            double np_x = before_neighbor.getX() + np_x_distance;
            double np_y = before_neighbor.getY() + np_y_distance;
            double np_pressure = before_neighbor.getPressure() + np_pressure_distance;
            //TODO time may be more complecated than this, I should think through what is going on
            double np_time = before_neighbor.getTime() + np_time_distance;

            // use np_x, np_y to determine the distance metric
            // euclidean distance from this point to the normalizing point?
            //TODO does this make sesne, is it useful
            double np_distance = Challenge.computeEuclideanDistance(
                    new Point(np_x, np_y), normalizingPoints.get(i));

            newNormalizedList.add(new Point(np_x, np_y, np_pressure, np_distance, np_time));
        }

        // the last point is exactly the values of the last point
        x = this.responsePattern.get(this.responsePattern.size()-1).getX();
        y = this.responsePattern.get(this.responsePattern.size()-1).getY();
        pressure = this.responsePattern.get(this.responsePattern.size()-1).getPressure();
        distance = Challenge.computeEuclideanDistance(
                this.responsePattern.get(this.responsePattern.size()-1),
                normalizingPoints.get(normalizingPoints.size()-1));
        time = this.responsePattern.get(this.responsePattern.size()-1).getTime();
        newNormalizedList.add(new Point(x, y, pressure, distance, time));

        this.normalizedResponsePattern = newNormalizedList;
    }

    /**
     * This is the best working normalization method
     *
     * Normalizes current Response Pattern to points within normalizingPoints; Interpolates values for
     * pressure, distance, time, etc.
     *
     * @param normalizingPoints List of points for the response to normalize to
     */
    public void normalize_8(List<Point> normalizingPoints) {
        //TODO error no response points added
        if(responsePattern.size() == 0){ return ; }

        ArrayList<Point> newNormalizedList = new ArrayList<>();

        double xTransform, yTransform; // For moving response points to align with normalizingPoints
        double theta, newX, newY, newPressure, newDistance, newTime, newVelocity, newAcceleration; // Values to use in creating normalizedResponsePattern
        double traceDistance; // Euclidean distance from entire current responsePattern
        double deltaD; // Distance between each of the normalizing points
        double remainingDistance; // Used to keep a running total of how far along the next point has gone
        double d; // Used when needing to interpolate more points if the trace isn't as long as list of normalizingPoints
        double cumulativeTime = 0; // Total time between normalized points
        double prevCumulativeTime = 0; // Time used to calculated normalized time; is subtracted from total time
        double interpolated_time; // Total Time / number of points, to be used to add points' time values when interpolating

        int numExtraNormalizingPoints; // Number of new Normalizing Points the current trace covers
        int normalizingPointsLength = normalizingPoints.size();
        int responseLength = responsePattern.size();
        int i, j; // i counts indices of normalizingPoints, j counts indices of responsePattern

        Point prevPoint, curPoint; // Previous and current trace points when looping through response

        // preform the transformation, add first point to new Normalized List
        xTransform = normalizingPoints.get(0).getX() - responsePattern.get(0).getX();
        yTransform = normalizingPoints.get(0).getY() - responsePattern.get(0).getY();

        // determine whether or not to transform the response
        if(TRANSFORM_RESPONSE) transform_response(responsePattern, xTransform, yTransform);

        // add the first point to the normalized list, define velocity and acceleration to be zero for this point
        Point first_point = new Point(responsePattern.get(0).getX(), responsePattern.get(0).getY());

        Point.Metrics[] metrics = Point.Metrics.values();
        for(int m=0; m<metrics.length; m++){
            first_point.set_metric(metrics[m], responsePattern.get(0).get_metric(metrics[m]));
        }

        newNormalizedList.add(first_point);

        // Catch if normalizingTrace is only 1 point (hopefully never happens)
        if (normalizingPoints.size() == 1) {
            this.normalizedResponsePattern = newNormalizedList;
            return;
        }

        // Distance to walk each point to normalized point
        deltaD = computeEuclideanDistance(normalizingPoints.get(0), normalizingPoints.get(1));
        traceDistance = computeTraceDistance();
        numExtraNormalizingPoints = (int) Math.floor((traceDistance / deltaD) + 1);

        // added to handle the case where the response deltaD is longer than normalizingPoints
        numExtraNormalizingPoints = (numExtraNormalizingPoints <= normalizingPoints.size()) ? numExtraNormalizingPoints : normalizingPoints.size();

        remainingDistance = deltaD;
        j = 1;

        // this loop will run NL-1 times
        for (i = 1; i < numExtraNormalizingPoints; i++) {
            prevPoint = responsePattern.get(j - 1);
            curPoint = responsePattern.get(j);

            while (computeEuclideanDistance(prevPoint, curPoint) < remainingDistance) {
                if (j >= responsePattern.size()) {
                    this.normalizedResponsePattern = newNormalizedList;
                    return;
                }
                remainingDistance -= computeEuclideanDistance(prevPoint, curPoint);
                cumulativeTime += curPoint.getTime();
                j++;
                prevPoint = responsePattern.get(j - 1);
                curPoint = responsePattern.get(j);
            }

            // Subtract off the last time added
            cumulativeTime -= curPoint.getTime();

            double x_difference = (curPoint.getX() - prevPoint.getX());
            double x_sine = 1;
            // check that the difference isn't equal to zero to ensure no divide by 0 error
            if (!(x_difference == 0)) {
                // this will make x_sine +1 or -1 depending on the sine
                x_sine = x_difference / Math.abs(x_difference);
            }

            // Now the point we are looking for is between responsePattern[j - 1] and responsePattern[j]
            theta = Math.atan((curPoint.getY() - prevPoint.getY()) / (curPoint.getX() - prevPoint.getX()));
            newX = prevPoint.getX() + (remainingDistance * Math.cos(theta) * x_sine);
            newY = prevPoint.getY() + (remainingDistance * Math.sin(theta) * x_sine);

            //Interpolate pressure and other attributes
            /* pressure */
            newPressure = prevPoint.getPressure() + ((remainingDistance / computeEuclideanDistance(prevPoint, curPoint)) * (curPoint.getPressure() - prevPoint.getPressure()));
            /* distance */
            newDistance = computeEuclideanDistance(new Point(newX, newY, 0), curPoint);
            /* time */
            newTime = cumulativeTime + (curPoint.getTime() * (remainingDistance / computeEuclideanDistance(prevPoint, curPoint))) - prevCumulativeTime;

            // negative time does not make sense
            if(newTime <= 0) {
                // setting this to 1 effectively makes the minimum distance between points 1 ms (seems reasonable)
                //TODO find a basis for this
                newTime = 1;
            }

            /* velocity
            * want to use normalized values for time from previous point (i.e. new time)
            * distance is euclidian distance from the previous point to the current normalized point*/
            newVelocity = (computeEuclideanDistance(new Point(newX, newY), newNormalizedList.get(newNormalizedList.size()-1))) / newTime;
            /* acceleration
            * want to use normalized velocity (ie newVelocity)
            * also ant to use normalized velocity from the previous points*/
            newAcceleration = (newVelocity - newNormalizedList.get(newNormalizedList.size()-1).get_metric(Point.Metrics.VELOCITY)) / newTime;

            Point p = new Point(newX, newY);
            p.set_metric(Point.Metrics.PRESSURE, newPressure);
            p.set_metric(Point.Metrics.DISTANCE, newDistance);
            p.set_metric(Point.Metrics.TIME, newTime);
            p.set_metric(Point.Metrics.VELOCITY, newVelocity);
            p.set_metric(Point.Metrics.ACCELERATION, newAcceleration);

            newNormalizedList.add(p);

            remainingDistance = deltaD + computeEuclideanDistance(prevPoint, newNormalizedList.get(i));

            prevCumulativeTime = cumulativeTime;
        }

        // this breaks things because if we have any points left to do and we don't do them then our normalized response lists will be differant sizes.
        // This means there will be an arrayIndexOoutOfBounds error when we get to the part of the code where we find mu. if >= 1
        if (responsePattern.size() <= 1) {
            this.normalizedResponsePattern = newNormalizedList;
            return;
        }

        // Now take care of remaining (NL - N) points which we need to interpolate
        prevPoint = responsePattern.get(responseLength - 2);
        curPoint = responsePattern.get(responseLength - 1);
        double x_difference = (curPoint.getX() - prevPoint.getX());
        double x_sine = 1;
        // check that the difference isn't equal to zero to ensure no divide by 0 error
        if (!(x_difference == 0)) {
            // this will make x_sine +1 or -1 depending on the sine
            x_sine = x_difference / Math.abs(x_difference);
        }

        theta = Math.atan((curPoint.getY() - prevPoint.getY()) / (curPoint.getX() - prevPoint.getX()));
        d = (numExtraNormalizingPoints * deltaD) - traceDistance;

        cumulativeTime += cumulativeTime + curPoint.getTime();
        interpolated_time = cumulativeTime / normalizingPointsLength;

        // negative time does not make sense
        //TODO reconsider this (and the analog above)
        if(interpolated_time <= 0) {
            // setting this to 1 effectively makes the minimum distance between points 1 ms (seems reasonable)
            // perhaps the absolute value of time would make more sense?
            //TODO find a basis for this
            interpolated_time = 1;
        }

        //TODO setting interpolated time to 11 fixed my issue, but why?
//        System.out.println("interpolated_time: " + interpolated_time + "\tcumulativeTime: " + cumulativeTime + "\tnormalizingPointsLength: " + normalizingPointsLength);
//        interpolated_time = 11;

        for (i = numExtraNormalizingPoints; i < normalizingPointsLength; i++) {
            newX = prevPoint.getX() + (d * Math.cos(theta) * x_sine);
            newY = prevPoint.getY() + (d * Math.sin(theta) * x_sine);

            // Compute pressure and other attributes
            /* pressure */
            newPressure = curPoint.getPressure() + (((curPoint.getPressure() - prevPoint.getPressure()) / (computeEuclideanDistance(curPoint, prevPoint))) * d);
            /* distance */
            newDistance = computeEuclideanDistance(new Point(newX, newY, 0), curPoint);
            /* velocity
            * want to use normalized values for time from previous point (i.e. new time)
            * distance is euclidian distance from the previous point to the current normalized point*/
            newVelocity = (computeEuclideanDistance(new Point(newX, newY), newNormalizedList.get(newNormalizedList.size()-1))) / interpolated_time;
            /* acceleration
            * want to use normalized velocity (ie newVelocity)
            * also ant to use normalized velocity from the previous points*/
            newAcceleration = (newVelocity - newNormalizedList.get(newNormalizedList.size()-1).get_metric(Point.Metrics.VELOCITY)) / interpolated_time;

            Point p = new Point(newX, newY);
            p.set_metric(Point.Metrics.PRESSURE, newPressure);
            p.set_metric(Point.Metrics.DISTANCE, newDistance);
            p.set_metric(Point.Metrics.TIME, interpolated_time);
            p.set_metric(Point.Metrics.VELOCITY, newVelocity);
            p.set_metric(Point.Metrics.ACCELERATION, newAcceleration);

            newNormalizedList.add(p);

            d += deltaD;
        }

        this.normalizedResponsePattern = newNormalizedList;
    }

    /**
     * this is a rewrite of the origional normalization algorithm,
     * hopefully to fix bugs
     *
     * goal:
     * 1. walk along response
     * 1.5 optionally transform response to begin at same point as normalizing points
     * 2. measure at same distance as normalizing points
     * 3. if normalizing points are longer than response,
     *      extrapolate using the last two points of the response
     */
    public void normalize_7(List<Point> normalizingPoints) {
        // responses of size <= 1 cannot be normalized
        if(responsePattern.size() <= 1) return;

        // preform the transformation, add first point to new Normalized List
        double xTransform = normalizingPoints.get(0).getX() - responsePattern.get(0).getX();
        double yTransform = normalizingPoints.get(0).getY() - responsePattern.get(0).getY();

        // determine whether or not to transform the response
        //TODO transform of response is happening on origional points,
        //TODO but this should actually be okay because it happens every time
        //TODO the response is normalized
        if(TRANSFORM_RESPONSE) transform_response(responsePattern, xTransform, yTransform);

        ArrayList<Point> newNormalizedList = new ArrayList<>();

        // np stands for normalizing point
        double np_length = Challenge.computeResponseLength(normalizingPoints);
        double response_length = Challenge.computeResponseLength(this.responsePattern);

        // determine the distance along the response at which each normalized point should be measured
        //double measure_ratio = Challenge.computeEuclideanDistance(normalizingPoints.get(0), normalizingPoints.get(1)) / (np_length - 2);
        //double measure_ratio = 0;
        //double response_measure_distance = measure_ratio * response_length;
        // m` = l`l/m
        //double response_measure_distance = response_length * Challenge.computeEuclideanDistance(normalizingPoints.get(0), normalizingPoints.get(1)) / np_length;

        // response measure distance is the same as the normalization points measure distance
        //TODO two cases,
        //TODO 1. the response length <= normalizing points length
        //TODO 2. the response length > normalizing points length
        double response_measure_distance = Challenge.computeEuclideanDistance(normalizingPoints.get(0), normalizingPoints.get(1));

        // the first point is exactly the values of the first point in the response
        double x,y,pressure,distance,time;
        x = this.responsePattern.get(0).getX();
        y = this.responsePattern.get(0).getY();
        pressure = this.responsePattern.get(0).getPressure();
        distance = Challenge.computeEuclideanDistance(
                this.responsePattern.get(0), normalizingPoints.get(0));
        time = this.responsePattern.get(0).getTime();
        newNormalizedList.add(new Point(x, y, pressure, distance, time));

        // response measure distance tells me that
        // i*response_measure_distance will map to the i'th normalization point
        for(int i=1; (i<normalizingPoints.size()-1); i++){
            double response_point_distance = i*response_measure_distance;

            // break out if the length of the response has been exceeded
            if((response_point_distance > response_length)){
                break;
            }

            // find the left and right neighbors at this distance
            // determine the left neighbor and
            // the distance along the trace at which left neighbor lies
            int before_neighbor_index = 0;
            double before_neighbor_distance = 0.0;
            double after_neighbor_distance = 0.0;
            for (int j = 1; j < this.responsePattern.size(); j++) {
                before_neighbor_index= j-1;
                before_neighbor_distance = after_neighbor_distance;

                after_neighbor_distance += computeEuclideanDistance(
                        this.responsePattern.get(j), this.responsePattern.get(j - 1));

                // if this last distance has pushed me over np_distance, exit
                // before_neighbor_index should be the left neighbor
                // before_neighbor_distance is the distance along the response of left neighbor
                if(after_neighbor_distance > response_point_distance) break;
            }

            // place the NP on the line between before_neighbor and after_neighbor
            // distance greater than before_neighbor
            //
            // determine the distance of the NP along the line (guarenteed to be positive)
            Point before_neighbor = this.responsePattern.get(before_neighbor_index);
            Point after_neighbor = this.responsePattern.get(before_neighbor_index+1);

            // get the next point to be added to the normalizing point list
            newNormalizedList.add(get_normalized_point(normalizingPoints.get(i),
                    before_neighbor, after_neighbor,
                    response_point_distance, before_neighbor_distance));
        }

        // preform extrapolation if needed
        if(newNormalizedList.size() < normalizingPoints.size()-1){
            // based on the last two points in the response pattern
            //TODO perhaps I could base it on the average of the last several points
            //TODO or the average slope of the values among the last 1/10th of the response
            Point before_neighbor = this.responsePattern.get(this.responsePattern.size()-2);
            Point after_neighbor = this.responsePattern.get(this.responsePattern.size()-1);

            double before_neighbor_distance = response_length - Challenge.computeEuclideanDistance(before_neighbor, after_neighbor);

            // we need to extrapolate
            // base the extrapolation on the last two response points
            for(int i=newNormalizedList.size(); i<normalizingPoints.size(); i++){
                // keep stepping out further and further beyond the end of the response
                double response_point_distance = i*response_measure_distance;

                // get the next point to be added to the normalizing point list
                newNormalizedList.add(get_normalized_point(normalizingPoints.get(i),
                        before_neighbor, after_neighbor,
                        response_point_distance, before_neighbor_distance));
            }
        }else {
            // no need to extrapolate
            //
            // the last point is exactly the values of the last point
            x = this.responsePattern.get(this.responsePattern.size() - 1).getX();
            y = this.responsePattern.get(this.responsePattern.size() - 1).getY();
            pressure = this.responsePattern.get(this.responsePattern.size() - 1).getPressure();
            distance = Challenge.computeEuclideanDistance(
                    this.responsePattern.get(this.responsePattern.size() - 1),
                    normalizingPoints.get(normalizingPoints.size() - 1));
            time = this.responsePattern.get(this.responsePattern.size() - 1).getTime();
            newNormalizedList.add(new Point(x, y, pressure, distance, time));
        }

        this.normalizedResponsePattern = newNormalizedList;
    }

    /**
     * return a normalizing point to add based on
     * current normalizing point,
     * before neighbor and after neighbor being mapped to the normalizing point,
     * the response_point_distance: the distance along the response which
     *      is being mapped to the current normalizing point
     * the before_neighbor_distance: the distance along the response to
     *      the before neighbor
     */
    private Point get_normalized_point(Point normalizingPoint,
                                      Point before_neighbor, Point after_neighbor,
                                      double response_point_distance, double before_neighbor_distance){
        double np_line_distance = response_point_distance - before_neighbor_distance;

        // move np_line_distance in the direction from before_neighbor to after_neighbor
        double neighbor_euclidean_distance = Challenge.computeEuclideanDistance(before_neighbor, after_neighbor);
        double neighbor_x_distance = after_neighbor.getX() - before_neighbor.getX();
        double neighbor_y_distance = after_neighbor.getY() - before_neighbor.getY();
        double neighbor_pressure_distance = after_neighbor.getPressure() - before_neighbor.getPressure();
        double neighbor_time_distance = after_neighbor.getTime() - before_neighbor.getTime(); //TODO

        // compute the ratio of the total distance between the points
        // the normalization point covers
        double np_distance_coverage_ratio = np_line_distance / neighbor_euclidean_distance;

        // now compute the x,y distance the normalization point should be from the left neighbor
        double np_x_distance = np_distance_coverage_ratio * neighbor_x_distance;
        double np_y_distance = np_distance_coverage_ratio * neighbor_y_distance;
        double np_pressure_distance = np_distance_coverage_ratio * neighbor_pressure_distance;
        double np_time_distance = np_distance_coverage_ratio * neighbor_time_distance;

        // compute the absolute x,y points of the normalization point
        double np_x = before_neighbor.getX() + np_x_distance;
        double np_y = before_neighbor.getY() + np_y_distance;
        double np_pressure = before_neighbor.getPressure() + np_pressure_distance;
        //TODO time may be more complecated than this, I should think through what is going on
        double np_time = before_neighbor.getTime() + np_time_distance;

        // use np_x, np_y to determine the distance metric
        // euclidean distance from this point to the normalizing point?
        //TODO does this make sesne, is it useful
        double np_distance = Challenge.computeEuclideanDistance(
                new Point(np_x, np_y), normalizingPoint);

        return new Point(np_x, np_y, np_pressure, np_distance, np_time);
    }

    /**
     * Computes total euclidean distance of response pattern
     *
     * @return distance of response pattern
     */
    double computeTraceDistance() {
        double distance = 0;
        Point firstPoint, secondPoint;
        for (int i = 0; i < responsePattern.size() - 1; i++) {
            firstPoint = responsePattern.get(i);
            secondPoint = responsePattern.get(i + 1);
            distance += computeEuclideanDistance(firstPoint, secondPoint);
        }
        return distance;
    }

    /**
     * transform a response. Add x,y to every point.
     */
    private void transform_response(List<Point> response_points, double x_transform, double y_transform) {
        // add x,y to every point
        for (int i = 0; i < response_points.size(); i++) {
            Point p = response_points.get(i);
            response_points.set(i, new Point(p.getX() + x_transform, p.getY() + y_transform, p.getPressure(), p.getDistance(), p.getTime()));
        }
    }

    private double getRadius(Point p) {
        return Math.sqrt(Math.pow(p.getX(), 2) + Math.pow(p.getY(), 2));
    }

    /**
     * compute the euclidean distance between two points
     */
    private double computeEuclideanDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow((p1.getX() - p2.getX()), 2) +
                Math.pow((p1.getY() - p2.getY()), 2));
    }

    @Override
    public String toString(){
        String s = "";

        s += "Response: ";
        s += "[size: " + getOrigionalResponse().size() + "]";
        s += "\n[";

        // print all the points in response
        for(Point p : getOrigionalResponse()){
            s += p.toString();
            s += ", ";
        }

        s += "]";

        return s;
    }

    /**
     * converts this response object to the string used in the .r
     * analysis scripts
     */
    public String toRString(){
        String s = "";
        //s += "[";

        // print all the points in response
        boolean first = true;
        for(Point p : getOrigionalResponse()){
            if(first){
                s += p.toRString();
                first = false;
            }else {
                s += ", ";
                s += p.toRString();
            }
        }

        //s += "]";

        return s;
    }

    /**
     * converts this response object to the string used in the .r
     * analysis scripts
     */
    public String toNormalizedRString(){
        String s = "";
        //s += "[";

        // print all the points in response
        boolean first = true;
        for(Point p : getNormalizedResponse()){
            if(first){
                s += p.toRString();
                first = false;
            }else {
                s += ", ";
                s += p.toRString();
            }
        }

        //s += "]";

        return s;
    }
}

