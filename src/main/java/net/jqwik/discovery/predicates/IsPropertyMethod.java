package net.jqwik.discovery.predicates;

import net.jqwik.api.Property;

public class IsPropertyMethod extends IsTestableMethod {

	public IsPropertyMethod() {
		super(Property.class);
	}

}
