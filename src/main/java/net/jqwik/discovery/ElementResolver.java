package net.jqwik.discovery;

import org.junit.platform.engine.*;

import java.lang.reflect.*;
import java.util.*;

interface ElementResolver {

	Set<TestDescriptor> resolveElement(AnnotatedElement element, TestDescriptor parent);

	Optional<TestDescriptor> resolveUniqueId(UniqueId.Segment segment, TestDescriptor parent);

}
