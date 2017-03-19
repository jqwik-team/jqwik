package net.jqwik.discovery;

import net.jqwik.descriptor.ExampleMethodDescriptor;
import net.jqwik.discovery.specs.ExampleDiscoverySpec;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import java.lang.reflect.Method;

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
