package examples.docs;

import java.util.List;
import java.util.stream.*;

import net.jqwik.api.*;

class FizzBuzzTests {
	@Property
	boolean every_third_element_starts_with_Fizz(@ForAll("divisibleBy3") int i) {
		return fizzBuzz().get(i - 1).startsWith("Fizz");
	}

	@Provide
	Arbitrary<Integer> divisibleBy3() {
		return Arbitraries.integers().between(1, 100).filter(i -> i % 3 == 0);
	}

	private List<String> fizzBuzz() {
		return IntStream.range(1, 100).mapToObj((int i) -> {
			boolean divBy3 = i % 3 == 0;
			boolean divBy5 = i % 5 == 0;

			return divBy3 && divBy5 ? "FizzBuzz" : divBy3 ? "Fizz" : divBy5 ? "Buzz" : String.valueOf(i);
		}).collect(Collectors.toList());
	}
}
