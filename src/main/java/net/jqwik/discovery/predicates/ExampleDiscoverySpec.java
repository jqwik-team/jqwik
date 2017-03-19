package net.jqwik.discovery.predicates;

import net.jqwik.api.Example;

import java.lang.reflect.Method;

public class ExampleDiscoverySpec extends TestableMethodDiscoverySpec {
	public ExampleDiscoverySpec() {
		super(Example.class);
	}

	@Override
	public String skippingReason(Method candidate) {
		return "A @Example method must not be static";
	}

}
