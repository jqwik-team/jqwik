package net.jqwik.discovery;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.Set;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

interface ElementResolver {

	Set<TestDescriptor> resolveElement(AnnotatedElement element, TestDescriptor parent);

	Optional<TestDescriptor> resolveUniqueId(UniqueId.Segment segment, TestDescriptor parent);

}
