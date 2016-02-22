package jUnitTests.dataTypes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import dataTypes.Point;
import dataTypes.Response;

public class ResponseUnit {
    private Response response;
    private MathContext context;

    // TODO make tests which incorporate distance
    @Before
    public void initObjects() throws SecurityException, InstantiationException, IllegalAccessException {
	// set up the context for compairing doubles
	context = new MathContext(10);

	// create the response object to be tested
	List<Point> response_points = new ArrayList<Point>();

	// populate the response_points list with 64 points
	for (int i = 0; i < 9; i++) {
	    response_points.add(new Point(i, i, .1 * i, 100, i));
	}

	response = new Response(response_points);
    }

    /**
     * test normalize to ensure it is working properly at a basic capacity
     */
    @Test
    public void testNormalizeBasicX() {
	// normalize the response to a list and see if it does it correctly
	// create the list to normalize to
	List<Point> normalizing_points = new ArrayList<Point>();

	for (int i = 0; i < 3; i++) {
	    normalizing_points.add(new Point(i * 3, 0, 0));
	}

	// run the method
	this.response.normalize(normalizing_points, true);

	// check to see that each of the points in the normalized list is
	// correct
	assertTrue(response.getNormalizedResponse().get(0).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(0).getY() == 0);
	assertThat(response.getNormalizedResponse().get(0).getPressure(), is(0.0));

	assertTrue(response.getNormalizedResponse().get(1).getX() == 3);
	assertTrue(response.getNormalizedResponse().get(1).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(1).getPressure()).round(context),
		is(new BigDecimal(.3).round(context)));

