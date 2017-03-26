package examples.packageWithProperties;

import javaslang.*;
import javaslang.collection.*;
import javaslang.test.*;
import net.jqwik.api.properties.*;
import org.junit.jupiter.api.*;

public class FizzBuzzTests {

	@Test
	void every_third_element_starts_with_Fizz() {
		Arbitrary<Integer> multiplesOf3 = Arbitrary.integer()
			.filter(i -> i > 0)
			.filter(i -> i % 3 == 0);

		CheckedFunction1<Integer, Boolean> mustStartWithFizz = i ->
			fizzBuzz().get(i - 1).startsWith("Fizz");

		CheckResult result = javaslang.test.Property
			.def("Every third element must start with Fizz")
			.forAll(multiplesOf3)
			.suchThat(mustStartWithFizz)
			.check();

		result.assertIsSatisfied();
	}

	@net.jqwik.api.properties.Property
	boolean every_third_element_starts_with_Fizz(@ForAll("divisibleBy3") int i) {
		return fizzBuzz().get(i - 1).startsWith("Fizz");
	}

	@Generate
	Arbitrary<Integer> divisibleBy3() {
		return Generator.integer(1, 1000).filter(i -> i % 3 == 0);
	}

	private Stream<String> fizzBuzz() {
		return Stream.from(1).map(i -> {
			boolean divBy3 = i % 3 == 0;
			boolean divBy5 = i % 5 == 0;

			return divBy3 && divBy5 ? "FizzBuzz" :
				divBy3 ? "Fizz" :
					divBy5 ? "Buzz" : i.toString();
		});
	}
}