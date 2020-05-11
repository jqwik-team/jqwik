---
title: jqwik Release Notes
---

# Release Notes

<!-- use `doctoc --maxlevel 2 release-notes.md` to recreate the TOC -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
### Table of Contents  

- [1.3.0-SNAPSHOT](#130-snapshot)
- [1.2.x](#12x)
- [1.1.x](#11x)
- [1.0.0](#100)
- [0.9.x](#09x)
- [0.8.x](#08x)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

## 1.3.0-SNAPSHOT

<p style="padding-left:1em;font-size:larger">
<a href="/docs/snapshot/user-guide.html">User guide</a>
and
<a href="/docs/snapshot/javadoc/index.html">javadoc</a>
</p>

#### New and Enhanced Features

- Added `Arbitraries.ofSuppliers(..)` to choose among a set of mutable objects.

- You can now influence the 
  [distribution of random number generation](/docs/snapshot/user-guide.html#random-numeric-distribution): 
  All numeric arbitraries now support `withDistribution(RandomDistribution)`
  to choose between `RandomDistribution.biased()` (default),
   `RandomDistribution.uniform()` and `RandomDistribution.gaussian(borderSigma)`. 
  
- Default number generation has now a much higher bias towards numbers
  that are closer to the shrinking target of a number range.

- Using a faster implementation of `java.util.Random` under the hood

- Massively improved and enhanced 
  [generation of edge cases](/docs/snapshot/user-guide.html#generation-of-edge-cases)

- Edge Cases Mode is now being reported per property

- Added `Arbitrary.withoutEdgeCases()` to switch off 
  preferred edge case generation for individual arbitraries. 

- Added `StringArbitrary.withChars(Arbitrary<Character> characterArbitrary)`

- Added `CharacterArbitrary.with(Arbitrary<Character> characterArbitrary)`

- Promoted APIs from `EXPERIMENTAL` to `MAINTAINED`
    - `Arbitraries.nothing()`
    - `Arbitrary.collect(Predicate<List<T>> until)`
    - `Arbitrary.sample()`
    - `Arbitrary.sampleStream()`
    - `Arbitrary.injectDuplicates(double duplicateProbability)`
    - `Arbitrary.tuple1()`
    - `Arbitrary.tuple2()`
    - `Arbitrary.tuple3()`
    - `Arbitrary.tuple4()`
    - Annotation `net.jqwik.api.From`
    - Class `net.jqwik.api.Functions`
    - Class `net.jqwik.api.arbitraries.FunctionArbitrary`

#### Breaking Changes

- Decimal Generation: `min` and `max` values are now rejected if they have more decimal places
  than the generator's `scale` allows. 
  
- Decimal Shrinking: Values without decimal places are no longer preferred while shrinking.

- Removed deprecated APIs
    - `Arbitrary.withSamples(T... samples)`
    - `RandomGenerator.withSamples(T... samples)`
    - `TryLifecycleContext.propertyContext()`
    - `net.jqwik.api.Statistics`: Replaced by `net.jqwik.api.statistics.Statistics` 

- Set most public methods of `RandomGenerator` to API status `INTERNAL`

- Set `Arbitrary.exhaustive()` to API status `INTERNAL`

- Set `ExhaustiveGenerator` and all its methods to API status `INTERNAL`

#### Bug Fixes

- An `OutOfMemoryError` will go through to the top. Fix for
  [bug report from jqwik-spring](https://github.com/jlink/jqwik-spring/issues/5). 

## 1.2.x

### 1.2.7

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.2.7/user-guide.html">User guide</a>
and
<a href="/docs/1.2.7/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- `BigDecimalArbitrary` now allows specifying _excluded_ min and max values:
    - `BigDecimalArbitrary.between(BigDecimal min, boolean minIncluded, BigDecimal max, boolean maxIncluded)`
    - `BigDecimalArbitrary.lessThan(BigDecimal max)`
    - `BigDecimalArbitrary.greaterThan(BigDecimal min)`

- Annotation `@BigRange` has two new optional attributes `minIncluded` and `maxIncluded`

- `DoubleArbitrary` now allows specifying _excluded_ min and max values:
    - `DoubleArbitrary.between(double min, boolean minIncluded, double max, boolean maxIncluded)`
    - `DoubleArbitrary.lessThan(double max)`
    - `DoubleArbitrary.greaterThan(double min)`

- Annotation `@DoubleRange` has two new optional attributes `minIncluded` and `maxIncluded`

- `FloatArbitrary` now allows specifying _excluded_ min and max values:
    - `FloatArbitrary.between(float min, boolean minIncluded, float max, boolean maxIncluded)`
    - `FloatArbitrary.lessThan(float max)`
    - `FloatArbitrary.greaterThan(float min)`

- Annotation `@DoubleRange` has two new optional attributes `minIncluded` and `maxIncluded`

- Warning about JUnit annotations only shows up when test container class has
  jqwik property or example methods.

- Upgrade to JUnit Platform 1.6.2

#### Breaking Changes

- Minor changes to yet undocumented Lifecycle Hooks API

### 1.2.6

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.2.6/user-guide.html">User guide</a>
and
<a href="/docs/1.2.6/javadoc/index.html">javadoc</a>
</p>

#### Breaking Changes

- More changes to Lifecycle Hooks API in order to support a 
  [jqwik Spring extension](https://github.com/jlink/jqwik-spring) 


### 1.2.5

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.2.5/user-guide.html">User guide</a>
and
<a href="/docs/1.2.5/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Added `@BeforeExample` as an alias of `@BeforeProperty`

- Added `@AfterExample` as an alias of `@AfterProperty`

- Lifecycle methods annotated with `@BeforeTry`, `@AfterTry`, `@BeforeProperty`, 
  `@AfterProperty`, `@BeforeContainer` and `@AfterContainer` can now have 
  parameters that will be resolved using registered `ResolveParameterHook` instances.

- Added `ActionSequence.withInvariant(String label, Invariant<M> invariant);`.

- Added `ActionSequence.peek(Consumer<M> modelPeeker)`.

- `Arbitraries.sequences(Arbitrary<? extends Action<M>> actionArbitrary)` does now accept
  covariant subtypes and still returns type `Arbitrary<Action<M>>`.

- Reporting text on failed statistics coverage check improved.

- Upgrade to JUnit Platform 1.6.1


#### Breaking Changes

- Some fundamental changes to Lifecycle Hooks API in order to support a 
  [jqwik Spring extension](https://github.com/jlink/jqwik-spring) 


#### Bug Fixes

- Labelled statistics reports should now reliably being reported 
  in order of first usage.

### 1.2.4

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.2.4/user-guide.html">User guide</a>
and
<a href="/docs/1.2.4/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- [Annotated lifecycle methods](/docs/1.2.4/user-guide.html#annotated-lifecycle-methods) 
  have been implemented:
  - `@BeforeContainer`
  - `@AfterContainer`
  - `@BeforeProperty`
  - `@AfterProperty`
  - `@BeforeTry`
  - `@AfterTry`

- `@StatisticsReport` can now also be used on container classes

- Statistical coverage checking can now be done in a fluent API style

- Improved shrinking of parameters that require duplicate values for falsifying a property

- Upgrade to JUnit Platform 1.6.0

#### Breaking Changes

- Removed `Statistics.coverageOf()`. It's now `Statistics.label(..).coverage(..)`

- `Falsifier` no longer extends `Predicate<T>`

- Many breaking changes in Lifecycle API; but this API had not been published 
  or documented anyway.

#### Bug Fixes

No open bugs had been reported.

### 1.2.3

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.2.3/user-guide.html">User guide</a>
and
<a href="/docs/1.2.3/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- The new big feature of this release is [coverage checking](/docs/1.2.3/user-guide.html#checking-coverage-of-collected-statistics).

- Statistics reporting
  [can now be switched off](/docs/1.2.3/user-guide.html#switch-statistics-reporting-off).
  Alternatively you can
  [plug in your own reporting format](/docs/1.2.3/user-guide.html#plug-in-your-own-statistics-report-format).

- Added `Arbitrary.injectDuplicates(duplicateProbability)` to enable
  [the high probability generation of duplicate values](/docs/1.2.3/user-guide.html#inject-duplicate-values)

- Added `Arbitrary.tuple1(), Arbitrary.tuple2(), Arbitrary.tuple3(), Arbitrary.tuple4()`
  to [generate tuples of same base type](/docs/1.2.3/user-guide.html#tuples-of-same-base-types)

- Character `\u0000` is being generated as default edge case
  in String and Character arbitraries - if it lies within the allowed character range.

#### Breaking Changes

- `Statistics.collect(..)` and `StatisticsCollector.collect(..)` can no longer
  be called with no values. There must be at least one - but it can be `null`.

- `Statistics.collect(..)` and `StatisticsCollector.collect(..)` must always
  be called with same number of parameters.

- Deprecated `net.jqwik.api.Statistics`. Use `net.jqwik.api.statistics.Statistics` instead.

- The standard property report is now the last thing to be reported,
  i.e. after statistics reports.

- Default priority in AbstractDomainContextBase is now 0 (previously 1),
  i.e. they no longer supersede built-in jqwik arbitrary providers but mix in.

#### Bug Fixes

No open bugs had been reported.

### 1.2.2

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.2.2/user-guide.html">User guide</a>
and
<a href="/docs/1.2.2/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- When configuration parameter `database` is set to empty, test run recording
  is now completely disabled.

- Exhaustive generation of single decimals and floats

- Added `Arbitrary.sample()` and `Arbitrary.sampleStream()` for enabling
  [the use of generators outside of properties](/docs/1.2.2/user-guide.html#using-arbitraries-directly)

- `Arbitraries.oneOf(Arbitrary<? extends T>... arbitraries)` does now accept
  covariant subtypes and still returns type `Arbitrary<T>`.

#### Breaking Changes

- Deprecated `Arbitrary.withSamples(T... samples)`

#### Bug Fixes

- [Statistics formatting bug](https://github.com/jlink/jqwik/issues/72)
- [Exhaustive generation bug](https://github.com/jlink/jqwik/issues/77)
- [Using Arbitraries.forType() outside property bug](https://github.com/jlink/jqwik/issues/79)

### 1.2.1

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.2.1/user-guide.html">User guide</a>
and
<a href="/docs/1.2.1/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- New configuration parameter `defaultGeneration` for `jqwik.properties` file
- Added `CharacterArbitrary.with(CharSequence allowedChars)`
- Added `StringArbitrary.withChars(CharSequence allowedChars)`
- Arbitraries of List, Set, Stream und arrays are now of type `StreamableArray`
- Introduced [`StreamableArray.reduce()`](/docs/1.2.1/javadoc/net/jqwik/api/arbitraries/StreamableArbitrary.html)
- `HashMap` can now be generated by default

#### Breaking Changes

- `Arbitrary.exhaustive()` must no longer be overridden in implementors of 
  Arbitrary. Override `Arbitrary.exhaustive(long maxNumberOfGeneratedSamples)` instead.

- `TypeUsage.getTypeArguments()`: In case of type variables or wildcard types
  this method will now return the upper bound's type arguments if there is
  a single upper bound and no lower bound. This enables correct generation
  of variables like `T extends Map<Integer, String>`.

#### Bug Fixes

- `@ForAll` annotation could not be replaced by self-made annotation. Now it can. 

- Annotations in type parameters of bounds of type variables 
  are now correctly recognized.

- Annotations in type parameters of bounds of wildcards 
  are now correctly recognized.

### 1.2.0

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.2.0/user-guide.html">User guide</a>
and
<a href="/docs/1.2.0/javadoc/index.html">javadoc</a>
</p>

#### New and Enhanced Features

- Introduced [Labeled Statistics](/docs/1.2.0/user-guide.html#labeled-statistics)
- Added [`Arbitraries.entries()`](/docs/1.2.0/user-guide.html#maps)
- Added default generation for `Map.Entry` instances
- [Enhanced statistics reporting](/docs/1.2.0/user-guide.html#collecting-and-reporting-statistics) 
  by absolute count
- _jqwik_ can now generate instances of 
  [functions and other functional types](/docs/1.2.0/user-guide.html#functional-types)
- Provider methods do now accept 
  [two optional parameters](docs/1.2.0/user-guide.html#parameter-provider-methods)
- New `@From` annotation to 
  [provide arbitraries for embedded type parameters](/docs/1.2.0/user-guide.html#providing-arbitraries-for-embedded-types) 


#### Breaking Changes

- Removed `CharacterArbitrary.between(min, max)` 
  which had been deprecated in [1.1.3](#113)


#### API Promotions

- Promoted from `API.Status.EXPERIMENTAL` to `API.Status.MAINTAINED`:

  - `Arbitraries.forType(Class<T> targetType)` and interface `TypeArbitrary`
  - Annotation `@UseType` and enum `UseTypeMode`
  - `Arbitrary.fixGenSize(int genSize)`
  - `Combinators.withBuilder(Supplier<B> builderSupplier)` and
    `Combinators.withBuilder(Arbitrary<B> builderArbitrary)`
  - Annotation `@Domain` and interface `DomainContext`

#### Dependency Upgrades and Bug Fixes 

- Upgrade to JUnit platform 1.5.1
- Upgrade to Gradle 5.5.1
- Default character generation now excludes codepoints `0xd800` through `0xdfff`
- Fixed set generation bug https://github.com/jlink/jqwik/issues/65
- Fixed [bug in Unshrinkable.equals](https://github.com/jlink/jqwik/pull/66).
  Many thanks to [mhyeon-lee](https://github.com/mhyeon-lee) for that pull request!
- List and array generation from unique arbitraries now have a default max size
  that makes sense


## 1.1.x

### 1.1.6

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.1.6/user-guide.html">User guide</a>
and
<a href="/docs/1.1.6/javadoc/index.html">javadoc</a>
</p>

- Removed dependencies to internal classes in JUnit platform
- Added [`Arbitraries.maps()`](/docs/1.1.6/user-guide.html#maps)
- Added default generation for `Map` instances
- `@Domain` annotation can now also refer to inner class of test container's base class.
  Many thanks to [larsrh](https://github.com/larsrh) for that pull request!

### 1.1.5

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.1.5/user-guide.html">User guide</a>
and
<a href="/docs/1.1.5/javadoc/index.html">javadoc</a>
</p>

- Added `shrinkTowards(value)` to all decimal number arbitraries: 
  FloatArbitrary, DoubleArbitrary, BigDecimalArbitrary
- Exceptions during test class instance creation are now reported as failures
- Removed caching of engine descriptor in JqwikTestEngine

### 1.1.4

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

### 1.1.3

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
- Added `CharacterArbitrary.with(char ... allowedChars)`
- Added `CharacterArbitrary.range(char min, char max)`
- Deprecated `CharacterArbitrary.between(char min, char max)`

### 1.1.2

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.1.2/user-guide.html">User guide</a>
and
<a href="/docs/1.1.2/javadoc/index.html">javadoc</a>
</p>

- Added `Arbitrary.forEachValue(Consumer action)`
- Fixed [Kotlin compatibility issue](https://github.com/jlink/jqwik/issues/52)
- Fixed [decimal generation bug](https://github.com/jlink/jqwik/issues/50)
- Update to JUnit Platform 5.4.1


### 1.1.1

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

### 1.1.0

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