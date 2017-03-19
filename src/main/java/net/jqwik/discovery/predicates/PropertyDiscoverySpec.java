package net.jqwik.discovery.predicates;

import net.jqwik.api.properties.Property;

import java.lang.reflect.Method;

public class PropertyDiscoverySpec extends TestableMethodDiscoverySpec {
	public PropertyDiscoverySpec() {
		super(Property.class);
	}

	@Override
	public String skippingReason(Method candidate) {
		return "A @Property method must not be static";
	}
}
