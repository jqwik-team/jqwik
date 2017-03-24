package net.jqwik.discovery;

import java.lang.reflect.*;

import org.junit.platform.engine.*;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.specs.*;

class ExampleMethodResolver extends AbstractMethodResolver {

	ExampleMethodResolver() {
		super(new ExampleDiscoverySpec());
	}

	@Override
	protected TestDescriptor createTestDescriptor(UniqueId uniqueId, Class<?> testClass, Method method) {
		return new ExampleMethodDescriptor(uniqueId, method, testClass);
	}

	@Override
	protected String getSegmentType() {
		return JqwikUniqueIDs.EXAMPLE_SEGMENT_TYPE;
	}

	@Override
	protected UniqueId createUniqueId(Method method, TestDescriptor parent) {
		return JqwikUniqueIDs.appendExample(parent.getUniqueId(), method);
	}


}
