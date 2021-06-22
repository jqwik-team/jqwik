package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.configurators.*;
import net.jqwik.api.domains.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class DomainContextBaseConfigurators {

	private static final Logger LOG = Logger.getLogger(DomainContextBaseConfigurators.class.getName());

	static public List<ArbitraryConfigurator> forContextBase(DomainContextBase base) {
		return configuratorsFromInnerClasses(base);
	}

	private static List<ArbitraryConfigurator> configuratorsFromInnerClasses(DomainContextBase base) {
		Predicate<Class<?>> implementsArbitraryConfigurator =
			clazz -> ArbitraryConfigurator.class.isAssignableFrom(clazz) && !JqwikReflectionSupport.isPrivate(clazz);
		List<Class<?>> arbitraryProviderClasses = ReflectionSupport.findNestedClasses(base.getClass(), implementsArbitraryConfigurator);
		warnIfClassesHaveNoFittingConstructor(arbitraryProviderClasses);
		return arbitraryProviderClasses.stream()
									   .filter(DomainContextBaseConfigurators::hasFittingConstructor)
									   .map(clazz -> createArbitraryConfigurator(clazz, base))
									   .collect(Collectors.toList());
	}

	private static void warnIfClassesHaveNoFittingConstructor(List<Class<?>> classes) {
		classes.stream()
			   .filter(aClass -> !hasFittingConstructor(aClass))
			   .forEach(aClass -> {
				   String message = String.format(
					   "Class <%s> does not have a default constructor and cannot be instantiated as arbitrary configurator.",
					   aClass.getName()
				   );
				   LOG.warning(message);
			   });

	}

	private static boolean hasFittingConstructor(Class<?> clazz) {
		if (JqwikReflectionSupport.isStatic(clazz)) {
			return hasDefaultConstructor(clazz);
		}
		return hasConstructor(clazz, clazz.getDeclaringClass());
	}

	private static ArbitraryConfigurator createArbitraryConfigurator(Class<?> clazz, DomainContextBase base) {
		return (ArbitraryConfigurator) JqwikReflectionSupport.newInstanceInTestContext(clazz, base);
	}

}
