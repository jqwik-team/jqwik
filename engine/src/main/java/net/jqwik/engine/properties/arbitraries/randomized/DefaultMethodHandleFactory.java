package net.jqwik.engine.properties.arbitraries.randomized;

import java.lang.invoke.*;
import java.lang.invoke.MethodHandles.*;
import java.lang.reflect.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

/**
 * This handles a difference between JDKs upto version 8 and after.
 * Remove as soon as default JDK is 9 or above
 */
class DefaultMethodHandleFactory {

	MethodHandle create(Method method) throws Throwable {
		if (isJava9orAbove()) {
			return java9orAboveMethodHandle(method);
		} else {
			return java8MethodHandle(method);
		}
	}

	private MethodHandle java9orAboveMethodHandle(Method method) throws Throwable {
		return MethodHandles.lookup()
							.findSpecial(
								method.getDeclaringClass(),
								method.getName(),
								MethodType.methodType(
									method.getReturnType(),
									method.getParameterTypes()
								),
								method.getDeclaringClass()
							);
	}

	private MethodHandle java8MethodHandle(Method method) throws Throwable {
		Constructor<Lookup> java8LookupConstructor = findConstructor(Lookup.class, Class.class, int.class);
		Lookup lookup =
			java8LookupConstructor.newInstance(
				method.getDeclaringClass(),
				Lookup.PRIVATE
			);
		return lookup.unreflectSpecial(method, method.getDeclaringClass());
	}

}