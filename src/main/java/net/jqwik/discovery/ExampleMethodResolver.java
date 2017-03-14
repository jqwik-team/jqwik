package net.jqwik.discovery;

import java.lang.reflect.Method;

import net.jqwik.descriptor.ExampleMethodDescriptor;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import net.jqwik.discovery.predicates.IsExampleMethod;

class ExampleMethodResolver extends AbstractMethodResolver {

	ExampleMethodResolver() {
		super(new IsExampleMethod());
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
