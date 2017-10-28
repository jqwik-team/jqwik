# The jqwik User Guide

_The user guide is still rough and incomplete. 
Volunteers for polishing and extending it are more than welcome._

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
### Table of Contents  

- [How to Use](#how-to-use)
  - [Gradle](#gradle)
  - [Maven](#maven)
- [How to Contribute](#how-to-contribute)
- [Creating an Example-based Test](#creating-an-example-based-test)
- [Creating a Property](#creating-a-property)
  - [Optional `@Property` Parameters](#optional-property-parameters)
  - [Optional `@ForAll` Parameters](#optional-forall-parameters)
- [Assertions](#assertions)
- [Grouping Tests](#grouping-tests)
- [Lifecycle](#lifecycle)
- [Automatic Parameter Generation](#automatic-parameter-generation)
- [Result Shrinking](#result-shrinking)
- [Customized Parameter Generation](#customized-parameter-generation)
- [Build your own Arbitraries](#build-your-own-arbitraries)
- [Assumptions](#assumptions)
- [Register default Generators and Arbitraries](#register-default-generators-and-arbitraries)
- [Running and Configuration](#running-and-configuration)
- [Self-Made Annotations](#self-made-annotations)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## How to Use

__jqwik__ is an alternative test engine for the
[JUnit 5 platform](http://junit.org/junit5/docs/current/api/org/junit/platform/engine/TestEngine.html).
That means that you can use it either stand-alone or combine it with any other JUnit 5 engine, e.g. 
[Jupiter (the standard engine)](http://junit.org/junit5/docs/current/user-guide/#dependency-metadata-junit-jupiter) or 
[Vintage (aka JUnit 4)](http://junit.org/junit5/docs/current/user-guide/#dependency-metadata-junit-vintage).

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
ext.jqwikVersion = '0.6.0'

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

Add the following repository and dependency to your `pom.xml` file:

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
        <version>0.6.0</version>
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


## Creating an Example-based Test

Just annotate a `public`, `protected` or package-scoped method with `@Example`.
Example-based tests work just like plain JUnit-style test cases and
are not supposed to take any parameters.

A test case method must
- either return a `boolean` value that signifies success (`true`)
  or failure (`false`) of this test case.
- or return nothing (`void`) in which case you will probably
  use [assertions](#assertions) in order to verify the test condition.
  
Here is a test class with two example-based tests:

```java
import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.*;
import org.assertj.core.data.*;

class ExampleBasedTests {
	
	@Example
	void squareRootOf16is4() { 
		assertThat(Math.sqrt(16)).isCloseTo(4.0, Offset.offset(0.01));
	}

	@Example
	boolean add1plu3is4() {
		return (1 + 3) == 4;
	}
}
```

## Creating a Property

You create a _Property_ by annotating a `public`, `protected` 
or package-scoped method with `@Property`. In contrast to
examples a property method is supposed to have one or
more parameters, all of which must be annotated with `@ForAll`.

At test runtime the exact parameter values of the property method
will be filled in by _jqwik_.

Just like an example test a property method has to 
- either return a `boolean` value that signifies success (`true`)
  or failure (`false`) of this property.
- or return nothing (`void`). In that case you will probably
  use [assertions](#assertions) to check the property's invariant.

If not [specified differently](#optional-property-parameters), 
_jqwik_ will run 1000 _tries_, i.e. a 1000 different sets of 
parameter values and execute the property method with each of those parameter sets. 
The first failed execution will stop value generation 
and be reported as failure - usually followed by an attempt to 
[shrink](#result-shrinking) the falsified parameter set.

Here are two properties whose failures might surprise you:

```java
import net.jqwik.api.*;
import org.assertj.core.api.*;

class PropertyBasedTests {

	@Property
	boolean absoluteValueOfAllNumbersIsPositive(@ForAll int anInteger) {
		return Math.abs(anInteger) >= 0;
	}

	@Property
	void lengthOfConcatenatedStringIsGreaterThanLengthOfEach(
		@ForAll String string1, @ForAll String string2
	) {
		String conc = string1 + string2;
		Assertions.assertThat(conc.length()).isGreaterThan(string1.length());
		Assertions.assertThat(conc.length()).isGreaterThan(string2.length());
	}
}
```

Currently _jqwik_ cannot deal with parameters that are not
annotated with '@ForAll'. However, this might change
in future versions.

### Optional `@Property` Parameters

The `@Property` annotation has a few optional values:

- `int tries`: The number of times _jqwik_ tries to generate paramter values for this method.
  Default is `1000`.
- `long seed`: The _random seed_ to use for generating values. If you do not specify a values
  _jqwik_ will use a random _random seed_. The actual seed used is being reported by 
  each run property.
- `int maxDiscardRatio`: The maximal number of tried versus actually checked property runs
  in case you are using [Assumptions](#assumptions). If the ratio is exceeded _jqwik_ will
  report this property as a failure. Default is `5`.
- `ShrinkingMode shrinking`: You can switch off parameter shrinking by using `ShrinkingMode.OFF`.
  Default is `ShrinkingMode.ON`

### Optional `@ForAll` Parameters

## Assertions

__jqwik__ does not come with any assertions, so you have to use one of the
third-party assertion libraries, e.g. [Hamcrest](http://hamcrest.org/) or 
[AssertJ](http://joel-costigliola.github.io/assertj/). 

If you have Jupiter in your test dependencies anyway, you can also use the
static methods in `org.junit.jupiter.api.Assertions`.

## Grouping Tests

## Lifecycle

## Automatic Parameter Generation

## Result Shrinking

## Customized Parameter Generation

## Build your own Arbitraries

## Assumptions

## Register default Generators and Arbitraries

## Running and Configuration

## Self-Made Annotations