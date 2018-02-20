package net.jqwik.api.configurators;

import static org.junit.platform.commons.support.ReflectionSupport.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;

public abstract class ArbitraryConfiguratorBase implements ArbitraryConfigurator {

	private final static String CONFIG_METHOD_NAME = "configure";

	@Override
	public Arbitrary<?> configure(Arbitrary<?> arbitrary, List<Annotation> annotations) {
		for (Annotation annotation : annotations) {
			Class<? extends Arbitrary> arbitraryClass = arbitrary.getClass();
			List<Method> configurationMethods = findMethods(getClass(),
					method -> hasCompatibleConfigurationSignature(method, arbitraryClass, annotation), HierarchyTraversalMode.BOTTOM_UP);
			for (Method configurationMethod : configurationMethods) {
				arbitrary = (Arbitrary<?>) invokeMethod(configurationMethod, this, arbitrary, annotation);
			}
		}
		return arbitrary;
	}

	private static boolean hasCompatibleConfigurationSignature(Method candidate, Class<? extends Arbitrary> arbitraryClass,
			Annotation annotation) {
		if (!CONFIG_METHOD_NAME.equals(candidate.getName())) {
			return false;
		}
		if (!Arbitrary.class.isAssignableFrom(candidate.getReturnType())) {
			return false;
		}
		if (candidate.getParameterCount() != 2) {
			return false;
		}
		if (candidate.getParameterTypes()[1] != annotation.annotationType()) {
			return false;
		}
		Class<?> upperArbitraryType = candidate.getParameterTypes()[0];
		return upperArbitraryType.isAssignableFrom(arbitraryClass);
	}

}
