
<p style="font-size: larger;margin-left: 1cm;">
    <em>jqwik</em> is pronounced like "jay quick" <code style="font-size: large">[ˈdʒeɪkwɪk]</code>.
</p>


The main purpose of __jqwik__ is to bring [Property-Based Testing](/property-based-testing.html) (PBT) 
to the JVM. 
The library's focus is mainly on _Java_ and _Kotlin_; Groovy works as well.

_Property-Based Testing_ tries to combine the intuitiveness of 
[Microtests](https://www.industriallogic.com/blog/history-microtests/) with the
effectiveness of randomized, generated test data.
Originally driven by the common hype about functional programming, 
PBT has meanwhile been recognized as an important ingredient of any up-to-date testing approach.

### Properties

A property is supposed to describe a _generic invariant or post condition_ of your code, given some
_precondition_. The testing library - _jqwik_ - will then try to 
**generate many value sets that fulfill the precondition** hoping that one of the generated sets
can falsify a wrong assumption.

The following property deals with a partial implementation for the (in)famous 
[Fizz Buzz Kata](http://codingdojo.org/kata/FizzBuzz/):

- _Precondition_: Consider numbers between 1 and 100 that are divisible by 3
- _Postcondition_: The string returned by `fizzBuzz()` starts with `Fizz` 

```java
import java.util.*;
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

			return divBy3 && divBy5 ? "FizzBuzz"
				: divBy3 ? "Fizz"
				: divBy5 ? "Buzz"
				: String.valueOf(i);
		}).collect(Collectors.toList());
	}
}
```

By using a few annotations __jqwik__ tries to make it as simple as possible 
for programmers to write and run Properties.

### Where to go from here

- Learn the details of _jqwik_ in [jqwik's elaborate user-guide](/docs/current/user-guide.html)
- Learn about [property-based testing in general](/property-based-testing.html)
- Read an introduction to [PBT in Java](https://blog.johanneslink.net/2018/03/24/property-based-testing-in-java-introduction/)
- Read an introduction to [PBT in Kotlin](https://johanneslink.net/property-based-testing-in-kotlin/)
- Ask or answer questions [on Stackoverflow](https://stackoverflow.com/questions/tagged/jqwik).
- Follow [jqwik on Twitter](https://twitter.com/jqwiknet).

