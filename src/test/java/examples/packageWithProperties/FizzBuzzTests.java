package examples.packageWithProperties;

import javaslang.collection.*;
import net.jqwik.api.*;
import net.jqwik.properties.*;

public class FizzBuzzTests {

	@Property
	boolean every_third_element_starts_with_Fizz(@ForAll("divisibleBy3") int i) {
		return fizzBuzz().get(i - 1).startsWith("Fizz");
	}

	@Generate
	Arbitrary<Integer> divisibleBy3() {
		return Arbitraries.integer(1, 1000).filter(i -> i % 3 == 0);
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