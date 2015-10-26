package jUnitTests.dataTypes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestUtil {
    /**
     * returns the method result.
     * 
     * @param o
     */
    public static Object runPrivateMethod(Object o, String methodName, Class[] argClasses, Object[] methodParameters) {
	Method method = null;
	Object object = null;

	try {
	    method = o.getClass().getDeclaredMethod(methodName, argClasses);

	    // set method to accessible
	    method.setAccessible(true);

	    // invoke the method
	    object = method.invoke(o, methodParameters);
	} catch (NoSuchMethodException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SecurityException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalAccessException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IllegalArgumentException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InvocationTargetException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return object;
    }
}
