package net.jqwik.discovery;

import net.jqwik.descriptor.PropertyMethodDescriptor;
import net.jqwik.discovery.predicates.PropertyDiscoverySpec;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;

import java.lang.reflect.Method;

class PropertyMethodResolver extends AbstractMethodResolver {

	PropertyMethodResolver() {
		super(new PropertyDiscoverySpec());
	}

	@Override
	protected TestDescriptor createTestDescriptor(UniqueId uniqueId, Class<?> testClass, Method method) {
		return new PropertyMethodDescriptor(uniqueId, method, testClass);
	}

	@Override
	protected String getSegmentType() {
		return JqwikUniqueIDs.PROPERTY_SEGMENT_TYPE;
	}

	@Override
	protected UniqueId createUniqueId(Method method, TestDescriptor parent) {
		return JqwikUniqueIDs.appendProperty(parent.getUniqueId(), method);
	}


}
