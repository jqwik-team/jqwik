The main purpose of __jqwik__ is to bring [Property-Based Testing](https://en.wikipedia.org/wiki/Property_testing) 
to the JVM. _Property-Based Testing_ tries to combine the intuitiveness of 
[Microtests](https://www.industriallogic.com/blog/history-microtests/) with the
effectiveness of randomized, generated test data.

__jqwik__ is an alternative test engine for the
[JUnit 5 platform](http://junit.org/junit5/docs/current/api/org/junit/platform/engine/TestEngine.html).
That means that you can combine it with any other JUnit 5 engine, e.g. 
[Jupiter (the standard engine)](http://junit.org/junit5/docs/current/user-guide/#dependency-metadata-junit-jupiter) or 
[Vintage (aka JUnit 4)](http://junit.org/junit5/docs/current/user-guide/#dependency-metadata-junit-vintage).

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
###Table of Contents  

- [How to Use](#how-to-use)
  - [Gradle](#gradle)
  - [Maven](#maven)
- [How to Contribute](#how-to-contribute)
- [Feature Overview](#feature-overview)
  - [Example Based Testing](#example-based-testing)
  - [Property Based Testing](#property-based-testing)
- [More about Property-Based Testing](#more-about-property-based-testing)
- [User Guide](#user-guide)
  - [Creating a Test Case](#creating-a-test-case)
  - [Grouping Tests](#grouping-tests)
  - [Lifecycle](#lifecycle)
  - [Automatic Parameter Generation](#automatic-parameter-generation)
  - [Result Shrinking](#result-shrinking)
  - [Customized Parameter Generation](#customized-parameter-generation)
  - [Build your own Arbitraries](#build-your-own-arbitraries)
  - [Register default Generators and Arbitraries](#register-default-generators-and-arbitraries)
  - [Running and Configuration](#running-and-configuration)
  - [Self-Made Annotations](#self-made-annotations)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## How to Use

__jqwik__ is currently _not_ deployed to Maven Central but [JitPack](https://jitpack.io/) is 
being used to provide [the latest release(s)](https://github.com/jlink/jqwik/releases). 
That's why you have to add the JitPack-Repository to your list of maven repositories.

### Gradle

Add the following stuff to your `build.gradle` file:

```
repositories {
    ...
    mavenCentral()
    maven { url "https://jitpack.io" }
}

ext.junitPlatformVersion = '1.0.0'
ext.junitJupiterVersion = '5.0.0'
ext.jqwikVersion = '0.5.0'

junitPlatform {
	filters {
		includeClassNamePattern '.*Test'
		includeClassNamePattern '.*Tests'
		includeClassNamePattern '.*Properties'
	}
	enableStandardTestTask true
}

dependencies {
    ...

    // to enable the platform to run tests at all
    testCompile("org.junit.platform:junit-platform-launcher:${junitPlatformVersion}")
    
    // Falsely required by IDEA's Junit 5 support
    testRuntime("org.junit.jupiter:junit-jupiter-engine:${junitJupiterVersion}")
    
    // jqwik dependency
    testCompile "com.github.jlink:jqwik:${jqwikVersion}"
    
    // You'll probably need some assertions
    testCompile("org.assertj:assertj-core:3.8.0")

}

```

See [the Gradle section in JUnit 5's user guide](http://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle)
for more details on how to configure test execution.

### Maven

Add the following repository to your `pom.xml` file:

```
<repositories>
    ...
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    ...
    <dependency>
        <groupId>com.github.jlink</groupId>
        <artifactId>jqwik</artifactId>
        <version>0.5.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

```

See [the Maven section in JUnit 5's user guide](http://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven)
for details on how to configure the surefire plugin and other dependencies.

## How to Contribute

Please, please, please add your suggestion, ideas and bug reports using the project's
[issue tracker on github](https://github.com/jlink/jqwik/issues).

Of course, you are also invited to send in pull requests. Be prepared, though, that
I'll be picky about what I accept, since the initial phase of a project 
is crucial for shaping the mid and long-term
future of a project's design and architecture.

If you want to become a long-term supporter, maintainer or committer for __jqwik__
please [get in touch](mailto:business@johanneslink.net).

## Feature Overview

__jqwik__ allows you to specify both [_example-based_ scenarios](#example-based-testing) and 
[_property-based_ test cases](#property-based-testing)
à la [Quickcheck](https://en.wikipedia.org/wiki/QuickCheck).


### Example Based Testing

Examples are just a fancy name for the usual unit tests that directly specify the
data being used to drive and check the behaviour of your code. 

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
This looks like standard Jupiter tests and works basically the same but without
the complicated lifecycle of Before's and After's.

__jqwik__ does not come with any assertions, so you have to use one of the
third-party assertion libs, e.g. [Hamcrest](http://hamcrest.org/) or 
[AssertJ](http://joel-costigliola.github.io/assertj/).


### Property Based Testing

Driven by the common hype about functional programming,
property-based testing with tools like Quickcheck is recognized as an
important ingredient of up-to-date testing approaches.

__jqwik__ tries to make this as easy as possible for Java programmers to use. 
Here's an example that checks the correctness of the (in)famous 
[Fizz Buzz Kata](http://codingdojo.org/kata/FizzBuzz/) for all numbers divisible by 3:

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
## More about Property-Based Testing

## User Guide

_The user guide has yet to be written_

### Creating a Test Case

### Grouping Tests

### Lifecycle

### Automatic Parameter Generation

### Result Shrinking

### Customized Parameter Generation

### Build your own Arbitraries

### Register default Generators and Arbitraries

### Running and Configuration

### Self-Made Annotations