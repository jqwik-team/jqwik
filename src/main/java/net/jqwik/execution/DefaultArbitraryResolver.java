package net.jqwik.execution;

import static org.junit.platform.commons.support.ReflectionSupport.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.support.*;

class DefaultArbitraryResolver {

	private final static String CONFIG_METHOD_NAME = "configure";

	private final List<ArbitraryProvider> defaultProviders;

	DefaultArbitraryResolver(List<ArbitraryProvider> defaultProviders) {
		this.defaultProviders = defaultProviders;
	}

	Optional<Arbitrary<?>> resolve( //
			GenericType targetType, //
			List<Annotation> annotations, //
			Function<GenericType, Optional<Arbitrary<?>>> subtypeProvider //
	) {
		for (ArbitraryProvider provider : defaultProviders) {
			if (provider.canProvideFor(targetType)) {
				Arbitrary<?> arbitrary = provider.provideFor(targetType, subtypeProvider);
				if (arbitrary == null) {
					continue;
				}
				arbitrary = configureArbitraryInProvider(arbitrary, provider, annotations);
				return Optional.of(arbitrary);
			}
		}

		return Optional.empty();
	}

	private Arbitrary<?> configureArbitraryInProvider(Arbitrary<?> arbitrary, ArbitraryProvider provider, List<Annotation> annotations) {
		for (Annotation annotation : annotations) {
			Class<? extends Arbitrary> arbitraryClass = arbitrary.getClass();
			Optional<Method> configurationMethod = JqwikReflectionSupport.findMethod(provider.getClass(),
					method -> hasCompatibleConfigurationSignature(method, arbitraryClass, annotation), HierarchyTraversalMode.BOTTOM_UP);
			if (configurationMethod.isPresent()) {
				arbitrary = (Arbitrary<?>) invokeMethod(configurationMethod.get(), provider, arbitrary, annotation);
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
