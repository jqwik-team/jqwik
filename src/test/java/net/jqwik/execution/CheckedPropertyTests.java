package net.jqwik.execution;

import net.jqwik.api.Example;
import org.assertj.core.api.Assertions;

import java.lang.reflect.Parameter;
import java.util.List;

class CheckedPropertyTests {

	@Example
	void singleParameter() {
		CheckedFunction forAllFunction = null;
		List<Parameter> forAllParameters = null;
		new CheckedProperty("prop1", forAllFunction, forAllParameters);
		Assertions.fail("TODO");
	}
}
