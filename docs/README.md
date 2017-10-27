The main purpose of __jqwik__ is to bring [Property-Based Testing](/property-based-testing.html) (PBT) 
to the JVM. _Property-Based Testing_ tries to combine the intuitiveness of 
[Microtests](https://www.industriallogic.com/blog/history-microtests/) with the
effectiveness of randomized, generated test data.
Originally driven by the common hype about functional programming, 
PBT has meanwhile been recognized as an important ingredient of any up-to-date testing approach.

### Properties

A property is supposed to describe a _generic invariant or post condition_ of your code, given some
_precondition_. The following property deals with a partial implementation for the (in)famous 
[Fizz Buzz Kata](http://codingdojo.org/kata/FizzBuzz/):

- Precondition: Consider numbers between 1 and 100 that are divisible by 3
- Postcondition: The string returned by `fizzBuzz()` contains the word `Fizz` 

```java
import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.properties.*;

class FizzBuzzTests {
	@Property
	boolean every_third_element_starts_with_Fizz(@ForAll("divisibleBy3") int i) {
		return fizzBuzz().get(i - 1).startsWith("Fizz");
	}

	@Generate
	Arbitrary<Integer> divisibleBy3() {
		return Generator.integer(1, 100).filter(i -> i % 3 == 0);
	}

	private List<String> fizzBuzz() {
		return IntStream.range(1, 100).mapToObj((int i) -> {
			boolean divBy3 = i % 3 == 0;
			boolean divBy5 = i % 5 == 0;

			return divBy3 && divBy5 ? "FizzBuzz"
				: divBy3 ? "Fizz"
				: divBy5 ? "Buzz"
				: String.valueOf(i);
		}).collect(Collectors.toList());
	}
}
```

By using a few Java annotations __jqwik__ tries to make it as simple as possible 
for programmers to create Properties. Classes are used as containers to group properties
that belong together.


### Examples

Unlike [Properties](#properties) an example-based test checks the correct behaviour
for a single usage scenario. Thus, examples are just a fancy name for the usual unit tests 
that directly specify the data being used to drive and assert a piece of code. 

```java
import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.*;
import org.assertj.core.data.*;

class SimpleExampleTests implements AutoCloseable {
	@Example
	void succeeding() { 
		assertThat(Math.sqrt(15)).isCloseTo(3.872, Offset.offset(0.01));
	}

	@Example
	void failing() {
		fail("failing");
	}

	// Executed after each test case
	public void close() { }

	@Group
	class AGroupOfCoherentTests {
		@Example
		void anotherSuccess() { }
	}
}
```
This looks like standard JUnit tests and works basically the same but without
the complicated lifecycle of Before's and After's.

