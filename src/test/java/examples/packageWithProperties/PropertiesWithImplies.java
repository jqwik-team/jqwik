package examples.packageWithProperties;

import javaslang.*;
import javaslang.test.*;
import javaslang.test.Property;
import net.jqwik.api.*;
import net.jqwik.api.properties.*;

public class PropertiesWithImplies {

	@net.jqwik.api.properties.Property
	@Assume("sumDivisibleBy2")
	boolean mustStartWithFizz(@ForAll int i, @ForAll int j) {
		return (i * j) % 6 == 0;
	}

	@net.jqwik.api.properties.Property
	boolean someProp(@ForAll("poops") int i, @ForAll int j) {
		return (i * j) % 6 == 0;
	}

	@Example
	void javaslangHasImplies() {
		Arbitrary<Integer> multiplesOf3 = Arbitrary.integer()
												   .filter(i -> i > 0)
												   .filter(i -> i % 3 == 0);
		Arbitrary<Integer> multiplesOf2 = Arbitrary.integer()
												   .filter(i -> i > 0)
												   .filter(i -> i % 2 == 0);

		CheckedFunction2<Integer, Integer, Boolean> precondition = (i, j) -> {
			return (i + j) % 2 == 0;
		};

		CheckedFunction2<Integer, Integer, Boolean> mustStartWithFizz = (i, j) -> {
			System.out.println(String.format("%s:%s", i, j));
			return (i * j) % 6 == 0;
		};

		// I'd prefer assume() instead of implies() which would trigger a new set of generated values
		CheckResult result = javaslang.test.Property
			.def("multiplicator divisibility")
			.forAll(multiplesOf3, multiplesOf2)
			.suchThat(precondition)
			.implies(mustStartWithFizz)
			.check(100, 10);

		result.assertIsSatisfied();

	}
}
