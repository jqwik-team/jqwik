---
title: jqwik User Guide - 1.1.4
---
<h1>The jqwik User Guide
<span style="padding-left:1em;font-size:50%;font-weight:lighter">1.1.4</span>
</h1>

<!-- use `doctoc --maxlevel 4 user-guide.md` to recreate the TOC -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
### Table of Contents  

- [How to Use](#how-to-use)
  - [Gradle](#gradle)
    - [Seeing jqwik Reporting in Gradle Output](#seeing-jqwik-reporting-in-gradle-output)
  - [Maven](#maven)
  - [Snapshot Releases](#snapshot-releases)
  - [Project without Build Tool](#project-without-build-tool)
- [Creating an Example-based Test](#creating-an-example-based-test)
- [Creating a Property](#creating-a-property)
  - [Optional `@Property` Parameters](#optional-property-parameters)
  - [Additional Reporting](#additional-reporting)
- [Assertions](#assertions)
- [Lifecycle](#lifecycle)
  - [Method Lifecycle](#method-lifecycle)
  - [Other Lifecycles](#other-lifecycles)
- [Grouping Tests](#grouping-tests)
- [Naming and Labeling Tests](#naming-and-labeling-tests)
- [Tagging Tests](#tagging-tests)
- [Disabling Tests](#disabling-tests)
- [Default Parameter Generation](#default-parameter-generation)
  - [Constraining Default Generation](#constraining-default-generation)
    - [Allow Null Values](#allow-null-values)
    - [Unique Values](#unique-values)
    - [String Length](#string-length)
    - [Character Sets](#character-sets)
    - [List, Set, Stream and Array Size:](#list-set-stream-and-array-size)
    - [Integer Constraints](#integer-constraints)
    - [Decimal Constraints](#decimal-constraints)
  - [Constraining parameterized types](#constraining-parameterized-types)
  - [Providing variable types](#providing-variable-types)
- [Self-Made Annotations](#self-made-annotations)
- [Customized Parameter Generation](#customized-parameter-generation)
  - [Static `Arbitraries` methods](#static-arbitraries-methods)
    - [Generate values yourself](#generate-values-yourself)
    - [Select values randomly](#select-values-randomly)
    - [Select randomly with Weights](#select-randomly-with-weights)
    - [Integers](#integers)
    - [Decimals](#decimals)
    - [Characters and Strings](#characters-and-strings)
    - [java.util.Random](#javautilrandom)
    - [Constants](#constants)
    - [Create](#create)
    - [Shuffling Permutations](#shuffling-permutations)
    - [Default Types](#default-types)
  - [Collections, Streams, Arrays and Optional](#collections-streams-arrays-and-optional)
  - [Collecting Values in a List](#collecting-values-in-a-list)
  - [Fluent Configuration Interfaces](#fluent-configuration-interfaces)
  - [Generate `null` values](#generate-null-values)
  - [Filtering](#filtering)
  - [Creating unique values](#creating-unique-values)
  - [Mapping](#mapping)
  - [Flat Mapping](#flat-mapping)
  - [Flat Mapping with Tuple Types](#flat-mapping-with-tuple-types)
  - [Randomly Choosing among Arbitraries](#randomly-choosing-among-arbitraries)
  - [Combining Arbitraries](#combining-arbitraries)
  - [Combining Arbitraries with Builder](#combining-arbitraries-with-builder)
    - [Flat Combination](#flat-combination)
  - [Fix an Arbitrary's `genSize`](#fix-an-arbitrarys-gensize)
  - [Generating all possible values](#generating-all-possible-values)
  - [Iterating through all possible values](#iterating-through-all-possible-values)
- [Recursive Arbitraries](#recursive-arbitraries)
  - [Probabilistic Recursion](#probabilistic-recursion)
  - [Deterministic Recursion](#deterministic-recursion)
  - [Deterministic Recursion with `recursive()`](#deterministic-recursion-with-recursive)
- [Contract Tests](#contract-tests)
- [Stateful Testing](#stateful-testing)
  - [Specify Actions](#specify-actions)
  - [Check Postconditions](#check-postconditions)
  - [Number of actions](#number-of-actions)
  - [Check Invariants](#check-invariants)
- [Assumptions](#assumptions)
- [Result Shrinking](#result-shrinking)
  - [Integrated Shrinking](#integrated-shrinking)
  - [Switch Shrinking Off](#switch-shrinking-off)
  - [Switch Shrinking to Full Mode](#switch-shrinking-to-full-mode)
  - [Change the Shrinking Target](#change-the-shrinking-target)
- [Collecting and Reporting Statistics](#collecting-and-reporting-statistics)
- [Providing Default Arbitraries](#providing-default-arbitraries)
  - [Simple Arbitrary Providers](#simple-arbitrary-providers)
  - [Arbitrary Providers for Parameterized Types](#arbitrary-providers-for-parameterized-types)
  - [Arbitrary Provider Priority](#arbitrary-provider-priority)
- [Create your own Annotations for Arbitrary Configuration](#create-your-own-annotations-for-arbitrary-configuration)
  - [Arbitrary Configuration Example: `@Odd`](#arbitrary-configuration-example-odd)
- [Domain and Domain Context](#domain-and-domain-context)
  - [Domain example: American Addresses](#domain-example-american-addresses)
- [Generation from a Type's Interface](#generation-from-a-types-interface)
- [Implement your own Arbitraries and Generators](#implement-your-own-arbitraries-and-generators)
- [Exhaustive Generation](#exhaustive-generation)
- [Data-Driven Properties](#data-driven-properties)
- [Rerunning Falsified Properties](#rerunning-falsified-properties)
- [jqwik Configuration](#jqwik-configuration)
- [Release Notes](#release-notes)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## How to Use

__jqwik__ is an alternative test engine for the
[JUnit 5 platform](https://junit.org/junit5/docs/current/api/org/junit/platform/engine/TestEngine.html).
That means that you can use it either stand-alone or combine it with any other JUnit 5 engine, e.g. 
[Jupiter (the standard engine)](https://junit.org/junit5/docs/current/user-guide/#dependency-metadata-junit-jupiter) or
[Vintage (aka JUnit 4)](https://junit.org/junit5/docs/current/user-guide/#dependency-metadata-junit-vintage).
All you have to do is add all needed engines to your `testCompile` dependencies as shown in the
[gradle file](#gradle) below.

The latest release of __jqwik__ is deployed to [Maven Central](https://mvnrepository.com/).

Snapshot releases can be fetched from https://oss.sonatype.org/content/repositories/snapshots.



### Gradle

Since version 4.6, Gradle has 
[built-in support for the JUnit platform](https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.Test.html).
Set up is rather simple; here are the relevant parts of a project's `build.gradle` file:


```
repositories {
    ...
    mavenCentral()

    # For snapshot releases only:
    maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }

}

ext.junitPlatformVersion = '1.4.2'
ext.junitJupiterVersion = '5.4.2'

ext.jqwikVersion = '1.1.4'

test {
	useJUnitPlatform {
		includeEngines "jqwik"

		// includeTags "fast", "medium"
		// excludeTags "slow"
	}

	include '**/*Properties.class'
	include '**/*Test.class'
	include '**/*Tests.class'
}

dependencies {
    ...

    // aggregate jqwik dependency
    testCompile "net.jqwik:jqwik:${jqwikVersion}"

    // Add if you also want to use the Jupiter engine or Assertions from it
    testCompile("org.junit.jupiter:junit-jupiter-engine:5.4.2")

    // Add any other test library you need...
    testCompile("org.assertj:assertj-core:3.9.1")

}
```

With version 1.0.0 `net.jqwik:jqwik` has become an aggregating module to simplify jqwik
integration for standard users. 
If you want to be more explicit about the real dependencies you can replace this dependency with

```
    testCompile "net.jqwik:jqwik-api:${jqwikVersion}"
    testRuntime "net.jqwik:jqwik-engine:${jqwikVersion}"
```

See [the Gradle section in JUnit 5's user guide](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle)
for more details on how to configure Gradle for the JUnit 5 platform.

#### Seeing jqwik Reporting in Gradle Output

Since Gradle does not yet support JUnit platform reporting
([see this Github issue](https://github.com/gradle/gradle/issues/4605))
jqwik has switched to do its own reporting by default. This behaviour
[can be configured](#jqwik-configuration) through parameter `useJunitPlatformReporter`
(default: `false`).

If you want to see jqwik's reports in the output use Gradle's command line option `--info`:

```
> gradle clean test --info
...
mypackage.MyClassProperties > myPropertyMethod STANDARD_OUT
    timestamp = 2019-02-28T18:01:14.302, MyClassProperties:myPropertyMethod = 
                                  |-----------------------jqwik-----------------------
    tries = 1000                  | # of calls to property
    checks = 1000                 | # of not rejected calls
    generation-mode = RANDOMIZED  | parameters are randomly generated
    seed = 1685744359484719817    | random seed to reproduce generated values
```

### Maven

Configure the surefire plugin as described in 
[the Maven section in JUnit 5's user guide](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven)
and add the following dependency to your `pom.xml` file:

```
<dependencies>
    ...
    <dependency>
        <groupId>net.jqwik</groupId>
        <artifactId>jqwik</artifactId>
        <version>1.1.4</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```


### Snapshot Releases

Snapshot releases are available through Sonatype's 
[snapshot repositories](#https://oss.sonatype.org/content/repositories/snapshots).

Adding 

```
https://oss.sonatype.org/content/repositories/snapshots
``` 

as a maven repository
will allow you to use _jqwik_'s snapshot release which contains all the latest features.

### Project without Build Tool

I've never tried it but using jqwik without gradle or some other tool to manage dependencies should also work.
You will have to add _at least_ the following jars to your classpath:

- `jqwik-1.1.4.jar`
- `junit-platform-engine-1.4.2.jar`
- `junit-platform-commons-1.4.2.jar`
- `opentest4j-1.1.1.jar`
- `assertj-core-3.11.x.jar` in case you need assertion support

## Creating an Example-based Test

Just annotate a `public`, `protected` or package-scoped method with
[`@Example`](/docs/1.1.4/javadoc/net/jqwik/api/Example.html).
Example-based tests work just like plain JUnit-style test cases and
are not supposed to take any parameters.

A test case method must
- either return a `boolean` value that signifies success (`true`)
  or failure (`false`) of this test case.
- or return nothing (`void`) in which case you will probably
  use [assertions](#assertions) in order to verify the test condition.
  
[Here](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/ExampleBasedTests.java)
is a test class with two example-based tests:

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

_Properties_ are the core concept of [property-based testing](/#properties).

You create a _Property_ by annotating a `public`, `protected` 
or package-scoped method with 
[`@Property`](/docs/1.1.4/javadoc/net/jqwik/api/Property.html). 
In contrast to examples a property method is supposed to have one or
more parameters, all of which must be annotated with 
[`@ForAll`](/docs/1.1.4/javadoc/net/jqwik/api/ForAll.html).

At test runtime the exact parameter values of the property method
will be filled in by _jqwik_.

Just like an example test a property method has to 
- either return a `boolean` value that signifies success (`true`)
  or failure (`false`) of this property.
- or return nothing (`void`). In that case you will probably
  use [assertions](#assertions) to check the property's invariant.

If not [specified differently](#optional-property-parameters), 
_jqwik_ __will run 1000 tries__, i.e. a 1000 different sets of 
parameter values and execute the property method with each of those parameter sets. 
The first failed execution will stop value generation 
and be reported as failure - usually followed by an attempt to 
[shrink](#result-shrinking) the falsified parameter set.

[Here](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/PropertyBasedTests.java)
are two properties whose failures might surprise you:

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

The [`@Property`](/docs/1.1.4/javadoc/net/jqwik/api/Property.html) 
annotation has a few optional values:

- `int tries`: The number of times _jqwik_ tries to generate parameter values for this method.
  
  The default is `1000` which can be overridden in [`jqwik.properties`](#jqwik-configuration).

- `String seed`: The _random seed_ to use for generating values. If you do not specify a values
  _jqwik_ will use a random _random seed_. The actual seed used is being reported by 
  each run property.

- `int maxDiscardRatio`: The maximal number of tried versus actually checked property runs
  in case you are using [Assumptions](#assumptions). If the ratio is exceeded _jqwik_ will
  report this property as a failure. 
  
  The default is `5` which can be overridden in [`jqwik.properties`](#jqwik-configuration).

- `ShrinkingMode shrinking`: You can influence the way shrinking is done
  - `ShrinkingMode.OFF`: No shrinking at all
  - `ShrinkingMode.FULL`: Shrinking continues until no smaller value can
    be found that also falsifies the property.
    This might take very long or not end at all in rare cases.
  - `ShrinkingMode.BOUNDED`: Shrinking is tried to a depth of 1000 steps
    maximum per value. This is the default.

  Most of the time you want to stick with the default. Only if
  bounded shrinking is reported - look at a falsified property's output! -
  should you try with `ShrinkingMode.FULL`.

- `GenerationMode generation`: You can direct _jqwik_ about the principal approach
  it takes towards value generation.

  - `GenerationMode.AUTO` is the default. This will choose [exhaustive generation](#exhaustive-generation) 
    whenever this is deemed sensible, i.e., when the maximum number of generated values is 
    equal or less thant the configured `tries` attribute.
  - `GenerationMode.RANDOMIZED` directs _jqwik_ to always generate values using its
    randomized generators.
  - `GenerationMode.EXHAUSTIVE` directs _jqwik_ to use [exhaustive generation](#exhaustive-generation)
    if the arbitraries in use support exhaustive generation at all and if the calculated
    maximum number of different values to generate is below `Integer.MAX_VALUE`.
  - `GenerationMode.DATA_DRIVEN` directs _jqwik_ to feed values from a data provider
    specified with `@FromData`. See [data-driven properties](#data-driven-properties) 
    for more information.
  
  The actual generation mode being used is reported for each property 
  together with the other information:
  
  ```
  tries = 10 
  checks = 10 
  generation-mode = EXHAUSTIVE 
  seed = 42859154278924201
  ```
  
    
### Additional Reporting

You can switch on additional reporting aspects by adding a
[`@Report(Reporting[])` annotation](/docs/1.1.4/javadoc/net/jqwik/api/Property.html)
to a property method.

The following reporting aspects are available:

- `Reporting.GENERATED` will report each generated set of parameters.
- `Reporting.FALSIFIED` will report each set of parameters
  that is falsified during shrinking.

## Assertions

__jqwik__ does not come with any assertions, so you have to use one of the
third-party assertion libraries, e.g. [Hamcrest](http://hamcrest.org/) or 
[AssertJ](http://joel-costigliola.github.io/assertj/). 

If you have Jupiter in your test dependencies anyway, you can also use the
static methods in `org.junit.jupiter.api.Assertions`.

## Lifecycle

### Method Lifecycle

The current lifecycle of jqwik test methods is rather simple:

- For each method, annotated with 
  [`@Property`](/docs/1.1.4/javadoc/net/jqwik/api/Property.html) 
  or [`@Example`](/docs/1.1.4/javadoc/net/jqwik/api/Example.html), 
  a new instance of the containing test class is created
  in order to keep the individual tests isolated from each other.
- If you have preparatory work to do for each method, 
  create a constructor without parameters and do the work there.
- If you have cleanup work to do for each method, 
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

[In this example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/TestsWithLifecycle.java)
both the constructor and `close()` will be called twice times: 
Once for `anExample()` and once for `aProperty(...)`.

### Other Lifecycles

Currently _jqwik_ does not have special support for a lifecycle per test container,
per test try or even package. Later versions of _jqwik_ might possible bring
more features in that field. 
[Create an issue on github](https://github.com/jlink/jqwik/issues) with your concrete needs.


## Grouping Tests

Within a containing test class you can group other containers by embedding
another non-static and non-private inner class and annotating it with `@Group`.
Grouping examples and properties is a means to improve the organization and 
maintainability of your tests.

Groups can be nested and there lifecycle is also nested, that means that
the lifecycle of a test class is also applied to inner groups of that container.
Have a look at [this example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/TestsWithGroups.java):

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

## Naming and Labeling Tests

Using Java-style camel case naming for your test container classes and property methods
will sometimes lead to hard to read display names in your test reports
and your IDE.
Therefore, _jqwik_ provides a simple way to insert spaces
into the displayed name of your test container or property:
just add underscores (`_`), which are valid Java identifier characters.
Each underscore will be replaced by a space for display purposes.

If you want to tweak display names even more,
test container classes, groups, example methods and property methods can be labeled
using the annotation `@Label("a label")`. This label will be used to display the element
in test reports or within the IDE. 
[In the following example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/NamingExamples.java),
every test relevant element has been labeled:

```java
@Label("Naming")
class NamingExamples {

	@Property
	@Label("a property")
	void aPropertyWithALabel() { }

	@Group
	@Label("A Group")
	class GroupWithLabel {
		@Example
		@Label("an example with äöüÄÖÜ")
		void anExampleWithALabel() { }
	}

    @Group
    class Group_with_spaces {
        @Example
        void example_with_spaces() { }
    }

}
```

Labels can consist of any characters and don't have to be unique - but you probably want them 
to be unique within their container.

## Tagging Tests

Test container classes, groups, example methods and property methods can be tagged
using the annotation `@Tag("a-tag")`. You can have many tags on the same element.

Those tag can be used to filter the set of tests 
[run by the IDE](https://blog.jetbrains.com/idea/2018/01/intellij-idea-starts-2018-1-early-access-program/) or 
[the build tool](https://docs.gradle.org/4.6/release-notes.html#junit-5-support).
Tags are handed down from container (class or group) to its children (test methods or groups).

Have a look at 
[the following example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/TaggingExamples.java).
Including the tag `integration-test` will include
all tests of the class.

```java
@Tag("integration-test")
class TaggingExamples {

	@Property
	@Tag("fast")
	void aFastProperty() { }

	@Example
	@Tag("slow") @Tag("involved")
	void aSlowTest() { }
}
```

Tags must follow certain rules as described 
[here](/docs/1.1.4/javadoc/net/jqwik/api/Tag.html)

## Disabling Tests

From time to time you might want to disable a test or all tests in a container
temporarily. You can do that by adding the
[`@Disabled`](/docs/1.1.4/javadoc/net/jqwik/api/Disabled.html) annotation
to a property method or a container class.

```java
import net.jqwik.api.Disabled

@Disabled("for whatever reason")
class DisablingExamples {

	@Property
	@Disabled
	void aDisabledProperty() { }

}
```

Disabled properties will be reported by IDEs and build tools as "skipped"
together with the reason - if one has been provided.

Be careful not to use the Jupiter annotation with the same name.
_Jqwik_ will refuse to execute methods that have Jupiter annotations.


## Default Parameter Generation

_jqwik_ tries to generate values for those property method parameters that are
annotated with [`@ForAll`](/docs/1.1.4/javadoc/net/jqwik/api/ForAll.html). If the annotation does not have a `value` parameter,
jqwik will use default generation for the following types:

- `Object`
- `String`
- Integral types `Byte`, `byte`, `Short`, `short` `Integer`, `int`, `Long`, `long` and `BigInteger`
- Floating types  `Float`, `float`, `Double`, `double` and `BigDecimal`
- `Boolean` and `boolean`
- `Character` and `char`
- All `enum` types
- Collection types `List<T>`, `Set<T>` and `Stream<T>` 
  as long as `T` can also be provided by default generation.
- `Iterable<T>` and `Iterator<T>` of types that are provided by default.
- `Optional<T>` of types that are provided by default.
- Array `T[]` of types that are provided by default.
- `java.util.Random`

If you use [`@ForAll`](/docs/1.1.4/javadoc/net/jqwik/api/ForAll.html) 
with a value, e.g. `@ForAll("aMethodName")`, the method
referenced by `"aMethodName"` will be called to provide an Arbitrary of the 
required type (see [Customized Parameter Generation](#customized-parameter-generation)). 

### Constraining Default Generation

Default parameter generation can be influenced and constrained by additional annotations, 
depending on the requested parameter type.

#### Allow Null Values

- [`@WithNull(double value = 0.1)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/WithNull.html): 
  Inject `null` into generated values with a probability of `value`. 
  
  Works for all generated types.
   
#### Unique Values

- [`@Unique`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/Unique.html):
  Prevent duplicate values to be generated _per try_. That means that
  there can still be duplicate values across several tries. That also means
  that `@Unique` only makes sense as annotation for a parameter type, e.g.:

  ```java
    @Property
    void uniqueInList(@ForAll @Size(5) List<@IntRange(min = 0, max = 10) @Unique Integer> aList) {
        Assertions.assertThat(aList).doesNotHaveDuplicates();
        Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 10);
    }
  ```

  Trying to generate a list with more than 11 elements would not work here.

  Works for all generated types.

#### String Length

- [`@StringLength(int value = 0, int min = 0, int max = 0)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/StringLength.html):
  Set either fixed length through `value` or configure the length range between `min` and `max`.

- [`@NotEmpty`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/NotEmpty.html):
  Set minimum length to `1`.

#### Character Sets

When generating chars any unicode character might be generated.

When generating Strings, however,
Unicode "noncharacters" and "private use characters"
will not be generated unless you explicitly include them using
`@Chars` or `@CharRange` (see below).

You can use the following annotations to restrict the set of allowed characters and even
combine several of them:

- [`@Chars(chars[] value = {})`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/Chars.html):
  Specify a set of characters.
  This annotation can be repeated which will add up all allowed chars.
- [`@CharRange(char from = 0, char to = 0)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/CharRange.html):
  Specify a start and end character.
  This annotation can be repeated which will add up all allowed chars.
- [`@NumericChars`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/NumericChars.html):
  Use digits `0` through `9`
- [`@LowerChars`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/LowerChars.html):
  Use lower case chars `a` through `z`
- [`@UpperChars`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/UpperChars.html):
  Use upper case chars `A` through `Z`
- [`@AlphaChars`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/AlphaChars.html):
  Lower and upper case chars are allowed.
- [`@Whitespace`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/Whitespace.html):
  All whitespace characters are allowed.

They work for generated `String`s and `Character`s.

#### List, Set, Stream and Array Size:

- [`@Size(int value = 0, int min = 0, int max = 0)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/Size.html): 
  Set either fixed size through `value` or configure the size range between `min` and `max`.

- [`@NotEmpty`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/NotEmpty.html):
  Set minimum size to `1`.


#### Integer Constraints

- [`@ByteRange(byte min = 0, byte max = Byte.MAX_VALUE)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/ByteRange.html):
  For `Byte` and `byte` only.
- [`@ShortRange(short min = 0, short max = Short.MAX_VALUE)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/ShortRange.html):
  For `Short` and `short` only.
- [`@IntRange(int min = 0, int max = Integer.MAX_VALUE)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/IntRange.html):
  For `Integer` and `int` only.
- [`@LongRange(long min = 0L, long max = Long.MAX_VALUE)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/LongRange.html):
  For `Long` and `long` only.
- [`@BigRange(String min = "", String max = "")`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/BigRange.html):
  For `BigInteger` generation.
- [`@Positive`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/Positive.html):
  Numbers larger than `0`. For all integral types.
- [`@Negative`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/Negative.html):
  Numbers lower than `0`. For all integral types.


#### Decimal Constraints

- [`@FloatRange(float min = 0.0f, float max = Float.MAX_VALUE)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/FloatRange.html):
  For `Float` and `float` only.
- [`@DoubleRange(double min = 0.0, double max = Double.MAX_VALUE)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/DoubleRange.html):
  For `Double` and `double` only.
- [`@BigRange(String min = "", String max = "")`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/BigRange.html):
  For `BigDecimal` generation.
- [`@Scale(int value)`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/Scale.html):
  Specify the maximum number of decimal places. For all decimal types.
- [`@Positive`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/Positive.html):
  Numbers larger than `0.0`. For all decimal types.
- [`@Negative`](/docs/1.1.4/javadoc/net/jqwik/api/constraints/Negative.html):
  Numbers lower than `0.0`. For all decimal types.

### Constraining parameterized types

When you want to constrain the generation of contained parameter types you can annotate 
the parameter type directly, e.g.:

```java
@Property
void aProperty(@ForAll @Size(min= 1) List<@StringLength(max=10) String> listOfStrings) {
}
```
will generate lists with a minimum size of 1 filled with Strings that have 10 characters max.

### Providing variable types

While checking properties of generically typed classes or functions, you often don't care
about the exact type of variables and therefore want to express them with type variables.
_jqwik_ can also handle type variables and wildcard types. The handling of upper and lower
bounds works mostly as you would expect it.

Consider
[the following examples](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/VariableTypedPropertyExamples.java):

```java
class VariableTypedPropertyExamples {

	@Property
	<T> boolean unboundedGenericTypesAreResolved(@ForAll List<T> items, @ForAll T newItem) {
		items.add(newItem);
		return items.contains(newItem);
	}

	@Property
	<T extends Serializable & Comparable> void someBoundedGenericTypesCanBeResolved(@ForAll List<T> items, @ForAll T newItem) {
	}

	@Property
	void someWildcardTypesWithUpperBoundsCanBeResolved(@ForAll List<? extends Serializable> items) {
	}

}
```

In the case of unbounded type variables or an unbounded wildcard type, _jqwik_
will create instanced of a special class (`WildcardObject`) under the hood.

In the case of bounded type variables and bounded wildcard types, _jqwik_
will check if any [registered arbitrary provider](#providing-default-arbitraries)
can provide suitable arbitraries and choose randomly between those.

There is, however, a potentially unexpected behaviour,
when the same type variable is used in more than one place and can be
resolved by more than one arbitrary. In this case it can happen that the variable
does not represent the same type in all places. You can see this above
in property method `someBoundedGenericTypesCanBeResolved()` where `items`
might be a list of Strings but `newItem` of some number type - and all that
_in the same call to the method_!

## Self-Made Annotations

You can [make your own annotations](http://junit.org/junit5/docs/5.0.0/user-guide/#writing-tests-meta-annotations)
instead of using _jqwik_'s built-in ones. BTW, '@Example' is nothing but a plain annotation using [`@Property`](/docs/1.1.4/javadoc/net/jqwik/api/Property.html)
as "meta"-annotation.

The following example provides an annotation to constrain String or Character generation to German letters only:

```java
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Digits
@AlphaChars
@Chars({'ä', 'ö', 'ü', 'Ä', 'Ö', 'Ü', 'ß'})
@Chars({' ', '.', ',', ';', '?', '!'})
@StringLength(min = 10, max = 100)
public @interface GermanText { }

@Property(tries = 10) @Reporting(Reporting.GENERATED)
void aGermanText(@ForAll @GermanText String aText) {}
```

The drawback of self-made annotations is that they do not forward their parameters to meta-annotations,
which constrains their applicability to simple cases.


## Customized Parameter Generation

Sometimes the possibilities of adjusting default parameter generation
through annotations is not enough. In that case you can delegate parameter
provision to another method. Look at the 
[following example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/ProvideMethodExamples.java):

```java
@Property
boolean concatenatingStringWithInt(
    @ForAll("shortStrings") String aShortString,
    @ForAll("10 to 99") int aNumber
) {
    String concatenated = aShortString + aNumber;
    return concatenated.length() > 2 && concatenated.length() < 11;
}

@Provide
Arbitrary<String> shortStrings() {
    return Arbitraries.strings().withCharRange('a', 'z')
        .ofMinLength(1).ofMaxLength(8);
}

@Provide("10 to 99")
Arbitrary<Integer> numbers() {
    return Arbitraries.integers().between(10, 99);
}
```

The String value of the [`@ForAll`](/docs/1.1.4/javadoc/net/jqwik/api/ForAll.html) 
annotation serves as a reference to a 
method within the same class (or one of its superclasses or owning classes).
This reference refers to either the method's name or the String value
of the method's `@Provide` annotation.

The providing method has to return an object of type 
[`@Arbitrary<T>`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html) 
where `T` is the static type of the parameter to be provided. 

Parameter provision usually starts with a 
[static method call to `Arbitraries`](#static-arbitraries-methods), maybe followed
by one or more [filtering](#filtering), [mapping](#mapping) or 
[combining](#combining-arbitraries) actions.


### Static `Arbitraries` methods 

The starting point for generation usually is a static method call on class 
[`Arbitraries`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html). 

#### Generate values yourself

- [`Arbitrary<T> randomValue(Function<Random, T> generator)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#randomValue-java.util.function.Function-): 
  Take a `random` instance and create an object from it.
  Those values cannot be [shrunk](#result-shrinking), though.
  
  Generating prime numbers might look like that:
  ```java
  @Provide
  Arbitrary<Integer> primesGenerated() {
      return Arbitraries.randomValue(random -> generatePrime(random));
  }

  private Integer generatePrime(Random random) {
      int candidate;
      do {
          candidate = random.nextInt(10000) + 2;
      } while (!isPrime(candidate));
      return candidate;
  }
  ```

- [`Arbitrary<T> fromGenerator(RandomGenerator<T> generator)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#fromGenerator-net.jqwik.api.RandomGenerator-):
  If the number of _tries_ influences value generation or if you want 
  to allow for [shrinking](#result-shrinking) you have to provide 
  your own `RandomGenerator` implementation. 
  
#### Select values randomly

- [`Arbitrary<U> of(U... values)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#of-U...-):
  Choose randomly from a list of values. Shrink towards the first one.
  
- [`Arbitrary<T> samples(T... samples)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#samples-T...-):
  Go through samples from first to last. Shrink towards the first sample.
  
  If instead you want to _add_ samples to an existing arbitrary you'd rather use 
  [`Arbitrary.withSamples(T... samples)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#withSamples-T...-).
  The following arbitrary:
  
  ```java
  @Provide 
  Arbitrary<Integer> integersWithPrimes() {
	  return Arbitraries.integers(- 1000, 1000).withSamples(2,3,5,7,11,13,17);
  }
  ```
  
  will first generate the 7 enumerated prime numbers and only then generate random 
  integers between -1000 and +1000.
  
- [`Arbitrary<T> of(Class<T  extends Enum> enumClass)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#of-java.lang.Class-):
  Choose randomly from all values of an `enum`. Shrink towards first enum value.

#### Select randomly with Weights

If you have a set of values to choose from with weighted probabilities, use 
[`Arbitraries.frequency(...)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#frequency-net.jqwik.api.Tuple.Tuple2...-):

```java
@Property
void abcdWithFrequencies(@ForAll("abcdWeighted") String aString) {
    Statistics.collect(aString);
}

@Provide
Arbitrary<String> abcdWeighted() {
    return Arbitraries.frequency(
        Tuple.of(1, "a"),
        Tuple.of(5, "b"),
        Tuple.of(10, "c"),
        Tuple.of(20, "d")
    );
}
```

The first value of the tuple specifies the frequency of a particular value in relation to the
sum of all frequencies. In 
[the given example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/ChoosingExamples.java#L17)
the sum is 36, thus `"a"` will be generated with a probability of `1/36` 
whereas `"d"` has a generation probability of `20/36` (= `5/9`).

Shrinking moves towards the start of the frequency list.

#### Integers

- [`ByteArbitrary bytes()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#bytes--)
- [`ShortArbitrary shorts()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#shorts--)
- [`IntegerArbitrary integers()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#integers--)
- [`LongArbitrary longs()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#longs--)
- [`BigIntegerArbitrary bigIntegers()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#bigIntegers--)

#### Decimals

- [`FloatArbitrary floats()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#floats--)
- [`DoubleArbitrary doubles()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#doubles--)
- [`BigDecimalArbitrary bigDecimals()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#bigDecimals--)

#### Characters and Strings

- [`StringArbitrary strings()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#strings--)
- [`CharacterArbitrary chars()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#chars--)

#### java.util.Random

- [`Arbitrary<Random> randoms()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#randoms--): 
  Random instances will never be shrunk

#### Constants

- [`Arbitrary<T> constant(T value)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#constant-T-): 
  In each try use the same unshrinkable `value` of type `T`.

#### Create

- [`Arbitrary<T> create(Supplier<T> supplier)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#constant-java.util.function.Supplier-): 
  In each try use a new unshrinkable instance of type `T` using `supplier` to freshly create it.

#### Shuffling Permutations

- [`Arbitrary<List<T>> shuffle(T ... values)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#shuffle-T...-):
  Return unshrinkable permutations of the `values` handed in.

- [`Arbitrary<List<T>> shuffle(List<T> values)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#shuffle-java.util.List-):
  Return unshrinkable permutations of the `values` handed in.

#### Default Types

- [`Arbitrary<T> defaultFor(Class<T> type, Class<?> ... parameterTypes)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#defaultFor-java.lang.Class-java.lang.Class...-): 
  Return the default arbitrary available for type `type` [if one is provided](#providing-default-arbitraries)
  by default. For parameterized types you can also specify the parameter types. 
  
  Keep in mind, though, that the parameter types are lost in the type signature and therefore
  cannot be used in the respective [`@ForAll`](/docs/1.1.4/javadoc/net/jqwik/api/ForAll.html) property method parameter. Raw types and wildcards, 
  however, match; thus the following example will work:
  
  ```java
  @Property
  boolean listWithWildcard(@ForAll("stringLists") List<?> stringList) {
      return stringList.isEmpty() || stringList.get(0) instanceof String;
  }
   
  @Provide
  Arbitrary<List> stringLists() {
      return Arbitraries.defaultFor(List.class, String.class);
  }
  ```

### Collections, Streams, Arrays and Optional

Generating types who have generic type parameters, requires to start with 
an `Arbitrary` instance for the generic type. You can create the corresponding collection arbitrary from there:

- [`Arbitrary.list()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#list--)
- [`Arbitrary.set()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#set--)
- [`Arbitrary.streamOf()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#stream--)
- [`Arbitrary.array(Class<A> arrayClass)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#array-java.lang.Class-)
- [`Arbitrary.optional()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#optional--)

### Collecting Values in a List

If you do not want any random combination of values in your list - as 
can be done with `Arbitrary.list()` - you have the possibility to collect random values
in a list until a certain condition is fulfilled. 
[`Arbitrary.collect(Predicate condition)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#collect-java.util.function.Predicate-)
is what you need in those cases.

Imagine you need a list of integers the sum of which should be at least `1000`.
Here's how you could do that:

```java
Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
Arbitrary<List<Integer>> collected = integers.collect(list -> sum(list) >= 1000);
```

### Fluent Configuration Interfaces

Most specialized arbitrary interfaces provide special methods to configure things
like size, length, boundaries etc. Have a look at the Java doc for the following types:

- [BigDecimalArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/BigDecimalArbitrary.html)
- [BigIntegerArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/BigIntegerArbitrary.html)
- [ByteArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/ByteArbitrary.html)
- [CharacterArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/CharacterArbitrary.html)
- [DoubleArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/DoubleArbitrary.html)
- [FloatArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/FloatArbitrary.html)
- [IntegerArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/IntegerArbitrary.html)
- [LongArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/LongArbitrary.html)
- [ShortArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/ShortArbitrary.html)
- [SizableArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/SizableArbitrary.html)
- [StringArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/StringArbitrary.html)


Here are a 
[two examples](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/FluentConfigurationExamples.java)
to give you a hint of what you can do:

```java
@Provide
Arbitrary<String> alphaNumericStringsWithMinLength5() {
    return Arbitraries.strings().ofMinLength(5).alpha().numeric();
}

@Provide
Arbitrary<List<Integer>> fixedSizedListOfPositiveIntegers() {
    return Arbitraries.integers().greaterOrEqual(0).list().ofSize(17);
}
```

### Generate `null` values

Predefined generators will never create `null` values. If you want to allow that,
call [`Arbitrary.injectNull(double probability)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#injectNull-double-). 
The following provider method creates an arbitrary that will return a `null` String 
in about 1 of 100 generated values.

```java
@Provide 
Arbitrary<String> stringsWithNull() {
  return Arbitraries.strings(0, 10).injectNull(0.01);
}
```

### Filtering

If you want to include only part of all the values generated by an arbitrary,
use 
[`Arbitrary.filter(Predicate<T> filterPredicate)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#filter-java.util.function.Predicate-). 
The following arbitrary will filter out all
even numbers from the stream of generated integers:

```java
@Provide 
Arbitrary<Integer> oddNumbers() {
  return Arbitraries.integers().filter(aNumber -> aNumber % 2 != 0);
}
```

Keep in mind that your filter condition should not be too restrictive. 
If the generator fails to find a suitable value after 10000 trials,
the current property will be abandoned by throwing an exception.

### Creating unique values

If you want to make sure that all the values generated by an arbitrary are unique,
use
[`Arbitrary.unique()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#unique--).
The following arbitrary will generate integers between 1 and 1000 but never the same integer twice:

```java
@Provide
Arbitrary<Integer> oddNumbers() {
  return Arbitraries.integers().between(1, 1000).unique();
}
```

This means that a maximum of 1000 values can be generated. If the generator fails
to find a yet unseen value after 10000 trials,
the current property will be abandoned by throwing an exception.

### Mapping

Sometimes it's easier to start with an existing arbitrary and use its generated values to
build other objects from them. In that case, use 
[`Arbitrary.map(Function<T, U> mapper)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#map-java.util.function.Function-).
The following example uses generated integers to create numerical Strings: 

```java
@Provide 
Arbitrary<String> fiveDigitStrings() {
  return Arbitraries.integers(10000, 99999).map(aNumber -> String.valueOf(aNumber));
}
```

You could generate the same kind of values by constraining and filtering a generated String.
However, the [shrinking](#result-shrinking) target would probably be different. In the example above, shrinking
will move towards the lowest allowed number, that is `10000`.


### Flat Mapping

Similar as in the case of `Arbitrary.map(..)` there are situations in which you want to use
a generated value in order to create another Arbitrary from it. Sounds complicated?
Have a look at the 
[following example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/FlatMappingExamples.java#L26):

```java
@Property
boolean fixedSizedStrings(@ForAll("listsOfEqualSizedStrings")List<String> lists) {
    return lists.stream().distinct().count() == 1;
}

@Provide
Arbitrary<List<String>> listsOfEqualSizedStrings() {
    Arbitrary<Integer> integers2to5 = Arbitraries.integers().between(2, 5);
    return integers2to5.flatMap(stringSize -> {
        Arbitrary<String> strings = Arbitraries.strings() //
                .withCharRange('a', 'z') //
                .ofMinLength(stringSize).ofMaxLength(stringSize);
        return strings.list();
    });
}
```
The provider method will create random lists of strings, but in each list the size of the contained strings
will always be the same - between 2 and 5.

### Flat Mapping with Tuple Types

In the example above you used a generated value in order to create another arbitrary.
In those situations you often want to also provide the original values to your property test.

Imagine, for instance, that you'd like to test properties of `String.substring(begin, end)`.
To randomize the method call, you not only need a string but also the `begin` and `end` indices.
However, both have dependencies:
- `end` must not be larger than the string size
- `begin` must not be larger than `end`
You can make _jqwik_ create all three values by using 
[`flatMap`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#flatMap-java.util.function.Function-) 
combined with a tuple type 
[like this](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/FlatMappingExamples.java#L32):


```java
@Property
void substringLength(@ForAll("stringWithBeginEnd") Tuple3<String, Integer, Integer> stringBeginEnd) {
    String aString = stringBeginEnd.get1();
    int begin = stringBeginEnd.get2();
    int end = stringBeginEnd.get3();
    Assertions.assertThat(aString.substring(begin, end).length())
        .isEqualTo(end - begin);
}

@Provide
Arbitrary<Tuple3<String, Integer, Integer>> stringWithBeginEnd() {
    Arbitrary<String> stringArbitrary = Arbitraries.strings() //
            .withCharRange('a', 'z') //
            .ofMinLength(2).ofMaxLength(20);
    return stringArbitrary //
            .flatMap(aString -> Arbitraries.integers().between(0, aString.length()) //
                    .flatMap(end -> Arbitraries.integers().between(0, end) //
                            .map(begin -> Tuple.of(aString, begin, end))));
}
```

Mind the nested flat mapping, which is an aesthetic nuisance but nevertheless
very useful. 


### Randomly Choosing among Arbitraries

If you have several arbitraries of the same type, you can create a new arbitrary of
the same type which will choose randomly one of those arbitraries before generating
a value:

```java
@Property
boolean intsAreCreatedFromOneOfThreeArbitraries(@ForAll("oneOfThree") int anInt) {
    String classifier = anInt < -1000 ? "below" : anInt > 1000 ? "above" : "one";
    Statistics.collect(classifier);
    
    return anInt < -1000 //
            || Math.abs(anInt) == 1 //
            || anInt > 1000;
}

@Provide
Arbitrary<Integer> oneOfThree() {
    IntegerArbitrary below1000 = Arbitraries.integers().between(-2000, -1001);
    IntegerArbitrary above1000 = Arbitraries.integers().between(1001, 2000);
    Arbitrary<Integer> oneOrMinusOne = Arbitraries.samples(-1, 1);
    
    return Arbitraries.oneOf(below1000, above1000, oneOrMinusOne);
}
```

[In this example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/OneOfExamples.java)
the statistics should also give you an equal distribution between
the three types of integers.

If you don't want to choose with equal probability - but with differing frequency -
you can do that in a similar way:

```java
@Property(tries = 100)
@Report(Reporting.GENERATED)
boolean intsAreCreatedFromOneOfThreeArbitraries(@ForAll("oneOfThree") int anInt) {
    return anInt < -1000 //
               || Math.abs(anInt) == 1 //
               || anInt > 1000;
}

@Provide
Arbitrary<Integer> oneOfThree() {
    IntegerArbitrary below1000 = Arbitraries.integers().between(-1050, -1001);
    IntegerArbitrary above1000 = Arbitraries.integers().between(1001, 1050);
    Arbitrary<Integer> oneOrMinusOne = Arbitraries.samples(-1, 1);

    return Arbitraries.frequencyOf(
        Tuple.of(1, below1000),
        Tuple.of(3, above1000),
        Tuple.of(6, oneOrMinusOne)
    );
}
```

### Combining Arbitraries

Sometimes just mapping a single stream of generated values is not enough to generate
a more complicated domain object. In those cases you can combine several arbitraries to
a single result arbitrary using 
[`Combinators.combine()`](/docs/1.1.4/javadoc/net/jqwik/api/Combinators.html#combine-net.jqwik.api.Arbitrary-net.jqwik.api.Arbitrary-) 
with up to eight arbitraries. 
[Create an issue on github](https://github.com/jlink/jqwik/issues) if you need more than eight. 

[The following example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/MappingAndCombinatorExamples.java#L25)
generates `Person` instances from three arbitraries as inputs.

```java
@Property
void validPeopleHaveIDs(@ForAll("validPeople") Person aPerson) {
    Assertions.assertThat(aPerson.getID()).contains("-");
    Assertions.assertThat(aPerson.getID().length()).isBetween(5, 24);
}

@Provide
Arbitrary<Person> validPeople() {
    Arbitrary<String> names = Arbitraries.strings().withCharRange('a', 'z')
        .ofMinLength(3).ofMaxLength(21);
    Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);
    return Combinators.combine(names, ages)
        .as((name, age) -> new Person(name, age));
}

class Person {
    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getID() {
        return name + "-" + age;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, age);
    }
}
```

The property should fail, thereby shrinking the falsified Person instance to
`[aaaaaaaaaaaaaaaaaaaaa:100]`.

The `Combinators.combine` method accepts up to 8 parameters of type Arbitrary.
If you need more you have a few options:

- Consider to group some parameters into an object of their own and change your design
- Generate inbetween arbitraries e.g. of type `Tuple` and combine those in another step
- Introduce a build for your domain object and combine them 
  [in this way](#combining-arbitraries-with-builder)


### Combining Arbitraries with Builder

There's an alternative way to combine arbitraries to create an aggregated object
by using a builder for the aggregated object. Consider the example from
[above](#combining-arbitraries) and throw a `PersonBuilder` into the mix:

```java
static class PersonBuilder {

    private String name = "A name";
    private int age = 42;

    public PersonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public PersonBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public Person build() {
        return new Person(name, age);
    }
}
```

Then you can go about generating people in the following way:
 
```java
@Provide
Arbitrary<Person> validPeopleWithBuilder() {
    Arbitrary<String> names = 
        Arbitraries.strings().withCharRange('a', 'z').ofMinLength(2).ofMaxLength(20);
    Arbitrary<Integer> ages = Arbitraries.integers().between(0, 130);
    
    return Combinators.withBuilder(() -> new PersonBuilder())
        .use(names).in((builder, name) -> builder.withName(name))
        .use(ages).in((builder, age)-> builder.withAge(age))
        .build( builder -> builder.build());
}
```

Have a look at 
[Combinators.withBuilder(Supplier)](/docs/1.1.4/javadoc/net/jqwik/api/Combinators.html#withBuilder-java.util.function.Supplier-)
and [Combinators.withBuilder(Arbitrary)](/docs/1.1.4/javadoc/net/jqwik/api/Combinators.html#withBuilder-net.jqwik.api.Arbitrary-)
to check the API.

#### Flat Combination

If generating domain values requires to use several generated values to be used
in generating another one, there's the combination of flat mapping and combining:

```java
@Property
boolean fullNameHasTwoParts(@ForAll("fullName") String aName) {
    return aName.split(" ").length == 2;
}

@Provide
Arbitrary<String> fullName() {
    IntegerArbitrary firstNameLength = Arbitraries.integers().between(2, 10);
    IntegerArbitrary lastNameLength = Arbitraries.integers().between(2, 10);
    return Combinators.combine(firstNameLength, lastNameLength).flatAs( (fLength, lLength) -> {
        Arbitrary<String> firstName = Arbitraries.strings().alpha().ofLength(fLength);
        Arbitrary<String> lastName = Arbitraries.strings().alpha().ofLength(fLength);
        return Combinators.combine(firstName, lastName).as((f,l) -> f + " " + l);
    });
}
```

Often, however, there's an easier way to achieve the same goal which
does not require the flat combination of arbitraries:

```java
@Provide
Arbitrary<String> fullName2() {
    Arbitrary<String> firstName = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
    Arbitrary<String> lastName = Arbitraries.strings().alpha().ofMinLength(2).ofMaxLength(10);
    return Combinators.combine(firstName, lastName).as((f, l) -> f + " " + l);
}
```

This is not only easier to understand but it usually improves shrinking.

### Fix an Arbitrary's `genSize`

Some generators (e.g. most number generators) are sensitive to the 
`genSize` value that is used when creating them. 
The default value for `genSize` is the number of tries configured for the property
they are used in. If there is a need to influence the behaviour of generators
you can do so by using 
[`Arbitrary.fixGenSize(int)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#fixGenSize-int-)..


### Generating all possible values

There are a few cases when you don't want to generate individual values from an
arbitrary but use all possible values to construct another arbitrary. This can be achieved through
[`Arbitrary.allValues()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#allValues--).

Return type is `Optional<Stream<T>>` because _jqwik_ can only perform this task if
[exhaustive generation](#exhaustive-generation) is doable.


### Iterating through all possible values

You can also use an arbitrary to iterate through all values it specifies.
Use
[`Arbitrary.forEachValue(Consumer action)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitrary.html#forEachValue-java.util.function.Consumer-).
for that purpose. This only works when [exhaustive generation](#exhaustive-generation) is possible.
In other cases the attempt to iterate will result in an exception.

This is typically useful when your test requires to assert some fact for all
values of a given (sub)set of objects. Here's a contrived example:

```java
@Property
void canPressAnyKeyOnKeyboard(@ForAll Keyboard keyboard, @ForAll Key key) {
    keyboard.press(key);
    assertThat(keyboard.isPressed(key));

    Arbitrary<Key> unpressedKeys = Arbitraries.of(keyboard.allKeys()).filter(k -> !k.equals(key));
    unpressedKeys.forEachValue(k -> assertThat(keyboard.isPressed(k)).isFalse());
}
```

In this example a simple for loop over `allKeys()` would also work. In more complicated scenarios
_jqwik_ will do all the combinations and filtering for you.


## Recursive Arbitraries

Sometimes it seems like a good idea to compose arbitraries and thereby
recursively calling an arbitrary creation method. Generating recursive data types
is one application field but you can also use it for other stuff. 

### Probabilistic Recursion

Look at the 
[following example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/RecursiveExamples.java)
which generates sentences by recursively adding words to a sentence:

```java
@Property
boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
    return aSentence.endsWith(".");
}

@Provide
Arbitrary<String> sentences() {
    Arbitrary<String> sentence = Combinators.combine( //
        Arbitraries.lazy(this::sentences), //
        word() //
    ).as((s, w) -> w + " " + s);
    return Arbitraries.oneOf( //
        word().map(w -> w + "."), //
        sentence, //
        sentence, //
        sentence //
    );
}

private StringArbitrary word() {
    return Arbitraries.strings().alpha().ofLength(5);
}
``` 

There are two things to which you must pay attention:

- Use [`Arbitraries.lazy(Supplier<Arbitrary<T>>)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#lazy-java.util.function.Supplier-) 
  to wrap the recursive call itself. 
  Otherwise _jqwik_'s attempt to build the arbitrary will quickly result in a stack overflow.
- Every recursion needs one or more base cases in order to stop recursion at some point. 
  Base cases must have a high enough probability, 
  otherwise a stack overflow will get you during value generation.
  
### Deterministic Recursion

An alternative to the non-deterministic recursion shown above, is to use classical
recursion with a counter to determine the base case. If you then use an arbitrary value
for the counter, the generated sentences will be very similar, and there is _no need_
for using `Arbitraries.lazy()` at all:

```java
@Property(tries = 10)
boolean sentencesEndWithAPoint(@ForAll("deterministic") String aSentence) {
    return aSentence.endsWith(".");
}

@Provide
Arbitrary<String> deterministic() {
    Arbitrary<Integer> length = Arbitraries.integers().between(0, 10);
    Arbitrary<String> lastWord = word().map(w -> w + ".");
    return length.flatMap(l -> deterministic(l, lastWord));
}

@Provide
Arbitrary<String> deterministic(int length, Arbitrary<String> sentence) {
    if (length == 0) {
        return sentence;
    }
    Arbitrary<String> more = Combinators.combine(word(), sentence).as((w, s) -> w + " " + s);
    return deterministic(length - 1, more);
}
```

### Deterministic Recursion with `recursive()`

To further simplify this _jqwik_ provides a helper function:
[`Arbitraries.recursive(...)`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#recursive-java.util.function.Supplier-java.util.function.Function-int-).
Using that further simplifies the example:

```java
@Property(tries = 10)
boolean sentencesEndWithAPoint(@ForAll("deterministic") String aSentence) {
    return aSentence.endsWith(".");
}

@Provide
Arbitrary<String> deterministic() {
    Arbitrary<Integer> length = Arbitraries.integers().between(0, 10);
    Arbitrary<String> lastWord = word().map(w -> w + ".");

    return length.flatMap(l -> Arbitraries.recursive(() -> lastWord, s -> prependWord(s), l));
}

private Arbitrary<String> prependWord(Arbitrary<String> sentence) {
    return Combinators.combine(word(), sentence).as((w, s) -> w + " " + s);
}
```



## Contract Tests

When you combine type variables with properties defined in superclasses or interfaces
you can do some kind of _contract testing_. That means that you specify
the properties in a generically typed interface and specify the concrete class to
instantiate in a test container implementing the interface.

The following example was influence by a similar feature in
[junit-quickcheck](http://pholser.github.io/junit-quickcheck/site/0.8/usage/contract-tests.html).
Here's the contract:

```java
interface ComparatorContract<T> {
	Comparator<T> subject();

	@Property
	default void symmetry(@ForAll("anyT") T x, @ForAll("anyT") T y) {
		Comparator<T> subject = subject();

		Assertions.assertThat(signum(subject.compare(x, y))).isEqualTo(-signum(subject.compare(y, x)));
	}

	@Provide
	Arbitrary<T> anyT();
}
```

And here's the concrete test container that can be run to execute
the property with generated Strings:

```java
class StringCaseInsensitiveProperties implements ComparatorContract<String> {

	@Override public Comparator<String> subject() {
		return String::compareToIgnoreCase;
	}

	@Override
	@Provide
	public Arbitrary<String> anyT() {
		return Arbitraries.strings().alpha().ofMaxLength(20);
	}
}
```

What we can see here is that _jqwik_ is able to figure out the concrete
type of type variables when they are used in subtypes that fill in
the variables.


## Stateful Testing

Despite its bad reputation _state_ is an important concept in object-oriented languages like Java.
We often have to deal with stateful objects or components whose state can be changed through methods. 

Thinking in a more formal way we can look at those objects as _state machines_ and the methods as
_actions_ that move the object from one state to another. Some actions have preconditions to constrain
when they can be invoked and some objects have invariants that should never be violated regardless
of the sequence of performed actions.

To make this abstract concept concrete, let's look at a 
[simple stack implementation](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/stateful/mystack/MyStringStack.java):

```java
public class MyStringStack {
	public void push(String element) { ... }
	public String pop() { ... }
	public void clear() { ... }
	public boolean isEmpty() { ... }
	public int size() { ... }
	public String top() { ... }
}
```

### Specify Actions

We can see at least three _actions_ with their preconditions and expected state changes:

- [`Push`](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/stateful/mystack/PushAction.java):
  Push a string onto the stack. The string should be on top afterwards and the size
  should have increased by 1.
  
  ```java
  import net.jqwik.api.stateful.*;
  import org.assertj.core.api.*;
  
  class PushAction implements Action<MyStringStack> {
  
  	private final String element;
  
  	PushAction(String element) {
  		this.element = element;
  	}
  
  	@Override
  	public MyStringStack run(MyStringStack model) {
  		int sizeBefore = model.size();
  		model.push(element);
  		Assertions.assertThat(model.isEmpty()).isFalse();
  		Assertions.assertThat(model.size()).isEqualTo(sizeBefore + 1);
  		return model;
  	}
  
  	@Override
  	public String toString() { return String.format("push(%s)", element); }
  }
  ``` 

- [`Pop`](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/stateful/mystack/PopAction.java):
  If (and only if) the stack is not empty, pop the element on top off the stack. 
  The size of the stack should have decreased by 1.
  
  ```java
  class PopAction implements Action<MyStringStack> {
    
        @Override
        public boolean precondition(MyStringStack model) {
            return !model.isEmpty();
        }
    
        @Override
        public MyStringStack run(MyStringStack model) {
            int sizeBefore = model.size();
            String topBefore = model.top();
    
            String popped = model.pop();
            Assertions.assertThat(popped).isEqualTo(topBefore);
            Assertions.assertThat(model.size()).isEqualTo(sizeBefore - 1);
            return model;
        }
    
        @Override
        public String toString() { return "pop"; }
  }
  ``` 

- [`Clear`](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/stateful/mystack/ClearAction.java):
  Remove all elements from the stack which should be empty afterwards.
  
  ```java
  class ClearAction implements Action<MyStringStack> {

        @Override
        public MyStringStack run(MyStringStack model) {
            model.clear();
            Assertions.assertThat(model.isEmpty()).isTrue();
            return model;
        }
    
        @Override
        public String toString() { return "clear"; }
  }
  ``` 

### Check Postconditions

The fundamental property that _jqwik_ should try to falsify is:

    For any valid sequence of actions all required state changes
    (aka postconditions) should be fulfilled.
    
We can formulate that quite easily as a 
[_jqwik_ property](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/stateful/mystack/MyStringStackProperties.java):

```java
class MyStringStackProperties {

	@Property
	void checkMyStack(@ForAll("sequences") ActionSequence<MyStringStack> actions) {
		actions.run(new MyStringStack());
	}

	@Provide
	Arbitrary<ActionSequence<MyStringStack>> sequences() {
		return Arbitraries.sequences(Arbitraries.oneOf(push(), pop(), clear()));
	}

	private Arbitrary<Action<MyStringStack>> push() {
		return Arbitraries.strings().alpha().ofLength(5).map(PushAction::new);
	}

	private Arbitrary<Action<MyStringStack>> clear() {
		return Arbitraries.constant(new ClearAction());
	}

	private Arbitrary<Action<MyStringStack>> pop() {
		return Arbitraries.constant(new PopAction());
	}
}
```

The interesting API elements are 
- [`ActionSequence`](/docs/1.1.4/javadoc/net/jqwik/api/stateful/ActionSequence.html):
  A generic collection type especially crafted for holding and shrinking of a list of actions.
  As a convenience it will apply the actions to a model when you call `run(model)`.
  
- [`Arbitraries.sequences()`](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#sequences-net.jqwik.api.Arbitrary-):
  This method will create the arbitrary for generating an `ActionSequence` given the
  arbitrary for generating actions.

To give _jqwik_ something to falsify, we broke the implementation of `clear()` so that
it won't clear everything if there are more than two elements on the stack:

```java
public void clear() {
    // Wrong implementation to provoke falsification for stacks with more than 2 elements
    if (elements.size() > 2) {
        elements.remove(0);
    } else {
        elements.clear();
    }
}
```

Running the property should now produce a result similar to:

```
org.opentest4j.AssertionFailedError: Run failed after following actions:
    push(AAAAA)
    push(AAAAA)
    push(AAAAA)
    clear
  final state: ["AAAAA", "AAAAA"]
```

### Number of actions

_jqwik_ will vary the number of generated actions according to the number
of `tries` of your property. For the default of 1000 tries a sequence will
have up to 32 actions. If need be you can specify the number of actions
to generate using either the fluent interface or the `@Size` annotation:

```java
@Property
// check stack with sequences of 7 actions:
void checkMyStack(@ForAll("sequences") @Size(max = 7) ActionSequence<MyStringStack> actions) {
    actions.run(new MyStringStack());
}
```

The minimum number of generated actions in a sequence is 1 since checking
an empty sequence does not make sense.

### Check Invariants

We can also add invariants to our sequence checking property:

```java
@Property
void checkMyStackWithInvariant(@ForAll("sequences") ActionSequence<MyStringStack> actions) {
    actions
        .withInvariant(stack -> Assertions.assertThat(stack.size()).isGreaterThanOrEqualTo(0))
        .withInvariant(stack -> Assertions.assertThat(stack.size()).isLessThan(5))
        .run(new MyStringStack());
}
```

If we first fix the bug in `MyStringStack.clear()` our property should eventually fail 
with the following result:

```
org.opentest4j.AssertionFailedError: Run failed after following actions:
    push(AAAAA)
    push(AAAAA)
    push(AAAAA)
    push(AAAAA)
    push(AAAAA)
  final state: ["AAAAA", "AAAAA", "AAAAA", "AAAAA", "AAAAA"]
```


## Assumptions

If you want to constrain the set of generated values in a way that embraces
more than one parameter, [filtering](#filtering) does not work. What you
can do instead is putting one or more assumptions at the beginning of your property.

[The following property](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/AssumptionExamples.java)
works only on strings that are not equal:

```java
@Property
boolean comparingUnequalStrings( //
        @ForAll @StringLength(min = 1, max = 10) String string1, //
        @ForAll @StringLength(min = 1, max = 10) String string2 //
) {
    Assume.that(!string1.equals(string2));

    return string1.compareTo(string2) != 0;
}
```

This is a reasonable use of 
[`Assume.that(boolean condition)`](/docs/1.1.4/javadoc/net/jqwik/api/Assume.html#that-boolean-) 
because most generated value sets will pass through.

Have a look at a seemingly similar example:

```java
@Property
boolean findingContainedStrings( //
        @ForAll @StringLength(min = 1, max = 10) String container, //
        @ForAll @StringLength(min = 1, max = 5) String contained //
) {
    Assume.that(container.contains(contained));

    return container.indexOf(contained) >= 0;
}
```

Despite the fact that the property condition itself is correct, the property will most likely
fail with the following message:

```
org.opentest4j.AssertionFailedError: 
    Property [findingContainedStrings] exhausted after [1000] tries and [980] rejections

tries = 1000 
checks = 20 
seed = 1066117555581106850
```

The problem is that - given a random generation of two strings - only in very few cases
one string will be contained in the other. _jqwik_ will report a property as `exhausted`
if the ratio between generated and accepted parameters is higher than 5. You can change
the maximum discard ratio by specifying a parameter `maxDiscardRatio` in the 
[`@Property`](/docs/1.1.4/javadoc/net/jqwik/api/Property.html) annotation.
That's why changing to `@Property(maxDiscardRatio = 100)` in the previous example 
will probably result in a successful property run, even though only a handful 
cases - of 1000 generated - will actually be checked.

In many cases turning up the accepted discard ration is a bad idea. With some creativity
we can often avoid the problem by generating out test data a bit differently. 
Look at this variant of the above property, which also uses 
[`Assume.that()`](/docs/1.1.4/javadoc/net/jqwik/api/Assume.html#that-boolean-)
but with a much lower discard ratio:

```java
@Property
boolean findingContainedStrings_variant( //
        @ForAll @StringLength(min = 5, max = 10) String container, //
        @ForAll @IntRange(min = 1, max = 5) int length, //
        @ForAll @IntRange(min = 0, max = 9) int startIndex //
) {
    Assume.that((length + startIndex) <= container.length());

    String contained = container.substring(startIndex, startIndex + length);
    return container.indexOf(contained) >= 0;
}
```

## Result Shrinking

If a property could be falsified with a generated set of values, _jqwik_ will
try to "shrink" this sample in order to find a "smaller" sample that also falsifies the property.

Try this property:

```java
@Property
boolean stringShouldBeShrunkToAA(@ForAll @AlphaChars String aString) {
    return aString.length() > 5 || aString.length() < 2;
}
```

The test run result should look something like:

```
AssertionFailedError: Property [stringShouldBeShrunkToAA] falsified with sample ["AA"]

tries = 38 
checks = 38 
seed = -633877439388930932 
sample = ["AA"]
original-sample ["LVtyB"] 
```

In this case the _originalSample_ could be any string between 2 and 5 chars, whereas the final _sample_
should be exactly `AA` since this is the shortest failing string and `A` has the lowest numeric value
of all allowed characters.

### Integrated Shrinking

_jqwik_'s shrinking approach is called _integrated shrinking_, as opposed to _type-based shrinking_
which most property-based testing tools use.
The general idea and its advantages are explained 
[here](http://hypothesis.works/articles/integrated-shrinking/).

Consider a somewhat 
[more complicated example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/ShrinkingExamples.java#L15):

```java
@Property
boolean shrinkingCanTakeLong(@ForAll("first") String first, @ForAll("second") String second) {
    String aString = first + second;
    return aString.length() > 5 || aString.length() < 2;
}

@Provide
Arbitrary<String> first() {
    return Arbitraries.strings()
        .withCharRange('a', 'z')
        .ofMinLength(1).ofMaxLength(10)
        .filter(string -> string.endsWith("h"));
}

@Provide
Arbitrary<String> second() {
    return Arbitraries.strings()
        .withCharRange('0', '9')
        .ofMinLength(0).ofMaxLength(10)
        .filter(string -> string.length() >= 1);
}
```

Shrinking still works, although there's quite a bit of filtering and string concatenation happening:
```
AssertionFailedError: Property [shrinkingCanTakeLong] falsified with sample ["h", "0"]

checks = 20 
tries = 20 
seed = -5596810132893895291 
sample = ["h", "0"]
original-sample ["gh", "774"] 
```

### Switch Shrinking Off

Sometimes shrinking takes a really long time or won't finish at all (usually a _jqwik_ bug!). 
In those cases you can switch shrinking off for an individual property:

```java
@Property(shrinking = ShrinkingMode.OFF)
void aPropertyWithLongShrinkingTimes(
	@ForAll List<Set<String>> list1, 
	@ForAll List<Set<String>> list2
) {	... }
```

### Switch Shrinking to Full Mode

Sometimes you can find a message like

```
shrinking bound reached =
    steps : 1000
    original value : [blah blah blah ...]
    shrunk value   : [bl bl bl ...]
```

in your testrun's output.
This happens in rare cases when _jqwik_ has not found the end of its search for
simpler falsifiable values after 1000 iterations. In those cases you
can try

```java
@Property(shrinking = ShrinkingMode.FULL)
```

to tell _jqwik_ to go all the way, even if it takes a million steps,
even if it never ends...

### Change the Shrinking Target

By default shrinking of numbers will move towards zero (0). 
If zero is outside the bounds of generation the closest number to zero 
- either the min or max value - is used as a target for shrinking.
There are cases, however, when you'd like _jqwik_ to choose a different 
shrinking target, usually when the default value of a number is not 0. 

Consider generating signals with a standard frequency of 50 hz that can vary by
plus/minus 5 hz. If possible, shrinking of falsified scenarios should move
towards the standard frequency. Here's how the provider method might look:

```java
@Provide
Arbitrary<List<Signal>> signals() {
	Arbitrary<Long> frequencies = 
	    Arbitraries
            .longs()
            .between(45, 55)
            .shrinkTowards(50);

	return frequencies.map(f -> Signal.withFrequency(f)).list().ofMaxSize(1000);
}
```

Currently shrinking targets are only supported for integral numbers, i.e.,
bytes, shorts, integers, longs and BigIntegers.



## Collecting and Reporting Statistics

In many situations you'd like to know if _jqwik_ will really generate
the kind of values you expect and if the frequency and distribution of
certain value classes meets your testing needs. 
[`Statistics.collect()`](/docs/1.1.4/javadoc/net/jqwik/api/Statistics.html#collect-java.lang.Object...-)
is made for this exact purpose.

In the most simple case you'd like to know how often a certain value
is being generated:

```java
@Property
void simpleStats(@ForAll RoundingMode mode) {
    Statistics.collect(mode);
}
```

will create an output similar to that:

```
statistics for [MyTest:simpleStats] = 
     UNNECESSARY : 15 %
     DOWN        : 14 %
     FLOOR       : 13 %
     UP          : 13 %
     HALF_DOWN   : 13 %
     HALF_EVEN   : 12 %
     CEILING     : 11 %
     HALF_UP     : 11 %
```

More typical is the case in which you'll classify generated values
into two or more groups:

```java
@Property
void integerStats(@ForAll int anInt) {
    Statistics.collect(anInt > 0 ? "positive" : "negative");
}
```

```
statistics for [MyTest:integerStats] = 
     negative : 52 %
     positive : 48 %
```

You can also collect the distribution in more than one category
and combine those categories:

```java
@Property
void combinedIntegerStats(@ForAll int anInt) {
    String posOrNeg = anInt > 0 ? "positive" : "negative";
    String evenOrOdd = anInt % 2 == 0 ? "even" : "odd";
    String bigOrSmall = Math.abs(anInt) > 50 ? "big" : "small";
    Statistics.collect(posOrNeg, evenOrOdd, bigOrSmall);
}
```

```
statistics for [MyTest:combinedIntegerStats] = 
     positive odd big    : 23 %
     negative even big   : 22 %
     positive even big   : 22 %
     negative odd big    : 21 %
     positive odd small  : 4 %
     negative odd small  : 3 %
     negative even small : 3 %
     positive even small : 2 %
```

And, of course, you can combine different generated parameters into
one statistical group:

```java
@Property
void twoParameterStats(
    @ForAll @Size(min = 1, max = 10) List<Integer> aList, //
    @ForAll @IntRange(min = 0, max = 10) int index //
) {
    Statistics.collect(aList.size() > index ? "index within size" : null);
}
```

```
collected statistics = 
     index within size : 48 %
```

As you can see, collected `null` values are not being reported.

[Here](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/StatisticsExamples.java)
are a couple of examples to try out.

## Providing Default Arbitraries

Sometimes you want to use a certain, self-made `Arbitrary` for one of your own domain
classes, in all of your properties, and without having to add `@Provide` method
to all test classes. _jqwik_ enables this feature by using 
Java’s `java.util.ServiceLoader` mechanism. All you have to do is:

- Implement the interface [`ArbitraryProvider`](/docs/1.1.4/javadoc/net/jqwik/api/providers/ArbitraryProvider.html).<br/>
  The implementing class _must_ have a default constructor without parameters.
- Register the implementation class in file

  ```
  META-INF/services/net.jqwik.api.providers.ArbitraryProvider
  ```

_jqwik_ will then add an instance of your arbitrary provider into the list of
its default providers. Those default providers are considered for every test parameter annotated 
with [`@ForAll`](/docs/1.1.4/javadoc/net/jqwik/api/ForAll.html) that has no explicit `value`.
By using this mechanism you can also replace the default providers
packaged into _jqwik_.

### Simple Arbitrary Providers

A simple provider is one that delivers arbitraries for types without type variables.
Consider the class [`Money`](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/defaultprovider/Money.java):

```java
public class Money {
	public BigDecimal getAmount() {
		return amount;
	}

	public String getCurrency() {
		return currency;
	}

	public Money(BigDecimal amount, String currency) {
		this.amount = amount;
		this.currency = currency;
	}

	public Money times(int factor) {
		return new Money(amount.multiply(new BigDecimal(factor)), currency);
	}
}
``` 

If you register the following class
[`MoneyArbitraryProvider`](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/defaultprovider/MoneyArbitraryProvider.java):

```java
package my.own.provider;

public class MoneyArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Money.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Arbitrary<BigDecimal> amount = Arbitraries.bigDecimals() //
				  .between(BigDecimal.ZERO, new BigDecimal(1_000_000_000)) //
				  .ofScale(2);
		Arbitrary<String> currency = Arbitraries.of("EUR", "USD", "CHF");
		return Collections.singleton(Combinators.combine(amount, currency).as(Money::new));
	}
}
```

in file 
[`META-INF/services/net.jqwik.api.providers.ArbitraryProvider`](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/resources/META-INF/services/net.jqwik.api.providers.ArbitraryProvider)
with such an entry:

```
my.own.provider.MoneyArbitraryProvider
```

The 
[following property](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/defaultprovider/MoneyProperties.java)
will run without further ado - regardless the class you put it in:

```java
@Property
void moneyCanBeMultiplied(@ForAll Money money) {
    Money times2 = money.times(2);
    Assertions.assertThat(times2.getCurrency()).isEqualTo(money.getCurrency());
    Assertions.assertThat(times2.getAmount())
        .isEqualTo(money.getAmount().multiply(new BigDecimal(2)));
}
```

### Arbitrary Providers for Parameterized Types

Providing arbitraries for generic types requires a little bit more effort
since you have to create arbitraries for the "inner" types as well. 
Let's have a look at the default provider for `java.util.Optional<T>`:

```java
public class OptionalArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Optional.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		TypeUsage innerType = targetType.getTypeArguments().get(0);
		return subtypeProvider.apply(innerType).stream() //
			.map(Arbitrary::optional)
			.collect(Collectors.toSet());
	}
}
```

Mind that `provideFor` returns a set of potential arbitraries.
That's necessary because the `subtypeProvider` might also deliver a choice of
subtype arbitraries. Not too difficult, is it?


### Arbitrary Provider Priority

When more than one provider is suitable for a given type, _jqwik_ will randomly
choose between all available options. That's why you'll have to take additional
measures if you want to replace an already registered provider. The trick
is to override a provider's `priority()` method that returns `0` by default:

```java
public class AlternativeStringArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isAssignableFrom(String.class);
	}

	@Override
	public int priority() {
		return 1;
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		return Collections.singleton(Arbitraries.constant("A String"));
	}
}
```

If you register this class as arbitrary provider any `@ForAll String` will
be resolved to `"A String"`.

## Create your own Annotations for Arbitrary Configuration

All you can do [to constrain default parameter generation](#constraining-default-generation)
is adding another annotation to a parameter or its parameter types. What if the existing parameters
do not suffice your needs? Is there a way to enhance the set of constraint annotations? Yes, there is!

The mechanism you can plug into is similar to what you do when 
[providing your own default arbitrary providers](#providing-default-arbitraries). That means:

1. Create an implementation of an interface, in this case 
  [`ArbitraryConfigurator`](/docs/1.1.4/javadoc/net/jqwik/api/configurators/ArbitraryConfigurator.html).
2. Register the implementation using using Java’s `java.util.ServiceLoader` mechanism.

### Arbitrary Configuration Example: `@Odd`

To demonstrate the idea let's create an annotation `@Odd` which will constrain any integer
generation to only generate odd numbers. First things first, so here's 
the [`@Odd` annotation](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/Odd.java)
together with the 
[configurator implementation](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/OddConfigurator.java):

```java
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Odd {
}

public class OddConfigurator extends ArbitraryConfiguratorBase {
	public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, Odd odd) {
		return arbitrary.filter(number -> Math.abs(number % 2) == 1);
	}
}
```

Mind that the implementation uses an abstract base class - instead of the interface itself -
which simplifies implementation if you're only interested in a single annotation.

If you now 
[register the implementation](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/resources/META-INF/services/net.jqwik.api.configurators.ArbitraryConfigurator),
the [following example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/OddProperties.java)
will work:

```java
@Property
boolean oddIntegersOnly(@ForAll @Odd int aNumber) {
    return Math.abs(aNumber % 2) == 1;
}
```

There are a few catches, though:

- Currently `OddConfigurator` will accept any target type since type erasure
  will get rid of `<Integer>` in configure-method's signature at runtime.
  Therefore, using `@Odd` together with e.g. `BigInteger` will lead to a runtime
  exception. You can prevent that by explicitly accepting only some target types:

  ```java
  public class OddConfigurator extends ArbitraryConfiguratorBase {

  	@Override
  	protected boolean acceptTargetType(TypeUsage targetType) {
  		return targetType.isAssignableFrom(Integer.class);
  	}

  	public Arbitrary<Integer> configure(Arbitrary<Integer> arbitrary, Odd odd) {
  		return arbitrary.filter(number -> Math.abs(number % 2) == 1);
  	}
  }
  ```

  Alternatively, you can check for an object's type directly and use different
  filter algorithms:

  ```java
  public Arbitrary<Number> configure(Arbitrary<Number> arbitrary, Odd odd) {
      return arbitrary.filter(number -> {
          if (number instanceof Integer)
              return Math.abs((int) number % 2) == 1;
          if (number instanceof BigInteger)
              return ((BigInteger) number).remainder(BigInteger.valueOf(2))
                                          .abs().equals(BigInteger.ONE);
          return false;
      });
  }
  ```

- You can combine `@Odd` with other annotations like `@Positive` or `@Range` or another
  self-made configurator. In this case the order of configurator application might play a role,
  which can be influenced by overriding the `order()` method of a configurator.
 
## Domain and Domain Context

Until now you have seen two ways to specify which arbitraries will be created for a given parameter:

- Annotate the parameter with `@ForAll("providerMethod")`.
- [Register a global arbitrary provider](#providing-default-arbitraries)
  that will be triggered by a known parameter signature.

In many cases both approaches can be tedious to set up or require constant repetition of the same
annotation value. There's another way that allows you to collect a number of arbitrary providers
(and also arbitrary configurators) in a single place, called a `DomainContext` and tell 
a property method or container to only use providers and configurators from those domain contexts
that are explicitly stated in a `@Domain(Class<? extends DomainContext>)` annotation.

As for ways to implement domain context classes have a look at
[DomainContext](/docs/1.1.4/javadoc/net/jqwik/api/domains/DomainContext.html)
and [AbstractDomainContextBase](/docs/1.1.4/javadoc/net/jqwik/api/domains/AbstractDomainContextBase.html).


### Domain example: American Addresses

Let's say that US postal addresses play a crucial role in the software that we're developing.
That's why there are a couple of classes that represent important domain concepts: 
`Street`, `State`, `City` and `Address`. Since we have to generate instances of those classes
for our properties, we collect all arbitrary provision code in
[AmericanAddresses](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/domains/AmericanAddresses.java).
Now look at
[this example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/domains/AddressProperties.java):

```java
class AddressProperties {

	@Property
	@Domain(AmericanAddresses.class)
	void anAddressWithAStreetNumber(@ForAll Address anAddress, @ForAll int streetNumber) {
	}

	@Property
	@Domain(AmericanAddresses.class)
	void globalDomainNotPresent(@ForAll Address anAddress, @ForAll String anyString) {
	}

	@Property
	@Domain(DomainContext.Global.class)
	@Domain(AmericanAddresses.class)
	void globalDomainCanBeAdded(@ForAll Address anAddress, @ForAll String anyString) {
	}
}
```

The first two properties above will resolve their arbitraries solely through providers
specified in `AmericanAddresses`, whereas the last one also uses the default (global) context.
Since `AmericanAddresses` does not configure any arbitrary provider for `String` parameters,
property method `globalDomainNotPresent` will fail with a `CannotFindArbitraryException`.

## Generation from a Type's Interface

Some domain classes are mostly data holders. They come with constructors
or factory methods to create them and you might want to create different
instances by "just" filling the constructors' parameters with values
that are themselves generated. Using the building blocks you've seen until
now requires the use of `Arbitrary.map()` or even `Combinators.combine(...).as(...)`
to invoke the relevant constructor(s) and/or factories yourself.
There's a simpler way, though...

Consider a simple `Person` class:

```java
public class Person {

	private String name;
	private final int age;

	public Person(String name, int age) {
		if (name == null || name.trim().isEmpty())
			throw new IllegalArgumentException();
		if (age < 0 || age > 130)
			throw new IllegalArgumentException();

		this.name = name;
		this.age = age;
	}

	@Override
	public String toString() {
		return String.format("%s (%d)", name, age);
	}
}
```

A first step to use arbitrarily generated `Person` objects without having
to write a lot of _jqwik_-specific boiler plat code could look like that:

```java
@Property
void aPersonsIsAlwaysValid(@ForAll @UseType Person aPerson) {
    Assertions.assertThat(aPerson.name).isNotBlank();
    Assertions.assertThat(aPerson.age).isBetween(0, 130);
}
```

Notice the annotation `@UseType` which tells _jqwik_ to use the type
information of `Person` to generate it. By default the framework will
use all public constructors and all public, static factory methods in
the class in order to generate instances. Whenever there's an exception during
generation they will be ignored; that way you'll only get valid instances.

There are quite a few ways usage and configuration options. Have a look
at the [complete example](https://github.com/jlink/jqwik/blob/1.1.4/documentation/src/test/java/net/jqwik/docs/types/TypeArbitraryExamples.java)
and check the following api entry points:

- [UseType](/docs/1.1.4/javadoc/net/jqwik/api/constraints/UseType.html)
- [UseTypeMode](/docs/1.1.4/javadoc/net/jqwik/api/constraints/UseTypeMode.html)
- [Arbitraries.forType()](/docs/1.1.4/javadoc/net/jqwik/api/Arbitraries.html#forType-java.lang.Class-)
- [TypeArbitrary](/docs/1.1.4/javadoc/net/jqwik/api/arbitraries/TypeArbitrary.html)

## Implement your own Arbitraries and Generators

In your everyday property testing you will often get along without ever implementing
an arbitrary yourself. In cases where 
[constraining default generation through annotations](#constraining-default-generation)
does not cut it, you can use all the mechanisms to configure, (flat-)map, filter and combine
the pre-implemented arbitraries.

However, there are a few circumstances when you should think about rolling your own
implementation. The most important of which are:

- You want to expand the fluent API for configuration purposes.
- The (randomized) generation of values needs different qualities than can easily be
  derived by reusing existing arbitraries.
- Standard shrinking attempts do not come up with simple enough examples.
  
In those - and maybe a few other cases - you can implement your own arbitrary.
To get a feel for what a usable implementation looks like, you might start with
having a look at some of the internal arbitraries:

- [DefaultBigDecimalArbitrary](https://github.com/jlink/jqwik/blob/1.1.4/engine/src/main/java/net/jqwik/engine/properties/arbitraries/DefaultBigDecimalArbitrary.java)
- [DefaultStringArbitrary](https://github.com/jlink/jqwik/blob/1.1.4/engine/src/main/java/net/jqwik/engine/properties/arbitraries/DefaultStringArbitrary.java)

Under the hood, most arbitraries use `RandomGenerator`s for the final value generation. Since
[`RandomGenerator`](/docs/1.1.4/javadoc/net/jqwik/api/RandomGenerator.html) 
is a SAM type, most implementations are just lambda expression. 
Start with the methods on
[`RandomGenerators`](https://github.com/jlink/jqwik/blob/1.1.4/engine/src/main/java/net/jqwik/engine/properties/arbitraries/randomized/RandomGenerators.java)
to figure out how they work.

Since the topic is rather complicated, a detailed example will one day be published 
in a separate article...


## Exhaustive Generation

Sometimes it is possible to run a property method with all possible value combinations.
Consider the following example:

```java
@Property
boolean allSquaresOnChessBoardExist(
    @ForAll @CharRange(from = 'a', to = 'h') char column,
    @ForAll @CharRange(from = '1', to = '8') char row
) {
    return new ChessBoard().square(column, row).isOnBoard();
}
```

The property is supposed to check that all valid squares in chess are present
on a new chess board. If _jqwik_ generated the values for `column` and `row`
randomly, 1000 tries might or might not produce all 64 different combinations.
Why not change strategies in cases like that and just iterate through all
possible values? 

This is exactly what _jqwik_ will do:
- As long as it can figure out that the maximum number of possible values
  is equal or below a property's `tries` attribute (1000 by default), 
  all combinations will be generated.
- You can also enforce an exhaustive or randomized generation mode by using the
  [Property.generation attribute](#optional-property-parameters).
- If _jqwik_ cannot figure out how to do exhaustive generation for one of the 
  participating arbitraries it will switch to randomized generation if in auto mode
  or throw an exception if in exhaustive mode.
  
Exhaustive generation is considered for:
- All integral types
- Characters and chars
- Enums
- Booleans
- Strings
- Fixed number of choices given by `Arbitraries.of()`
- Fixed number of choices given by `Arbitraries.shuffle()`
- Lists, sets, streams, optionals of the above
- Combinations of the above using `Combinators.combine()`
- Mapped arbitraries using `Arbitrary.map()`
- Filtered arbitraries using `Arbitrary.filter()`
- Flat mapped arbitraries using `Arbitrary.flatMap()`
- And a few other derived arbitraries...


## Data-Driven Properties

In addition to the usual randomized generation of property parameters you have also
the possibility to feed a property with preconceived or deterministically generated
parameter sets. Why would you want to do that? One reason might be that you are aware
of some problematic test cases but they are rare enough that _jqwik_'s randomization
strategies don't generate them (often enough). Another reason could be that you'd like
to feed some properties with prerecorded data - maybe even from production.
And last but not least there's a chance that you want to check for a concrete result
given a set of input parameters.

Feeding data into a property is quite simple:

```java
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
```

All you have to do is annotate the property method with 
`@FromData("dataProviderReference")`. The method you reference must be
annotated with `@Data` and return an object of type `Iterable<? extends Tuple>`.
The [`Table` class](/docs/1.1.4/javadoc/net/jqwik/api/Table.html) 
is just a convenient way to create such an object, but you can return
any collection or create an implementation of your own. 

Keep in mind that the `Tuple` subtype you choose must conform to the 
number of `@ForAll` parameters in your property method, e.g. `Tuple.Tuple3` 
for a method with three parameters. Otherwise _jqwik_ will fail the property
and tell you that the provided data is inconsistent with the method's parameters. 

Data points are fed to the property in their provided order. 
The `tries` parameter of `@Property` will constrain the maximum data points
being tried.
Unlike parameterized tests in JUnit4 or Jupiter, _jqwik_ will report only the
first falsified data point. Thus, fixing the first failure might lead to another
falsified data point later on. There is also _no shrinking_ being done for data-driven
properties since _jqwik_ has no information about the constraints under which 
the external data was conceived or generated.

## Rerunning Falsified Properties

When you rerun properties after they failed, they will - by default - use
the previous random seed so that the next run will generate the exact same
parameter data and thereby expose the same failing behaviour. This simplifies
debugging and regression testing since it makes a property's falsification
stick until the problem has been fixed.

If you want to, you can change this behaviour for a given property like this:

```java
@Property(afterFailure = AfterFailureMode.RANDOM_SEED)
void myProperty() { ... }
```

The `afterFailure` property can have one of four values:

- `AfterFailureMode.PREVIOUS_SEED`: Choose the same seed that provoked the failure in the first place.
  Provided no arbitrary provider code has been changed, this will generate the same
  sequence of generated parameters as the previous test run.

- `AfterFailureMode.RANDOM_SEED`: Choose a new random seed even after failure in the previous run.
  A constant seed will always prevail thought, as in the following example:

  ```java
  @Property(seed = "424242", afterFailure = AfterFailureMode.RANDOM_SEED)
  void myProperty() { ... }
  ```

- `AfterFailureMode.SAMPLE_ONLY`: Only run the property with just the last falsified (and shrunk)
  generated sample set of parameters. This only works if all parameters could
  be serialized. Look into your test run log to check out if a serialization problem occurred.

- `AfterFailureMode.SAMPLE_FIRST`: Same as `SAMPLE_ONLY` but generate additional examples if the
  property no longer fails with the recorded sample.


You can also determine the default behaviour of all properties by setting
the `defaultAfterFailure` property in the [configuration file](jqwik-configuration)
to one of those enum values.

## jqwik Configuration

_jqwik_ will look for a file `jqwik.properties` in your classpath in which you can configure
a few basic parameters:

```
database = .jqwik-database          # The database to store data of previous runs
defaultTries = 1000                 # The default number of tries for each property
defaultMaxDiscardRatio = 5          # The default ratio before assumption misses make a property fail
useJunitPlatformReporter = false    # Set to true if you want to use platform reporting
defaultAfterFailure = PREVIOUS_SEED # Set default behaviour for falsified properties
reportOnlyFailures = false          # Set to true if only falsified properties should be reported
```

## Release Notes

Read this version's [release notes](/release-notes.html#114).