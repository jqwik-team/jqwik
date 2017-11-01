# The jqwik User Guide

_The user guide is still rough and incomplete. 
Volunteers for polishing and extending it are more than welcome._

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
### Table of Contents  

- [How to Use](#how-to-use)
  - [Gradle](#gradle)
  - [Maven](#maven)
- [Creating an Example-based Test](#creating-an-example-based-test)
- [Creating a Property](#creating-a-property)
  - [Optional `@Property` Parameters](#optional-property-parameters)
- [Assertions](#assertions)
- [Lifecycle](#lifecycle)
- [Grouping Tests](#grouping-tests)
- [Default Parameter Generation](#default-parameter-generation)
  - [Constraining Default Generation](#constraining-default-generation)
- [Customized Parameter Generation](#customized-parameter-generation)
- [Result Shrinking](#result-shrinking)
- [Assumptions](#assumptions)
- [Program your own Generators and Arbitraries](#program-your-own-generators-and-arbitraries)
- [Register default Generators and Arbitraries](#register-default-generators-and-arbitraries)
- [Running and Configuration](#running-and-configuration)
  - [jqwik Configuration](#jqwik-configuration)
- [Self-Made Annotations](#self-made-annotations)
- [Glossary](#glossary)

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

ext.junitPlatformVersion = '1.0.1'
ext.junitJupiterVersion = '5.0.1'
ext.jqwikVersion = '0.6.4'

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
        <version>0.6.4</version>
        <scope>test</scope>
    </dependency>
</dependencies>

```

See [the Maven section in JUnit 5's user guide](http://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven)
for details on how to configure the surefire plugin and other dependencies.

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

- `int tries`: The number of times _jqwik_ tries to generate parameter values for this method.
  Default is `1000`.
- `long seed`: The _random seed_ to use for generating values. If you do not specify a values
  _jqwik_ will use a random _random seed_. The actual seed used is being reported by 
  each run property.
- `int maxDiscardRatio`: The maximal number of tried versus actually checked property runs
  in case you are using [Assumptions](#assumptions). If the ratio is exceeded _jqwik_ will
  report this property as a failure. Default is `5`.
- `ShrinkingMode shrinking`: You can switch off parameter shrinking by using `ShrinkingMode.OFF`.
  Default is `ShrinkingMode.ON`
- `ReportingMode reporting`: You can switch to more verbose reporting. 
  `ReportingMode.GENERATED` will report each generated set of parameters.
  Default is `ReportingMode.MINIMAL`

## Assertions

__jqwik__ does not come with any assertions, so you have to use one of the
third-party assertion libraries, e.g. [Hamcrest](http://hamcrest.org/) or 
[AssertJ](http://joel-costigliola.github.io/assertj/). 

If you have Jupiter in your test dependencies anyway, you can also use the
static methods in `org.junit.jupiter.api.Assertions`.

## Lifecycle

The current lifecycle of jqwik test methods is rather simple:

- For [each try](#try), annotated with `@Property` or `@Example`, 
  a new instance of the containing test class is created
  in order to keep the individual tests isolated from each other.
- If you have preparatory work to do for _each_ [each try](#try), 
  create a constructor without parameters and do the work there.
- If you have cleanup work to do for _each_ [each try](#try), 
  the containing test class can implement `java.lang.AutoCloseable`.
  The `close`-Method will be called after each test method execution.
  
```java
import net.jqwik.api.*;

class TestsWithLifecycle implements AutoCloseable {

	TestsWithLifecycle() {
		System.out.println("Before each");
	}

	@Example void anExample() {
		System.out.println("anExample");
	}

	@Property(tries = 5)
	void aProperty(@ForAll String aString) {
		System.out.println("anProperty: " + aString);
	}

	@Override
	public void close() throws Exception {
		System.out.println("After each");
	}
}
```

In this example both the constructor and `close()` will be called 6 times: 
Once for `anExample()` and 5 times for `aProperty(...)`.

## Grouping Tests

Within a containing test class you can group other containers by embedding
another non-static and non-private inner class and annotating it with `@Group`.
Grouping examples and properties is a means to improve the organization and 
maintainability of your tests.

Groups can be nested and there lifecycle is also nested, that means that
the lifecycle of a test class is also applied to inner groups of that container.

```java
import net.jqwik.api.*;

class TestsWithGroups {

	@Property
	void outer(@ForAll String aString) {
	}

	@Group
	class Group1 {
		@Property
		void group1Property(@ForAll String aString) {
		}

		@Group
		class Subgroup {
			@Property
			void subgroupProperty(@ForAll String aString) {
			}
		}
	}

	@Group
	class Group2 {
		@Property
		void group2Property(@ForAll String aString) {
		}
	}
}
```

## Default Parameter Generation

_jqwik_ tries to generate values for those property method parameters that are
annotated with `@ForAll`. If the annotation does not have a `value` parameter,
jqwik will use default generation for the following types:

- `String`
- Integral types `Integer`, `int`, `Long`, `long` and `BigInteger`
- Floating types  `Float`, `float`, `Double`, `double` and `BigDecimal`
- `Boolean` and `boolean`
- All `enum` types
- Collection types `List<T>`, `Set<T>` and `Stream<T>` 
  as long as `T` can also be provided by default generation.
- `Optional<T>` of types that are provided by default.
- Array `T[]` of types that are provided by default.

If you use `@ForAll` with a value, e.g. `@ForAll("aMethodName")`, the method
referenced by `"aMethodName"` will be called to provide an Arbitrary of the 
required type (see [Customized Parameter Generation](#customized-parameter-generation)). 

### Constraining Default Generation

Default parameter generation can be influenced and constrained by additional annotations, depending
on the requested parameter type.

All types:

- `@WithNull(double value = 0.1)`: Also generate `null` values with a probability of `value`. 

Strings:

- `@Chars(chars[] value = {}, char from = 0, char to = 0)`
- `@StringLength(int min = 0, int max)`

List, Set, Stream and Arrays:

- `@Size(int min = 0, int max)`

Integer and int:

- `@IntRange(int min = 0, int max)`

Long, long and BigInteger:

- `@LongRange(long min = 0L, long max)`

Float and float:

- `@FloatRange(float min = 0.0f, float max)`

Double, double and BigDecimal:

- `@DoubleRange(double min = 0.0, double max)`

All floating types:

- `@Scale(int value)`

In case of collections, arrays and `Optional` the constraining annotations are also applied to the
contained type, e.g.:

```java
@Property
void aProperty(@ForAll @StringLength(max=10) List<String> listOfStrings) {
}
```
will generate lists of Strings that have 10 characters max.

## Customized Parameter Generation

## Result Shrinking

## Assumptions

## Program your own Generators and Arbitraries

This topic will probably need a page of its own.

## Register default Generators and Arbitraries

The API for providing Arbitraries and Generators by default is not public yet.

## Running and Configuration

When running _jqwik_ tests (through your IDE or your build tool) you might notice 
that - once a [property](#property) has been falsified - it will always be tried
with the same seed to enhance the reproducibility of a bug. This requires
that _jqwik_ will persist some runtime data across test runs.

You can configure this and other default behaviour in [jqwik's configuration](#jqwik_configuration).

### jqwik Configuration

_jqwik_ will look for a file `jqwik.properties` in your classpath in which you can configure
a few basic parameters:

```
database = .jqwik-database
rerunFailuresWithSameSeed = true
runFailuresFirst = false
```

This type of configuration is preliminary and will be replaced by 
[JUnit 5's platform configuration](http://junit.org/junit5/docs/5.0.0/user-guide/#running-tests-config-params)
mechanism soon. Moreover, there will probably be many more default parameters to change.

## Self-Made Annotations

You can [make your own annotations](http://junit.org/junit5/docs/5.0.0/user-guide/#writing-tests-meta-annotations)
instead of using _jqwik_'s built-in ones. BTW, '@Example' is nothing but a plain annotation using `@Property`
as "meta"-annotation.




 

## Glossary

#### Arbitrary

#### Property

A _property_ is a [test method](#test-method) that has one or more
parameters annotated with `@ForAll`. 

#### RandomGenerator

#### Test Method

A _test method_ is a a `public`, `protected` or package-scoped method, 
annotated with `@Example` or `@Property`.

#### Try

A _try_ is the attempt at running a [test method](#test-method) 
a single time with a specific set of parameters.