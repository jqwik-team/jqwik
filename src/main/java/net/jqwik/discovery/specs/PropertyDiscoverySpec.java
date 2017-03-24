package net.jqwik.discovery.specs;

import java.lang.reflect.*;

import net.jqwik.api.properties.*;

public class PropertyDiscoverySpec extends TestableMethodDiscoverySpec {
	public PropertyDiscoverySpec() {
		super(Property.class);
	}

	@Override
	public String skippingReason(Method candidate) {
		return "A @Property method must not be static";
	}
}
