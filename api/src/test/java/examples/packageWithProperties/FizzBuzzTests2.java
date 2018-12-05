package examples.packageWithProperties;

import java.util.stream.*;

import net.jqwik.api.*;

public class FizzBuzzTests2 {

	@Property
	boolean every_third_element_starts_with_Fizz(@ForAll("divisibleBy3") int i) {
		String nthCount = nth(fizzBuzz(), i - 1);
		return nthCount.startsWith("Fizz");
	}

	@Provide
	Arbitrary<Integer> divisibleBy3() {
		return Arbitraries.integers().between(1, 1000).filter(i -> i % 3 == 0);
	}

	@Property
	boolean every_fifth_element_ends_with_Buzz(@ForAll("divisibleBy5") int i) {
		String nthCount = nth(fizzBuzz(), i - 1);
		return nthCount.endsWith("Buzz");
	}

	@Provide
	Arbitrary<Integer> divisibleBy5() {
		return Arbitraries.integers().between(1, 1000).filter(i -> i % 5 == 0);
	}

	@Property
	boolean every_other_element_returns_number(@ForAll("notDivisibleBy3or5") int i) {
		String nthCount = nth(fizzBuzz(), i - 1);
		return nthCount.equals(Integer.toString(i));
	}

	@Provide
	Arbitrary<Integer> notDivisibleBy3or5() {
		return Arbitraries.integers().between(1, 1000) //
				.filter(i -> i % 5 != 0) //
				.filter(i -> i % 3 != 0);
	}

	private Stream<String> fizzBuzz() {
		return IntStream.iterate(1, i -> i + 1).mapToObj(i -> {
			boolean divBy3 = i % 3 == 0;
			boolean divBy5 = i % 5 == 0;

			return divBy3 && divBy5 ? "FizzBuzz" : divBy3 ? "Fizz" : divBy5 ? "Buzz" : Integer.toString(i);
		});
	}

	private <T> T nth(Stream<T> stream, int n) {
		return stream.limit(n + 1).reduce((first, second) -> second).get();
	}

}