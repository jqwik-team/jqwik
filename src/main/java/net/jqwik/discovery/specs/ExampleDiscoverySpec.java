package net.jqwik.discovery.specs;

import java.lang.reflect.*;

import net.jqwik.api.*;

public class ExampleDiscoverySpec extends TestableMethodDiscoverySpec {
	public ExampleDiscoverySpec() {
		super(Example.class);
	}

	@Override
	public String skippingReason(Method candidate) {
		return "A @Example method must not be static";
	}

}
