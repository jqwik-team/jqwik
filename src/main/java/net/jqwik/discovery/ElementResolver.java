package net.jqwik.discovery;

import java.lang.reflect.*;
import java.util.*;

import org.junit.platform.engine.*;

interface ElementResolver {

	Set<TestDescriptor> resolveElement(AnnotatedElement element, TestDescriptor parent);

	Optional<TestDescriptor> resolveUniqueId(UniqueId.Segment segment, TestDescriptor parent);

}
