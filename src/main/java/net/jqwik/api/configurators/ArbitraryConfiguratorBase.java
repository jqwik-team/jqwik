package net.jqwik.api.configurators;

import net.jqwik.api.*;
import org.junit.platform.commons.support.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

import static org.junit.platform.commons.support.ReflectionSupport.*;

/**
 * Using this base class is the easiest way to make use of the configuration mechanism
 * described in {@linkplain ArbitraryConfigurator}
 *
 * <p>
 * Implementations must be registered in <code>/META-INF/services/net.jqwik.api.configurators.ArbitraryConfigurator</code>
 * so that they will be automatically considered for arbitrary configuration.
 * <p>
 * <p>
 * Some examples that come with jqwik:
 *
 * @see net.jqwik.configurators.CharsConfigurator
 * @see net.jqwik.configurators.SizeConfigurator
 */
public abstract class ArbitraryConfiguratorBase implements ArbitraryConfigurator {

	private final static String CONFIG_METHOD_NAME = "configure";

	@Override
	public <T> Arbitrary<T> configure(Arbitrary<T> arbitrary, List<Annotation> annotations) {
		if (arbitrary instanceof SelfConfiguringArbitrary) {
			return performSelfConfiguration(arbitrary, annotations);
		}
		for (Annotation annotation : annotations) {
			List<Method> configurationMethods = findConfigurationMethods(arbitrary, annotation);
			for (Method configurationMethod : configurationMethods) {
				arbitrary = configureWithMethod(arbitrary, annotation, configurationMethod);
			}
		}
		return arbitrary;
	}

	private <T> Arbitrary<T> configureWithMethod(Arbitrary<T> arbitrary, Annotation annotation, Method configurationMethod) {
		Object configurationResult = invokeMethod(configurationMethod, this, arbitrary, annotation);
		if (configurationResult == null) {
			return arbitrary;
		}
		if (!(configurationResult instanceof Arbitrary)) {
			throw new ArbitraryConfigurationException(configurationMethod);
		}
		//noinspection unchecked
		arbitrary = (Arbitrary<T>) configurationResult;
		return arbitrary;
	}

	private <T> List<Method> findConfigurationMethods(Arbitrary<T> arbitrary, Annotation annotation) {
		Class<? extends Arbitrary> arbitraryClass = arbitrary.getClass();
		return findMethods(getClass(),
				method -> hasCompatibleConfigurationSignature(method, arbitraryClass, annotation), HierarchyTraversalMode.BOTTOM_UP);
	}

	private <T> Arbitrary<T> performSelfConfiguration(Arbitrary<T> arbitrary, List<Annotation> annotations) {
		@SuppressWarnings("unchecked")
		SelfConfiguringArbitrary<T> selfConfiguringArbitrary = (SelfConfiguringArbitrary<T>) arbitrary;
		return selfConfiguringArbitrary.configure(this, annotations);
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
