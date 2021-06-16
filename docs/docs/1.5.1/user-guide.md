---
title: jqwik User Guide - 1.5.1
---
<h1>The jqwik User Guide
<span style="padding-left:1em;font-size:50%;font-weight:lighter">1.5.1</span>
</h1>

<h3>Table of Contents
<span style="padding-left:1em;font-size:50%;font-weight:lighter">
    <a href="#detailed-table-of-contents">Detailed Table of Contents</a>
</span>
</h3>

- [How to Use](#how-to-use)
- [Writing Properties](#writing-properties)
- [Default Parameter Generation](#default-parameter-generation)
- [Customized Parameter Generation](#customized-parameter-generation)
- [Recursive Arbitraries](#recursive-arbitraries)
- [Using Arbitraries Directly](#using-arbitraries-directly)
- [Contract Tests](#contract-tests)
- [Stateful Testing](#stateful-testing)
- [Assumptions](#assumptions)
- [Result Shrinking](#result-shrinking)
- [Collecting and Reporting Statistics](#collecting-and-reporting-statistics)
- [Providing Default Arbitraries](#providing-default-arbitraries)
- [Domain and Domain Context](#domain-and-domain-context)
- [Generation from a Type's Interface](#generation-from-a-types-interface)
- [Generation of Edge Cases](#generation-of-edge-cases)
- [Exhaustive Generation](#exhaustive-generation)
- [Data-Driven Properties](#data-driven-properties)
- [Rerunning Falsified Properties](#rerunning-falsified-properties)
- [jqwik Configuration](#jqwik-configuration)
- [Additional Modules](#additional-modules)
- [Advanced Topics](#advanced-topics)
- [API Evolution](#api-evolution)
- [Release Notes](#release-notes)



<!-- use `doctoc --maxlevel 4 user-guide.md` to recreate the TOC -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
### Detailed Table of Contents  

- [How to Use](#how-to-use)
  - [Required Version of JUnit Platform](#required-version-of-junit-platform)
  - [Gradle](#gradle)
    - [Seeing jqwik Reporting in Gradle Output](#seeing-jqwik-reporting-in-gradle-output)
  - [Maven](#maven)
  - [Snapshot Releases](#snapshot-releases)
  - [Project without Build Tool](#project-without-build-tool)
- [Writing Properties](#writing-properties)
  - [Creating a Property](#creating-a-property)
    - [Failure Reporting](#failure-reporting)
    - [Additional Reporting Options](#additional-reporting-options)
    - [Platform Reporting with Reporter Object](#platform-reporting-with-reporter-object)
  - [Optional `@Property` Attributes](#optional-property-attributes)
    - [Setting Defaults for `@Property` Attributes](#setting-defaults-for-property-attributes)
  - [Creating an Example-based Test](#creating-an-example-based-test)
  - [Assertions](#assertions)
  - [Lifecycle](#lifecycle)
    - [Simple Property Lifecycle](#simple-property-lifecycle)
    - [Annotated Lifecycle Methods](#annotated-lifecycle-methods)
    - [Single Property Lifecycle](#single-property-lifecycle)
  - [Grouping Tests](#grouping-tests)
  - [Naming and Labeling Tests](#naming-and-labeling-tests)
  - [Tagging Tests](#tagging-tests)
  - [Disabling Tests](#disabling-tests)
- [Default Parameter Generation](#default-parameter-generation)
  - [Constraining Default Generation](#constraining-default-generation)
    - [Allow Null Values](#allow-null-values)
    - [String Length](#string-length)
    - [String not Blank](#string-not-blank)
    - [Character Sets](#character-sets)
    - [List, Set, Stream, Iterator, Map and Array Size](#list-set-stream-iterator-map-and-array-size)
    - [Unique Elements](#unique-elements)
    - [Integer Constraints](#integer-constraints)
    - [Decimal Constraints](#decimal-constraints)
  - [Constraining parameterized types](#constraining-parameterized-types)
  - [Providing variable types](#providing-variable-types)
  - [Self-Made Annotations](#self-made-annotations)
- [Customized Parameter Generation](#customized-parameter-generation)
  - [Parameter Provider Methods](#parameter-provider-methods)
  - [Provider Methods with Parameters](#provider-methods-with-parameters)
  - [Providing Arbitraries for Embedded Types](#providing-arbitraries-for-embedded-types)
  - [Static `Arbitraries` methods](#static-arbitraries-methods)
    - [Generate values yourself](#generate-values-yourself)
    - [Select or generate values randomly](#select-or-generate-values-randomly)
    - [Select randomly with Weights](#select-randomly-with-weights)
    - [Characters and Strings](#characters-and-strings)
    - [java.util.Random](#javautilrandom)
    - [Shuffling Permutations](#shuffling-permutations)
    - [Default Types](#default-types)
  - [Numeric Arbitrary Types](#numeric-arbitrary-types)
    - [Integrals](#integrals)
    - [Decimals](#decimals)
    - [Random Numeric Distribution](#random-numeric-distribution)
  - [Collections, Streams, Iterators and Arrays](#collections-streams-iterators-and-arrays)
  - [Collecting Values in a List](#collecting-values-in-a-list)
  - [Optional](#optional)
  - [Tuples of same base type](#tuples-of-same-base-type)
  - [Maps](#maps)
  - [Functional Types](#functional-types)
  - [Fluent Configuration Interfaces](#fluent-configuration-interfaces)
  - [Generate `null` values](#generate-null-values)
  - [Inject duplicate values](#inject-duplicate-values)
  - [Filtering](#filtering)
  - [Mapping](#mapping)
  - [Flat Mapping](#flat-mapping)
  - [Flat Mapping with Tuple Types](#flat-mapping-with-tuple-types)
  - [Randomly Choosing among Arbitraries](#randomly-choosing-among-arbitraries)
  - [Combining Arbitraries](#combining-arbitraries)
    - [Combining Arbitraries with Builder](#combining-arbitraries-with-builder)
    - [Flat Combination](#flat-combination)
  - [Uniqueness Constraints](#uniqueness-constraints)
  - [Ignoring Exceptions During Generation](#ignoring-exceptions-during-generation)
  - [Fix an Arbitrary's `genSize`](#fix-an-arbitrarys-gensize)
- [Recursive Arbitraries](#recursive-arbitraries)
  - [Probabilistic Recursion](#probabilistic-recursion)
    - [Using lazy() instead of lazyOf()](#using-lazy-instead-of-lazyof)
  - [Deterministic Recursion](#deterministic-recursion)
  - [Deterministic Recursion with `recursive()`](#deterministic-recursion-with-recursive)
- [Using Arbitraries Directly](#using-arbitraries-directly)
  - [Generating a Single Value](#generating-a-single-value)
  - [Generating a Stream of Values](#generating-a-stream-of-values)
  - [Generating all possible values](#generating-all-possible-values)
  - [Iterating through all possible values](#iterating-through-all-possible-values)
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
  - [Labeled Statistics](#labeled-statistics)
  - [Statistics Report Formatting](#statistics-report-formatting)
    - [Switch Statistics Reporting Off](#switch-statistics-reporting-off)
    - [Histograms](#histograms)
    - [Make Your Own Statistics Report Format](#make-your-own-statistics-report-format)
  - [Checking Coverage of Collected Statistics](#checking-coverage-of-collected-statistics)
    - [Check Percentages and Counts](#check-percentages-and-counts)
    - [Check Ad-hoc Query Coverage](#check-ad-hoc-query-coverage)
- [Providing Default Arbitraries](#providing-default-arbitraries)
  - [Simple Arbitrary Providers](#simple-arbitrary-providers)
  - [Arbitrary Providers for Parameterized Types](#arbitrary-providers-for-parameterized-types)
  - [Arbitrary Provider Priority](#arbitrary-provider-priority)
  - [Create your own Annotations for Arbitrary Configuration](#create-your-own-annotations-for-arbitrary-configuration)
    - [Arbitrary Configuration Example: `@Odd`](#arbitrary-configuration-example-odd)
- [Domain and Domain Context](#domain-and-domain-context)
  - [Domain example: American Addresses](#domain-example-american-addresses)
- [Generation from a Type's Interface](#generation-from-a-types-interface)
- [Generation of Edge Cases](#generation-of-edge-cases)
  - [Configuring Edge Case Injection](#configuring-edge-case-injection)
  - [Configuring Edge Cases Themselves](#configuring-edge-cases-themselves)
- [Exhaustive Generation](#exhaustive-generation)
- [Data-Driven Properties](#data-driven-properties)
- [Rerunning Falsified Properties](#rerunning-falsified-properties)
- [jqwik Configuration](#jqwik-configuration)
- [Additional Modules](#additional-modules)
  - [Web Module](#web-module)
    - [Email Address Generation](#email-address-generation)
  - [Time Module](#time-module)
    - [Generation of Dates](#generation-of-dates)
    - [Generation of Times](#generation-of-times)
    - [Generation of DateTimes](#generation-of-datetimes)
  - [Testing Module](#testing-module)
- [Advanced Topics](#advanced-topics)
  - [Implement your own Arbitraries and Generators](#implement-your-own-arbitraries-and-generators)
  - [Lifecycle Hooks](#lifecycle-hooks)
    - [Principles of Lifecycle Hooks](#principles-of-lifecycle-hooks)
    - [Lifecycle Hook Types](#lifecycle-hook-types)
    - [Lifecycle Execution Hooks](#lifecycle-execution-hooks)
    - [Other Hooks](#other-hooks)
    - [Lifecycle Storage](#lifecycle-storage)
- [API Evolution](#api-evolution)
- [Release Notes](#release-notes)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


## How to Use

__jqwik__ is an alternative test engine for the
[JUnit 5 platform](https://junit.org/junit5/docs/current/api/org/junit/platform/engine/TestEngine.html).
That means that you can use it either stand-alone or combine it with any other JUnit 5 engine, e.g.
[Jupiter (the standard engine)](https://junit.org/junit5/docs/current/user-guide/#dependency-metadata-junit-jupiter) or
[Vintage (aka JUnit 4)](https://junit.org/junit5/docs/current/user-guide/#dependency-metadata-junit-vintage).
All you have to do is add all needed engines to your `testImplementation` dependencies as shown in the
[gradle file](#gradle) below.

The latest release of __jqwik__ is deployed to [Maven Central](https://search.maven.org/search?q=g:net.jqwik).
Snapshot releases are created on a regular basis and can be fetched from 
[Sonatype's snapshot repository](https://oss.sonatype.org/content/repositories/snapshots). 

### Required Version of JUnit Platform

The minimum required version of the JUnit platform is `1.7.1`.

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

ext.junitPlatformVersion = '1.7.1'
ext.junitJupiterVersion = '5.7.1'

ext.jqwikVersion = '1.5.1'

compileTestJava {
    // To enable argument names in reporting and debugging
	options.compilerArgs += '-parameters'
}

test {
	useJUnitPlatform {
		includeEngines 'jqwik'
        
        // Or include several Junit engines if you use them
        // includeEngines 'jqwik', 'junit-jupiter', 'junit-vintage'

		// includeTags 'fast', 'medium'
		// excludeTags 'slow'
	}

	include '**/*Properties.class'
	include '**/*Test.class'
	include '**/*Tests.class'
}

dependencies {
    ...

    // aggregate jqwik dependency
    testImplementation "net.jqwik:jqwik:${jqwikVersion}"

    // Add if you also want to use the Jupiter engine or Assertions from it
    testImplementation "org.junit.jupiter:junit-jupiter:5.7.1"

    // Add any other test library you need...
    testImplementation "org.assertj:assertj-core:3.12.2"

}
```

With version 1.0.0 `net.jqwik:jqwik` has become an aggregating module to
simplify jqwik integration for standard users. If you want to be more explicit
about the real dependencies you can replace this dependency with

```
    testImplementation "net.jqwik:jqwik-api:${jqwikVersion}"
    testImplementation "net.jqwik:jqwik-web:${jqwikVersion}"
    testImplementation "net.jqwik:jqwik-time:${jqwikVersion}"
    testRuntime "net.jqwik:jqwik-engine:${jqwikVersion}"
```

In jqwik's samples repository you can find a rather minimal
[starter example for jqwik with Gradle](https://github.com/jlink/jqwik-samples/tree/main/jqwik-starter-gradle).

See [the Gradle section in JUnit 5's user guide](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle)
for more details on how to configure Gradle for the JUnit 5 platform.
There is also a comprehensive
[list of options for Gradle's `test` task](https://docs.gradle.org/current/userguide/java_plugin.html#sec:java_test).

#### Seeing jqwik Reporting in Gradle Output

Since Gradle does not yet support JUnit platform reporting --
[see this Github issue](https://github.com/gradle/gradle/issues/4605) --
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
    generation = RANDOMIZED       | parameters are randomly generated
    after-failure = PREVIOUS_SEED | use the previous seed
    when-fixed-seed = ALLOW       | fixing the random seed is allowed
    edge-cases#mode = MIXIN       | edge cases are generated first
    edge-cases#total = 0          | # of all combined edge cases
    edge-cases#tried = 0          | # of edge cases tried in current run
    seed = 1685744359484719817    | random seed to reproduce generated values
```

### Maven

Starting with version 2.22.0, Maven Surefire and Maven Failsafe provide native support
for executing tests on the JUnit Platform and thus for running _jqwik_ properties.
The configuration of Maven Surefire is described in
[the Maven section of JUnit 5's user guide](https://junit.org/junit5/docs/current/user-guide/#running-tests-build-maven).

Additionally you have to add the following dependency to your `pom.xml` file:

```
<dependencies>
    ...
    <dependency>
        <groupId>net.jqwik</groupId>
        <artifactId>jqwik</artifactId>
        <version>1.5.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

In jqwik's samples repository you can find a rather minimal
[starter example for jqwik with Maven](https://github.com/jlink/jqwik-samples/tree/main/jqwik-starter-maven).

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

- `jqwik-api-1.5.1.jar`
- `jqwik-engine-1.5.1.jar`
- `junit-platform-engine-1.7.1.jar`
- `junit-platform-commons-1.7.1.jar`
- `opentest4j-1.2.0.jar`

Optional jars are:
- `jqwik-web-1.5.1.jar`
- `jqwik-time-1.5.1.jar`



## Writing Properties

### Creating a Property

_Properties_ are the core concept of [property-based testing](/#properties).

You create a _Property_ by annotating a `public`, `protected`
or package-scoped method with
[`@Property`](/docs/1.5.1/javadoc/net/jqwik/api/Property.html).
In contrast to examples a property method is supposed to have one or
more parameters, all of which must be annotated with
[`@ForAll`](/docs/1.5.1/javadoc/net/jqwik/api/ForAll.html).

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

[Here](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/PropertyBasedTests.java)
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
- register an implementation of [`net.jqwik.api.SampleReportingFormat`](/docs/1.5.1/javadoc/net/jqwik/api/SampleReportingFormat.html)
  through Java’s `java.util.ServiceLoader` mechanism.


#### Additional Reporting Options

You can switch on additional reporting aspects by adding a
[`@Report(Reporting[])` annotation](/docs/1.5.1/javadoc/net/jqwik/api/Property.html)
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

[net.jqwik.api.Reporter](/docs/1.5.1/javadoc/net/jqwik/api/Reporter.html)
has different publishing methods.
Those with `report` in their name use jqwik's reporting mechanism and formats
described [above](#failure-reporting).


### Optional `@Property` Attributes

The [`@Property`](/docs/1.5.1/javadoc/net/jqwik/api/Property.html)
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
  that has failed in the previous run.

    - `AfterFailureMode.PREVIOUS_SEED` is the default. jqwik will use the same seed and thereby generate
      the same sequence of parameters as in the previous, failing run.
    - `AfterFailureMode.SAMPLE_ONLY` means that jqwik will only use the last shrunk example of parameters.
      This requires that all parameters can be serialized.
    - `AfterFailureMode.SAMPLE_FIRST` means that jqwik will use the last shrunk example of parameters first
      and then, if successful, go for a new randomly generated set of parameters.
    - `AfterFailureMode.RANDOM_SEED` makes jqwik use a new random seed even directly after a failure.
      This might lead to a "flaky" property that sometimes fails and sometimes succeeds.

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
after-failure = PREVIOUS_SEED
when-fixed-seed = ALLOW
edge-cases#mode = MIXIN 
edge-cases#total = 2 
edge-cases#tried = 2 
seed = 42859154278924201
```

#### Setting Defaults for `@Property` Attributes

If you want to set the defaults for all property methods in a container class
(and all the [groups](#grouping-tests) in it) you can use annotation
[`@PropertyDefaults`](/docs/1.5.1/javadoc/net/jqwik/api/PropertyDefaults.html).

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

1. Use jqwik's built-in defaults
2. which can be overridden in the [configuration file](#jqwik-configuration)
3. which can be changed in a container class' `@PropertyDefaults` annotation
4. which can be overridden by a method's
   [`@Property` annotation attributes](#optional-property-attributes).

### Creating an Example-based Test

_jqwik_ also supports example-based testing.
In order to write an example test annotate a `public`, `protected` or package-scoped method with
[`@Example`](/docs/1.5.1/javadoc/net/jqwik/api/Example.html).
Example-based tests work just like plain JUnit-style test cases.

A test case method must
- either return a `boolean` value that signifies success (`true`)
  or failure (`false`) of this test case.
- or return nothing (`void`) in which case you will probably
  use [assertions](#assertions) in order to verify the test condition.

[Here](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/ExampleBasedTests.java)
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
  [`@Property`](/docs/1.5.1/javadoc/net/jqwik/api/Property.html) or
  [`@Example`](/docs/1.5.1/javadoc/net/jqwik/api/Example.html).
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

- [`@BeforeContainer`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/BeforeContainer.html):
  _Static_ methods with this annotation will run exactly once before any property
  of a container class will be executed, even before the first instance of this class will be created.
- [`@AfterContainer`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/AfterContainer.html):
  _Static_ methods with this annotation will run exactly once after all properties
  of a container class have run.
- [`@BeforeProperty`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/BeforeProperty.html):
  Methods with this annotation will run once before each property or example.
  `@BeforeExample` is an alias with the same functionality.
- [`@AfterProperty`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/AfterProperty.html):
  Methods with this annotation will run once after each property or example.
  `@AfterExample` is an alias with the same functionality.
- [`@BeforeTry`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/BeforeTry.html):
  Methods with this annotation will run once before each try, i.e. execution
  of a property or example method.
- [`@AfterTry`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/AfterTry.html):
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

Have a look at [`PerProperty.Lifecycle`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/PerProperty.Lifecycle.html)
to find out which aspects of a property's lifecycle you can control.


### Grouping Tests

Within a containing test class you can group other containers by embedding
another non-static and non-private inner class and annotating it with `@Group`.
Grouping examples and properties is a means to improve the organization and
maintainability of your tests.

Groups can be nested and there lifecycle is also nested, that means that
the lifecycle of a test class is also applied to inner groups of that container.
Have a look at [this example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/TestsWithGroups.java):

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
[In the following example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/NamingExamples.java),
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
[the following example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/TaggingExamples.java).
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
[here](/docs/1.5.1/javadoc/net/jqwik/api/Tag.html)

### Disabling Tests

From time to time you might want to disable a test or all tests in a container
temporarily. You can do that by adding the
[`@Disabled`](/docs/1.5.1/javadoc/net/jqwik/api/Disabled.html) annotation
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

Be careful not to use the Jupiter annotation with the same name.
_Jqwik_ will refuse to execute methods that have Jupiter annotations.




## Default Parameter Generation

_jqwik_ tries to generate values for those property method parameters that are
annotated with [`@ForAll`](/docs/1.5.1/javadoc/net/jqwik/api/ForAll.html). If the annotation does not have a `value` parameter,
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
- `Map<K, V>` as long as `K` and `V` can also be provided by default generation.
- `HashMap<K, V>` as long as `K` and `V` can also be provided by default generation.
- `Map.Entry<K, V>` as long as `K` and `V` can also be provided by default generation.
- `java.util.Random`
- [Functional Types](#functional-types)
- Most types of package `java.time` are handled in the [Time Module](#time-module)

If you use [`@ForAll`](/docs/1.5.1/javadoc/net/jqwik/api/ForAll.html)
with a value, e.g. `@ForAll("aMethodName")`, the method
referenced by `"aMethodName"` will be called to provide an Arbitrary of the
required type (see [Parameter Provider Methods](#parameter-provider-methods)).

### Constraining Default Generation

Default parameter generation can be influenced and constrained by additional annotations,
depending on the requested parameter type.

#### Allow Null Values

- [`@WithNull(double value = 0.1)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/WithNull.html):
  Inject `null` into generated values with a probability of `value`.

  Works for all generated types.

#### String Length

- [`@StringLength(int value = 0, int min = 0, int max = 0)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/StringLength.html):
  Set either fixed length through `value` or configure the length range between `min` and `max`.

- [`@NotEmpty`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/NotEmpty.html):
  Set minimum length to `1`.

#### String not Blank

- [`@NotBlank`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/NotBlank.html):
  Strings must not be empty or only contain whitespace.


#### Character Sets

When generating chars any unicode character might be generated.

When generating Strings, however,
Unicode "noncharacters" and "private use characters"
will not be generated unless you explicitly include them using
`@Chars` or `@CharRange` (see below).

You can use the following annotations to restrict the set of allowed characters and even
combine several of them:

- [`@Chars(chars[] value = {})`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/Chars.html):
  Specify a set of characters.
  This annotation can be repeated which will add up all allowed chars.
- [`@CharRange(char from = 0, char to = 0)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/CharRange.html):
  Specify a start and end character.
  This annotation can be repeated which will add up all allowed chars.
- [`@NumericChars`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/NumericChars.html):
  Use digits `0` through `9`
- [`@LowerChars`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/LowerChars.html):
  Use lower case chars `a` through `z`
- [`@UpperChars`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/UpperChars.html):
  Use upper case chars `A` through `Z`
- [`@AlphaChars`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/AlphaChars.html):
  Lower and upper case chars are allowed.
- [`@Whitespace`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/Whitespace.html):
  All whitespace characters are allowed.

They work for generated `String`s and `Character`s.

#### List, Set, Stream, Iterator, Map and Array Size

- [`@Size(int value = 0, int min = 0, int max = 0)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/Size.html):
  Set either fixed size through `value` or configure the size range between `min` and `max`.

- [`@NotEmpty`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/NotEmpty.html):
  Set minimum size to `1`.


#### Unique Elements

- [`@UniqueElements`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/UniqueElements.html):
  Constrain a container object (`List`, `Set`, `Stream`, `Iterator` or array) so that its
  elements are unique or unique in relation to a certain feature.

  ```java
    @Property
    void uniqueInList(@ForAll @Size(5) @UniqueElements List<@IntRange(min = 0, max = 10) Integer> aList) {
        Assertions.assertThat(aList).doesNotHaveDuplicates();
        Assertions.assertThat(aList).allMatch(anInt -> anInt >= 0 && anInt <= 10);
    }
  ```

  Trying to generate a list with more than 11 elements would not work here.

  The following example will change the uniqueness criterion to use the first character
  of the list's String elements by providing a feature extraction function
  in the `by` attribute of the `@UniqueElements` annotation.

  ```java
    @Property
    void listOfStringsTheFirstCharacterOfWhichMustBeUnique(
      @ForAll @Size(max = 25) @UniqueElements(by = FirstChar.class) 
        List<@AlphaChars @StringLength(min = 1, max = 10) String> listOfStrings
    ) {
      Iterable<Character> firstCharacters = listOfStrings.stream().map(s -> s.charAt(0)).collect(Collectors.toList());
      Assertions.assertThat(firstCharacters).doesNotHaveDuplicates();
    }

    private class FirstChar implements Function<String, Object> {
      @Override
      public Object apply(String aString) {
        return aString.charAt(0);
      }
    }
  ```

  `@UniqueElements` can be used for parameters of type `List`, `Set`, `Stream`, `Iterator` or any array.


#### Integer Constraints

- [`@ByteRange(byte min = 0, byte max = Byte.MAX_VALUE)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/ByteRange.html):
  For `Byte` and `byte` only.
- [`@ShortRange(short min = 0, short max = Short.MAX_VALUE)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/ShortRange.html):
  For `Short` and `short` only.
- [`@IntRange(int min = 0, int max = Integer.MAX_VALUE)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/IntRange.html):
  For `Integer` and `int` only.
- [`@LongRange(long min = 0L, long max = Long.MAX_VALUE)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/LongRange.html):
  For `Long` and `long` only.
- [`@BigRange(String min = "", String max = "")`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/BigRange.html):
  For `BigInteger` generation.
- [`@Positive`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/Positive.html):
  Numbers larger than `0`. For all integral types.
- [`@Negative`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/Negative.html):
  Numbers lower than `0`. For all integral types.


#### Decimal Constraints

- [`@FloatRange(float min = 0.0f, minIncluded = true, float max = Float.MAX_VALUE, maxIncluded = true)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/FloatRange.html):
  For `Float` and `float` only.
- [`@DoubleRange(double min = 0.0, minIncluded = true, double max = Double.MAX_VALUE, boolean maxIncluded = true)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/DoubleRange.html):
  For `Double` and `double` only.
- [`@BigRange(String min = "", minIncluded = true, String max = "", maxIncluded = true)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/BigRange.html):
  For `BigDecimal` generation.
- [`@Scale(int value)`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/Scale.html):
  Specify the maximum number of decimal places. For all decimal types.
- [`@Positive`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/Positive.html):
  Numbers larger than `0.0`. For all decimal types.
- [`@Negative`](/docs/1.5.1/javadoc/net/jqwik/api/constraints/Negative.html):
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
[the following examples](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/VariableTypedPropertyExamples.java):

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

### Self-Made Annotations

You can [make your own annotations](http://junit.org/junit5/docs/5.0.0/user-guide/#writing-tests-meta-annotations)
instead of using _jqwik_'s built-in ones. BTW, '@Example' is nothing but a plain annotation using [`@Property`](/docs/1.5.1/javadoc/net/jqwik/api/Property.html)
as "meta"-annotation.

The following example provides an annotation to constrain String or Character generation to German letters only:

```java
@Target({ ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@NumericChars
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
through annotations is not enough. You want to control the creation
of values programmatically. The means to do that are _provider methods_.

### Parameter Provider Methods

Look at the
[following example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/ProvideMethodExamples.java):

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

The String value of the [`@ForAll`](/docs/1.5.1/javadoc/net/jqwik/api/ForAll.html)
annotation serves as a reference to a
method within the same class (or one of its superclasses or owning classes).
This reference refers to either the method's name or the String value
of the method's `@Provide` annotation.

The providing method has to return an object of type
[`@Arbitrary<T>`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html)
where `T` is the static type of the parameter to be provided. Optionally
the provider method can take tow optional parameters:

- a first parameter of type `TypeUsage` that describes the details of the target parameter to be provided
- a second parameter of type `ArbitraryProvider.SubtypeProvider`

These two objects can be used to get detailed information about the parameter,
like annotations and embedded type parameters, and to resolve other types,
usually from type parameters embedded in the original parameter. Use with care!

Parameter provision usually starts with a
[static method call to `Arbitraries`](#static-arbitraries-methods), maybe followed
by one or more [filtering](#filtering), [mapping](#mapping) or
[combining](#combining-arbitraries) actions.

### Provider Methods with Parameters

The examples of [provider methods](#parameter-provider-methods) you've seen so far
had no parameters. In more complicated scenarios, however, you may want to tune
an arbitrary depending on the concrete parameter to be generated.

Imagine you want to randomly choose one of your favourite primes; that's easy:

```java
@Property
void favouritePrimes(@ForAll("favouritePrimes") int aFavourite) {
}

@Provide
Arbitrary<Integer> favouritePrimes() {
	return Arbitraries.of(3, 5, 7, 13, 17, 23, 41, 101);
}
```

From time to time, though, you need it as a `BigInteger` instead of an `int`. 
You can kill both types with a single method:

```java
@Property
void favouritePrimesAsInts(@ForAll("favouritePrimes") int aFavourite) { ... }

@Property
void favouritePrimesAsBigInts(@ForAll("favouritePrimes") BigInteger aFavourite) { ... }

@Provide
Arbitrary<?> favouritePrimes(TypeUsage targetType) {
	Arbitrary<Integer> ints = Arbitraries.of(3, 5, 7, 13, 17, 23, 41);
	if (targetType.getRawType().equals(BigInteger.class)) {
		return ints.map(BigInteger::valueOf);
	}
	return ints;
}
```

Mind the parameters and return type of `favouritePrimes()`. 
The second parameter `ArbitraryProvider.SubtypeProvider subtypeProvider` is optional and can be left out;
it would be needed in case of variable subtypes that require their own dynamic resolution.


### Providing Arbitraries for Embedded Types

There is an alternative syntax to `@ForAll("methodRef")` using a `From` annotation:

```java
@Property
boolean concatenatingStringWithInt(
    @ForAll @From("shortStrings") String aShortString,
    @ForAll @From("10 to 99") int aNumber
) { ... }
```

Why this redundancy? Well, `@From` becomes a necessity when you want to provide
the arbitrary of an embedded type parameter. Consider this example:

```java
@Property
boolean joiningListOfStrings(@ForAll List<@From("shortStrings") String> listOfStrings) {
    String concatenated = String.join("", listOfStrings);
    return concatenated.length() <= 8 * listOfStrings.size();
}
```

Here, the list is created using the default list arbitrary, but the
String elements are generated using the arbitrary from the method `shortStrings`.

### Static `Arbitraries` methods

The starting point for generation usually is a static method call on class
[`Arbitraries`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html).

#### Generate values yourself

- [`Arbitrary<T> randomValue(Function<Random, T> generator)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#randomValue(java.util.function.Function)):
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

- [`Arbitrary<T> fromGenerator(RandomGenerator<T> generator)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#fromGenerator(net.jqwik.api.RandomGenerator)):
  If the number of _tries_ influences value generation or if you want
  to allow for [shrinking](#result-shrinking) you have to provide
  your own `RandomGenerator` implementation.

#### Select or generate values randomly

- [`Arbitrary<U> of(U... values)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#of(U...)):
  Choose randomly from a list of values. Shrink towards the first one.

- [`Arbitrary<U> ofSuppliers(Supplier<U>... valueSuppliers)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#ofSuppliers(java.util.function.Supplier...)):
  Choose randomly from a list of value suppliers and get the object from this supplier.
  This is useful when dealing with mutable objects where `Arbitrary.of(..)` would reuse a potentially changed object.

- [`Arbitrary<T> just(T constantValue)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#just(T)):
  Always provide the same constant value in each try. Mostly useful to combine with other arbitraries.

- [`Arbitrary<T> of(Class<T  extends Enum> enumClass)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#of(java.lang.Class)):
  Choose randomly from all values of an `enum`. Shrink towards first enum value.

- [`Arbitrary<T> create(Supplier<T> supplier)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#create(java.util.function.Supplier)):
  In each try use a new unshrinkable instance of type `T` using `supplier` to freshly create it.
  This is useful when dealing with mutable objects where `Arbitrary.just()` may reuse a changed object.

#### Select randomly with Weights

If you have a set of values to choose from with weighted probabilities, use
[`Arbitraries.frequency(...)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#frequency(net.jqwik.api.Tuple.Tuple2...)):

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
[the given example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/ChoosingExamples.java#L17)
the sum is 36, thus `"a"` will be generated with a probability of `1/36`
whereas `"d"` has a generation probability of `20/36` (= `5/9`).

Shrinking moves towards the start of the frequency list.

#### Characters and Strings

- [`StringArbitrary strings()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#strings())
- [`CharacterArbitrary chars()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#chars())

#### java.util.Random

- [`Arbitrary<Random> randoms()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#randoms()):
  Random instances will never be shrunk

#### Shuffling Permutations

- [`Arbitrary<List<T>> shuffle(T ... values)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#shuffle(T...)):
  Return unshrinkable permutations of the `values` handed in.

- [`Arbitrary<List<T>> shuffle(List<T> values)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#shuffle(java.util.List)):
  Return unshrinkable permutations of the `values` handed in.

#### Default Types

- [`Arbitrary<T> defaultFor(Class<T> type, Class<?> ... parameterTypes)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#defaultFor-java.lang.Class(java.lang.Class...)):
  Return the default arbitrary available for type `type` [if one is provided](#providing-default-arbitraries)
  by default. For parameterized types you can also specify the parameter types.

  Keep in mind, though, that the parameter types are lost in the type signature and therefore
  cannot be used in the respective [`@ForAll`](/docs/1.5.1/javadoc/net/jqwik/api/ForAll.html) property method parameter. Raw types and wildcards,
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

### Numeric Arbitrary Types

Creating an arbitrary for numeric values also starts by calling a static method
on class `Arbitraries`. There are two fundamental types of numbers: _integral_ numbers
and _decimal_ numbers. _jqwik_ supports all of Java's built-in number types.

Each type has its own [fluent interface](https://en.wikipedia.org/wiki/Fluent_interface)
but all numeric arbitrary types share some things:

- You can constrain their minimum and maximum values using `between(min, max)`,
  `greaterOrEqual(min)` and `lessOrEqual(max)`.
- You can determine the _target value_ through `shrinkTowards(target)`.
  This value is supposed to be the "center" of all possible values used for shrinking
  and as a mean for [random distributions](random-numeric-distribution).

#### Integrals

- [`ByteArbitrary bytes()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#bytes())
- [`ShortArbitrary shorts()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#shorts())
- [`IntegerArbitrary integers()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#integers())
- [`LongArbitrary longs()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#longs())
- [`BigIntegerArbitrary bigIntegers()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#bigIntegers())

#### Decimals

- [`FloatArbitrary floats()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#floats())
- [`DoubleArbitrary doubles()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#doubles())
- [`BigDecimalArbitrary bigDecimals()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#bigDecimals())

Decimal arbitrary types come with a few additional capabilities:

- You can include or exclude the borders using `between(min, minIncluded, max, maxIncluded)`,
  `greaterThan(minExcluded)` and `lessThan(maxExclude)`.
- You can set the _scale_, i.e. number of significant decimal places with `ofScale(scale)`.
  The default scale is `2`.

#### Random Numeric Distribution

With release `1.3.0` jqwik provides you with a means to influence the probability distribution
of randomly generated numbers. The way to do that is by calling
[`withDistribution(distribution)`](https://jqwik.net/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/NumericalArbitrary.html#withDistribution(net.jqwik.api.RandomDistribution)).
Currently three different distributions are supported:

- [`RandomDistribution.biased()`](https://jqwik.net/docs/1.5.1/javadoc/net/jqwik/api/RandomDistribution.html#biased()):
  This is the default.
  It generates values closer to the center of a numerical range with a higher probability.
  The bigger the range the stronger the bias.

- [`RandomDistribution.uniform()`](https://jqwik.net/docs/1.5.1/javadoc/net/jqwik/api/RandomDistribution.html#uniform()):
  This distribution will generate values across the allowed range
  with a uniform probability distribution.

- [`RandomDistribution.gaussian(borderSigma)`](https://jqwik.net/docs/1.5.1/javadoc/net/jqwik/api/RandomDistribution.html#gaussian(double)):
  A (potentially asymmetric) gaussian distribution --
  aka "normal distribution" () the mean of which is the specified center
  and the probability at the borders is `borderSigma` times _standard deviation_.
  Gaussian generation is approximately 10 times slower than biased or uniform generation.

- [`RandomDistribution.gaussian()`](https://jqwik.net/docs/1.5.1/javadoc/net/jqwik/api/RandomDistribution.html#gaussian()):
  A gaussian distribution with `borderSigma` of 3, i.e. approximately 99.7% of values are within the borders.

The specified distribution does not influence the generation of [edge cases](#generation-of-edge-cases).

The following example generates numbers between 0 and 20 using a gaussian probability distribution
with its mean at 10 and a standard deviation of about 3.3:

```java
@Property(generation = GenerationMode.RANDOMIZED)
@StatisticsReport(format = Histogram.class)
void gaussianDistributedIntegers(@ForAll("gaussians") int aNumber) {
    Statistics.collect(aNumber);
}

@Provide
Arbitrary<Integer> gaussians() {
    return Arbitraries
               .integers()
               .between(0, 20)
               .shrinkTowards(10)
               .withDistribution(RandomDistribution.gaussian());
}
```

Look at the statistics to see if it fits your expectation:
```
[RandomDistributionExamples:gaussianDistributedIntegers] (1000) statistics = 
       # | label | count | 
    -----|-------|-------|---------------------------------------------------------------------------------
       0 |     0 |    15 | ■■■■■
       1 |     1 |     8 | ■■
       2 |     2 |    12 | ■■■■
       3 |     3 |     9 | ■■■
       4 |     4 |    14 | ■■■■
       5 |     5 |    28 | ■■■■■■■■■
       6 |     6 |    38 | ■■■■■■■■■■■■■
       7 |     7 |    67 | ■■■■■■■■■■■■■■■■■■■■■■■
       8 |     8 |    77 | ■■■■■■■■■■■■■■■■■■■■■■■■■■
       9 |     9 |   116 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      10 |    10 |   231 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      11 |    11 |   101 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      12 |    12 |    91 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      13 |    13 |    60 | ■■■■■■■■■■■■■■■■■■■■
      14 |    14 |    45 | ■■■■■■■■■■■■■■■
      15 |    15 |    36 | ■■■■■■■■■■■■
      16 |    16 |    19 | ■■■■■■
      17 |    17 |    10 | ■■■
      18 |    18 |     7 | ■■
      19 |    19 |     1 | 
      20 |    20 |    15 | ■■■■■
```

You can notice that values `0` and `20` should have the lowest probability but they do not.
This is because they will be generated a few times as edge cases.


### Collections, Streams, Iterators and Arrays

Arbitraries for multi value types require to start with an `Arbitrary` instance for the element type.
You can then create the corresponding multi value arbitrary from there:

- [`ListArbitrary<T> Arbitrary.list()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#list())
- [`SetArbitrary<T> Arbitrary.set()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#set())
- [`StreamArbitrary<T> Arbitrary.streamOf()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#stream())
- [`IteratorArbitrary<T> Arbitrary.iterator()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#iterator())
- [`ArrayArbitrary<T, A> Arbitrary.array(Class<A> arrayClass)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#array(java.lang.Class))


### Collecting Values in a List

If you do not want any random combination of values in your list - as
can be done with `Arbitrary.list()` - you have the possibility to collect random values
in a list until a certain condition is fulfilled.
[`Arbitrary.collect(Predicate condition)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#collect(java.util.function.Predicate))
is what you need in those cases.

Imagine you need a list of integers the sum of which should be at least `1000`.
Here's how you could do that:

```java
Arbitrary<Integer> integers = Arbitraries.integers().between(1, 100);
Arbitrary<List<Integer>> collected = integers.collect(list -> sum(list) >= 1000);
```

### Optional

Using [`Arbitrary.optional()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#optional())
allows to generate an optional of any type.
`Optional.empty()` values are injected with a probability of `0.05`, i.e. 1 in 20.

### Tuples of same base type

If you want to generate tuples of the same base types that also use the same generator, that's how you can do it:

```java
Arbitrary<Tuple.Tuple2> integerPair = Arbitrary.integers().between(1, 25).tuple2();
```

There's a method for tuples of length 1 to 5:

- [`Arbitrary.tuple1()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#tuple1())
- [`Arbitrary.tuple2()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#tuple2())
- [`Arbitrary.tuple3()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#tuple3())
- [`Arbitrary.tuple4()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#tuple4())
- [`Arbitrary.tuple5()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#tuple5())

### Maps

Generating instances of type `Map` is a bit different since two arbitraries
are needed, one for the key and one for the value. Therefore you have to use
[`Arbitraries.maps(...)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#maps-net.jqwik.api.Arbitrary(net.jqwik.api.Arbitrary)) like this:

```java
@Property
void mapsFromNumberToString(@ForAll("numberMaps")  Map<Integer, String> map) {
    Assertions.assertThat(map.keySet()).allMatch(key -> key >= 0 && key <= 1000);
    Assertions.assertThat(map.values()).allMatch(value -> value.length() == 5);
}

@Provide
Arbitrary<Map<Integer, String>> numberMaps() {
    Arbitrary<Integer> keys = Arbitraries.integers().between(1, 100);
    Arbitrary<String> values = Arbitraries.strings().alpha().ofLength(5);
    return Arbitraries.maps(keys, values);
}
```

For generating individual `Map.Entry` instances there is
[`Arbitraries.entries(...)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#maps(net.jqwik.api.Arbitrary,net.jqwik.api.Arbitrary)).

### Functional Types

Interfaces that have a single (non default) method are considered to be
_Functional types_; they are sometimes called _SAM_ types for "single abstract method".
If a functional type is used as a `@ForAll`-parameter _jqwik_ will automatically
generate instances of those functions. The generated functions have the following
characteristics:

- Given the input parameters they will produce the same return values.
- The return values are generated using the type information and constraints
  in the parameter.
- Given different input parameters they will _usually_ produce different
  return values.
- Shrinking of generated functions will try constant functions, i.e. functions
  that always return the same value.

Let's look at an example:

```java
@Property
void fromIntToString(@ForAll Function<Integer, @StringLength(5) String> function) {
    assertThat(function.apply(42)).hasSize(5);
    assertThat(function.apply(1)).isEqualTo(function.apply(1));
}
```

This works for any _interface-based_ functional types, even your own.
If you [register a default provider](#providing-default-arbitraries) for
a functional type with a priority of 0 or above, it will take precedence.

If the functions need some specialized arbitrary for return values or if you
want to fix the function's behaviour for some range of values, you can define
the arbitrary manually:

```java
@Property
void emptyStringsTestFalse(@ForAll("predicates") Predicate<String> predicate) {
    assertThat(predicate.test("")).isFalse();
}

@Provide
Arbitrary<Predicate<String>> predicates() {
    return Functions
        .function(Predicate.class)
        .returns(Arbitraries.of(true, false))
        .when(parameters -> parameters.get(0).equals(""), parameters -> false);
}
```

In this example the generated predicate will always return `false` when
given an empty String and randomly choose between `true` and `false` in
all other cases.

### Fluent Configuration Interfaces

Most specialized arbitrary interfaces provide special methods to configure things
like size, length, boundaries etc. Have a look at the Java doc for the following types,
which are organized in a flat hierarchy:

- [NumericalArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/BigDecimalArbitrary.html)
    - [BigDecimalArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/BigDecimalArbitrary.html)
    - [BigIntegerArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/BigIntegerArbitrary.html)
    - [ByteArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/ByteArbitrary.html)
    - [CharacterArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/CharacterArbitrary.html)
    - [DoubleArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/DoubleArbitrary.html)
    - [FloatArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/FloatArbitrary.html)
    - [IntegerArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/IntegerArbitrary.html)
    - [LongArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/LongArbitrary.html)
    - [ShortArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/ShortArbitrary.html)
- [SizableArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/SizableArbitrary.html)
    - [MapArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/MapArbitrary.html)
    - [StreamableArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/StreamableArbitrary.html)
        - [SetArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/SetArbitrary.html)
        - [ListArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/ListArbitrary.html)
        - [StreamArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/StreamArbitrary.html)
        - [IteratorArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/IteratorArbitrary.html)
        - [ArrayArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/ArrayArbitrary.html)
- [StringArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/StringArbitrary.html)
- [FunctionArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/FunctionArbitrary.html)
- [TypeArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/TypeArbitrary.html)


Here are a
[two examples](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/FluentConfigurationExamples.java)
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
call [`Arbitrary.injectNull(double probability)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#injectNull(double)).
The following provider method creates an arbitrary that will return a `null` String
in about 1 of 100 generated values.

```java
@Provide 
Arbitrary<String> stringsWithNull() {
  return Arbitraries.strings(0, 10).injectNull(0.01);
}
```

### Inject duplicate values

Sometimes it is important that your generator will create _a previous value_
again in order to trigger certain scenarios or branches in your code.
Imagine you want to check if your carefully hand-crafted String comparator really
is as symmetric as it's supposed to be:

```java
Comparator<String> comparator = (s1, s2) -> {
    if (s1.length() + s2.length() == 0) return 0;
    if (s1.compareTo(s2) > 0) {
        return 1;
    } else {
        return -1;
    }
};

@Property
boolean comparing_strings_is_symmetric(@ForAll String first, @ForAll String second) {
    int comparison = comparator.compare(first, second);
    return comparator.compare(second, first) == -comparison;
}
```

The property (most probably) succeeds and will give you confidence in your code.
Or does it? Natural scepticism makes you check some statistics:

```java
@Property(edgeCases = EdgeCasesMode.NONE)
boolean comparing_strings_is_symmetric(@ForAll String first, @ForAll String second) {
    int comparison = comparator.compare(first, second);
    String comparisonRange = comparison < 0 ? "<0" : comparison > 0 ? ">0" : "=0";
    String empty = first.isEmpty() || second.isEmpty() ? "empty" : "not empty";
    Statistics.collect(comparisonRange, empty);
    return comparator.compare(second, first) == -comparison;
}
```

The following output

```
[comparing strings is symmetric] (1000) statistics = 
    <0 not empty (471) : 47,10 %
    >0 not empty (456) : 45,60 %
    <0 empty     ( 37) :  3,70 %
    >0 empty     ( 35) :  3,50 %
    =0 empty     (  1) :  0,10 %
```

reveals that our generated test data is missing one combination:
Comparison value of 0 for non-empty strings. In theory a generic String arbitrary
could generate the same non-empty string but it's highly unlikely.
This is where we have to think about raising the probability of the same
value being generated more often:

```
@Property
boolean comparing_strings_is_symmetric(@ForAll("pair") Tuple2<String, String> pair) {
    String first = pair.get1();
    String second = pair.get2();
    int comparison = comparator.compare(first, second);
    return comparator.compare(second, first) == -comparison;
}

@Provide
Arbitrary<Tuple2<String, String>> pair() {
    return Arbitraries.strings().injectDuplicates(0.1).tuple2();
}
```

This will cover the missing case and will reveal a bug in the comparator.
Mind that you have to make sure that the _same generator instance_ is being used
for the two String values - using `tuple2()` does that.


### Filtering

If you want to include only part of all the values generated by an arbitrary,
use
[`Arbitrary.filter(Predicate<T> filterPredicate)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#filter(java.util.function.Predicate)).
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

### Mapping

Sometimes it's easier to start with an existing arbitrary and use its generated values to
build other objects from them. In that case, use
[`Arbitrary.map(Function<T, U> mapper)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#map(java.util.function.Function)).
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
[following example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/FlatMappingExamples.java#L26):

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
  [`flatMap`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#flatMap(java.util.function.Function))
  combined with a tuple type
  [like this](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/FlatMappingExamples.java#L32):


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

[In this example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/OneOfExamples.java)
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
[`Combinators.combine()`](/docs/1.5.1/javadoc/net/jqwik/api/Combinators.html#combine(net.jqwik.api.Arbitrary,net.jqwik.api.Arbitrary))
with up to eight arbitraries.
[Create an issue on github](https://github.com/jlink/jqwik/issues) if you need more than eight.

[The following example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/MappingAndCombinatorExamples.java#L25)
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
```
Shrunk Sample (<n> steps)
-------------------------
  aPerson: aaaaaaaaaaaaaaaaaaaaa:100
```

The `Combinators.combine` method accepts up to 8 parameters of type Arbitrary.
If you need more you have a few options:

- Consider to group some parameters into an object of their own and change your design
- Generate inbetween arbitraries e.g. of type `Tuple` and combine those in another step
- Introduce a build for your domain object and combine them
  [in this way](#combining-arbitraries-with-builder)


#### Combining Arbitraries with Builder

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
[Combinators.withBuilder(Supplier)](/docs/1.5.1/javadoc/net/jqwik/api/Combinators.html#withBuilder(java.util.function.Supplier))
and [Combinators.withBuilder(Arbitrary)](/docs/1.5.1/javadoc/net/jqwik/api/Combinators.html#withBuilder(net.jqwik.api.Arbitrary))
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


### Uniqueness Constraints

In many problem domains there exist identifying features or attributes 
that must not appear more than once.
In those cases the multiple generation of objects can be restricted by
either [annotating parameters with `@UniqueElements`](#unique-elements)
or by using one of the many `uniqueness(..)` configuration methods for 
collections and collection-like types:

- `ListArbitrary<T>.uniqueElements(Function<T, Object>)`
- `ListArbitrary<T>.uniqueElements()`
- `SetArbitrary<T>.uniqueElements(Function<T, Object>)`
- `StreamArbitrary<T>.uniqueElements(Function<T, Object>)`
- `StreamArbitrary<T>.uniqueElements()`
- `IteratorArbitrary<T>.uniqueElements(Function<T, Object>)`
- `IteratorArbitrary<T>.uniqueElements()`
- `ArrayArbitrary<T, A>.uniqueElements(Function<T, Object>)`
- `ArrayArbitrary<T, A>.uniqueElements()`
- `MapArbitrary<K, V>.uniqueKeys(Function<K, Object>)`
- `MapArbitrary<K, V>.uniqueValues(Function<V, Object>)`
- `MapArbitrary<K, V>.uniqueValues()`

The following examples demonstrates how to generate a list of `Person` objects
whose names must be unique:

```java
@Property
void listOfPeopleWithUniqueNames(@ForAll("people") List<Person> people) {
  List<String> names = people.stream().map(p -> p.name).collect(Collectors.toList());
  Assertions.assertThat(names).doesNotHaveDuplicates();
}

@Provide
Arbitrary<List<Person>> people() {
  Arbitrary<String> names = Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(20);
  Arbitrary<Integer> ages = Arbitraries.integers().between(0, 120);
  
  Arbitrary<Person> persons = Combinators.combine(names, ages).as((name, age) -> new Person(name, age));
  return persons.list().uniqueElements(p -> p.name);
};
```

### Ignoring Exceptions During Generation

Once in a while, usually when [combining generated values](#combining-arbitraries),
it's difficult to figure out in advance all the constraints that make the generation of objects
valid. In a good object-oriented model, however, the objects themselves --
i.e. their constructors or factory methods -- take care that only valid objects
can be created. The attempt to create an invalid value will be rejected with an
exception.

As a good example have a look at JDK's `LocalDate` class, which allows to instantiate dates
using `LocalDate.of(int year, int month, int dayOfMonth)`.
In general `dayOfMonth` can be between `1` and `31` but trying to generate a
"February 31" will throw a `DateTimeException`. Therefore, when you want to randomly
generated dates between "January 1 1900" and "December 31 2099" you have two choices:

- Integrate all rules about valid dates -- including leap years! -- into your generator.
  This will probably require a cascade of flat-mapping `years` to `months` to `days`.
- Rely on the factory method's built-in validation and just ignore thrown
  `DateTimeException` instances:

```java
@Provide
Arbitrary<LocalDate> datesBetween1900and2099() {
  Arbitrary<Integer> years = Arbitraries.integers().between(1900, 2099);
  Arbitrary<Integer> months = Arbitraries.integers().between(1, 12);
  Arbitrary<Integer> days = Arbitraries.integers().between(1, 31);
  
  return Combinators.combine(years, months, days)
  	  .as(LocalDate::of)
  	  .ignoreException(DateTimeException.class);
}
```

### Fix an Arbitrary's `genSize`

Some generators (e.g. most number generators) are sensitive to the
`genSize` value that is used when creating them.
The default value for `genSize` is the number of tries configured for the property
they are used in. If there is a need to influence the behaviour of generators
you can do so by using
[`Arbitrary.fixGenSize(int)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#fixGenSize(int)).




## Recursive Arbitraries

Sometimes it seems like a good idea to compose arbitraries and thereby
recursively calling an arbitrary creation method. Generating recursive data types
is one application field but you can also use it for other stuff.

### Probabilistic Recursion

Look at the
[following example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/RecursiveExamples.java)
which generates sentences by recursively adding words to a sentence:

```java
@Property
@Report(Reporting.GENERATED)
boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
	return aSentence.endsWith(".");
	// return !aSentence.contains("x"); // using this condition instead 
	                                    // should shrink to "AAAAx."
}

@Provide
Arbitrary<String> sentences() {
	return Arbitraries.lazyOf(
		() -> word().map(w -> w + "."),
		this::sentence,
		this::sentence,
		this::sentence
	);
}

private Arbitrary<String> sentence() {
	return Combinators.combine(sentences(), word())
					  .as((s, w) -> w + " " + s);
}

private StringArbitrary word() {
    return Arbitraries.strings().alpha().ofLength(5);
}
```

There are two things to which you must pay attention:

- It is important to use 
  [`lazyOf(suppliers)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#lazyOf(java.util.function.Supplier,java.util.function.Supplier...))
  instead of the seemingly simpler 
  [`oneOf(arbitraries)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#oneOf(net.jqwik.api.Arbitrary,net.jqwik.api.Arbitrary...)).
  Otherwise _jqwik_'s attempt to build the arbitrary would result in a stack overflow.

- Every recursion needs one or more base cases in order to stop recursion at some point.
  Here, the base case is `() -> word().map(w -> w + ".")`.
  Base cases must have a high enough probability,
  otherwise a stack overflow will get you during value generation.

- The supplier `() -> sentence` is used three times to raise its probability
  and thus create longer sentences.

There is also a caveat of which you should be aware:
Never use this construct if suppliers make use of variable state
like method parameters or changing instance members.
In those cases use [`lazy()`](#using-lazy-instead-of-lazyof) as explained below.

#### Using lazy() instead of lazyOf()

There is an _almost equivalent_ variant to the example above:

```java
@Property
boolean sentencesEndWithAPoint(@ForAll("sentences") String aSentence) {
    return aSentence.endsWith(".");
}

@Provide
Arbitrary<String> sentences() {
    Arbitrary<String> sentence = Combinators.combine(
        Arbitraries.lazy(this::sentences),
        word()
    ).as((s, w) -> w + " " + s);

    return Arbitraries.oneOf(
        word().map(w -> w + "."),
        sentence,
        sentence,
        sentence
    );
}

private StringArbitrary word() {
    return Arbitraries.strings().alpha().ofLength(5);
}
``` 

The disadvantage of `lazy()` combined with `oneOf()` or `frequencyOf()`
is its worse shrinking behaviour compared to `lazyOf()`.
Therefore, choose `lazyOf()` whenever you can.

### Deterministic Recursion

An alternative to probabilistic recursion shown above, is to use deterministic
recursion with a counter to determine the base case. If you then use an arbitrary value
for the counter, the generated sentences will be very similar, and you can often forgo
using `Arbitraries.lazyOf()` or `Arbitraries.lazy()`:

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
[`Arbitraries.recursive(...)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#recursive(java.util.function.Supplier,java.util.function.Function,int)).
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

	return length.flatMap(depth -> Arbitraries.recursive(
		() -> lastWord,
		this::prependWord,
		depth
	));
}

private Arbitrary<String> prependWord(Arbitrary<String> sentence) {
    return Combinators.combine(word(), sentence).as((w, s) -> w + " " + s);
}
```



## Using Arbitraries Directly

Most of the time arbitraries are used indirectly, i.e. _jqwik_ uses them under
the hood to inject generated values as parameters. There are situations, though,
in which you might want to generate values directly.

### Generating a Single Value

Getting a single random value out of an arbitrary is easy and can be done
with [`Arbitrary.sample()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#sample()):

```java
Arbitrary<String> strings = Arbitraries.of("string1", "string2", "string3");
String aString = strings.sample();
assertThat(aString).isIn("string1", "string2", "string3");
```

Among other things, this allows you to use jqwik's generation functionality
with other test engines like Jupiter.
Mind that _jqwik_ uses a default `genSize` of 1000 under the hood and that
the `Random` object will be either taken from the current property's context or
freshly instantiated if used outside a property.

### Generating a Stream of Values

Getting a stream of generated values is just as easy with [`Arbitrary.sampleStream()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#sampleStream()):

```java
List<String> values = Arrays.asList("string1", "string2", "string3");
Arbitrary<String> strings = Arbitraries.of(values);
Stream<String> streamOfStrings = strings.sampleStream().limit(100);

assertThat(streamOfStrings).allMatch(values::contains);
```

### Generating all possible values

There are a few cases when you don't want to generate individual values from an
arbitrary but use all possible values to construct another arbitrary. This can be achieved through
[`Arbitrary.allValues()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#allValues()).

Return type is `Optional<Stream<T>>` because _jqwik_ can only perform this task if
[exhaustive generation](#exhaustive-generation) is doable.


### Iterating through all possible values

You can also use an arbitrary to iterate through all values it specifies.
Use
[`Arbitrary.forEachValue(Consumer action)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#forEachValue(java.util.function.Consumer)).
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
[simple stack implementation](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/stateful/mystack/MyStringStack.java):

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

- [`Push`](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/stateful/mystack/PushAction.java):
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
  	public MyStringStack run(MyStringStack stack) {
  		int sizeBefore = stack.size();
  		stack.push(element);
  		Assertions.assertThat(stack.isEmpty()).isFalse();
  		Assertions.assertThat(stack.size()).isEqualTo(sizeBefore + 1);
  		return stack;
  	}
  
  	@Override
  	public String toString() { return String.format("push(%s)", element); }
  }
  ``` 

- [`Pop`](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/stateful/mystack/PopAction.java):
  If (and only if) the stack is not empty, pop the element on top off the stack.
  The size of the stack should have decreased by 1.

  ```java
  class PopAction implements Action<MyStringStack> {
    
        @Override
        public boolean precondition(MyStringStack stack) {
            return !stack.isEmpty();
        }
    
        @Override
        public MyStringStack run(MyStringStack stack) {
            int sizeBefore = stack.size();
            String topBefore = stack.top();
    
            String popped = stack.pop();
            Assertions.assertThat(popped).isEqualTo(topBefore);
            Assertions.assertThat(stack.size()).isEqualTo(sizeBefore - 1);
            return stack;
        }
    
        @Override
        public String toString() { return "pop"; }
  }
  ``` 

- [`Clear`](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/stateful/mystack/ClearAction.java):
  Remove all elements from the stack which should be empty afterwards.

  ```java
  class ClearAction implements Action<MyStringStack> {

        @Override
        public MyStringStack run(MyStringStack stack) {
            stack.clear();
            Assertions.assertThat(stack.isEmpty()).isTrue();
            return stack;
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
[_jqwik_ property](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/stateful/mystack/MyStringStackProperties.java):

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
		return Arbitraries.just(new ClearAction());
	}

	private Arbitrary<Action<MyStringStack>> pop() {
		return Arbitraries.just(new PopAction());
	}
}
```

The interesting API elements are
- [`ActionSequence`](/docs/1.5.1/javadoc/net/jqwik/api/stateful/ActionSequence.html):
  A generic collection type especially crafted for holding and shrinking of a list of actions.
  As a convenience it will apply the actions to a state-based object when you call `run(state)`.

- [`Arbitraries.sequences()`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#sequences(net.jqwik.api.Arbitrary)):
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
org.opentest4j.AssertionFailedError: 
  Run failed after following actions:
      push(AAAAA)
      push(AAAAA)
      push(AAAAA)
      clear
    final state: ["AAAAA", "AAAAA"]
```

### Number of actions

_jqwik_ will vary the number of generated actions according to the number
of `tries` of your property. For the default of 1000 tries a sequence will
have 32 actions. If need be you can specify the number of actions
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
org.opentest4j.AssertionFailedError: 
  Run failed after following actions:
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

[The following property](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/AssumptionExamples.java)
works only on strings that are not equal:

```java
@Property
boolean comparingUnequalStrings(
        @ForAll @StringLength(min = 1, max = 10) String string1,
        @ForAll @StringLength(min = 1, max = 10) String string2
) {
    Assume.that(!string1.equals(string2));

    return string1.compareTo(string2) != 0;
}
```

This is a reasonable use of
[`Assume.that(boolean condition)`](/docs/1.5.1/javadoc/net/jqwik/api/Assume.html#that(boolean))
because most generated value sets will pass through.

Have a look at a seemingly similar example:

```java
@Property
boolean findingContainedStrings(
        @ForAll @StringLength(min = 1, max = 10) String container,
        @ForAll @StringLength(min = 1, max = 5) String contained
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
generation = RANDOMIZED
after-failure = PREVIOUS_SEED
when-fixed-seed = ALLOW
edge-cases#mode = MIXIN 
seed = 1066117555581106850
```

The problem is that - given a random generation of two strings - only in very few cases
one string will be contained in the other. _jqwik_ will report a property as `exhausted`
if the ratio between generated and accepted parameters is higher than 5. You can change
the maximum discard ratio by specifying a parameter `maxDiscardRatio` in the
[`@Property`](/docs/1.5.1/javadoc/net/jqwik/api/Property.html) annotation.
That's why changing to `@Property(maxDiscardRatio = 100)` in the previous example
will probably result in a successful property run, even though only a handful
cases - of 1000 generated - will actually be checked.

In many cases turning up the accepted discard ration is a bad idea. With some creativity
we can often avoid the problem by generating out test data a bit differently.
Look at this variant of the above property, which also uses
[`Assume.that()`](/docs/1.5.1/javadoc/net/jqwik/api/Assume.html#that(boolean))
but with a much lower discard ratio:

```java
@Property
boolean findingContainedStrings_variant(
        @ForAll @StringLength(min = 5, max = 10) String container,
        @ForAll @IntRange(min = 1, max = 5) int length,
        @ForAll @IntRange(min = 0, max = 9) int startIndex
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
AssertionFailedError: Property [stringShouldBeShrunkToAA] falsified with sample {0="aa"}

tries = 38 
checks = 38 
...
Shrunk Sample (5 steps)
-------------------------
  aString: "AA"

Original Sample
---------------
  aString: "RzZ"
```

In this case the _original sample_ could be any string between 2 and 5 chars,
whereas the final _sample_ should be exactly `AA` since this is the shortest
failing string and `A` has the lowest numeric value of all allowed characters.

### Integrated Shrinking

_jqwik_'s shrinking approach is called _integrated shrinking_, as opposed to _type-based shrinking_
which most property-based testing tools use.
The general idea and its advantages are explained
[here](http://hypothesis.works/articles/integrated-shrinking/).

Consider a somewhat more complicated example:

```java
@Property
boolean shrinkingCanTakeAWhile(@ForAll("first") String first, @ForAll("second") String second) {
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
AssertionFailedError: Property [shrinkingCanTakeLong] falsified with sample {0="a", 1="000"}}

checks = 20 
tries = 20 
...
Shrunk Sample (3 steps)
-----------------------
  first: "a"
  second: "000"

Original Sample
---------------
  first: "h"
  second: "901"
```

This example also shows that sometimes there is no single "smallest example".
Depending on the starting random seed, this property will shrink to either
`{0="a", 1="000"}`, `{0="ah", 1="00"}` or `{0="aah", 1="0"}`, all of which
are considered to be the smallest possible for jqwik's current way of
measuring a sample's size.

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
shrinking bound reached = after 1000 steps.
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
If zero is outside the bounds of generation the closest number to zero -
either the min or max value - is used as a target for shrinking.
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

Currently shrinking targets are supported for all [number types](#numeric-arbitrary-types).





## Collecting and Reporting Statistics

In many situations you'd like to know if _jqwik_ will really generate
the kind of values you expect and if the frequency and distribution of
certain value classes meets your testing needs.
[`Statistics.collect()`](/docs/1.5.1/javadoc/net/jqwik/api/statistics/Statistics.html#collect(java.lang.Object...))
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
[MyTest:simpleStats] (1000) statistics = 
    FLOOR       (158) : 16 %
    HALF_EVEN   (135) : 14 %
    DOWN        (126) : 13 %
    UP          (120) : 12 %
    HALF_UP     (118) : 12 %
    CEILING     (117) : 12 %
    UNNECESSARY (117) : 12 %
    HALF_DOWN   (109) : 11 %
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
[MyTest:integerStats] (1000) statistics = 
    negative (506) : 51 %
    positive (494) : 49 %
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
[MyTest:combinedIntegerStats] (1000) statistics = 
    negative even big   (222) : 22 %
    positive even big   (201) : 20 %
    positive odd big    (200) : 20 %
    negative odd big    (194) : 19 %
    negative even small ( 70) :  7 %
    positive odd small  ( 42) :  4 %
    negative odd small  ( 38) :  4 %
    positive even small ( 33) :  3 %
```

And, of course, you can combine different generated parameters into
one statistical group:

```java
@Property
void twoParameterStats(
    @ForAll @Size(min = 1, max = 10) List<Integer> aList,
    @ForAll @IntRange(min = 0, max = 10) int index
) {
    Statistics.collect(aList.size() > index ? "index within size" : null);
}
```

```
[MyTest:twoParameterStats] (1000) statistics = 
    index within size (507) : 51 %
```

As you can see, collected `null` values are not being reported.

[Here](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/statistics/StatisticsExamples.java)
are a couple of examples to try out.

### Labeled Statistics

If you want more than one statistic in a single property, you must give them labels for differentiation:

```java
@Property
void severalStatistics(@ForAll @IntRange(min = 1, max = 10) Integer anInt) {
    String range = anInt < 3 ? "small" : "large";
    Statistics.label("range").collect(range);
    Statistics.label("value").collect(anInt);
}
```

produces the following reports:

```
[MyTest:labeledStatistics] (1000) range = 
    large (783) : 78 %
    small (217) : 22 %

[MyTest:labeledStatistics] (1000) value = 
    1  (115) : 12 %
    5  (109) : 11 %
    10 (105) : 11 %
    4  (103) : 10 %
    2  (102) : 10 %
    3  ( 99) : 10 %
    6  ( 97) : 10 %
    8  ( 92) :  9 %
    7  ( 91) :  9 %
    9  ( 87) :  9 %
```

### Statistics Report Formatting

There is a
[`@StatisticsReport`](/docs/1.5.1/javadoc/net/jqwik/api/statistics/StatisticsReport.html)
annotation that allows to change statistics report
formats or to even switch it off. The annotation can be used on property methods
or on container classes.

The `value` attribute is of type
[StatisticsReportMode.OFF](/docs/1.5.1/javadoc/net/jqwik/api/statistics/StatisticsReport.StatisticsReportMode.html) and can have one of:

- __`STANDARD`__: Use jqwik's standard reporting format. This is used anyway
  if you leave the annotation away.
- __`OFF`__: Switch statistics reporting off
- __`PLUG_IN`__: Plug in your homemade format. This is the default so that
  you only have to provide the `format` attribute
  [as shown below](#plug-in-your-own-statistics-report-format)

When using [labeled statistics](#labeled-statistics) you can set mode and format
for each label individually by using the annotation attribute `@StatisticsReport.value`.

#### Switch Statistics Reporting Off

You can switch off statistics report as simple as that:

```java
@Property
@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
void queryStatistics(@ForAll int anInt) {
	Statistics.collect(anInt);
}
```

#### Histograms

_jqwik_ comes with two report formats to display collected data as histograms:
[`Histogram`](/docs/1.5.1/javadoc/net/jqwik/api/statistics/Histogram.html)
and [`NumberRangeHistogram`](/docs/1.5.1/javadoc/net/jqwik/api/statistics/NumberRangeHistogram.html).

`Histogram` displays the collected raw data as a histogram:

```java
@Property(generation = GenerationMode.RANDOMIZED)
@StatisticsReport(format = Histogram.class)
void integers(@ForAll("gaussians") int aNumber) {
    Statistics.collect(aNumber);
}

@Provide
Arbitrary<Integer> gaussians() {
    return Arbitraries
               .integers()
               .between(0, 20)
               .shrinkTowards(10)
               .withDistribution(RandomDistribution.gaussian());
}
```

```
[HistogramExamples:integers] (1000) statistics = 
       # | label | count | 
    -----|-------|-------|---------------------------------------------------------------------------------
       0 |     0 |    13 | ■■■■
       1 |     1 |    13 | ■■■■
       2 |     2 |    15 | ■■■■■
       3 |     3 |     6 | ■■
       4 |     4 |    10 | ■■■
       5 |     5 |    22 | ■■■■■■■
       6 |     6 |    49 | ■■■■■■■■■■■■■■■■
       7 |     7 |    60 | ■■■■■■■■■■■■■■■■■■■■
       8 |     8 |   102 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
       9 |     9 |   100 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      10 |    10 |   233 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      11 |    11 |   114 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      12 |    12 |    74 | ■■■■■■■■■■■■■■■■■■■■■■■■■
      13 |    13 |    64 | ■■■■■■■■■■■■■■■■■■■■■
      14 |    14 |    43 | ■■■■■■■■■■■■■■
      15 |    15 |    32 | ■■■■■■■■■■
      16 |    16 |    16 | ■■■■■
      17 |    17 |     8 | ■■
      18 |    18 |     7 | ■■
      19 |    20 |    19 | ■■■■■■
```

`NumberRangeHistogram` clusters the collected raw data into ranges:

```java
@Property(generation = GenerationMode.RANDOMIZED)
@StatisticsReport(format = NumberRangeHistogram.class)
void integersInRanges(@ForAll @IntRange(min = -1000, max = 1000) int aNumber) {
    Statistics.collect(aNumber);
}
```

```
[HistogramExamples:integersInRanges] (1000) statistics = 
       # |         label | count | 
    -----|---------------|-------|---------------------------------------------------------------------------------
       0 | [-1000..-900[ |    20 | ■■■■■
       1 |  [-900..-800[ |    17 | ■■■■
       2 |  [-800..-700[ |    16 | ■■■■
       3 |  [-700..-600[ |     8 | ■■
       4 |  [-600..-500[ |    12 | ■■■
       5 |  [-500..-400[ |    14 | ■■■
       6 |  [-400..-300[ |    17 | ■■■■
       7 |  [-300..-200[ |    46 | ■■■■■■■■■■■
       8 |  [-200..-100[ |    59 | ■■■■■■■■■■■■■■
       9 |     [-100..0[ |   315 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      10 |      [0..100[ |   276 | ■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■
      11 |    [100..200[ |    47 | ■■■■■■■■■■■
      12 |    [200..300[ |    49 | ■■■■■■■■■■■■
      13 |    [300..400[ |    25 | ■■■■■■
      14 |    [400..500[ |    14 | ■■■
      15 |    [500..600[ |    13 | ■■■
      16 |    [600..700[ |    15 | ■■■
      17 |    [700..800[ |    14 | ■■■
      18 |    [800..900[ |    11 | ■■
      19 |   [900..1000] |    12 | ■■■
```

Both types can be subclassed to override behaviour like the number of buckets,
the maximum drawing range of the bar, the order of elements, the label of a bucket
and the header of the label column.

#### Make Your Own Statistics Report Format

In order to format statistics to your own liking you have to create an
implementation of type
[StatisticsReportFormat](/docs/1.5.1/javadoc/net/jqwik/api/statistics/StatisticsReportFormat.html) and

```java
@Property
@StatisticsReport(format = MyStatisticsFormat.class)
void statisticsWithHandMadeFormat(@ForAll Integer anInt) {
	String range = anInt < 0 ? "negative" : anInt > 0 ? "positive" : "zero";
	Statistics.collect(range);
}

class MyStatisticsFormat implements StatisticsReportFormat {
	@Override
	public List<String> formatReport(List<StatisticsEntry> entries) {
		return entries.stream()
					  .map(e -> String.format("%s: %d", e.name(), e.count()))
					  .collect(Collectors.toList());
	}
}
```

Running this property should produce a report similar to that:

```
[StatisticsExamples:statisticsWithHandMadeFormat] (1000) statistics = 
    negative: 520
    positive: 450
    zero: 30
```

### Checking Coverage of Collected Statistics

Just looking at the statistics of generated values might not be sufficient.
Sometimes you want to make sure that certain scenarios are being covered by
your generators and fail a property otherwise. In _jqwik_ you do that
by first
[collecting statistics](#collecting-and-reporting-statistics)
and then specifying coverage conditions for those statistics.

#### Check Percentages and Counts

The following example does that for generated values of enum `RoundingMode`:

```java
@Property(generation = GenerationMode.RANDOMIZED)
void simpleStats(@ForAll RoundingMode mode) {
	Statistics.collect(mode);

	Statistics.coverage(coverage -> {
		coverage.check(RoundingMode.CEILING).percentage(p -> p > 5.0);
		coverage.check(RoundingMode.FLOOR).count(c -> c > 2);
	});
}
```

The same thing is possible for values collected with a specific label
and in a fluent API style.

```java
@Property(generation = GenerationMode.RANDOMIZED)
void labeledStatistics(@ForAll @IntRange(min = 1, max = 10) Integer anInt) {
	String range = anInt < 3 ? "small" : "large";

	Statistics.label("range")
			  .collect(range)
			  .coverage(coverage -> coverage.check("small").percentage(p -> p > 20.0));

	Statistics.label("value")
			  .collect(anInt)
			  .coverage(coverage -> coverage.check(0).count(c -> c > 0));
}
```

Start by looking at
[`Statistics.coverage()`](/docs/1.5.1/javadoc/net/jqwik/api/statistics/Statistics.html#coverage(java.util.function.Consumer))
to see all the options you have for checking percentages and counts.

#### Check Ad-hoc Query Coverage

Instead of classifying values at collection time you have the possibility to
collect the raw data and use a query when doing coverage checking:

```java
@Property
@StatisticsReport(StatisticsReport.StatisticsReportMode.OFF)
void queryStatistics(@ForAll int anInt) {
	Statistics.collect(anInt);

	Statistics.coverage(coverage -> {
		Predicate<List<Integer>> isZero = params -> params.get(0) == 0;
		coverage.checkQuery(isZero).percentage(p -> p > 5.0);
	});
}
```

In those cases you probably want to
[switch off reporting](#switch-statistics-reporting-off),
otherwise the reports might get very long - and without informative value.




## Providing Default Arbitraries

Sometimes you want to use a certain, self-made `Arbitrary` for one of your own domain
classes, in all of your properties, and without having to add `@Provide` method
to all test classes. _jqwik_ enables this feature by using
Java’s `java.util.ServiceLoader` mechanism. All you have to do is:

- Implement the interface [`ArbitraryProvider`](/docs/1.5.1/javadoc/net/jqwik/api/providers/ArbitraryProvider.html).<br/>
  The implementing class _must_ have a default constructor without parameters.
- Register the implementation class in file

  ```
  META-INF/services/net.jqwik.api.providers.ArbitraryProvider
  ```

_jqwik_ will then add an instance of your arbitrary provider into the list of
its default providers. Those default providers are considered for every test parameter annotated
with [`@ForAll`](/docs/1.5.1/javadoc/net/jqwik/api/ForAll.html) that has no explicit `value`.
By using this mechanism you can also replace the default providers
packaged into _jqwik_.

### Simple Arbitrary Providers

A simple provider is one that delivers arbitraries for types without type variables.
Consider the class [`Money`](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/defaultprovider/Money.java):

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
[`MoneyArbitraryProvider`](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/defaultprovider/MoneyArbitraryProvider.java):

```java
package my.own.provider;

public class MoneyArbitraryProvider implements ArbitraryProvider {
	@Override
	public boolean canProvideFor(TypeUsage targetType) {
		return targetType.isOfType(Money.class);
	}

	@Override
	public Set<Arbitrary<?>> provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider) {
		Arbitrary<BigDecimal> amount = Arbitraries.bigDecimals()
				  .between(BigDecimal.ZERO, new BigDecimal(1_000_000_000))
				  .ofScale(2);
		Arbitrary<String> currency = Arbitraries.of("EUR", "USD", "CHF");
		return Collections.singleton(Combinators.combine(amount, currency).as(Money::new));
	}
}
```

in file
[`META-INF/services/net.jqwik.api.providers.ArbitraryProvider`](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/resources/META-INF/services/net.jqwik.api.providers.ArbitraryProvider)
with such an entry:

```
my.own.provider.MoneyArbitraryProvider
```

The
[following property](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/defaultprovider/MoneyProperties.java)
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
		return subtypeProvider.apply(innerType).stream()
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
		return Collections.singleton(Arbitraries.just("A String"));
	}
}
```

If you register this class as arbitrary provider any `@ForAll String` will
be resolved to `"A String"`.

### Create your own Annotations for Arbitrary Configuration

All you can do [to constrain default parameter generation](#constraining-default-generation)
is adding another annotation to a parameter or its parameter types. What if the existing parameters
do not suffice your needs? Is there a way to enhance the set of constraint annotations? Yes, there is!

The mechanism you can plug into is similar to what you do when
[providing your own default arbitrary providers](#providing-default-arbitraries). That means:

1. Create an implementation of an interface, in this case
   [`ArbitraryConfigurator`](/docs/1.5.1/javadoc/net/jqwik/api/configurators/ArbitraryConfigurator.html).
2. Register the implementation using using Java’s `java.util.ServiceLoader` mechanism.

#### Arbitrary Configuration Example: `@Odd`

To demonstrate the idea let's create an annotation `@Odd` which will constrain any integer
generation to only generate odd numbers. First things first, so here's
the [`@Odd` annotation](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/Odd.java)
together with the
[configurator implementation](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/OddConfigurator.java):

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
[register the implementation](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/resources/META-INF/services/net.jqwik.api.configurators.ArbitraryConfigurator),
the [following example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/arbitraryconfigurator/OddProperties.java)
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
[DomainContext](/docs/1.5.1/javadoc/net/jqwik/api/domains/DomainContext.html)
and [AbstractDomainContextBase](/docs/1.5.1/javadoc/net/jqwik/api/domains/AbstractDomainContextBase.html).


### Domain example: American Addresses

Let's say that US postal addresses play a crucial role in the software that we're developing.
That's why there are a couple of classes that represent important domain concepts:
`Street`, `State`, `City` and `Address`. Since we have to generate instances of those classes
for our properties, we collect all arbitrary provision code in
[AmericanAddresses](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/domains/AmericanAddresses.java).
Now look at
[this example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/domains/AddressProperties.java):

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

	private final String name;
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
at the [complete example](https://github.com/jlink/jqwik/blob/1.5.1/documentation/src/test/java/net/jqwik/docs/types/TypeArbitraryExamples.java)
and check the following api entry points:

- [UseType](/docs/1.5.1/javadoc/net/jqwik/api/constraints/UseType.html)
- [UseTypeMode](/docs/1.5.1/javadoc/net/jqwik/api/constraints/UseTypeMode.html)
- [Arbitraries.forType()](/docs/1.5.1/javadoc/net/jqwik/api/Arbitraries.html#forType(java.lang.Class))
- [TypeArbitrary](/docs/1.5.1/javadoc/net/jqwik/api/arbitraries/TypeArbitrary.html)




## Generation of Edge Cases

It's well-known that many programming bugs and specification gaps happen at the border
of allowed value ranges. For example, in the domain of integer numbers the minimum
(`Integer.MIN_VALUE`) and maximum (`Integer.MAX_VALUE`) belong in the set of those
[_edge cases_](https://en.wikipedia.org/wiki/Edge_case). Many people use the term a bit
more loosely and also include other special values that tend to have a higher chance
of revealing implementation problems, like `0` for numbers or an empty string.

_jqwik_ has special treatment for edge cases. Most base type arbitraries come with
their set of edge cases. You can find out about edge cases by asking an arbitrary
about it. Run the following example

```java
@Example
void printEdgeCases() {
    System.out.println(Arbitraries.integers().edgeCases());
    System.out.println(Arbitraries.strings().withCharRange('a', 'z').edgeCases());
    System.out.println(Arbitraries.floats().list().edgeCases());
}
```

and you will see this output:

```
EdgeCases[-2, -1, 0, 2, 1, -2147483648, 2147483647]
EdgeCases["a", "z", ""]
EdgeCases[[], [0.0], [1.0], [-1.0], [0.01], [-0.01], [-3.4028235E38], [3.4028235E38]]
```

You may notice that edge cases are not just hard-coded values but also make use
of underlying arbitraries' edge cases to arrive at new ones.
That's why a list of floats arbitrary has single element lists of floats as edge cases.
Edge cases are also being combined and permuted when
[`Combinators`](#combining-arbitraries) are used.
Also, most methods from `Arbitrary` - like `map()`, `filter()` and `flatMap()` - provide
sensible edge cases behaviour.
Thus, your self-made domain-specific arbitraries get edge cases automatically.

_jqwik_ makes use of edge cases in two ways:

1. Whenever an arbitrary is asked to produce a value it will mix-in edge cases
   from time to time.
2. By default jqwik will mix the _combination of permutations of edge cases_
   of a property's parameters with purely randomized generation of parameters.
   You can even try all edge case combinations first as the next property shows.

```java
@Property(edgeCases = EdgeCasesMode.FIRST)
void combinedEdgeCasesOfTwoParameters(
    @ForAll List<Integer> intList,
    @ForAll @IntRange(min = -100, max = 0) int anInt
) {
    String parameters = String.format("%s, %s", intList, anInt);
    System.out.println(parameters);
}
```

Run it and have a look at the output.

### Configuring Edge Case Injection

How jqwik handles edge cases generation can be controlled with
[an annotation property](#optional-property-attributes) and
[a configuration parameter](#jqwik-configuration).

To switch it off for a single property, use:

```java
@Property(edgeCases = EdgeCasesMode.NONE)
void combinedEdgeCasesOfTwoParameters(
    @ForAll List<Integer> intList,
    @ForAll @IntRange(min = -100, max = 0) int anInt
) {
    // whatever you do   
}
```

If you want to suppress edge case generation for a single arbitrary that's also possible:
Just use `Arbitrary.withoutEdgeCases()`. 
Running the following property will regularly create empty lists - because - this is one
of the default list edge cases, but it will not create integer values of `0`, `1`, `-1` etc.
with higher probability.

```java
@Property
void noNumberEdgeCases(@ForAll List<@From("withoutEdgeCases") Integer> intList) {
  System.out.println(intList);
}

@Provide
Arbitrary<Integer> withoutEdgeCases() {
  return Arbitraries.integers().withoutEdgeCases();}
```

### Configuring Edge Cases Themselves

Besides switching edge cases completely off, you can also filter some edge cases out,
include only certain ones or add new ones. 
This is done through [`Arbitrary.edgeCases(config)`](/docs/1.5.1/javadoc/net/jqwik/api/Arbitrary.html#edgeCases(java.util.function.Consumer)). 
Here's an example that shows how to add a few "special" strings to a generator:

```java
@Property
void stringsWithSpecialEdgeCases(@ForAll("withSpecials") String aString) {
  System.out.println(aString);
}

@Provide
Arbitrary<String> withSpecials() {
  return Arbitraries.strings()
          .alpha().ofMinLength(1).ofMaxLength(10)
          .edgeCases(stringConfig -> {
            stringConfig.add("hello", "hallo", "hi");
          });
}
```


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
  [Property.generation attribute](#optional-property-attributes).
  The default generation mode can be set in the [configuration file](jqwik-configuration).
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
- Lists, sets, streams, optionals and maps of the above
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
The [`Table` class](/docs/1.5.1/javadoc/net/jqwik/api/Table.html)
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

_jqwik_ uses JUnit's [configuration parameters](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params) to configure itself.

The simplest form is a file `junit-platform.properties` in your classpath in which you can configure
a few basic parameters:

```
jqwik.database = .jqwik-database             # The database file in which to store data of previous runs.
                                             # Set to empty to fully disable test run recording.
jqwik.tries.default = 1000                   # The default number of tries for each property
jqwik.maxdiscardratio.default = 5            # The default ratio before assumption misses make a property fail
jqwik.reporting.onlyfailures = false         # Set to true if only falsified properties should be reported
jqwik.reporting.usejunitplatform = false     # Set to true if you want to use platform reporting
jqwik.failures.runfirst = false              # Set to true if you want to run the failing tests from the previous run first
jqwik.failures.after.default = PREVIOUS_SEED # Set default behaviour for falsified properties:
                                             # PREVIOUS_SEED, SAMPLE_ONLY or SAMPLE_FIRST
jqwik.generation.default = AUTO              # Set default behaviour for generation:
                                             # AUTO, RANDOMIZED, or EXHAUSTIVE
jqwik.edgecases.default = MIXIN              # Set default behaviour for edge cases generation:
                                             # FIRST, MIXIN, or NONE
jqwik.shrinking.default = BOUNDED            # Set default shrinking behaviour:
                                             # BOUNDED, FULL, or OFF
jqwik.shrinking.bounded.seconds = 10         # The maximum number of seconds to shrink if
                                             # shrinking behaviour is set to BOUNDED
jqwik.seeds.whenfixed = ALLOW                # How a test should act when a seed is fixed. Can set to ALLOW, WARN or FAIL
                                             # Useful to prevent accidental commits of fixed seeds into source control.                                             
```

Prior releases of _jqwik_ used a custom `jqwik.properties`. While this continues to work, it is deprecated
and will be removed in a future release. Some names have changed:

- `database` -> `jqwik.database`
- `defaultTries` -> `jqwik.tries.default`
- `defaultMaxDiscardRatio` -> `jqwik.maxdiscardratio.default`
- `useJunitPlatformReporter` -> `jqwik.reporting.usejunitplatform`
- `defaultAfterFailure` -> `jqwik.failures.after.default`
- `reportOnlyFailures` -> `jqwik.reporting.onlyfailures`
- `defaultGeneration` -> `jqwik.generation.default`
- `defaultEdgeCases` -> `jqwik.edgecases.default`
- `defaultShrinking` -> `jqwik.shrinking.default`
- `boundedShrinkingSeconds` -> `jqwik.shrinking.bounded.seconds`
- `runFailuresFirst` -> `jqwik.failures.runfirst`




## Additional Modules

_jqwik_ comes with a few additional modules:

- The [`web` module](#web-module)
- The [`time` module](#time-module)
- The [`testing` module](#testing-module)

### Web Module

This module's artefact name is `jqwik-web`. It's supposed to provide arbitraries,
default generation and annotations for web related types. Currently only
[email generation](#email-address-generation) is supported.

This module is part of jqwik's default dependencies.


#### Email Address Generation

To generate email addresses you can either

- call up the static method [`Web.emails()`](/docs/1.5.1/javadoc/net/jqwik/web/api/Web.html#emails()).
  The return type is [`EmailArbitrary`](/docs/1.5.1/javadoc/net/jqwik/web/api/EmailArbitrary.html)
  which provides a few configuration methods.

- or use the [`@Email`](/docs/1.5.1/javadoc/net/jqwik/web/api/Email.html)
  annotation on `@ForAll` parameters as in the examples below.

An email address consists of two parts: `local-part` and `host`.
The complete email address is therefore `local-part@host`.
The `local-part` can be `unquoted` or `quoted` (in double quotes), which allows for more characters to be used.
The `host` can be a standard domain name, but also an IP (v4 or v6) address, surrounded by square brackets `[]`.

For example, valid email addresses are:
```
abc@example
abc@example.com
" "@example.example
"admin@server"@[192.168.201.0]
admin@[32::FF:aBc:79a:83B:FFFF:345]
```

By default, only addresses with unquoted local part and domain hosts are
generated (e.g. `me@myhost.com`), because many - if not most - applications
and web forms only accept those.

The `@Email` annotation comes with a few configuration attributes:
- `quotedLocalPart` also allow quoted local parts to be generated
- `ipv4Host` also allow ipv4 addresses to be generated in the host part
- `ipv6Host` also allow ipv4 addresses to be generated in the host part
  
You can use it as follows:

```java
@Property
void defaultEmailAddresses(@ForAll @Email String email) {
    assertThat(email).contains("@");
}

@Property
void restrictedEmailAddresses(@ForAll @Email(quotedLocalPart = true, ipv4Host = true) String email) {
    assertThat(email).contains("@");
}
```


### Time Module

This module's artefact name is `jqwik-time`. It's supposed to provide arbitraries,
default generation and annotations for date and time types.

This module is part of jqwik's default dependencies.

The module provides: 

- [Generation of Dates](#generation-of-dates)
    - [default generation](#default-generation-of-dates) for date-related Java types
    - [Programmatic API](#programmatic-generation-of-dates) to configure date-related types
    
- [Generation of Times](#generation-of-times)
    - [default generation](#default-generation-of-times) for time-related Java types
    - [Programmatic API](#programmatic-generation-of-times) to configure time-related types
    
- [Generation of DateTimes](#generation-of-datetimes)
    - [default generation](#default-generation-of-datetimes) for date time-related Java types
    - [Programmatic API](#programmatic-generation-of-datetimes) to configure date time-related types

#### Generation of Dates

##### Default Generation of Dates

Default generation currently is supported for `LocalDate`, `Year`, `YearMonth`,
`DayOfWeek`, `MonthDay` and `Period`. Here's a small example:

```java
@Property
void generateLocalDatesWithAnnotation(@ForAll @DateRange(min = "2019-01-01", max = "2020-12-31") LocalDate localDate) {
  assertThat(localDate).isBetween(
    LocalDate.of(2019, 1, 1),
    LocalDate.of(2020, 12, 31)
  );
}
```

The following annotations can be used to constrain default generation of the enumerated types:

- [`@DateRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/DateRange.html)
- [`@YearRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/YearRange.html)
- [`@YearMonthRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/YearMonthRange.html)
- [`@MonthRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/MonthRange.html)
- [`@MonthDayRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/MonthDayRange.html)
- [`@DayOfMonthRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/DayOfMonthRange.html)
- [`@DayOfWeekRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/DayOfWeekRange.html)
- [`@LeapYears`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/LeapYears.html)
- [`@PeriodRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/PeriodRange.html)

`@DateRange`, `@MonthDayRange`, `@YearMonthRange` and `@PeriodRange` 
use the ISO format for date strings. 
Examples: `2013-05-25`, `--05-25`, `2013-05` and `P1Y2M15D`.

##### Programmatic Generation of Dates

Programmatic generation of dates and date-related types always starts with a static
method call on class [`Dates`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html).
For example:

```java
@Property
void generateLocalDates(@ForAll("dates") LocalDate localDate) {
  assertThat(localDate).isAfter(LocalDate.of(2000, 12, 31));
}

@Provide
Arbitrary<LocalDate> dates() {
  return Dates.dates().atTheEarliest(LocalDate.of(2001, 1, 1));
}
```

Here's the list of available methods:

- [`LocalDateArbitrary dates()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#dates())
- [`CalendarArbitrary datesAsCalendar()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#datesAsCalendar())
- [`DateArbitrary datesAsDate()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#datesAsDate())
- [`YearArbitrary years()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#years())
- [`Arbitrary<Month> months()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#months())
- [`Arbitrary<Integer> daysOfMonth()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#daysOfMonth())
- [`Arbitrary<DayOfWeek> daysOfWeek()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#daysOfWeek())
- [`YearMonthArbitrary yearMonths()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#yearMonths())
- [`MonthDayArbitrary monthDays()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#monthDays())
- [`PeriodArbitrary periods()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#periods())

###### LocalDateArbitrary

- The target type is `LocalDate`.
- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)`.

###### CalendarArbitrary

- The target type is `Calendar`. The time-related parts of `Calendar` instances are set to 0.
- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)`.

###### DateArbitrary

- The target type is `Date`. The time-related parts of `Date` instances are set to 0.
- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.
- You can limit the generation of days of week to only a few days of week using `onlyDaysOfWeek(daysOfWeek)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)`.

###### YearArbitrary

- By default, only years between 1900 and 2500 are generated.
- You can constrain its minimum and maximum value using `between(min, max)`.

###### YearMonthArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- By default, only years between 1900 and 2500 are generated.
- You can constrain the minimum and maximum value for years using `yearBetween(min, max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can decide whether leap years to generate or not using `leapYears(withLeapYears)`.

###### MonthDayArbitrary

- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for months using `monthBetween(min, max)`.
- You can limit the generation of months to only a few months using `onlyMonths(months)`.
- You can constrain the minimum and maximum value for days of month using `dayOfMonthBetween(min, max)`.

###### PeriodArbitrary

- By default, periods between `-1000 years` and `1000 years` are generated.
- Generated periods are always in a "reduced" form, 
  i.e. months are always between `-11` and `11` and days between `-30` and `30`.   
- You can constrain the minimum and maximum value using `between(Period min, Period max)`.
- If you really want something like `Period.ofDays(3000)` generate an integer
  and map it on `Period`.

#### Generation of Times

##### Default Generation of Times

Default generation currently is supported for `LocalTime`, `OffsetTime`, `ZoneOffset`,
`TimeZone`, `ZoneId` and `Duration`. Here's a small example:

```java
@Property
void generateLocalTimesWithAnnotation(@ForAll @TimeRange(min = "01:32:21", max = "03:49:32") LocalTime localTime) {
    assertThat(time).isAfterOrEqualTo(LocalTime.of(1, 32, 21));
    assertThat(time).isBeforeOrEqualTo(LocalTime.of(3, 49, 32));
}
```

The following annotations can be used to constrain default generation of the enumerated types:

- [`@TimeRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/TimeRange.html)
- [`@OffsetRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/OffsetRange.html)
- [`@HourRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/HourRange.html)
- [`@MinuteRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/MinuteRange.html)
- [`@SecondRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/SecondRange.html)
- [`@Precision`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/Precision.html)
- [`@DurationRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/DurationRange.html)

`@TimeRange`, `@OffsetRange` and `@DurationRange` 
use the standard format of their classes. 
Examples:

- `@TimeRange`: "01:32:31.394920222", "23:43:21" or "03:02" (See [`LocalTime.parse`](https://docs.oracle.com/javase/8/docs/api/java/time/LocalTime.html#parse-java.lang.CharSequence-))
- `@OffsetRange`: "-09:00", "+3", "+11:22:33" or "Z" (See [`ZoneOffset.of`](https://docs.oracle.com/javase/8/docs/api/java/time/ZoneOffset.html#of-java.lang.String-))
- `@DurationRange`: "PT-3000H-39M-22.123111444S", "PT1999H22M11S" or "P2DT3H4M" (See [`Duration.parse`](https://docs.oracle.com/javase/8/docs/api/java/time/Duration.html#parse-java.lang.CharSequence-))

##### Programmatic Generation of Times

Programmatic generation of times always starts with a static
method call on class [`Times`](/docs/1.5.1/javadoc/net/jqwik/time/api/Times.html).
For example:

```java
@Property
void generateLocalTimes(@ForAll("times") LocalTime localTime) {
  assertThat(localTime).isAfter(LocalTime.of(13, 53, 21));
}

@Provide
Arbitrary<LocalTime> times() {
  return Times.times().atTheEarliest(LocalTime.of(13, 53, 22));
}
```

Here's the list of available methods:

- [`LocalTimeArbitrary times()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Times.html#times())
- [`OffsetTimeArbitrary offsetTimes()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Times.html#offsetTimes())
- [`ZoneOffsetArbitrary zoneOffsets()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Times.html#zoneOffsets())
- [`Arbitrary<TimeZone> timeZones()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Times.html#timeZones())
- [`Arbitrary<ZoneId> zoneIds()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Times.html#zoneIds())
- [`DurationArbitrary durations()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Times.html#durations())

###### LocalTimeArbitrary

- The target type is `LocalTime`.
- By default, precision is seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for hours using `hourBetween(min, max)`.
- You can constrain the minimum and maximum value for minutes using `minuteBetween(min, max)`.
- You can constrain the minimum and maximum value for seconds using `secondBetween(min, max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.

###### OffsetTimeArbitrary

- The target type is `OffsetTime`.
- By default, precision is seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
- You can constrain the minimum and maximum time value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the minimum and maximum value for hours using `hourBetween(min, max)`.
- You can constrain the minimum and maximum value for minutes using `minuteBetween(min, max)`.
- You can constrain the minimum and maximum value for seconds using `secondBetween(min, max)`.
- You can constrain the minimum and maximum value for offset using `offsetBetween(min, max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.

###### ZoneOffsetArbitrary

- The target type is `ZoneOffset`.
- You can constrain its minimum and maximum value using `between(min, max)`.

###### DurationArbitrary

- The target type is `Duration`.
- By default, precision is seconds.
- You can constrain its minimum and maximum value using `between(min, max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.

#### Generation of DateTimes

##### Default Generation of DateTimes

Default generation currently is supported for `LocalDateTime`. 
Here's a small example:

```java
@Property
void generateLocalDateTimesWithAnnotation(@ForAll @DateTimeRange(min = "2019-01-01T01:32:21", max = "2020-12-31T03:11:11") LocalDateTime localDateTime) {
  assertThat(localDateTime).isBetween(
    LocalDateTime.of(2019, 1, 1, 1, 32, 21),
    LocalDateTime.of(2020, 12, 31, 3, 11, 11)
  );
}
```

The following annotations can be used to constrain default generation of the enumerated types:

- [`@DateTimeRange`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/DateTimeRange.html)
- [`@Precision`](/docs/1.5.1/javadoc/net/jqwik/time/api/constraints/Precision.html)

`@DateTimeRange` uses the standard format of its class. 
Example: `2013-05-25T01:34:22.231`.

##### Programmatic Generation of DateTimes

Programmatic generation of date times always starts with a static
method call on class [`DateTimes`](/docs/1.5.1/javadoc/net/jqwik/time/api/DateTimes.html).
For example:

```java
@Property
void generateLocalDateTimes(@ForAll("dateTimes") LocalDateTime localDateTime) {
  assertThat(localDateTime).isAfter(LocalDateTime.of(2013, 5, 25, 19, 48, 32));
}

@Provide
Arbitrary<LocalDateTime> dateTimes() {
  return DateTimes.dateTimes().atTheEarliest(LocalDateTime.of(2013, 5, 25, 19, 48, 33));
}
```

Here's the list of available methods:

- [`LocalDateTimeArbitrary dateTimes()`](/docs/1.5.1/javadoc/net/jqwik/time/api/Dates.html#dateTimes())

###### LocalDateTimeArbitrary

- The target type is `LocalDateTime`.
- By default, only years between 1900 and 2500 are generated.
- By default, precision is seconds. If you don't explicitly set the precision and use min/max values with precision milliseconds/microseconds/nanoseconds, the precision of your min/max value is implicitly set.
- You can constrain its minimum and maximum value using `between(min, max)`, `atTheEarliest(min)` and `atTheLatest(max)`.
- You can constrain the precision using `ofPrecision(ofPrecision)`.



### Testing Module

This module's artefact name is `jqwik-testing`. It provides a few helpful methods
and classes for generator writers to test their generators - including 
edge cases and shrinking.

This module is _not_ in jqwik's default dependencies. It's usually added as a
test-implementation dependency.




## Advanced Topics

### Implement your own Arbitraries and Generators

Looking at _jqwik_'s most prominent interfaces -- `Arbitrary` and `RandomGenerator` -- you might
think that rolling your own implementations is a reasonable thing to do.
I'd like to tell you that it _never_ is, but I've learned that "never" is a word you should never use.
There's just too many things to consider when implementing a new type of `Arbitrary`
to make it work smoothly with the rest of the framework.

Therefore, use the innumerable features to combine existing arbitraries into your special one.
If you cannot figure out how to create an arbitrary with the desired behaviour
either [ask on stack overflow](https://stackoverflow.com/questions/tagged/jqwik)
or [open a Github issue](https://github.com/jlink/jqwik/issues).

### Lifecycle Hooks

Similar to [Jupiter's Extension Model](https://junit.org/junit5/docs/current/user-guide/#extensions)
_jqwik_ provides a means to extend and change the way how properties and containers are being
configured, run and reported on. The API -- interfaces, classes and annotations -- for accessing
those _lifecycle hooks_ lives in the package `net.jqwik.api.lifecycle` and is -- as of this release --
are now mostly in the [API evolution status](#api-evolution) `MAINTAINED`.

#### Principles of Lifecycle Hooks

There are a few fundamental principles that determine and constrain the lifecycle hook API:

1. There are several [types of lifecycle hooks](#lifecycle-hook-types),
   each of which is an interface that extends
   [`net.jqwik.api.lifecycle.LifecycleHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/LifecycleHook.html).
2. A concrete lifecycle hook is an implementation of one or more lifecycle hook interfaces.
3. You can add a concrete lifecycle hook to a container class or a property method with the annotation
   [`@AddLifecycleHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/AddLifecycleHook.html).
   By default, a lifecycle hook is only added to the annotated element, not to its children.
   However, you can override this behaviour by either:
    - Override `LifecycleHook.propagateTo()`
    - Use the annotation attribute `@AddLifecycleHook.propagateTo()`
4. To add a global lifecycle use Java’s `java.util.ServiceLoader` mechanism and add the concrete lifecylcle hook
   class to file `META-INF/services/net.jqwik.api.lifecycle.LifecycleHook`.
   Do not forget to override `LifecycleHook.propagateTo()` if the global hook should be applied to all test elements.
5. In a single test run there will only be a single instance of each concrete lifecycle hook implementation.
   That's why you have to use jqwik's [lifecycle storage](#lifecycle-storage) mechanism if shared state
   across several calls to lifecycle methods is necessary.
6. Since all instances of lifecycle hooks are created before the whole test run is started,
   you cannot use non-static inner classes of test containers to implement lifecycle interfaces.
7. If relevant, the order in which hook methods are being applied is determined by dedicated methods
   in the hook interface, e.g.
   [`BeforeContainerHook.beforeContainerProximity()`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/BeforeContainerHook.html#beforeContainerProximity()).

Mind that much of what you can do with hooks can also be done using the simpler
mechanisms of [annotated lifecycle methods](#annotated-lifecycle-methods) or
a [property lifecycle class](#single-property-lifecycle).
You usually start to consider using lifecycle hooks when you want to
reuse generic behaviour in many places or even across projects.


#### Lifecycle Hook Types

All lifecycle hook interfaces extend `net.jqwik.api.lifecycle.LifecycleHook` which
has two methods that may be overridden:

- [`propagateTo()`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/LifecycleHook.html#propagateTo()):
  Determine if and how a hook will be propagated to an element's children.

- [`appliesTo(Optional<AnnotatedElement>)`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/LifecycleHook.html#appliesTo(java.util.Optional)):
  Determine if a hook will be applied to a concrete element. For example, you might want to constrain a certain hook
  to apply only to property methods and not to containers:

  ```java
  @Override
  public boolean appliesTo(final Optional<AnnotatedElement> element) {
      return element
          .map(annotatedElement -> annotatedElement instanceof Method)
          .orElse(false);
  }
  ```

_jqwik_ currently supports eight types of lifecycle hooks:

- [Lifecycle execution hooks](#lifecycle-execution-hooks):
    - `SkipExecutionHook`
    - `BeforeContainerHook`
    - `AfterContainerHook`
    - `AroundContainerHook`
    - `AroundPropertyHook`
    - `AroundTryHook`

- [Other hooks](#other-hooks)
    - `ResolveParameterHook`
    - `RegistrarHook`

#### Lifecycle Execution Hooks

With these hooks you can determine if a test element will be run at all,
and what potential actions should be done before or after running it.

##### SkipExecutionHook


Implement [`SkipExecutionHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/SkipExecutionHook.html)
to filter out a test container or property method depending on some runtime condition.

Given this hook implementation:

```java
public class OnMacOnly implements SkipExecutionHook {
    @Override
    public SkipResult shouldBeSkipped(final LifecycleContext context) {
        if (System.getProperty("os.name").equals("Mac OS X")) {
            return SkipResult.doNotSkip();
        }
        return SkipResult.skip("Only on Mac");
    }
}
```

The following property will only run on a Mac:

```java
@Property
@AddLifecycleHook(OnMacOnly.class)
void macSpecificProperty(@ForAll int anInt) {
}
```

##### BeforeContainerHook

Implement [`BeforeContainerHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/BeforeContainerHook.html)
for a hook that's supposed to do some work exactly once before any of its property methods and child containers
will be run.
This is typically used to set up a resource to share among all properties within this container.

##### AfterContainerHook

Implement [`AfterContainerHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/AfterContainerHook.html)
for a hook that's supposed to do some work exactly once after all of its property methods and child containers
have been run.
This is typically used to tear down a resource that has been shared among all properties within this container.

##### AroundContainerHook

[`AroundContainerHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/AroundContainerHook.html)
is a convenience interface to implement both [`BeforeContainerHook`](#beforecontainerhook) and
[`AfterContainerHook`](#aftercontainerhook) in one go.
This is typically used to set up and tear down a resource that is intended to be shared across all the container's children.

Here's an example that shows how to start and stop an external server once for all
properties of a test container:

```java
@AddLifecycleHook(ExternalServerResource.class)
class AroundContainerHookExamples {
    @Example
    void example1() {
        System.out.println("Running example 1");
    }
    @Example
    void example2() {
        System.out.println("Running example 2");
    }
}

class ExternalServerResource implements AroundContainerHook {
    @Override
    public void beforeContainer(final ContainerLifecycleContext context) {
        System.out.println("Starting server...");
    }
  
    @Override
    public void afterContainer(final ContainerLifecycleContext context) {
        System.out.println("Stopping server...");
    }
}
```

Running this example should output

```
Starting server...

Running example 1

Running example 2

Stopping server...
```

If you wanted to do something before and/or after _the whole jqwik test run_,
using a container hook and registering it globally is probably the easiest way.

##### AroundPropertyHook

[`AroundPropertyHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/AroundPropertyHook.html)
comes in handy if you need to define behaviour that should "wrap" the execution of a property,
i.e., do something directly before or after running a property - or both.
Since you have access to an object that describes the final result of a property
you can also change the result, e.g. make a failed property successful or vice versa.

Here is a hook implementation that will measure the time spent on running a property
and publish the result using a [`Reporter`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/Reporter.html):

```java
@Property(tries = 100)
@AddLifecycleHook(MeasureTime.class)
void measureTimeSpent(@ForAll Random random) throws InterruptedException {
    Thread.sleep(random.nextInt(50));
}

class MeasureTime implements AroundPropertyHook {
    @Override
    public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) {
        long before = System.currentTimeMillis();
        PropertyExecutionResult executionResult = property.execute();
        long after = System.currentTimeMillis();
        context.reporter().publish("time", String.format("%d ms", after - before));
        return executionResult;
    }
}
```

The additional output from reporting is concise:

```
timestamp = ..., time = 2804 ms
```

##### AroundTryHook

Wrapping the execution of a single try can be achieved by implementing
[`AroundTryHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/AroundTryHook.html).
This hook can be used for a lot of things. An incomplete list:

- Closely watch each execution of a property method
- Reset a resource for each call
- Swallow certain exceptions
- Filter out tricky (and invalid) parameter constellations
- Let a try fail depending on external circumstances

The following example shows how to fail if a single try will take longer than 100 ms:

```java
@Property(tries = 10)
@AddLifecycleHook(FailIfTooSlow.class)
void sleepingProperty(@ForAll Random random) throws InterruptedException {
    Thread.sleep(random.nextInt(101));
}

class FailIfTooSlow implements AroundTryHook {
    @Override
    public TryExecutionResult aroundTry(
        final TryLifecycleContext context,
        final TryExecutor aTry,
        final List<Object> parameters
    ) {
        long before = System.currentTimeMillis();
        TryExecutionResult result = aTry.execute(parameters);
        long after = System.currentTimeMillis();
        long time = after - before;
        if (time >= 100) {
            String message = String.format("%s was too slow: %s ms", context.label(), time);
            return TryExecutionResult.falsified(new AssertionFailedError(message));
        }
        return result;
    }
}
```

Since the sleep time is chosen randomly the property will fail from time to time
with the following error:

```
org.opentest4j.AssertionFailedError: sleepingProperty was too slow: 100 ms
```

#### Other Hooks

##### ResolveParameterHook

Besides the well-known `@ForAll`-parameters, property methods and [annotated lifecycle methods](#annotated-lifecycle-methods)
can take other parameters as well. These can be injected by concrete implementations of
[`ResolveParameterHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/ResolveParameterHook.html).

Consider this stateful `Calculator`:

```java
public class Calculator {
    private int result = 0;
  
    public int result() {
        return result;
    }
  
    public void plus(int addend) {
        result += addend;
    }
}
```

When going to check its behaviour with properties you'll need a fresh calculator instance
in each try. This can be achieved by adding a resolver hook that creates a freshly
instantiated calculator per try.

```java
@AddLifecycleHook(CalculatorResolver.class)
class CalculatorProperties {
    @Property
    void addingANumberTwice(@ForAll int aNumber, Calculator calculator) {
        calculator.plus(aNumber);
        calculator.plus(aNumber);
        Assertions.assertThat(calculator.result()).isEqualTo(aNumber * 2);
    }
}

class CalculatorResolver implements ResolveParameterHook {
    @Override
    public Optional<ParameterSupplier> resolve(
        final ParameterResolutionContext parameterContext,
        final LifecycleContext lifecycleContext
    ) {
        return Optional.of(optionalTry -> new Calculator());
    }
    @Override
    public PropagationMode propagateTo() {
        // Allow annotation on container level
        return PropagationMode.ALL_DESCENDANTS;
    }
}
```

There are a few constraints regarding parameter resolution of which you should be aware:

- Parameters annotated with `@ForAll` or with `@ForAll` present as a meta annotation
  (see [Self-Made Annotations](#self-made-annotations)) cannot be resolved;
  they are fully controlled by jqwik's arbitrary-based generation mechanism.
- If more than one applicable hook returns a non-empty instance of `Optional<ParameterSupplier>`
  the property will throw an instance of `CannotResolveParameterException`.
- If you want to keep the same object around to inject it in more than a single method invocation,
  e.g. for setting it up in a `@BeforeTry`-method, you are supposed to use jqwik's
  [lifecycle storage mechanism](#lifecycle-storage).


##### RegistrarHook

Use [`RegistrarHook`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/RegistrarHook.html)
if you need to apply several hook implementations that implement the desired behaviour together
but cannot be implemented in a single class.
For example, more than one implementation of the same hook type is needed,
but those implementations have a different proximity or require a different propagation mode.

This is really advanced stuff, the mechanism of which will probably evolve or change in the future.
If you really really want to see an example, look at
[`JqwikSpringExtension`](#https://github.com/jlink/jqwik-spring/blob/main/src/main/java/net/jqwik/spring/JqwikSpringExtension.java)

#### Lifecycle Storage

As [described above](#principles-of-lifecycle-hooks) one of the fundamental principles
is that there will be only a single instance of any lifecycle hook implementation
during runtime.
Since -- depending on configuration and previous rung -- containers and properties are
not run in a strict sequential order this guarantee comes with a drawback:
You cannot use a hook instance's member variables to hold state that should be shared
across all tries of a property or across all properties of a container or across
different lifecycle phases of a single try.
That's when lifecycle storage management enters the stage in the form of type
[`net.jqwik.api.lifecycle.Store`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/Store.html).

A `Store` object...

- holds a single piece of shared state
- has a _globally unique identifier_ of your choice.
  The identifier can be just a string or you compose whatever you deem necessary to make it unique.
- has a [`Lifespan`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/Lifespan.html).
  The lifespan determines when the initializer of a store will be called:
    - `Lifespan.RUN`: Only on first access
    - `Lifespan.PROPERTY`: On first access of each single property method (or one of its lifecycle hook methods)
    - `Lifespan.TRY`: On first access of each single try (or one of its lifecycle hook methods)

You create a store like this:

```java
Store<MyObject> myObjectStore = Store.create("myObjectStore", Lifespan.PROPERTY, () -> new MyObject());
```

And you retrieve a store similarly:

```java
Store<MyObject> myObjectStore = Store.get("myObjectStore");
```

A store with the same identifier can only be created once, that's why there is also a convenience
method for creating or retrieving it:

```java
Store<MyObject> myObjectStore = Store.getOrCreate("myObjectStore", Lifespan.PROPERTY, () -> new MyObject());
```

You now have the choice to use or update the shared state:

```java
Store<MyObject> myObjectStore = ...;

myObjectStore.get().doSomethingWithMyObject();
myObjectStore.update(old -> {
    old.changeState();
    return old;
});
```

Let's look at an example...

##### TemporaryFileHook

The following hook implementation gives you the capability to access _one_ (and only one)
temporary file per try using [parameter resolution](#resolveparameterhook):

```java
class TemporaryFileHook implements ResolveParameterHook {

    public static final Tuple.Tuple2 STORE_IDENTIFIER = Tuple.of(TemporaryFileHook.class, "temporary files");
  
    @Override
    public Optional<ParameterSupplier> resolve(ParameterResolutionContext parameterContext, LifecycleContext lifecycleContext) {
        if (parameterContext.typeUsage().isOfType(File.class)) {
            return Optional.of(ignoreTry -> getTemporaryFileForTry());
        }
        return Optional.empty();
    }
  
    private File getTemporaryFileForTry() {
        Store<File> tempFileStore = Store.getOrCreate(STORE_IDENTIFIER, Lifespan.TRY, this::createTempFile);
        tempFileStore.onClose(file -> file.delete());
        return tempFileStore.get();
    }
  
    private File createTempFile() {
        try {
            return File.createTempFile("temp", ".txt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
```

There are a few interesting things going on:

- The identifier is a tuple of the `TemporaryFileHook.class` object and a string.
  This makes sure that no other hook will use the same identifier accidentally.
- The temporary file is created only once per try.
  That means that all parameters in the scope of this try will contain _the same file_.
- A callback is added through
  [`onClose(..)`](/docs/1.5.1/javadoc/net/jqwik/api/lifecycle/Store.html#onClose(java.util.function.Consumer))
  which takes care of deleting the file as soon as the lifespan's scope (the try) is finished.

With this information you can probably figure out how the following test container works --
especially why the assertion in `@AfterTry`-method `assertFileNotEmpty()` succeeds.

```java
@AddLifecycleHook(value = TemporaryFileHook.class, propagateTo = PropagationMode.ALL_DESCENDANTS)
class TemporaryFilesExample {
    @Property(tries = 10)
    void canWriteToFile(File anyFile, @ForAll @AlphaChars @StringLength(min = 1) String fileContents) throws Exception {
        assertThat(anyFile).isEmpty();
        writeToFile(anyFile, fileContents);
        assertThat(anyFile).isNotEmpty();
    }
  
    @AfterTry
    void assertFileNotEmpty(File anyFile) {
        assertThat(anyFile).isNotEmpty();
    }
  
    private void writeToFile(File anyFile, String contents) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(anyFile));
        writer.write(contents);
        writer.close();
    }
}
```




## API Evolution

In agreement with the JUnit 5 platform _jqwik_ uses the
[@API Guardian project](https://github.com/apiguardian-team/apiguardian)
to communicate version and status of all parts of its API.
The different types of status are:

-`STABLE`: Intended for features that will not be changed in a backwards-incompatible way in the current major version (1.*).

-`MAINTAINED`: Intended for features that will not be changed in a backwards-incompatible way for at least the current minor release of the current major version. If scheduled for removal, it will be demoted to `DEPRECATED` first.

-`EXPERIMENTAL`: Intended for new, experimental features where we are looking for feedback. Use this element with caution; it might be promoted to `MAINTAINED` or `STABLE` in the future, but might also be removed without prior notice, even in a patch.

-`DEPRECATED`: Should no longer be used; might disappear in the next minor release.

-`INTERNAL`: Must not be used by any code other than _jqwik_ itself. Might be removed without prior notice.

Since annotation `@API` has runtime retention you find the actual API status in an element's source code,
its [Javadoc](/docs/1.5.1/javadoc) but also through reflection.
If a certain element, e.g. a method, is not annotated itself, then it carries the status of its containing class.





## Release Notes

Read this version's [release notes](/release-notes.html#151).
