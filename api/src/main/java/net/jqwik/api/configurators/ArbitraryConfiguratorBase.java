package net.jqwik.api.configurators;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

import org.apiguardian.api.*;
import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;
import static org.junit.platform.commons.support.ReflectionSupport.*;

/**
 * Using this base class is the easiest way to make use of the configuration mechanism
 * described in {@linkplain ArbitraryConfigurator}
 *
 * <p>
 * Implementations must be registered in <code>/META-INF/services/net.jqwik.api.configurators.ArbitraryConfigurator</code>
 * so that they will be automatically considered for arbitrary configuration.
 *
 * <p>
 * Some examples that come with jqwik:
 *
 * <ul>
 *     <li><a href="https://github.com/jqwik-team/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/properties/configurators/CharsConfigurator.java"
 *     >net.jqwik.engine.properties.configurators.CharsConfigurator</a></li>
 *     <li><a href="https://github.com/jqwik-team/jqwik/blob/master/engine/src/main/java/net/jqwik/engine/properties/configurators/SizeConfigurator.java"
 *     >net.jqwik.engine.properties.configurators.SizeConfigurator</a></li>
 * </ul>
 */
@API(status = MAINTAINED, since = "1.0")
public abstract class ArbitraryConfiguratorBase implements ArbitraryConfigurator {

	private final static String CONFIG_METHOD_NAME_PATTERN = "configure.*";

	@Override
	public <T> Arbitrary<T> configure(Arbitrary<T> arbitrary, TypeUsage targetType) {
		if (!acceptTargetType(targetType)) {
			return arbitrary;
		}
		List<Annotation> annotations = configurationAnnotations(targetType);
		for (Annotation annotation : annotations) {
			List<Method> configurationMethods = findConfigurationMethods(arbitrary, targetType, annotation);
			for (Method configurationMethod : configurationMethods) {
				arbitrary = configureWithMethod(arbitrary, annotation, configurationMethod);
			}
		}
		return arbitrary;
	}

	/**
	 * Override if configurator only works for certain types of domain objects
	 *
	 * @param targetType The concrete domain type to be generated
	 */
	protected boolean acceptTargetType(TypeUsage targetType) {
		return true;
	}

	private List<Annotation> configurationAnnotations(TypeUsage parameter) {
		return parameter.getAnnotations().stream()
						.filter(annotation -> !annotation.annotationType().equals(ForAll.class))
						.collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	private <T> Arbitrary<T> configureWithMethod(Arbitrary<T> arbitrary, Annotation annotation, Method configurationMethod) {
		Object configurationResult = invokeMethod(configurationMethod, this, arbitrary, annotation);
		if (configurationResult == null) {
			return arbitrary;
		}
		if (!(configurationResult instanceof Arbitrary)) {
			throw new ArbitraryConfigurationException(configurationMethod);
		}
		return (Arbitrary<T>) configurationResult;
	}

	@SuppressWarnings("unchecked")
	private <T> List<Method> findConfigurationMethods(Arbitrary<T> arbitrary, TypeUsage targetType, Annotation annotation) {
		Class<? extends Arbitrary<T>> arbitraryClass = (Class<? extends Arbitrary<T>>) arbitrary.getClass();
		return findMethods(
			getClass(),
			method -> hasCompatibleConfigurationSignature(method, arbitraryClass, targetType, annotation),
			HierarchyTraversalMode.BOTTOM_UP
		);
	}

	private static boolean hasCompatibleConfigurationSignature(
		Method candidate,
		Class<? extends Arbitrary<?>> arbitraryClass,
		TypeUsage targetType,
		Annotation annotation
	) {
		if (!ModifierSupport.isPublic(candidate)) {
			return false;
		}
		if (!candidate.getName().matches(CONFIG_METHOD_NAME_PATTERN)) {
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

		TypeUsage configurationArbitraryType = TypeUsage.forType(candidate.getGenericParameterTypes()[0]);
		TypeUsage providedArbitraryType = TypeUsage.of(arbitraryClass);
		if (!providedArbitraryType.canBeAssignedTo(configurationArbitraryType)) {
			return false;
		}

		// To prevent application of a configuration method to a target type that is generic, e.g. a filtered arbitrary
		TypeUsage targetArbitraryType = TypeUsage.of(
			Arbitrary.class,
			targetType
		);

		return targetArbitraryType.canBeAssignedTo(configurationArbitraryType);
	}

}
