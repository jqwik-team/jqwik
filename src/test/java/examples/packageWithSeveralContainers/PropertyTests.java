package examples.packageWithSeveralContainers;

import javaslang.test.*;
import net.jqwik.api.properties.*;
import net.jqwik.api.properties.Property;

public class PropertyTests {

	@Property
	boolean isTrue() {
		return true;
	}

	@Property
	Boolean isAlsoTrue() {
		return Boolean.TRUE;
	}

	@Property
	boolean isFalse() {
		return false;
	}

	@Property
	boolean allNumbersAreZero(@ForAll int aNumber) {
		return aNumber == 0;
	}

	@Property
	String incompatibleReturnType() {
		return "aString";
	}

	@Property(tries = 100, seed = 42L)
	@Assume("numberIsLength")
	boolean withEverything(@ForAll("lessThan5") int aNumber, @ForAll("shorterThan5") String aString) {
		return aString.length() == aNumber;
	}

	@Generate
	Arbitrary<Integer> lessThan5() {
		return Generator.integer(0, 4);
	}

	@Generate
	Arbitrary<String> shorterThan5() {
		return Generator.string(new char[]{'a', 'b', 'c'}, 4);
	}

	@Assumption
	boolean numberIsLength(int aNumber, String aString) {
		return aNumber == aString.length();
	}
}
