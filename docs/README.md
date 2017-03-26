## A Simpler JUnit Test Engine

An alternative test engine for the 
[JUnit 5 platform](http://junit.org/junit5/docs/current/api/org/junit/platform/engine/TestEngine.html)

### Why you might want to use an alternative test engine

[Jupiter](http://junit.org/junit5/docs/current/user-guide/) is JUnit 5's approach for a new test engine. 
It has a very elaborate [programming](http://junit.org/junit5/docs/current/user-guide/#writing-tests) 
and [extension](http://junit.org/junit5/docs/current/user-guide/#extensions) model.

Let's see if we can get away with less complexity?

The idea is to evolve a test engine from first principles:
1. Only add features that solve a real testing problem
2. Add extension possibilities only if you have at least two working examples 
   that can be generalized.

### Current Features

Jqwik allows you to write Example-based tests and Property tests.

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

Jqwik tries to make it as easy as possible
for Java programmers to use it; it currently builds on [Javaslang](http://www.javaslang.io/)
and it's testing lib to enable this feature.

```java
class FizzBuzzTests {
	// TODO: Property tests to add
}

```

Read [this article](https://www.sitepoint.com/property-based-testing-with-javaslang/) 
to see how this test looks when using Javaslang with JUnit 4.