	assertTrue(response.getNormalizedResponse().get(2).getX() == 6);
	assertTrue(response.getNormalizedResponse().get(2).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(2).getPressure()).round(context),
		is(new BigDecimal(.6).round(context)));
    }

    /**
     * test normalize to ensure it is working properly at a basic capacity
     */
    @Test
    public void testNormalizeBasicY() {
	// normalize the response to a list and see if it does it correctly
	// create the list to normalize to
	List<Point> normalizing_points = new ArrayList<Point>();

	for (int i = 0; i < 3; i++) {
	    normalizing_points.add(new Point(0, i * 3, 0));
	}

	// run the method
	this.response.normalize(normalizing_points, false);

	// check to see that each of the points in the normalized list is
	// correct
	assertTrue(response.getNormalizedResponse().get(0).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(0).getY() == 0);
	assertThat(response.getNormalizedResponse().get(0).getPressure(), is(0.0));

	assertTrue(response.getNormalizedResponse().get(1).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(1).getY() == 3);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(1).getPressure()).round(context),
		is(new BigDecimal(.3).round(context)));

	assertTrue(response.getNormalizedResponse().get(2).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(2).getY() == 6);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(2).getPressure()).round(context),
		is(new BigDecimal(.6).round(context)));
    }

    /**
     * test normalize to ensure it is working properly for points inbetween
     * existing points
     */
    @Test
    public void testNormalizePointsInbetweenX() {
	// normalize the response to a list and see if it does it correctly
	// create the list to normalize to
	List<Point> normalizing_points = new ArrayList<Point>();

	for (int i = 0; i < 3; i++) {
	    normalizing_points.add(new Point((i * 3) + .5, 0, 0));
	}

	// run the method
	this.response.normalize(normalizing_points, true);

	// check to see that each of the points in the normalized list is
	// correct
	assertTrue(response.getNormalizedResponse().get(0).getX() == .5);
	assertTrue(response.getNormalizedResponse().get(0).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(0).getPressure()).round(context),
		is(new BigDecimal(.05).round(context)));

	assertTrue(response.getNormalizedResponse().get(1).getX() == 3.5);
	assertTrue(response.getNormalizedResponse().get(1).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(1).getPressure()).round(context),
		is(new BigDecimal(.35).round(context)));

	assertTrue(response.getNormalizedResponse().get(2).getX() == 6.5);
	assertTrue(response.getNormalizedResponse().get(2).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(2).getPressure()).round(context),
		is(new BigDecimal(.65).round(context)));
    }

    /**
     * test normalize to ensure it is working properly for points inbetween
     * existing points
     */
    @Test
    public void testNormalizePointsInbetweenY() {
	// normalize the response to a list and see if it does it correctly
	// create the list to normalize to
	List<Point> normalizing_points = new ArrayList<Point>();

	for (int i = 0; i < 3; i++) {
	    normalizing_points.add(new Point(0, (i * 3) + .5, 0));
	}

	// run the method
	this.response.normalize(normalizing_points, false);

	// check to see that each of the points in the normalized list is
	// correct
	assertTrue(response.getNormalizedResponse().get(0).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(0).getY() == .5);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(0).getPressure()).round(context),
		is(new BigDecimal(.05).round(context)));

	assertTrue(response.getNormalizedResponse().get(1).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(1).getY() == 3.5);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(1).getPressure()).round(context),
		is(new BigDecimal(.35).round(context)));

	assertTrue(response.getNormalizedResponse().get(2).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(2).getY() == 6.5);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(2).getPressure()).round(context),
		is(new BigDecimal(.65).round(context)));
    }

    /**
     * test normalize to ensure it is working properly at a basic capacity
     */
    @Test
    public void testNormalizeBasicXBackward() {
	// normalize the response to a list and see if it does it correctly
	// create the list to normalize to
	List<Point> normalizing_points = new ArrayList<Point>();

	for (int i = 2; i >= 0; i--) {
	    normalizing_points.add(new Point(i * 3, 0, 0));
	}

	// run the method
	this.response.normalize(normalizing_points, true);

	// check to see that each of the points in the normalized list is
	// correct
	assertTrue(response.getNormalizedResponse().get(2).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(2).getY() == 0);
	assertThat(response.getNormalizedResponse().get(2).getPressure(), is(0.0));

	assertTrue(response.getNormalizedResponse().get(1).getX() == 3);
	assertTrue(response.getNormalizedResponse().get(1).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(1).getPressure()).round(context),
		is(new BigDecimal(.3).round(context)));

	assertTrue(response.getNormalizedResponse().get(0).getX() == 6);
	assertTrue(response.getNormalizedResponse().get(0).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(0).getPressure()).round(context),
		is(new BigDecimal(.6).round(context)));
    }

    /**
     * test normalize to ensure it is working properly at a basic capacity
     */
    @Test
    public void testNormalizeBasicYBackward() {
	// normalize the response to a list and see if it does it correctly
	// create the list to normalize to
	List<Point> normalizing_points = new ArrayList<Point>();

	for (int i = 2; i >= 0; i--) {
	    normalizing_points.add(new Point(0, i * 3, 0));
	}

	// run the method
	this.response.normalize(normalizing_points, false);

	// check to see that each of the points in the normalized list is
	// correct
	assertTrue(response.getNormalizedResponse().get(2).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(2).getY() == 0);
	assertThat(response.getNormalizedResponse().get(2).getPressure(), is(0.0));

	assertTrue(response.getNormalizedResponse().get(1).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(1).getY() == 3);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(1).getPressure()).round(context),
		is(new BigDecimal(.3).round(context)));

	assertTrue(response.getNormalizedResponse().get(0).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(0).getY() == 6);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(0).getPressure()).round(context),
		is(new BigDecimal(.6).round(context)));
    }

    /**
     * test normalize to ensure it is working properly for points inbetween
     * existing points
     */
    @Test
    public void testNormalizePointsInbetweenXBackward() {
	// normalize the response to a list and see if it does it correctly
	// create the list to normalize to
	List<Point> normalizing_points = new ArrayList<Point>();

	for (int i = 2; i >= 0; i--) {
	    normalizing_points.add(new Point((i * 3) + .5, 0, 0));
	}

	// run the method
	this.response.normalize(normalizing_points, true);

	// check to see that each of the points in the normalized list is
	// correct
	assertTrue(response.getNormalizedResponse().get(2).getX() == .5);
	assertTrue(response.getNormalizedResponse().get(2).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(2).getPressure()).round(context),
		is(new BigDecimal(.05).round(context)));

	assertTrue(response.getNormalizedResponse().get(1).getX() == 3.5);
	assertTrue(response.getNormalizedResponse().get(1).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(1).getPressure()).round(context),
		is(new BigDecimal(.35).round(context)));

	assertTrue(response.getNormalizedResponse().get(0).getX() == 6.5);
	assertTrue(response.getNormalizedResponse().get(0).getY() == 0);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(0).getPressure()).round(context),
		is(new BigDecimal(.65).round(context)));
    }

    /**
     * test normalize to ensure it is working properly for points inbetween
     * existing points
     */
    @Test
    public void testNormalizePointsInbetweenYBackward() {
	// normalize the response to a list and see if it does it correctly
	// create the list to normalize to
	List<Point> normalizing_points = new ArrayList<Point>();

	for (int i = 2; i >= 0; i--) {
	    normalizing_points.add(new Point(0, (i * 3) + .5, 0));
	}

	// run the method
	this.response.normalize(normalizing_points, false);

	// check to see that each of the points in the normalized list is
	// correct
	assertTrue(response.getNormalizedResponse().get(2).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(2).getY() == .5);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(2).getPressure()).round(context),
		is(new BigDecimal(.05).round(context)));

	assertTrue(response.getNormalizedResponse().get(1).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(1).getY() == 3.5);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(1).getPressure()).round(context),
		is(new BigDecimal(.35).round(context)));

	assertTrue(response.getNormalizedResponse().get(0).getX() == 0);
	assertTrue(response.getNormalizedResponse().get(0).getY() == 6.5);
	assertThat(new BigDecimal(response.getNormalizedResponse().get(0).getPressure()).round(context),
		is(new BigDecimal(.65).round(context)));
    }
}
