## A Simpler JUnit Test Engine

An alternative test engine for the
[JUnit 5 platform](http://junit.org/junit5/docs/current/api/org/junit/platform/engine/TestEngine.html)

### Why you might want to use an alternative test engine

[Jupiter](http://junit.org/junit5/docs/current/user-guide/) is JUnit 5's approach for a new test engine.
It has a very elaborate [programming](http://junit.org/junit5/docs/current/user-guide/#writing-tests)
and [extension](http://junit.org/junit5/docs/current/user-guide/#extensions) model.
Maybe we can get away with less complexity?

And sure enough, competition stimulates business, they say.

### Principles

The Jqwik test engine will be developed from a few basic principles:

- Every additional feature must solve a _real testing problem_ that cannot be
  tackled by existing mechanism in a reasonably simple way.
- Keeping the design simpler - and thereby more maintainable - is a feature
  itself and will often prevail over adding another feature of unproven or rather
  esoteric value.
- [Microtests](https://www.industriallogic.com/blog/history-microtests/)
  are the foundation of scalable and maintainable Agile test automation.
  When in doubt, I'll rank features that simplify microtesting over those that
  are intended to facilitate or enable integrated testing.

### Contribute

Please, please, please add your suggestion, ideas and bug reports using the project's
[issue tracker on github](https://github.com/jlink/jqwik/issues).

Of course, you can also send in pull requests. Be prepared, though, that
I'll be very strict about what I accept, since I consider
the first months of a project to be crucial for shaping the mid and long-term
future of a project's design and architecture.

### Current Features

Jqwik allows you to specify _example-based_ scanarios and _property-based_ tests
à la [Quickcheck](https://en.wikipedia.org/wiki/QuickCheck).

Features are supposed to be documented withing Jqwik's
[github Wiki](https://github.com/jlink/jqwik/wiki).

#### Gradle Dependencies

Add the following stuff to your `build.gradle` file.
Maven users can sure figure the corresponding lines on their own :).

```
repositories {
	mavenCentral()
	maven { url "https://jitpack.io" }
  ...
}

ext.junitPlatformVersion = '1.0.0-RC2'
ext.junitJupiterVersion = '5.0.0-RC2'
ext.jqwikVersion = '0.4.4'

dependencies {
    ...

  // to enable the platform to run tests at all
  testCompile("org.junit.platform:junit-platform-launcher:${junitPlatformVersion}")

  // Falsely required by IDEA's Junit 5 support
  testRuntime("org.junit.jupiter:junit-jupiter-engine:${junitJupiterVersion}")

  // jqwik dependency
  testCompile "com.github.jlink:jqwik:${jqwikVersion}"

  // You'll probably need some assertions
  testCompile("org.assertj:assertj-core:3.6.2")

}

```

#### Example Based Testing

```java
import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.*;

public class SimpleExampleTests implements AutoCloseable {
	@Example
	void succeeding() { }

	@Example
	void failing() {
		fail("failing");
	}

	// Executed after each test case
	void close() { }

	@Group
	class AGroupOfCoherentTests {
		@Example
		void anotherSuccess() { }
	}
}
```
This looks like standard Jupiter tests and works basically the same but without
the complicated lifecycle of Before's and After's.

Jqwik does not come with any assertions, so you have to use one of the
third-party assertion libs, e.g. Hamcrest or AssertJ.


#### Property Based Testing

Driven by the common hype about functional programming,
property-based testing with tools like Quickcheck is recognized as an
important ingredient of  up-to-date testing approaches.

Jqwik tries to make this as easy as possible for Java programmers to use.

```java
import net.jqwik.api.*;

public class FizzBuzzTests {

	@Property
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
```