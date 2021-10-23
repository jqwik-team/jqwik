package net.jqwik.api.lifecycle;

import java.lang.reflect.*;

import org.apiguardian.api.*;
import org.junit.platform.commons.support.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Implement this hook to override behaviour that invokes a method through reflection.
 * Per default {@linkplain org.junit.platform.commons.support.ReflectionSupport#invokeMethod(Method, Object, Object...)}
 * is called.
 *
 * <p>
 * Caveat: Only one hook per property method is possible.
 * </p>
 */
@API(status = EXPERIMENTAL, since = "1.6.0")
@FunctionalInterface
public interface InvokePropertyMethodHook extends LifecycleHook {

	InvokePropertyMethodHook DEFAULT = ReflectionSupport::invokeMethod;

	/**
	 * Invoke a method through some reflective mechanism.
	 * You may want to override the default call if invocation requires something special.
	 *
	 * @param method A method to be called
	 * @param target An instance of the method's declaring class or null if it's a static method
	 * @param args The arguments to user for method call
	 * @return any object
	 */
	Object invoke(Method method, Object target, Object... args) throws Throwable;
}
