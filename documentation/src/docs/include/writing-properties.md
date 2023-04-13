### Creating a Property

_Properties_ are the core concept of [property-based testing](/#properties).

You create a _Property_ by annotating a `public`, `protected`
or package-scoped method with
[`@Property`](/docs/${docsVersion}/javadoc/net/jqwik/api/Property.html).
In contrast to examples a property method is supposed to have one or
more parameters, all of which must be annotated with
[`@ForAll`](/docs/${version}/javadoc/net/jqwik/api/ForAll.html).

At test runtime the exact parameter values of the property method
will be filled in by _jqwik_.

Just like an example test a property method has to
- either return a `boolean` value that signifies success (`true`)
  or failure (`false`) of this property.
- or return nothing (`void`). In that case you will probably
  use [assertions](#assertions) to check the property's invariant.

If not [specified differently](#optional-property-attributes),
_jqwik_ __will run 1000 tries__, i.e. a 1000 different sets of
parameter values and execute the property method with each of those parameter sets.
The first failed execution will stop value generation
and be reported as failure - usually followed by an attempt to
[shrink](#result-shrinking) the falsified parameter set.

[Here](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/PropertyBasedTests.java)
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

Mind that only parameters that are annotated with '@ForAll' are considered for value generation.
Other kinds of parameters can be injected through the [resolve parameter hook](#resolveparameterhook).

#### Failure Reporting

If a property fails then jqwik's reporting is more thorough:
- Report the relevant exception, usually a subtype of `AssertionError`
- Report the property's base parameters
- Report both the original failing sample and the shrunk sample.

  **Caveat**: The samples are reported _after their use_ in the property method.
  That means that mutable objects that are being changed during a property show
  their final state, not the state in which the arbitrary generated them.

In the case of `lengthOfConcatenatedStringIsGreaterThanLengthOfEach`
from above the report looks like that:

```
PropertyBasedTests:lengthOfConcatenatedStringIsGreaterThanLengthOfEach = 
  java.lang.AssertionError: 
    Expecting:
     <0>
    to be greater than:
     <0> 
                              |-----------------------jqwik-----------------------
tries = 16                    | # of calls to property
checks = 16                   | # of not rejected calls
generation = RANDOMIZED       | parameters are randomly generated
after-failure = SAMPLE_FIRST  | try previously failed sample, then previous seed
when-fixed-seed = ALLOW       | fixing the random seed is allowed
edge-cases#mode = MIXIN       | edge cases are mixed in
edge-cases#total = 4          | # of all combined edge cases
edge-cases#tried = 0          | # of edge cases tried in current run
seed = -2370223836245802816   | random seed to reproduce generated values

Shrunk Sample (<n> steps)
-------------------------
  string1: ""
  string2: ""

Original Sample
---------------
  string1: "乮��깼뷼檹瀶�������የ뷯����ঘ꼝���焗봢牠"
  string2: ""

  Original Error
  --------------
  java.lang.AssertionError: 
    Expecting:
     <29>
    to be greater than:
     <29> 
```

The source code names of property method parameters can only be reported when compiler argument
`-parameters` is used.
_jqwik_ goes for structured reporting with collections, arrays and maps.
If you want to provide nice reporting for your own domain classes you can either

- implement a potentially multiline `toString()` method or
- register an implementation of [`net.jqwik.api.SampleReportingFormat`](/docs/${docsVersion}/javadoc/net/jqwik/api/SampleReportingFormat.html)
  through Java’s `java.util.ServiceLoader` mechanism.
- add an implementation of [`net.jqwik.api.SampleReportingFormat`](/docs/${docsVersion}/javadoc/net/jqwik/api/SampleReportingFormat.html)
  to a [`DomainContext`](#domain-and-domain-context).


#### Additional Reporting Options

You can switch on additional reporting aspects by adding a
[`@Report(Reporting[])` annotation](/docs/${docsVersion}/javadoc/net/jqwik/api/Property.html)
to a property method.

The following reporting aspects are available:

- `Reporting.GENERATED` will report each generated set of parameters.
- `Reporting.FALSIFIED` will report each set of parameters
  that is falsified during shrinking.

Unlike sample reporting these reports will show _the freshly generated parameters_,
i.e. potential changes to mutable objects during property execution cannot be seen here.

#### Platform Reporting with Reporter Object

If you want to provide additional information during a test or a property using
`System.out.println()` is a common choice. The JUnit platform, however, provides
a better mechanism to publish additional information in the form of key-value pairs.
Those pairs will not only printed to stdout but are also available to downstream
tools like test report generators in continue integration.

You can hook into this reporting mechanism through jqwik's `Reporter` object.
This object is available in [lifecycle hooks](#lifecycle-hooks) but you can
also have it injected as a parameter into your test method:

```java
@Example
void reportInCode(Reporter reporter, @ForAll List<@AlphaChars String> aList) {
	reporter.publishReport("listOfStrings", aList);
	reporter.publishValue("birthday", LocalDate.of(1969, 1, 20).toString());
}
```

[net.jqwik.api.Reporter](/docs/${docsVersion}/javadoc/net/jqwik/api/Reporter.html)
has different publishing methods.
Those with `report` in their name use jqwik's reporting mechanism and formats
described [above](#failure-reporting).


#### Adding Footnotes to Failure Reports

By using the [platform reporting mechanism](#platform-reporting-with-reporter-object)
you can publish additional key-value pairs for each and every run of a property method.
In many cases, however, you only want some clarifying information for failing
property tries. _Footnotes_ provide this capability:

```java
@EnableFootnotes
public class FootnotesExamples {
	@Property
	void differenceShouldBeBelow42(@ForAll int number1, @ForAll int number2, Footnotes footnotes) {
		int difference = Math.abs(number1 - number2);
		footnotes.addFootnote(Integer.toString(difference));
		Assertions.assertThat(difference).isLessThan(42);
	}
}
```

Unlike standard reporting, the footnotes feature must be explicitly enabled through
the annotation `@EnableFootnotes`, which can be added to container classes or individual property methods.
Now you can add a parameter of type `net.jqwik.api.footnotes.Footnotes` to a property method
or a lifecycle method annotated with either `@BeforeTry` or `@AfterTry`.
The footnote string will then be part of the sample reporting:

```
Shrunk Sample (5 steps)
-----------------------
  number1: 0
  number2: 42
  footnotes: net.jqwik.api.footnotes.Footnotes[differenceShouldBeBelow42]

  #1 42

Original Sample
---------------
  number1: 399308
  number2: -14
  footnotes: net.jqwik.api.footnotes.Footnotes[differenceShouldBeBelow42]

  #1 399322
```

For footnotes that require significant computation you can also use 
`Footnotes.addAfterFailure(Supplier<String> footnoteSupplier)`.
Those suppliers will only be evaluated if the property fails, and then as early as possible.
Mind that this evaluation can still happen quite often during shrinking.

### Optional `@Property` Attributes

The [`@Property`](/docs/${docsVersion}/javadoc/net/jqwik/api/Property.html)
annotation has a few optional values:

- `int tries`: The number of times _jqwik_ tries to generate parameter values for this method.

  The default is `1000` which can be overridden in [`junit-platform.properties`](#jqwik-configuration).

- `String seed`: The _random seed_ to use for generating values. If you do not specify a values
  _jqwik_ will use a random _random seed_. The actual seed used is being reported by
  each run property.

- `FixedSeedMode whenFixedSeed`: Influence how to react when this property's random seed
  is fixed through the `seed` attribute:
  - `FixedSeedMode.ALLOW`: Just use the seed.
  - `FixedSeedMode.WARN`: Log a warning.
  - `FixedSeedMode.FAIL`: Fail this property with an exception.

  This can be useful to prevent accidental commits of fixed seeds into source control.
  The default is `ALLOW`, which can be overridden in [`junit-platform.properties`](#jqwik-configuration).

- `int maxDiscardRatio`: The maximal number of tried versus actually checked property runs
  in case you are using [Assumptions](#assumptions). If the ratio is exceeded _jqwik_ will
  report this property as a failure.

  The default is `5`, which can be overridden in [`junit-platform.properties`](#jqwik-configuration).

- `ShrinkingMode shrinking`: You can influence the way [shrinking](#result-shrinking) is done
    - `ShrinkingMode.OFF`: No shrinking at all
    - `ShrinkingMode.FULL`: Shrinking continues until no smaller value can
      be found that also falsifies the property.
      This might take very long or not end at all in rare cases.
    - `ShrinkingMode.BOUNDED`: Shrinking is tried for 10 seconds maximum and then times out.
      The best shrunk sample at moment of time-out will be reported. This is the default.
      The default time out of 10 seconds can be changed in
      [jqwik's configuration](#jqwik-configuration).

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

- `AfterFailureMode afterFailure`: Determines how jqwik will generate values of a property
  that has [failed in the previous run](#rerunning-falsified-properties).

    - `AfterFailureMode.SAMPLE_FIRST` is the default. It means that jqwik will use the last shrunk set of parameters first
      and then, if successful, go for a new randomly generated set of parameters.
    - `AfterFailureMode.SAMPLE_ONLY` means that jqwik will only use the last _shrunk set of parameters_.
    - `AfterFailureMode.PREVIOUS_SEED` means that jqwik will use the same seed and thereby generate
      the same sequence of parameters as in the previous, failing run.
    - `AfterFailureMode.RANDOM_SEED` makes jqwik use a new random seed even directly after a failure.
      This might lead to a "flaky" property that sometimes fails and sometimes succeeds.
      If the seed for this property has been fixed, the fixed seed will always be used.

- `EdgeCasesMode edgeCases`: Determines if and when jqwik will generate
  the permutation of [edge cases](#generation-of-edge-cases).

    - `EdgeCasesMode.MIXIN` is the default. Edge cases will be mixed with randomly generated parameter sets
      until all known permutations have been mixed in.
    - `EdgeCasesMode.FIRST` results in all edge cases being generated before jqwik starts with randomly
      generated samples.
    - `EdgeCasesMode.NONE` will not generate edge cases for the full parameter set at all. However,
      edge cases for individual parameters are still being mixed into the set from time to time.

The effective values for tries, seed, after-failure mode, generation mode edge-cases mode
and edge cases numbers are reported after each run property:

```
tries = 10 
checks = 10 
generation = EXHAUSTIVE
after-failure = SAMPLE_FIRST
when-fixed-seed = ALLOW
edge-cases#mode = MIXIN 
edge-cases#total = 2 
edge-cases#tried = 2 
seed = 42859154278924201
```

#### Setting Defaults for `@Property` Attributes

If you want to set the defaults for all property methods in a container class
(and all the [groups](#grouping-tests) in it) you can use annotation
[`@PropertyDefaults`](/docs/${docsVersion}/javadoc/net/jqwik/api/PropertyDefaults.html).

In the following example both properties are tried 10 times.
Shrinking mode is set for all but is overridden in the second property.

```java
@PropertyDefaults(tries = 10, shrinking = ShrinkingMode.FULL)
class PropertyDefaultsExamples {

	@Property
	void aLongRunningProperty(@ForAll String aString) {}

	@Property(shrinking = ShrinkingMode.OFF)
	void anotherLongRunningProperty(@ForAll String aString) {}
}
```

Thus, the order in which a property method's attributes are determined is:

1. Use jqwik's built-in defaults,
2. which can be overridden in the [configuration file](#jqwik-configuration),
3. which can be changed in a container class' `@PropertyDefaults` annotation,
4. which override `@PropertyDefaults` attributes in a container's superclass or 
   implemented interfaces,
5. which can be overridden by a method's
   [`@Property` annotation attributes](#optional-property-attributes).


### Creating an Example-based Test

_jqwik_ also supports example-based testing.
In order to write an example test annotate a `public`, `protected` or package-scoped method with
[`@Example`](/docs/${docsVersion}/javadoc/net/jqwik/api/Example.html).
Example-based tests work just like plain JUnit-style test cases.

A test case method must
- either return a `boolean` value that signifies success (`true`)
  or failure (`false`) of this test case.
- or return nothing (`void`) in which case you will probably
  use [assertions](#assertions) in order to verify the test condition.

[Here](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/ExampleBasedTests.java)
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

Internally _jqwik_ treats examples as properties with the number of tries hardcoded to `1`.
Thus, everything that works for property methods also works for example methods --
including random generation of parameters annotated with `@ForAll`.

### Assertions

__jqwik__ does not come with any assertions, so you have to use one of the
third-party assertion libraries, e.g. [Hamcrest](http://hamcrest.org/) or
[AssertJ](http://joel-costigliola.github.io/assertj/).

If you have Jupiter in your test dependencies anyway, you can also use the
static methods in `org.junit.jupiter.api.Assertions`.

### Lifecycle

To understand the lifecycle it is important to know that _the tree of test elements_
consists of two main types of elements:

- __Containers__: The root engine container, container classes
  and embedded container classes (those annotated with `@Group`)
- __Properties__: Methods annotated with
  [`@Property`](/docs/${docsVersion}/javadoc/net/jqwik/api/Property.html) or
  [`@Example`](/docs/${docsVersion}/javadoc/net/jqwik/api/Example.html).
  An _example_ is just a property with a single _try_ (see below).

So a typical tree might look like:

```
Jqwik Engine
    class MyFooTests
        @Property fooProperty1()
        @Property fooProperty2()
        @Example fooExample()
    class MyBarTests
        @Property barProperty()
        @Group class Group1 
            @Property group1Property()
        @Group class Group2 
            @Example group2Example()
```   

Mind that packages do not show up as in-between containers!

When running your whole test suite there are additional things happening:

- For each property or example a new instance of the containing class
  will be created.
- Each property will have 1 to n _tries_. Usually each try gets its own
  set of generated arguments which are bound to parameters annotated
  with `@ForAll`.

_jqwik_ gives you more than one way to hook into the lifecycle of containers,
properties and tries.

#### Simple Property Lifecycle

If you need nothing but some initialization and cleanup of the container instance
per property or example:

- Do the initialization work in a constructor without parameters.
- If you have cleanup work to do for each property method,
  the container class can implement `java.lang.AutoCloseable`.
  The `close`-Method will be called after each test method execution.

```java
import net.jqwik.api.*;

class SimpleLifecycleTests implements AutoCloseable {

	SimpleLifecycleTests() {
		System.out.println("Before each");
	}

	@Example void anExample() {
		System.out.println("anExample");
	}

	@Property(tries = 5)
	void aProperty(@ForAll String aString) {
		System.out.println("aProperty: " + aString);
	}

	@Override
	public void close() throws Exception {
		System.out.println("After each");
	}
}
```

In this example both the constructor and `close()` will be called twice times:
Once for `anExample()` and once for `aProperty(...)`. However, all five calls
to `aProperty(..)` will share the same instance of `SimpleLifecycleTests`.

#### Annotated Lifecycle Methods

The other way to influence all elements of a test run is through annotated lifecycle
methods, which you might already know from JUnit 4 and 5. _jqwik_ currently has
eight annotations:

- [`@BeforeContainer`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/BeforeContainer.html):
  _Static_ methods with this annotation will run exactly once before any property
  of a container class will be executed, even before the first instance of this class will be created.
- [`@AfterContainer`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/AfterContainer.html):
  _Static_ methods with this annotation will run exactly once after all properties
  of a container class have run.
- [`@BeforeProperty`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/BeforeProperty.html):
  Methods with this annotation will run once before each property or example.
  `@BeforeExample` is an alias with the same functionality.
- [`@AfterProperty`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/AfterProperty.html):
  Methods with this annotation will run once after each property or example.
  `@AfterExample` is an alias with the same functionality.
- [`@BeforeTry`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/BeforeTry.html):
  Methods with this annotation will run once before each try, i.e. execution
  of a property or example method.
- [`@AfterTry`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/AfterTry.html):
  Methods with this annotation will run once after each try, i.e. execution
  of a property or example method.

Given the following container class:

```java
class FullLifecycleExamples {

	@BeforeContainer
	static void beforeContainer() {
		System.out.println("before container");
	}

	@AfterContainer
	static void afterContainer() {
		System.out.println("after container");
	}

	@BeforeProperty
	void beforeProperty() {
		System.out.println("before property");
	}

	@AfterProperty
	void afterProperty() {
		System.out.println("after property");
	}

	@BeforeTry
	void beforeTry() {
		System.out.println("before try");
	}

	@AfterTry
	void afterTry() {
		System.out.println("after try");
	}

	@Property(tries = 3)
	void property(@ForAll @IntRange(min = -5, max = 5) int anInt) {
		System.out.println("property: " + anInt);
	}
}
```

Running this test container should produce something like the following output
(maybe with your test report in-between):

```
before container

before property
before try
property: 3
after try
before try
property: 1
after try
before try
property: 4
after try
after property

after container
```

All those lifecycle methods are being run through _jqwik_'s mechanism for
writing [_lifecycle hooks_](#lifecycle-hooks) under the hood.

#### Annotated Lifecycle Variables

One of the lifecycle annotations from above has an additional meaning: 
[`@BeforeTry`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/BeforeTry.html):
It can also be used on a test container class' member variable 
to make sure that it will be reset to its initial value - the one it had before the first try -
for each try:

```java
class BeforeTryMemberExample {

	@BeforeTry
	int theAnswer = 42;

	@Property
	void theAnswerIsAlways42(@ForAll int addend) {
		Assertions.assertThat(theAnswer).isEqualTo(42);
		theAnswer += addend;
	}
}
```


#### Single Property Lifecycle

All [lifecycle methods](#annotated-lifecycle-methods) described in the previous section
apply to all property methods of a container class.
In rare cases, however, you may feel the need to hook into the lifecycle of a single property,
for example when you expect a property to fail.

Here is one example that checks that a property will fail with an `AssertionError`
and succeed in that case:

```java
@Property
@PerProperty(SucceedIfThrowsAssertionError.class)
void expectToFail(@ForAll int aNumber) {
    Assertions.assertThat(aNumber).isNotEqualTo(1);
}

private class SucceedIfThrowsAssertionError implements PerProperty.Lifecycle {
    @Override
    public PropertyExecutionResult onFailure(PropertyExecutionResult propertyExecutionResult) {
        if (propertyExecutionResult.throwable().isPresent() &&
                propertyExecutionResult.throwable().get() instanceof AssertionError) {
            return propertyExecutionResult.mapToSuccessful();
        }
        return propertyExecutionResult;
    }
}
```

Have a look at [`PerProperty.Lifecycle`](/docs/${docsVersion}/javadoc/net/jqwik/api/lifecycle/PerProperty.Lifecycle.html)
to find out which aspects of a property's lifecycle you can control.


### Grouping Tests

Within a containing test class you can group other containers by embedding
another non-static and non-private inner class and annotating it with `@Group`.
Grouping examples and properties is a means to improve the organization and
maintainability of your tests.

Groups can be nested, which makes their lifecycles also nested. 
That means that the lifecycle of a test class is also applied to inner groups of that container.
Have a look at [this example](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/TestsWithGroups.java):

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

### Naming and Labeling Tests

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
[In the following example](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/NamingExamples.java),
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

### Tagging Tests

Test container classes, groups, example methods and property methods can be tagged
using the annotation `@Tag("a-tag")`. You can have many tags on the same element.

Those tag can be used to filter the set of tests
[run by the IDE](https://blog.jetbrains.com/idea/2018/01/intellij-idea-starts-2018-1-early-access-program/) or
[the build tool](https://docs.gradle.org/4.6/release-notes.html#junit-5-support).
Tags are handed down from container (class or group) to its children (test methods or groups).

Have a look at
[the following example](https://github.com/jqwik-team/jqwik/blob/${gitVersion}/documentation/src/test/java/net/jqwik/docs/TaggingExamples.java).
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
[here](/docs/${docsVersion}/javadoc/net/jqwik/api/Tag.html).
Note that the `@Tag` annotation you'll have to use with jqwik is
`net.jqwik.api.Tag` rather than `org.junit.jupiter.api.Tag`.

### Disabling Tests

From time to time you might want to disable a test or all tests in a container
temporarily. You can do that by adding the
[`@Disabled`](/docs/${docsVersion}/javadoc/net/jqwik/api/Disabled.html) annotation
to a property method or a container class.

```java
import net.jqwik.api.Disabled;

@Disabled("for whatever reason")
class DisablingExamples {

	@Property
	@Disabled
	void aDisabledProperty() { }

}
```

Disabled properties will be reported by IDEs and build tools as "skipped"
together with the reason - if one has been provided.

Be careful __not to use__ the Jupiter annotation with the same name.
_Jqwik_ will refuse to execute methods that have Jupiter annotations.

