package examples.packageWithProperties;

import javaslang.test.*;
import net.jqwik.api.properties.*;
import net.jqwik.api.properties.Property;

public class PropertiesWithImplies {

	@Property(tries = 10)
	@Assume("sumDivisibleBy2")
	boolean sixMustBeDivisor(@ForAll("multipleOf3") int i, @ForAll("multipleOf2") Integer j) {
		return (i * j) % 6 == 0;
	}

	@Assumption
	boolean sumDivisibleBy2(int i, int j) {
		boolean condition = (i + j) % 2 == 0;
		System.out.println(i + ":" + j + " = " + condition);
		return condition;
	}

	@Generate
	Arbitrary<Integer> multipleOf3() {
		return Generator.integer(1, 1000).filter(i -> i % 3 == 0);
	}

	@Generate
	Arbitrary<Integer> multipleOf2() {
		return Generator.integer(1, 1000).filter(i -> i % 2 == 0);
	}

}
