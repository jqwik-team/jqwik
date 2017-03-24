package net.jqwik.discovery;

import java.lang.reflect.*;

import org.junit.platform.engine.*;

import net.jqwik.descriptor.*;
import net.jqwik.discovery.specs.*;

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
