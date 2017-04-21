package examples.packageWithProperties;

import net.jqwik.api.*;
import net.jqwik.properties.*;

public class PropertiesWithAssumptions {

	@Property(tries = 10)
	boolean sixMustBeDivisor(@ForAll("multipleOf3") int i, @ForAll("multipleOf2") Integer j) {
		Assume.that((i + j) % 2 == 0);
		return (i * j) % 6 == 0;
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
