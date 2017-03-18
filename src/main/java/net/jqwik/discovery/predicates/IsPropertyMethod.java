package net.jqwik.discovery.predicates;

import net.jqwik.api.properties.Property;

public class IsPropertyMethod extends IsTestableMethod {

	public IsPropertyMethod() {
		super(Property.class);
	}

}
