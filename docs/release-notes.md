---
title: jqwik Release Notes
---

# Release Notes

<!-- use `doctoc --maxlevel 2 release-notes.md` to recreate the TOC -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
### Table of Contents  

- [1.1.6-SNAPSHOT](#116-snapshot)
- [1.1.5](#115)
- [1.1.4](#114)
- [1.1.3](#113)
- [1.1.2](#112)
- [1.1.1](#111)
- [1.1.0](#110)
- [1.0.0](#100)
- [0.9.x](#09x)
- [0.8.x](#08x)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 1.1.6-SNAPSHOT

<p style="padding-left:1em;font-size:larger">
<a href="/docs/snapshot/user-guide.html">User guide</a>
</p>

- Removed dependencies to internal classes in JUnit platform
- Added [`Arbitraries.maps()`](/docs/snapshot/user-guide.html#maps)
- Added default generation for `Map` instances
- `@Domain` annotation can now also refer to inner class of test container's base class.
  Thanks to https://github.com/larsrh for that pull request!

## 1.1.5

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.1.5/user-guide.html">User guide</a>
and
<a href="/docs/1.1.5/javadoc/index.html">javadoc</a>
</p>

- Added `shrinkTowards(value)` to all decimal number arbitraries: 
  FloatArbitrary, DoubleArbitrary, BigDecimalArbitrary
- Exceptions during test class instance creation are now reported as failures
- Removed caching of engine descriptor in JqwikTestEngine

## 1.1.4

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.1.4/user-guide.html">User guide</a>
and
<a href="/docs/1.1.4/javadoc/index.html">javadoc</a>
</p>

- Upgrade to junit-platform 1.4.2
- Added `Arbitrary.collect(Predicate<List<T>> until)` in order to
  [collect values in a list](/docs/1.1.4/user-guide.html#collecting-values-in-a-list)
- Added `shrinkTowards(value)` to all integral number arbitraries: 
  ByteArbitrary, ShortArbitrary, IntegerArbitrary, LongArbitrary, BigIntegerArbitrary
  in order to [change the shrinking target](/docs/1.1.4/user-guide.html#change-the-shrinking-target)
- Upgrade Gradle to 5.4.2
- Improved shrinking of collections

## 1.1.3

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.1.3/user-guide.html">User guide</a>
and
<a href="/docs/1.1.3/javadoc/index.html">javadoc</a>
</p>

- Whitespace characters for generation of Strings and chars are now 
  platform and JDK version dependent. 
  See [this Github issue](https://github.com/jlink/jqwik/issues/55).
- Missing `@ForAll` annotation will now fail test - instead of skipping it.
  See [this Github issue](https://github.com/jlink/jqwik/issues/54).
- Added `CharacterArbitrary.with(char ... allowedChars)'
- Added `CharacterArbitrary.range(char min, char max)'
- Deprecated `CharacterArbitrary.between(char min, char max)'

## 1.1.2

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.1.2/user-guide.html">User guide</a>
and
<a href="/docs/1.1.2/javadoc/index.html">javadoc</a>
</p>

- Added `Arbitrary.forEachValue(Consumer action)`
- Fixed [Kotlin compatibility issue](https://github.com/jlink/jqwik/issues/52)
- Fixed [decimal generation bug](https://github.com/jlink/jqwik/issues/50)
- Update to JUnit Platform 5.4.1


## 1.1.1

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.1.1/user-guide.html">User guide</a>
and
<a href="/docs/1.1.1/javadoc/index.html">javadoc</a>
</p>

- Added `Combinators.withBuilder` to enable a 
  [different way for combining arbitraries](/docs/1.1.1/user-guide.html#combining-arbitraries-with-builder).

- You can configure if you want to [report only failures](/docs/1.1.1/user-guide.html#jqwik-configuration).

- The reporting format for run properties has changed.

- Added `@Domain.priority()` to enable prioritisation of domain context classes.

- Added [`Arbitraries.create`](/docs/1.1.1/user-guide.html#create).

- Fixed problem with double and float conversion in range constraints.
  My thanks go to [blairdye](https://github.com/blairdye) for that! 

- Annotation `@Provide` can now be placed on a "super method" i.e. a method from
  a superclass or super interface that is overridden.

- Annotation `@Provide` can now be placed on another annotation for composite annotations.

- Loosened matching of return type of `@Provide` to enable generic provider
  method's in derived container classes.

## 1.1.0

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.1.0/user-guide.html">User guide</a>
and
<a href="/docs/1.1.0/javadoc/index.html">javadoc</a>
</p>

- Added capability to
  [collect arbitrary providers and configurators in domains](/docs/1.1.0/user-guide.html#domain-and-domain-context)

- Added `Arbitraries.defaultFor(TypeUsage typeUsage)`

- Added capability to generate objects from their type's
  [constructors and factories](/docs/1.1.0/user-guide.html#generation-from-a-types-interface)

- __Breaking Change__: Best fit search for provider methods is no longer supported. 
  Reason: Using `@Domain` requires less magic and is a better abstraction for automatic arbitrary provision. 

- Update to JUnit Platform 5.4

## 1.0.0

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.0.0/user-guide.html">User guide</a>
and
<a href="/docs/1.0.0/javadoc/index.html">javadoc</a>
</p>

- Removed all deprecated APIs
  - Annotation attribute `net.jqwik.api.Property.reporting()`
  - Class `net.jqwik.api.Tuples`
  - Method `net.jqwik.api.providers.TypeUsage.getAnnotation()`

- jqwik now produces three artefacts: jqwik-api, jqwik-engine, jqwik

- Filled in some missing parts in API Javadoc

- Introduced `Arbitrary.allValues()`

- Adding annotations from JUnit Jupiter or Vintage engine will lead to warnings being logged

- Range annotations (`IntRange` etc.) don't need `max` attribute any more

- `@Positive` and `@Negative` constraints no longer include 0 as possible value

- Added `@org.apiguardian.api.API` annotation to all types in `net.jqwik.api.**`

- Added annotation `@Disabled` as a means to temporarily
  [skip test methods or test containers](/docs/1.0.0/user-guide.html#disabling-tests)

## 0.9.x

### 0.9.3

<p style="padding-left:1em;font-size:larger">
<a href="/docs/0.9.3/user-guide.html">User guide</a>
and
<a href="/docs/0.9.3/javadoc/index.html">javadoc</a>
</p>

- The probability of edge cases being generated is now higher
- New constraint annotation `@NotEmpty`
- Arrays and varargs parameters hand configuration annotations down to their
  component type arbitrary
- Properties with unconstrained wildcards will now use any registered
  arbitrary for value generation
- Added [`Arbitraries.frequencyOf()`](/docs/0.9.3/user-guide.html#randomly-choosing-among-arbitraries)
- Added [`Arbitraries.recursive()`](/docs/0.9.3/user-guide.html#deterministic-recursion-with-recursive)
- Integral number generation generates a few more edge cases
- You can use `@Size` to [constrain the generation](/docs/0.9.3/user-guide.html#number-of-actions)
  of `ActionSequence` parameters
- Some incompatible changes to the `ActionSequence` interface
- jqwik.jar does no longer deliver a jqwik.properties file in its classpath
- jqwik logs WARNING if unsupported property is used in
  `jqwik.properties` file
- Replaced configuration file property `rerunFailuresWithSameSeed` by
  `defaultAfterFailure'
- Introduced [`@Property(afterFailure)`](/docs/0.9.3/user-guide.html#rerunning-falsified-properties)
- `ArbitraryConfiguratorBase` has new method `acceptType(TypeUsage)`,
  which can be overridden.
- Added two new after-failure-modes: `SAMPLE_ONLY` and `SAMPLE_FIRST`
- Action sequences for state-based properties are serializable now
  in order to enable `SAMPLE_ONLY` and `SAMPLE_FIRST`

### 0.9.2

- Exhaustive generation works for ambiguous arbitrary resolution if each arbitrary
  can be generated exhaustively
- Fixed bug related to correct throwable propagation when shrinking
- Erroneous properties will also be shrunk now

### 0.9.1

- Container shrinking tries one more thing to get smaller results
- Removed `RandomGenerator.reset()` since it's no longer needed for uniqueness behaviour
  <p/>_This is an incompatible API change!_
- `Arbitrary.unique()` does no longer propagate uniqueness across usages
  of same arbitrary.
- Implemented exhaustive generation for `Arbitrary.unique()`
- Fixed bug when resolving recursive types
- Added `Arbitraries.shuffle()` as a way to generate [permutations](/docs/0.9.3/user-guide.html#shuffling-permutations)
- Implemented exhaustive generation for `Arbitraries.frequency()`
- Changed display name of test engine to "jqwik for Java"
- Per default jqwik no longer uses the JUnit platform reporter for reporting
  because Gradle does not support it yet
- Using JUnit platform reporter [can now be configured](/docs/0.9.3/user-guide.html#jqwik-configuration)
- `@Size`: min and max values can be used without the other
- `@StringLength`: min and max values can be used without the other
- Implemented exhaustive generation for `Arbitrary.flatMap()`
- Implemented exhaustive generation for `Arbitraries.oneOf()`
- Implemented exhaustive generation for `Arbitraries.strings()`
- Support default generation of Iterables and Iterators

### 0.9.0

- Removed deprecated static methods in `Arbitraries`
- Removed deprecated method `ArbitraryProvider.provideFor(TypeUsage targetType, Function<TypeUsage, Optional<Arbitrary<?>>> subtypeProvider)`
- Removed default implementation of `ArbitraryProvider.provideFor(TypeUsage targetType, SubtypeProvider subtypeProvider)`
- Removed deprecated annotation `@Digits`
- Renamed `TypeUsage.getAnnotation(Class annotationClass)` to `findAnnotation`
- Added `TypeUsage.isAnnotated(Class annotationClass)`
- Added [`Arbitrary.unique()`](/docs/0.9.3/user-guide.html#creating-unique-values)
- Added constraint [`@Unique`](/docs/0.9.3/user-guide.html#unique-values)
- Implementations of `ArbitraryConfigurator` can optionally implement `int order()`
- It's now possible to ["flat combine"](/docs/0.9.3/user-guide.html#flat-combination) arbitraries
- Deprecated all types and methods in `net.jqwik.api.Tuples.*`
  in favour of `net.jqwik.api.Tuple.*`
- There are new tuple types `Tuple5` up to `Tuple8`
- [Data-Driven Properties:](/docs/0.9.3/user-guide.html#data-driven-properties) Feed data into your properties
  instead of randomized generation
- Display names of test containers and properties
  [will now automatically be prettified](/docs/0.9.3/user-guide.html#naming-and-labeling-tests),
  i.e. each underscore will be replaced by a single space.
- Added [`@Report` annotation](/docs/0.9.3/user-guide.html#additional-reporting) to replace `Property.reporting`
- Added [exhaustive value generation](/docs/0.9.3/user-guide.html#exhaustive-generation)


## 0.8.x

### 0.8.15

- `StringArbitrary.withChars()` now accepts varargs.
- Added `StringArbitrary.whitespace()`
- Added `@Whitespace` annotation
- Improved shrinking of action sequences
- Default String generation does no longer generate Unicode "noncharacters"
  or "private use characters"
- Added `StringArbitrary.all()` for also generating
  Unicode "noncharacters" and "private use characters"

### 0.8.14

- Some potentially incompatible stuff has changed for
  [default arbitrary providers](/docs/0.9.3/user-guide.html#providing-default-arbitraries):
  - Introduced `ArbitraryProvider.priority()`
  - The old `ArbitraryProvider.provideFor(TypeUsage, Function)` is now deprecated, override
    `ArbitraryProvider.provideFor(TypeUsage, SubtypeProvider)` instead
  - If more than one provider fits a given type, one of the will be
    chosen randomly
- `Arbitraries.defaultFor()` will randomly choose one arbitrary if
  there is more than one fitting registered arbitrary provider

### 0.8.13

Faulty release. Do not use!

### 0.8.12

- Implemented generic type resolution to enable [contract tests](/docs/0.9.3/user-guide.html#contract-tests)
- Renamed `GenericType` to `TypeUsage`
  <p/>_This is an incompatible API change!_

### 0.8.11

- Reporting with `Reporting.FALSIFIED` now reports much less, and hopefully no wrong values anymore.
- Shrinking with filtered values finds simpler values in some circumstance
- Generation of strings will allow any unicode character by default
- `Combinators.combine()` can now take a list of arbitraries of same return type.
- Generated edge cases are now injected again and again and not only in the beginning.
- Complete re-implementation of shrinking with a few implications:
  - Shrinking should work better and more efficient overall. There might
    be situations, though, in which shrinking does no longer find
    the simplest example.
  - `ShrinkingMode.BOUNDED` might interrupt shrinking with completely different results.
  - When using `Reporting.FALSIFIED` you will see different inbetween
    shrinking steps that before.
  - The public interface of `Shrinkable` has changed in an incompatible way,
    but that shouldn't affect anyone but myself.


### 0.8.10

- Fixed shrinking bug that could result in integers not being shrunk
  as far as possible
- Integer shrinking should be faster in most cases and cover more cases

### 0.8.9

- Some minor but potentially incompatible API changes in `GenericType`.
- Tags from parent (e.g. container class) are now also present in children (methods) 
- Renamed `ShrinkingMode.ON` to `ShrinkingMode.FULL`
  <p/>_This is an incompatible API change!_
- Introduced `ShrinkingMode.BOUNDED` and made it the default
- Introduced `ShrinkingMode.FULL`
- Some bounded wildcard types and type variables can be provided automatically

### 0.8.8

- Added `Arbitraries.lazy()` 
  to allow [recursive value generation](/docs/0.9.3/user-guide.html#recursive-arbitraries)
- Added `Arbitrary.fixGenSize()` to enable a fixed genSize when creating random generators
- Added `Arbitrary.sequences()` to create sequences of actions for [stateful testing](/docs/0.9.3/user-guide.html#stateful-testing)

### 0.8.7

- Property methods that also have Jupiter annotations are skipped
- Added `@Label` to allow the [labeling of examples, properties and containers](/docs/0.9.3/user-guide.html#naming-and-labeling-tests)
- Changed license from EPL 1.0 to EPL 2.0
- Added `@Tag` to allow the [tagging of examples, properties and containers](/docs/0.9.3/user-guide.html#tagging-tests)
- User guide: Added links to example sources on github
- Added `Arbitraries.frequency()` to enable 
  [choosing values with weighted probabilities](/docs/0.9.3/user-guide.html#select-randomly-with-weights)
- Collection and String generation now explores a wider range of sizes and lengths

### 0.8.6

- BigInteger generation does no longer support `@LongRange` but only `@BigRange`
  <p/>_This is an incompatible API change!_
- BigDecimal generation does no longer support `@DoubleRange` but only `@BigRange`
  <p/>_This is an incompatible API change!_
- BigInteger generation now supports numbers outside long range
- Property.seed is now of type String
  <p/>_This is an incompatible API change!_
- Property methods without @ForAll parameters are now also tried as many times as 
  specified by `tries` parameter.
- Added new method `Arbitraries.constant()`
- Added new method `Arbitraries.defaultFor()`
- `@WithNull.target()` has been removed
  <p/>_This is an incompatible API change!_
- Parameterized types [can now be annotated directly](/docs/0.9.3/user-guide.html#constraining-parameterized-types)
- Added `@Size.value()` for fixed size collections
- Added `@StringLength.value()` for fixed size Strings

### 0.8.5

- All decimal generation (float, double, BigDecimal) now uses BigDecimal under the hood
- All integral generation (int, short, byte, long, BigInteger) now uses BigInteger under the hood
- Numbers are now generated within their full domain (min, max)
- Decimal shrinking improved
- Fixed bug: Reporting.FALSIFIED now also works for falsification through exception
- Added support for running all tests in a module (Java 9 only). I HAVE NOT TESTED IT! 

### 0.8.4

- Completely rebuild the annotation-based configuration of registered arbitrary providers
- Introduced [fluent configuration interfaces](/docs/0.9.3/user-guide.html#fluent-configuration-interfaces)
- Introduced [Arbitrary.list/set/stream/optional/array](/docs/0.9.3/user-guide.html#collections-streams-arrays-and-optional)
- Combinators.combine() now allows up to 8 parameters
- Character creation does no longer support `@Chars` but only `@CharRange`
  <p/>_This is an incompatible API change!_
- 'Arbitraries.chars(char[] validChars)' does no longer exist
  <p/>_This is an incompatible API change!_
- Added [`Arbitraries.oneOf`](/docs/0.9.3/user-guide.html#randomly-choosing-among-arbitraries)
- `@Char` cannot take `from` and `to` any longer. Replaced by `@CharRange`
- Deprecated many methods in `Arbitraries` class. Replaced by fluent interface methods.
- Deprecated `@Digits` constraint. Replaced by `@NumericChars`.
- Upgrade to JUnit 5.1.0

### 0.8.3

- Bugfix: Injected empty list samples are now mutable
- Bugfix: Injected empty set samples are now mutable
- Unbound type variables in properties [can now be provided](/docs/0.9.3/user-guide.html#providing-variable-types)

### 0.8.2

- Added support for `java.util.Random` generation.
- Added [Tuple types](/docs/0.9.3/user-guide.html#flat-mapping-with-tuple-types)
  (`Tuple2`, `Tuple3`, `Tuple4`) to use in `Arbitrary.flatMap()`.
- Renamed `ReportingMode` to `Reporting` and removed `Reporting.MINIMAL`.
  <p/>_This is an incompatible API change!_

- Added `Reporting.FALSIFIED`. See [section on optional property parameters](/docs/0.9.3/user-guide.html#optional-property-parameters)

### 0.8.1

- Added support for [default arbitrary providers](/docs/0.9.3/user-guide.html#providing-default-arbitraries).
- Added support for `byte` and `Byte` generation.
- Added support for `short` and `Short` generation.

### 0.8.0

The first release published on maven central.