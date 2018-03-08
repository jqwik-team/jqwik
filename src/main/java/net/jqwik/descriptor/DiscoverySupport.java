package net.jqwik.descriptor;

import net.jqwik.*;
import net.jqwik.api.*;
import org.junit.platform.engine.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import static java.util.stream.Collectors.*;
import static org.junit.platform.commons.support.AnnotationSupport.*;

public class DiscoverySupport {
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

	public static String determineLabel(AnnotatedElement element, Supplier<String> defaultName) {
		return findAnnotation(element, Label.class)
			.map(Label::value)
			.filter(displayName -> !displayName.trim().isEmpty())
			.orElseGet(defaultName);
	}
}
