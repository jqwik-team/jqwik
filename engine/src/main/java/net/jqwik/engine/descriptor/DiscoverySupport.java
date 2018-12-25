package net.jqwik.engine.descriptor;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;

import static java.util.stream.Collectors.*;
import static org.junit.platform.commons.support.AnnotationSupport.*;

public class DiscoverySupport {

	private static final Logger LOG = Logger.getLogger(DiscoverySupport.class.getName());

	public static Set<TestTag> findTestTags(AnnotatedElement element) {
		return findRepeatableAnnotations(element, Tag.class)
			.stream()
			.map(Tag::value)
			.filter(tag -> {
				if (!TestTag.isValid(tag)) {
					String message = String.format("Invalid tag format in @Tag(\"%s\") on [%s].", tag, element);
					throw new JqwikException(message);
				}
				return true;
			})
			.map(TestTag::create)
			.collect(collectingAndThen(toCollection(LinkedHashSet::new), Collections::unmodifiableSet));
	}

	public static String determineLabel(AnnotatedElement element, Supplier<String> defaultNameSupplier) {
		return findAnnotation(element, Label.class)
				   .map(Label::value)
				   .filter(displayName -> !displayName.trim().isEmpty())
				   .orElseGet(readableNameSupplier(defaultNameSupplier));
	}

	private static Supplier<String> readableNameSupplier(Supplier<String> nameSupplier) {
		return () -> nameSupplier
						 .get()
						 .replaceAll("_", " ");
	}

	public static void warnWhenJunitAnnotationsArePresent(AnnotatedElement element) {
		Annotation[] directAnnotations = element.getDeclaredAnnotations();
		for (Annotation annotation : directAnnotations) {
			if (annotation.annotationType().getPackage().getName().startsWith("org.junit")) {
				String message = String.format("[%s] has annotation [%s] from JUnit which cannot be processed by jqwik", element, annotation);
				LOG.warning(message);
			}
		}
	}
}
