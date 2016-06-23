package jqwik.experiments;

import java.util.Arrays;
import java.util.stream.Stream;

public class SimpleSpec {

	@Fact("A String is a String")
	boolean aStringIsAString() {
		return "aString" instanceof String;
	}

	@Fact
	boolean aNameIsAString(Object aName) {
		return aName instanceof String;
	}

	@Data
	Object aName() {
		return "Johannes";
	}

	@Data(referBy = "aName")
	Object otherName() {
		return "Marcus";
	}

	@DataSample(referBy = "aName")
	Stream<Object>  manyNames() {
		return Arrays.stream(new String[] {"Frank", "John"});
	}

}
