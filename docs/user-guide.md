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
  - [Method Lifecycle](#method-lifecycle)
  - [Other Lifecycles](#other-lifecycles)
- [Grouping Tests](#grouping-tests)
- [Default Parameter Generation](#default-parameter-generation)
  - [Constraining Default Generation](#constraining-default-generation)
- [Self-Made Annotations](#self-made-annotations)
- [Customized Parameter Generation](#customized-parameter-generation)
  - [Static `Arbitraries` methods](#static-arbitraries-methods)
  - [Generate `null` values](#generate-null-values)
  - [Filtering](#filtering)
  - [Mapping](#mapping)
  - [Using generated values to create another Arbitrary](#using-generated-values-to-create-another-arbitrary)
  - [Combining Arbitraries](#combining-arbitraries)
- [Assumptions](#assumptions)
- [Result Shrinking](#result-shrinking)
  - [Integrated Shrinking](#integrated-shrinking)
  - [Switch Shrinking Off](#switch-shrinking-off)
- [Running and Configuration](#running-and-configuration)
  - [jqwik Configuration](#jqwik-configuration)
- [Program your own Generators and Arbitraries](#program-your-own-generators-and-arbitraries)
- [Register default Generators and Arbitraries](#register-default-generators-and-arbitraries)
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
ext.jqwikVersion = '0.7.2'

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
        <version>0.7.2</version>
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
_jqwik_ __will run 1000 tries__, i.e. a 1000 different sets of 
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

### Method Lifecycle

The current lifecycle of jqwik test methods is rather simple:

- For each method, annotated with `@Property` or `@Example`, 
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

In this example both the constructor and `close()` will be called 6 times: 
Once for `anExample()` and 5 times for `aProperty(...)`.

### Other Lifecycles

Currently _jqwik_ does not have special support for a lifecycle per test container,
[test try](#try) or even package. Later versions of _jqwik_ might possible bring
more features in that field. 
[Create an issue on github](https://github.com/jlink/jqwik/issues) with your concrete needs.


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
- `Character` and `char
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

#### All types:

- `@WithNull(double value = 0.1)`: Also generate `null` values with a probability of `value`. 

#### Strings:

If Strings are not constrained a standard set of alphanumeric characters and a few other chars is used.

- `@StringLength(int min = 0, int max)`

The following constraints can be combined with each other:

- `@Chars(chars[] value = {}, char from = 0, char to = 0)`: Specify a set of characters
  or a start and end character. This annotation can be repeated which will add up all allowed chars.
- `@Digits`: Use only digits `0` through `9`
- `@LowerChars`: Use only lower case chars `a` through `z`
- `@UpperChars`: Use only upper case chars `A` through `Z`
- `@AlphaChars`: Lower and upper case chars are allowed.

#### Characters:

If Characters are not constrained any char between `'\u0000'` and `'\uffff'` might be created.

The following constraints can be combined with each other:

- `@Chars(chars[] value = {}, char from = 0, char to = 0)`: Specify a set of characters
  or a start and end character. This annotation can be repeated which will add up all allowed chars.
- `@Digits`: Use only digits `0` through `9`
- `@LowerChars`: Use only lower case chars `a` through `z`
- `@UpperChars`: Use only upper case chars `A` through `Z`
- `@AlphaChars`: Lower and upper case chars are allowed.

#### List, Set, Stream and Arrays:

- `@Size(int min = 0, int max)`

#### Integer and int:

- `@IntRange(int min = 0, int max)`
- `@Positive`: Numbers equal to or larger than `0`.
- `@Negative`: Numbers lower than or equals to `-0`.

#### Long, long and BigInteger:

- `@LongRange(long min = 0L, long max)`
- `@Positive`: Numbers equal to or larger than `0L`.
- `@Negative`: Numbers lower than or equals to `-0L`.

#### Float and float:

- `@FloatRange(float min = 0.0f, float max)`
- `@Positive`: Numbers equal to or larger than `0f`.
- `@Negative`: Numbers lower than or equals to `-0f`.
- `@Scale(int value)`

#### Double, double and BigDecimal:

- `@DoubleRange(double min = 0.0, double max)`
- `@Positive`: Numbers equal to or larger than `0.0`.
- `@Negative`: Numbers lower than or equals to `-0.0`.
- `@Scale(int value)`

#### Constraining contained types

In case of collections, arrays and `Optional` the constraining annotations are also applied to the
contained type, e.g.:

```java
@Property
void aProperty(@ForAll @StringLength(max=10) List<String> listOfStrings) {
}
```
will generate lists of Strings that have 10 characters max.

<div style="border:1px solid black; padding: 1em; width:auto; text-size: smaller">
<h4>Side Note</h4>
In future versions of <em>jqwik</em> constraints for contained types might have to be added to the type itself, like: 
    <code>
    @ForAll List<@StringLength(max=10) String> listOfStrings
    </code>.
Currently, though, not all Java&nbsp;8 implementations support the retrieval of 
type parameter annotations through reflection.
</div>


## Self-Made Annotations

You can [make your own annotations](http://junit.org/junit5/docs/5.0.0/user-guide/#writing-tests-meta-annotations)
instead of using _jqwik_'s built-in ones. BTW, '@Example' is nothing but a plain annotation using `@Property`
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

@Property(tries = 10, reporting = ReportingMode.GENERATED)
void aGermanText(@ForAll @GermanText String aText) {}

```

The drawback of self-made annotations is that they do not forward their parameters to meta-annotations,
which constrains their applicability to simple cases.


## Customized Parameter Generation

Sometimes the possibilities of adjusting default parameter generation
through annotations is not enough. In that case you can delegate parameter
provision to another method. Look at the following example:

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
    return Arbitraries.strings('a', 'z', 1, 8);
}

@Provide("10 to 99")
Arbitrary<Integer> numbers() {
    return Arbitraries.integers(10, 99);
}
```

The String value of the `@ForAll` annotation serves as a reference to a 
method within the same class (or one of its superclasses or owning classes).
This reference refers to either the method's name or the String value
of the method's `@Provide` annotation.

The providing method has to return an object of type `@Arbitrary<T>` where
`T` is the static type of the parameter to be provided. 

Parameter provision usually starts with a 
[static method call to `Arbitraries`](#static-arbitraries-methods), maybe followed
by one or more [filtering](#filtering), [mapping](#mapping) or 
[combining](#combining-arbitraries) actions.

For types that have no default generation at all, _jqwik_ will use
any provider method returning the correct type even if there is no
explicit reference value in `@ForAll`. If provision is ambiguous
_jqwik_ will complain and throw an exception at runtime. 


### Static `Arbitraries` methods 

The starting point for generation usually is a static method call on class `Arbitraries`. 

#### Generate values yourself

- `Arbitrary<T> randomValue(Function<Random, T> generator)`: 
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

- `Arbitrary<T> fromGenerator(RandomGenerator<T> generator)`:
  If the number of _tries_ influences value generation or if you want 
  to allow for [shrinking](#result-shrinking) you have to provide 
  your own `RandomGenerator` implementation. 
  
#### Select values randomly

- `Arbitrary<U> of(U... values)`:
  Choose randomly from a list of values. Shrink towards the first one.
  
- `Arbitrary<T> samples(T... samples)`:
  Go through samples from first to last. Shrink towards the first sample.
  
  If instead you want to _add_ samples to an existing arbitrary you'd rather use 
  `Arbitrary.withSamples(T... samples)`. The following arbitrary:
  
  ```java
  @Provide 
  Arbitrary<Integer> integersWithPrimes() {
	  return Arbitraries.integers(- 1000, 1000).withSamples(2,3,5,7,11,13,17);
  }
  ```
  
  will first generate the 7 enumerated prime numbers and only then generate random 
  integers between -1000 and +1000.
  
- `Arbitrary<T> of(Class<T  extends Enum> enumClass)`:
  Choose randomly from all values of an `enum`. Shrink towards first enum value.

#### Integers

- `Arbitrary<Integer> integers()`
- `Arbitrary<Integer> integers(int min, int max)`
- `Arbitrary<Long> longs(long min, long max)`
- `Arbitrary<Long> longs()`
- `Arbitrary<BigInteger> bigIntegers(long min, long max)`
- `Arbitrary<BigInteger> bigIntegers()`

#### Decimals

- `Arbitrary<Float> floats()`
- `Arbitrary<Float> floats(Float min, Float max, int scale)`
- `Arbitrary<Double> doubles()`
- `Arbitrary<Double> doubles(double min, double max, int scale)`
- `Arbitrary<BigDecimal> bigDecimals(BigDecimal min, BigDecimal max, int scale)`
- `Arbitrary<BigDecimal> bigDecimals()`

#### Characters and Strings

- `Arbitrary<String> strings()`
- `Arbitrary<String> strings(char[] validChars, int minLength, int maxLength)`
- `Arbitrary<String> strings(char[] validChars)`
- `Arbitrary<String> strings(char from, char to, int minLength, int maxLength)`
- `Arbitrary<String> strings(char from, char to)`
- `Arbitrary<Character> chars()`
- `Arbitrary<Character> chars(char from, char to)`
- `Arbitrary<Character> chars(char[] validChars)`

#### Collections, Streams, Arrays and Optional

Generating types who have generic type parameters, requires to hand in 
an `Arbitrary` instance for the generic types.

- `Arbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize)`
- `Arbitrary<List<T>> listOf(Arbitrary<T> elementArbitrary)`
- `Arbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize)`
- `Arbitrary<Set<T>> setOf(Arbitrary<T> elementArbitrary)`
- `Arbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary, int minSize, int maxSize)`
- `Arbitrary<Stream<T>> streamOf(Arbitrary<T> elementArbitrary)`
- `Arbitrary<A> arrayOf(Class<A> arrayClass, Arbitrary<T> elementArbitrary, int minSize, int maxSize)`
- `Arbitrary<A> arrayOf(Class<A> arrayClass, Arbitrary<T> elementArbitrary)`
- `Arbitrary<Optional<T>> optionalOf(Arbitrary<T> elementArbitrary)`


### Generate `null` values

Predefined generators will never create `null` values. If you want to allow that,
call `Arbitrary.injectNull(double probability)`. The following
provider method creates an arbitrary that will return a `null` String 
in about 1 of 20 generated values.

```java
@Provide 
Arbitrary<String> stringsWithNull() {
  return Arbitraries.strings(0, 10).injectNull(0.05);
}
```

### Filtering

If you want to include only part of all the values generated by an arbitrary,
use `Arbitrary.filter(Predicate<T> filterPredicate)`. The following arbitrary will filter out all
even numbers from the stream of generated integers:

```java
@Provide 
Arbitrary<Integer> oddNumbers() {
  return Arbitraries.integers().filter(aNumber -> aNumber % 2 != 0);
}
```

Keep in mind that your filter condition should not be too restrictive. 
Otherwise the generation of suitable values might take very long or even never succeed,
resulting in an endless loop.

### Mapping

Sometimes it's easier to start with an existing arbitrary and use its generated values to
build other objects from them. In that case, use `Arbitrary.map(Function<T, U> mapper)`.
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


### Using generated values to create another Arbitrary

Similar as in the case of `Arbitrary.map(..)` there are situations in which you want to use
a generated value in order to create another Arbitrary from it. Sounds complicated?
Have a look at the following example:

```java
@Property
boolean fixedSizedStrings(@ForAll("listsOfEqualSizedStrings")List<String> lists) {
    return lists.stream().distinct().count() == 1;
}

@Provide
Arbitrary<List<String>> listsOfEqualSizedStrings() {
    Arbitrary<Integer> integers2to5 = Arbitraries.integers(2, 5);
    return integers2to5.flatMap(stringSize -> {
        Arbitrary<String> strings = Arbitraries.strings('a', 'z', stringSize, stringSize);
        return Arbitraries.listOf(strings);
    });
}
```
The provider method will create random lists of strings, but in each list the size of the contained strings
will always be the same - between 2 and 5.

### Combining Arbitraries

Sometimes just mapping a single stream of generated values is not enough to generate
a more complicated domain object. In those cases you can combine several arbitraries to
a single result arbitrary using `Combinators.combine()` with up to four arbitraries. 
[Create an issue on github](https://github.com/jlink/jqwik/issues) if you need more than four. 

The following example generates `Person` instances from three arbitraries as inputs.

```java
@Property
void validPeopleHaveIDs(@ForAll Person aPerson) {
    Assertions.assertThat(aPerson.getID()).contains("-");
    Assertions.assertThat(aPerson.getID().length()).isBetween(5, 24);
}

@Provide
Arbitrary<Person> validPeople() {
    Arbitrary<Character> initials = Arbitraries.chars('A', 'Z');
    Arbitrary<String> names = Arbitraries.strings('a', 'z', 2, 20);
    Arbitrary<Integer> ages = Arbitraries.integers(0, 130);
    return Combinators.combine(initials, names, ages)
        .as((initial, name, age) -> new Person(initial + name, age));
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
`[Aaaaaaaaaaaaaaaaaaaaa:100]`.

## Assumptions

If you want to constrain the set of generated values in a way that embraces
more than one parameter, [filtering](#filtering) does not work. What you
can do instead is putting one or more assumptions at the beginning of your property.

The following property works only on strings that are not equal:

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

This is a reasonable use of `Assume.that(boolean condition)` because most generated
value sets will pass through.

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
timestamp = 2017-11-06T14:36:15.134, 
    seed = 1066117555581106850
    tries = 1000, 
    checks = 20, 

org.opentest4j.AssertionFailedError: 
    Property [findingContainedStrings] exhausted after [1000] tries and [980] rejections
```

The problem is that - given a random generation of two strings - only in very few cases
one string will be contained in the other. _jqwik_ will report a property as `exhausted`
if the ratio between generated and accepted parameters is higher than 5. You can change
the maximum discard ratio by specifying a parameter `maxDiscardRatio` in the `@Property` annotation.
That's why changing to `@Property(maxDiscardRatio = 100)` in the previous example 
will probably result in a successful property run, even though only a handful 
cases - of 1000 generated - will actually be checked.

In many cases turning up the accepted discard ration is a bad idea. With some creativity
we can often avoid the problem by generating out test data a bit differently. 
Look at this variant of the above property, which also uses `Assume.that()` but with
a much lower discard ratio:

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
timestamp = 2017-11-04T16:42:25.859, 
    seed = -633877439388930932, 
    tries = 38, 
    checks = 38, 
    originalSample = ["LVtyB"], 
    sample = ["AA"]

AssertionFailedError: Property [stringShouldBeShrunkToAA] falsified with sample ["AA"]
```

In this case the _originalSample_ could be any string between 2 and 5 chars, whereas the final _sample_
should be exactly `AA` since this is the shortest failing string and `A` has the lowest numeric value
of all allowed characters.

### Integrated Shrinking

_jqwik_'s shrinking approach is called _integrated shrinking_, as opposed to _type-based shrinking_
which most property-based testing tools use.
The general idea and its advantages are explained 
[here](http://hypothesis.works/articles/integrated-shrinking/).

Consider a somewhat more complicated examples:

```java
@Property
boolean shrinkingCanTakeLong(@ForAll("first") String first, @ForAll("second") String second) {
    String aString = first + second;
    return aString.length() > 5 || aString.length() < 2;
}

@Provide
Arbitrary<String> first() {
    return Arbitraries.strings('a', 'z', 1, 10).filter(string -> string.endsWith("h"));
}

@Provide
Arbitrary<String> second() {
    return Arbitraries.strings('0', '9', 0, 10).filter(string -> string.length() >= 1);
}
```

Shrinking still works, although there's quite a bit of filtering and string concatenation happening:
```
timestamp = 2017-11-04T16:58:45.431, 
    seed = -5596810132893895291, 
    checks = 20, 
    tries = 20, 
    originalSample = ["gh", "774"], 
    sample = ["h", "0"]

AssertionFailedError: Property [shrinkingCanTakeLong] falsified with sample ["h", "0"]
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
```

This type of configuration is preliminary and will likely be replaced by 
[JUnit 5's platform configuration](http://junit.org/junit5/docs/5.0.0/user-guide/#running-tests-config-params)
mechanism soon. Moreover, there will probably be many more default parameters to change.

## Program your own Generators and Arbitraries

This topic will probably need a page of its own.


## Register default Generators and Arbitraries

The API for providing Arbitraries and Generators by default is not public yet.


## Glossary

#### Arbitrary

The fundamental type that is used to generate values. The name was first chosen by QuickCheck
and later adopted by most property-based-testing libraries.

Under the hood, most instances of type `Arbitrary` use [`RandomGenerator`s](#randomgenerator)
to do the actual generation for them.

#### Property

A _property_ is a [test method](#test-method) that has one or more
parameters annotated with `@ForAll`. 

#### RandomGenerator

Instances of type `RandomGenerator` take care of actually generating a specific parameter instance.

#### Test Method

A _test method_ is a a `public`, `protected` or package-scoped method, 
annotated with `@Example` or `@Property`.

#### Try

A _try_ is the attempt at running a [test method](#test-method) 
a single time with a specific set of parameters.