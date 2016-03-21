
package net.jqwik;

import java.lang.reflect.Method;
import java.util.Optional;

import org.junit.gen5.commons.util.ExceptionUtils;
import org.junit.gen5.commons.util.ReflectionUtils;

import static org.junit.gen5.commons.util.ReflectionUtils.*;

public class Utils {

	public static <T> T clone(T objectToClone) {
		try {
			final Optional<Method> cloneMethod = getCloneMethod(objectToClone);
			if (cloneMethod.isPresent())
				return (T) invokeMethod(cloneMethod.get(), objectToClone);
			else
				return objectToClone;
		}
		catch (Throwable t) {
			ExceptionUtils.throwAsUncheckedException(t);
		}
		return null; //cannot happen
	}

	private static Optional<Method> getCloneMethod(Object anObject) throws NoSuchMethodException {
		return ReflectionUtils.findMethod(anObject.getClass(), "clone");
	}

}
