package net.jqwik.engine.properties;

import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.support.JqwikReflectionSupport.*;

public class DomainContextBaseReportingFormats {

	private static final Logger LOG = Logger.getLogger(DomainContextBaseReportingFormats.class.getName());

	static public Collection<SampleReportingFormat> forContextBase(DomainContextBase base) {
		return Stream.concat(
			formatsFromInnerClasses(base),
			formatsFromBaseItself(base)
		).collect(Collectors.toList());
	}

	private static Stream<SampleReportingFormat> formatsFromInnerClasses(DomainContextBase base) {
		Predicate<Class<?>> implementsReportingFormat =
			clazz -> SampleReportingFormat.class.isAssignableFrom(clazz) && !JqwikReflectionSupport.isPrivate(clazz);
		List<Class<?>> reportingFormatClasses = ReflectionSupport.findNestedClasses(base.getClass(), implementsReportingFormat);
		warnIfClassesHaveNoFittingConstructor(reportingFormatClasses);
		return reportingFormatClasses.stream()
									 .filter(DomainContextBaseReportingFormats::hasFittingConstructor)
									 .map(clazz -> createArbitraryConfigurator(clazz, base));
	}

	private static Stream<SampleReportingFormat> formatsFromBaseItself(DomainContextBase base) {
		if (base instanceof SampleReportingFormat) {
			return Stream.of((SampleReportingFormat) base);
		}
		return Stream.empty();
	}

	private static void warnIfClassesHaveNoFittingConstructor(List<Class<?>> classes) {
		classes.stream()
			   .filter(aClass -> !hasFittingConstructor(aClass))
			   .forEach(aClass -> {
				   String message = String.format(
					   "Class <%s> does not have a default constructor and cannot be instantiated as %s.",
					   aClass.getName(),
					   SampleReportingFormat.class
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

	private static SampleReportingFormat createArbitraryConfigurator(Class<?> clazz, DomainContextBase base) {
		return (SampleReportingFormat) JqwikReflectionSupport.newInstanceInTestContext(clazz, base);
	}

}
