package net.jqwik.docs;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

class DataDrivenPropertyExamples {

	@Data
	Iterable<Tuple2<Integer, String>> fizzBuzzExamples() {
		return Table.of(
			Tuple.of(1, "1"),
			Tuple.of(3, "Fizz"),
			Tuple.of(5, "Buzz"),
			Tuple.of(15, "FizzBuzz")
		);
	}

	@Property
	@FromData("fizzBuzzExamples")
	void fizzBuzzWorks(@ForAll int index, @ForAll String result) {
		Assertions.assertThat(fizzBuzz(index)).isEqualTo(result);
	}

	private String fizzBuzz(int index) {
		return Integer.toString(index);
	}


	@Property(generation = GenerationMode.RANDOMIZED) @FromData("myStrings")
	void dataDrivenAndRandomizedIsNotPossible(@ForAll String aString) {}

	@Data
	Iterable<Tuple1<String>> myStrings() {
		return Table.of("a", "b");
	}

}
