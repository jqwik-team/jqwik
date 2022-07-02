package net.jqwik.engine.descriptor;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.logging.*;
import java.util.stream.*;

import org.junit.platform.engine.*;

import net.jqwik.api.*;
import net.jqwik.api.domains.*;

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
						   String message = String.format(
							   "Invalid tag format in @Tag(\"%s\") on [%s]. " +
								   "Tag will be ignored",
							   tag, element
						   );
						   LOG.warning(message);
						   return false;
					   }
					   return true;
				   })
				   .map(TestTag::create)
				   .collect(collectingAndThen(toCollection(LinkedHashSet::new), Collections::unmodifiableSet));
	}

	public static Set<Domain> findDomains(AnnotatedElement element) {
		return new LinkedHashSet<>(findRepeatableAnnotations(element, Domain.class));
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
		List<Annotation> junitAnnotations = findJUnitAnnotations(element);
		for (Annotation annotation : junitAnnotations) {
			String message = String.format(
				"[%s] has annotation [%s] from JUnit which cannot be processed by jqwik",
				element,
				annotation
			);
			LOG.warning(message);
		}
	}

	public static List<Annotation> findJUnitAnnotations(AnnotatedElement candidate) {
		return Arrays.stream(candidate.getDeclaredAnnotations())
					 .filter(annotation -> annotation.annotationType()
													 .getPackage()
													 .getName()
													 .startsWith("org.junit"))
					 .collect(Collectors.toList());
	}

	public static Set<TestTag> getTags(Optional<TestDescriptor> parent, Set<TestTag> tags) {
		Set<TestTag> allTags = new LinkedHashSet<>(tags);
		parent.ifPresent(parentDescriptor -> allTags.addAll(parentDescriptor.getTags()));
		return allTags;
	}

	public static Set<Domain> getDomains(Optional<JqwikDescriptor> parent, Set<Domain> domains) {
		Set<Domain> allContexts = new LinkedHashSet<>(domains);
		parent.ifPresent(parentDescriptor -> allContexts.addAll(parentDescriptor.getDomains()));
		return allContexts;
	}

}
