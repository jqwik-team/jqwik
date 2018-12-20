---
title: jqwik Release Notes
---

# Release Notes

<!-- use `doctoc --maxlevel 2 release-notes.md` to recreate the TOC -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
### Table of Contents  

- [1.0.0-SNAPSHOT](#100-snapshot)
- [0.9.x](#09x)
- [0.8.x](#08x)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


## 1.0.0-SNAPSHOT

[User guide](/docs/snapshot/user-guide.html)
and [javadoc](/docs/snapshot/javadoc/index.html).

- Removed all deprecated APIs
  - Annotation attribute `net.jqwik.api.Property.reporting()`
  - Class `net.jqwik.api.Tuples`
  - Method `net.jqwik.api.providers.TypeUsage.getAnnotation()`

- jqwik now produces three artefacts: jqwik-api, jqwik-engine, jqwik

- Filled in some missing parts in API Javadoc

## 0.9.x

### 0.9.3

[User guide](/docs/0.9.3/user-guide.html)
and [javadoc](/docs/0.9.3/javadoc/index.html).

- The probability of edge cases being generated is now higher
- New constraint annotation `@NotEmpty`
- Arrays and varargs parameters hand configuration annotations down to their
  component type arbitrary
- Properties with unconstrained wildcards will now use any registered
  arbitrary for value generation
- Added [`Arbitraries.frequencyOf()`](#randomly-choosing-among-arbitraries)
- Added [`Arbitraries.recursive()`](#deterministic-recursion-with-recursive)
- Integral number generation generates a few more edge cases
- You can use `@Size` to [constrain the generation](#number-of-actions)
  of `ActionSequence` parameters
- Some incompatible changes to the `ActionSequence` interface
- jqwik.jar does no longer deliver a jqwik.properties file in its classpath
- jqwik logs WARNING if unsupported property is used in
  `jqwik.properties` file
- Replaced configuration file property `rerunFailuresWithSameSeed` by
  `defaultAfterFailure'
- Introduced [`@Property(afterFailure)`](#rerunning-falsified-properties)
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
- Added `Arbitraries.shuffle()` as a way to generate [permutations](#shuffling-permutations)
- Implemented exhaustive generation for `Arbitraries.frequency()`
- Changed display name of test engine to "jqwik for Java"
- Per default jqwik no longer uses the JUnit platform reporter for reporting
  because Gradle does not support it yet
- Using JUnit platform reporter [can now be configured](#jqwik-configuration)
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
- Renamed `TypeUsage.getAnnotation(Class annotationClass)` to `findAnnotation`
- Added `TypeUsage.isAnnotated(Class annotationClass)`
- Added [`Arbitrary.unique()`](#creating-unique-values)
- Added constraint [`@Unique`](#unique-values)
- Implementations of `ArbitraryConfigurator` can optionally implement `int order()`
- It's now possible to ["flat combine"](#flat-combination) arbitraries
- Deprecated all types and methods in `net.jqwik.api.Tuples.*`
  in favour of `net.jqwik.api.Tuple.*`
- There are new tuple types `Tuple5` up to `Tuple8`
- [Data-Driven Properties:](#data-driven-properties) Feed data into your properties
  instead of randomized generation
- Display names of test containers and properties
  [will now automatically be prettified](#naming-and-labeling-tests),
  i.e. each underscore will be replaced by a single space.
- Added [`@Report` annotation](#additional-reporting) to replace `Property.reporting`
- Added [exhaustive value generation](#exhaustive-generation)


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
  [default arbitrary providers](#providing-default-arbitraries):
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

- Implemented generic type resolution to enable [contract tests](#contract-tests)
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
  to allow [recursive value generation](#recursive-arbitraries)
- Added `Arbitrary.fixGenSize()` to enable a fixed genSize when creating random generators
- Added `Arbitrary.sequences()` to create sequences of actions for [stateful testing](#stateful-testing)

### 0.8.7

- Property methods that also have Jupiter annotations are skipped
- Added `@Label` to allow the [labeling of examples, properties and containers](#naming-and-labeling-tests)
- Changed license from EPL 1.0 to EPL 2.0
- Added `@Tag` to allow the [tagging of examples, properties and containers](#tagging-tests)
- User guide: Added links to example sources on github
- Added `Arbitraries.frequency()` to enable 
  [choosing values with weighted probabilities](#select-randomly-with-weights)
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
- Parameterized types [can now be annotated directly](#constraining-parameterized-types)
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
- Introduced [fluent configuration interfaces](#fluent-configuration-interfaces)
- Introduced [Arbitrary.list/set/stream/optional/array](#collections-streams-arrays-and-optional)
- Combinators.combine() now allows up to 8 parameters
- Character creation does no longer support `@Chars` but only `@CharRange`
  <p/>_This is an incompatible API change!_
- 'Arbitraries.chars(char[] validChars)' does no longer exist
  <p/>_This is an incompatible API change!_
- Added [`Arbitraries.oneOf`](#randomly-choosing-among-arbitraries)
- `@Char` cannot take `from` and `to` any longer. Replaced by `@CharRange`
- Deprecated many methods in `Arbitraries` class. Replaced by fluent interface methods.
- Deprecated `@Digits` constraint. Replaced by `@NumericChars`.
- Upgrade to JUnit 5.1.0

### 0.8.3

- Bugfix: Injected empty list samples are now mutable
- Bugfix: Injected empty set samples are now mutable
- Unbound type variables in properties [can now be provided](#providing-variable-types)

### 0.8.2

- Added support for `java.util.Random` generation.
- Added [Tuple types](#flat-mapping-with-tuple-types) 
  (`Tuple2`, `Tuple3`, `Tuple4`) to use in `Arbitrary.flatMap()`.
- Renamed `ReportingMode` to `Reporting` and removed `Reporting.MINIMAL`.
  <p/>_This is an incompatible API change!_

- Added `Reporting.FALSIFIED`. See [section on optional property parameters](#optional-property-parameters)

### 0.8.1

- Added support for [default arbitrary providers](#providing-default-arbitraries).
- Added support for `byte` and `Byte` generation.
- Added support for `short` and `Short` generation.

### 0.8.0

The first release published on maven central.