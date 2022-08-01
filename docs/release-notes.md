---
title: jqwik Release Notes
---
 
# Release Notes

<!-- use `doctoc --maxlevel 2 release-notes.md` to recreate the TOC -->
<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
### Table of Contents  

- [1.7.0-SNAPSHOT](#170-snapshot)
- [1.6.x](#16x)
- [1.5.x](#15x)
- [1.4.0](#140)
- [1.3.x](#13x)
- [1.2.x](#12x)
- [1.1.x](#11x)
- [1.0.0](#100)
- [0.9.x](#09x)
- [0.8.x](#08x)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


## 1.7.0-SNAPSHOT

#### New and Enhanced Features

- Promoted APIs from `EXPERIMENTAL` to `MAINTAINED`:
    - `net.jqwik.api.ArbitrarySupplier`
    - `net.jqwik.api.Arbitraries.subsetOf(Collection<T>)`
    - `net.jqwik.api.Arbitraries.subsetOf(T...)`
    - `net.jqwik.api.JavaBeanReportingFormat`
    - `net.jqwik.api.arbitraries.StringArbitrary.repeatChars(double)`
    - `net.jqwik.api.domains.DomainContext.initialize(PropertyLifecycleContext)`
    - `net.jqwik.api.footnotes.EnableFootnotes`
    - `net.jqwik.api.footnotes.Footnotes`
    - Large chunks of the hooks and extension API in package `net.jqwik.api.lifecycle`

- Upgrade to JUnit Platform 1.9.0

#### Breaking Changes

- [Default configuration](https://jqwik.net/docs/current/user-guide.html#jqwik-configuration) 
  for `jqwik.failures.after.default` is now `SAMPLE_FIRST`.
  Set it to `PREVIOUS_SEED` if you want the behaviour of jqwik < 1.7.

- `Arbitrary.filter(Predicate<T> predicate, int maxMisses)` has swapped arguments:
  `Arbitrary.filter(int maxMisses, Predicate<T> predicate)` to allow for a more idiomatic use in Kotlin.
  See https://github.com/jlink/jqwik/issues/334.

- Removed method `ActionSequenceArbitrary.ofMinSize(int)` which had been deprecated in 1.5.3

- Removed method `ActionSequenceArbitrary.ofMaxSize(int)` which had been deprecated in 1.5.3

- Removed method `Combinators.withBuilder(Supplier)` which had been deprecated in 1.5.4

- Removed method `Combinators.withBuilder(Arbitrary)` which had been deprecated in 1.5.4

- Removed type `Combinators.BuilderCombinator` which had been deprecated in 1.5.4

- Removed type `Combinators.CombinableBuilder` which had been deprecated in 1.5.4

- Removed method `Functions.FunctionWrapper.returns(Arbitrary)` which had been deprecated in 1.6.0

- Removed method `CharacterArbitrary.digit()` which had been deprecated in 1.5.3

#### Bug Fixes and Improvements

- Generated TLDs in `web-module` will no longer start with digit.
  See https://github.com/jlink/jqwik/issues/316.

- Fixed potential stack overflow when dealing with recursive types.
  See https://github.com/jlink/jqwik/issues/327.

- Property methods in Kotlin with `internal` modifier get now their correct name.

- Combined arbitraries will not explode so easily now.
  See https://github.com/jlink/jqwik/issues/342.

- Frequency based arbitraries now perform better with large number of options.
  See https://github.com/jlink/jqwik/issues/332.

- Generator memoization now works for most kinds of arbitraries and therefore uses less memory.
  See https://github.com/jlink/jqwik/issues/339.

- Arbitrary API methods `ofMinSize()` and `ofMinLength()` are handled more intelligently.
  See https://github.com/jlink/jqwik/issues/377.


## 1.6.x

### 1.6.5

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.6.5/user-guide.html">User guide</a>,
<a href="/docs/1.6.5/javadoc/index.html">javadoc</a>
and <a href="/docs/1.6.5/kdoc/index.html">kdoc</a>
</p>

#### New and Enhanced Features

- After-execution sample will only be reported if there are _visible differences_ 
  to the before-execution sample.

#### Breaking Changes

- The return type of `@Provide` methods in subclasses of `DomainContextBase` 
  can no longer be supertypes of the target type to match.
  For example `Arbitrary<List<?>>` will no longer match target type `List<String>``

#### Bug Fixes

- Some false positive matching of provider method return types has been fixed.

- Data-driven properties now allow compatible null values.
  See https://github.com/jlink/jqwik/issues/308.


### 1.6.4

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.6.4/user-guide.html">User guide</a>,
<a href="/docs/1.6.4/javadoc/index.html">javadoc</a>
and <a href="/docs/1.6.4/kdoc/index.html">kdoc</a>
</p>

#### New and Enhanced Features

- Added `Arbitraries.subsetOf(T...values)` and `Arbitraries.subsetOf(Collection<? extends T> values)`

- Added Kotlin convenience function `Collection<T>.anySubset() : SetArbitrary<T>`

- `DomainContext` and `DomainContextBase` implementations can now provide `SampleReportingFormat` classes and instances.

- Added `Arbitraries.recursive(Supplier<Arbitrary<T>> base, Function<Arbitrary<T>, Arbitrary<T>> recur, int minDepth, int maxDepth)`.
  You can find a usage example [here](https://jqwik.net/docs/1.6.4/user-guide.html#deterministic-recursion-with-recursive)

#### Breaking Changes

- Changed `DomainContext.getArbitraryProviders()` to return `Collection<ArbitraryProvider>`

- Changed `DomainContext.getArbitraryConfigurators()` to return `Collection<ArbitraryConfigurator>`

#### Bug Fixes

- With `AfterFailureMode` set to `SAMPLE_ONLY` or `SAMPLE_FIRST` recreating previous sample could sometimes
  take VERY long. This has been fixed.


### 1.6.3

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.6.3/user-guide.html">User guide</a>,
<a href="/docs/1.6.3/javadoc/index.html">javadoc</a>
and <a href="/docs/1.6.3/kdoc/index.html">kdoc</a>
</p>

#### New and Enhanced Features

- Upgraded `kotlinx` to version `1.6.0`

- Upgraded to Kotlin 1.6.10.

- `@ForAll` and `@From` now support 
  [arbitrary suppliers](/docs/1.6.3/user-guide.html#arbitrary-suppliers) 
  through a `supplier` attribute.

- Changed [Lifecycle Storage API](/docs/1.6.3/user-guide.html#lifecycle-storage) 
  so that stored values can now implement `Store.CloseOnReset` if they need closing action.

- Added capability to automatically resolve Arbitrary parameters.
  This is an experimental feature.

- Module `time` has added the capability to 
  [generate `ZonedDateTime` objects](/docs/1.6.3/user-guide.html#zoneddatetimearbitrary). 
  Many thanks to https://github.com/zinki97 for his continued support with this module!

#### Breaking Changes

- `kotlinx-coroutine-test` replaced `runBlockingTest` with `runTest`.

- Arbitrary provider methods can no longer have a `SubtypeProvider` parameter.

- Removed experimental API `Store.onClose()`.

#### Bug Fixes

- Shrinking now still works when thrown exception has no stacktrace.
  See https://github.com/jlink/jqwik/issues/283.

- Builders can now use nullable arbitraries.
  See https://github.com/jlink/jqwik/issues/295.


### 1.6.2

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.6.2/user-guide.html">User guide</a>,
<a href="/docs/1.6.2/javadoc/index.html">javadoc</a>
and <a href="/docs/1.6.2/kdoc/index.html">kdoc</a>
</p>


#### New and Enhanced Features

- Complete Rework of [after-failure handling](/docs/1.6.2/user-guide.html#rerunning-falsified-properties):
  - `AfterFailureMode.SAMPLE_FIRST` and `AfterFailureMode.SAMPLE_ONLY` no longer depends on serializability of generated parameters.
  - If random seed is manually changed after a failing test run using `Property.seed=<new random seed>`
    the configured after-failure-mode does not apply for the next test run.
  - `SAMPLE_FIRST` and `SAMPLE_ONLY` now also work for data-driven properties and exhaustive generation.
    I recommend now to use `SAMPLE_FIRST` as 
    [default configuration value](/docs/1.6.2/user-guide.html#jqwik-configuration) for most projects. 

- Added `PropertyDefaults.maxDiscardRatio`

- Added two Kotlin convenience functions:
  - `fun <T> frequency(vararg frequencies: Pair<Int, T>)` 
  - `fun <T> frequencyOf(vararg frequencies: Pair<Int, Arbitrary<out T>>)` 

- Maximum size of generated collections and arrays is now generated regularly, if no size distribution is specified

#### Breaking Changes

- Parameter annotations on array types (e.g. `@WithNull String[]`) 
  [are no longer applied to the component type](/docs/1.6.2/user-guide.html#constraining-array-types).

- The jqwik database no longer stores test run data for succeeding tests and properties.
  This means that the file `.jqwik-database` in now much smaller in most cases.

#### Bug Fixes

- Using `@WithNull` on primitive array types no longer leads to an `IllegalArgumentException`.
  See https://github.com/jlink/jqwik/issues/270.

- Generic array types in parameters can now be properly resolved

- After-failure handling now (hopefully) works as expected

### 1.6.1

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.6.1/user-guide.html">User guide</a>,
<a href="/docs/1.6.1/javadoc/index.html">javadoc</a>
and <a href="/docs/1.6.1/kdoc/index.html">kdoc</a>
</p>

#### New and Enhanced Features

- Added `TypeArbitrary.enableRecursion()` to enable recursive usage of type based
  generation for types that do not have an applicable default arbitrary.

- `@UseType` now has an additional attribute `enableRecursion`,
  which is set to `true` by default.

- Added experimental `Arbitraries.traverse(..)` as a mechanism to traverse a type
  and all the types it depends on for arbitrary generation.

- `TypeArbitrary.use(Executable creator)` is now deprecated and will be removed in 1.8.0

- A container class will now inherit `@PropertiesDefault` annotations from superclass and interfaces.

- A container class will now inherit `@AddLifecycleHook` annotations from superclass and interfaces.

- A container class will now inherit `@Domain` annotations from superclass and interfaces.

- A container class will now inherit `@Tag` annotations from superclass and interfaces.

- Subclasses of `DomainContextBase` can now implement `ArbitraryProvider` which
  is used as a provider for this domain.

- Subclasses of `DomainContextBase` can now implement `ArbitraryConfigurator` which
  is used as a configurator for this domain.

- Multi-value arbitraries (ListArbitrary, SetArbitrary etc.) check minSize and maxSize
  values at configuration time now.

- Upgrade to Kotlin 1.6.0. Versions 1.5.x should still work with the binary. 

- `DomainContext` implementation classes can now be annotated with `@Domain` themselves. 

- Tags added to containers or property methods through `@Tag` annotations
  are now being reported in execution result report as part of the key.

- After failure modes `SAMPLE_ONLY` and `SAMPLE_FIRST` now discover changes
  to parameter configuration with better accuracy, which leads to fewer
  "impossible" samples being injected into property methods.

- Upgrade to JUnit Platform 1.8.2

#### Breaking Changes

- Inherited property defaults could change behaviour of existing properties

- Inherited lifecycle hooks could break existing properties

- Inherited domain contexts could break existing properties

- `@UseType` will by default allow recursively resolve by type

#### Bug Fixes

- `Arbitraries.defaultFor(..)` did not apply configurators, but does now.

- Kotlin provider methods can now have `internal` modifier

### 1.6.0

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.6.0/user-guide.html">User guide</a>,
<a href="/docs/1.6.0/javadoc/index.html">javadoc</a>
and <a href="/docs/1.6.0/kdoc/index.html">kdoc</a>
</p>

#### New and Enhanced Features

- Added new module [`jqwik-kotlin`](/docs/1.6.0/user-guide.html#kotlin-module) 
  for improved Kotlin support.

- Introduced new lifecycle hook [`InvokePropertyMethodHook`](/docs/1.6.0/user-guide.html#invokepropertymethodhook) 
  in order to support some Kotlin specialties.

- Introduced new lifecycle hook [`ProvidePropertyInstanceHook`](/docs/1.6.0/user-guide.html#providepropertyinstancehook) 
  in order to allow Kotlin singleton objects as test containers.

- Promoted APIs from `EXPERIMENTAL` to `MAINTAINED`:
    - `net.jqwik.api.Arbitrary.ignoreException(..)`
    - `net.jqwik.api.Property.whenFixedSeed`
    - `net.jqwik.api.RandomDistribution` and its usages in numerical arbitraries
    - `net.jqwik.api.arbitraries.ArbitraryDecorator`
    - `net.jqwik.api.constraints.NotBlank`
    - `net.jqwik.api.domains.DomainContextBase`
    - `net.jqwik.api.statistics.StatisticsReport`

- Upgraded to JUnit Platform 1.8.1

- Added experimental `JqwikSession` API to 
  [use arbitraries outside jqwik's lifecycle](/docs/1.6.0/user-guide.html#using-arbitraries-outside-jqwik-lifecycle).

- Added `Functions.FunctionWrapper.returning()` as replacement for deprecated `returns()`.

- Added explicit module information for all modules.
  See https://github.com/jlink/jqwik/issues/243. 
  Thank you https://github.com/sormuras for the support!


#### Breaking Changes

- Removed `AbstractDomainContextBase` which had been deprecated in 1.5.2

- `@WithNull` now has a default probability of 0.05 instead of 0.1

- Configuration through a `jqwik.properties` file is no longer supported.
  Please use [JUnit Platform configuration](/docs/1.6.0/user-guide.html#jqwik-configuration) instead.

- If a property methods returns `false` or `Boolean.FALSE` it will now be considered to have failed.
  All other return values - including `null` - are regarded as success.
  Before this version the method's return type had to be `boolean` or `Boolean` for the return value to matter.

#### Bug Fixes

- Fixed memory leak when calling `Arbitrary.sample()` in jqwik scope.
  See https://github.com/jlink/jqwik/issues/236.

- Sometimes Kotlin method names have a postfix. 
  They can now be resolved and have normal display names.

- Arbitrary.withoutEdgeCases() sometimes did not work in combination with 
  filter, map and flatMap.

- When an arbitrary fails to generate values, the property seed will now be reported.

## 1.5.x

### 1.5.6

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.5.6/user-guide.html">User guide</a>
and
<a href="/docs/1.5.6/javadoc/index.html">javadoc</a>
</p>

#### New and Enhanced Features

- Upgraded to JUnit platform 1.8.0

- Improved performance of large collection generation.
  See https://github.com/jlink/jqwik/pull/227.

#### Breaking Changes

- Parameters for `StringArbitrary.ofMinLength()` and `StringArbitrary.ofMaxLength()` are validated.
  See https://github.com/jlink/jqwik/issues/221.

#### Bug Fixes

- Added reporting format for type `char[]`.

- `@StatisticsReport(onFailureOnly = true)` now also works when failure 
  is a statistics check failure.


### 1.5.5

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.5.5/user-guide.html">User guide</a>
and
<a href="/docs/1.5.5/javadoc/index.html">javadoc</a>
</p>

#### New and Enhanced Features

- You can now [add footnotes to failure reports](/docs/1.5.5/user-guide.html#adding-footnotes-to-failure-reports)

- Added `StatisticsReport.onFailureOnly()` to suppress statistics reporting
  for properties that do not fail.

- Shrinking across several for-all parameters has been improved.

- The Web module now supports
  [web domain name generation](/docs/1.5.5/user-guide.html#web-domain-generation).

- The Time module now supports the generation of `java.time.OffsetDateTime` instances.

#### Breaking Changes

- Properties with single try (aka examples) with at least one `@ForAll` parameter will now
  produce a test report even if they succeed.
  See https://github.com/jlink/jqwik/issues/217.

#### Bug Fixes

- Fixed edge cases performance problem. 
  See https://github.com/jlink/jqwik/issues/214


### 1.5.4

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.5.4/user-guide.html">User guide</a>
and
<a href="/docs/1.5.4/javadoc/index.html">javadoc</a>
</p>

#### New and Enhanced Features

- Added default method `DomainContext.initialize(PropertyLifecycleContext)` which
  can be overridden if domain context implementations need access to the property context.
  
- Added `StringArbitrary.repeatChars(double repeatProbability)`

- Added `Arbitrary.optional(double presenceProbability)`

- Improved shrinking performance of combinator-based arbitraries

- Added [`net.jqwik.api.Builders`](/docs/1.5.4/javadoc/net/jqwik/api/Builders.html)
  as replacement for the now deprecated `net.jqwik.api.Combinators.withBuilder(..)` API.
  Here's the [relevant section](/docs/1.5.4/user-guide.html#combining-arbitraries-with-builder) in the user guide.

- Progress on [generation of DateTimes](/docs/1.5.4/user-guide.html#generation-of-datetimes):
  Objects of type `java.time.Instant` can now be generated by default.

- `FloatArbitrary` and `DoubleArbitrary` can now 
  [generate special values on demand](/docs/1.5.4/user-guide.html#special-decimal-values).


#### Breaking Changes

- Generated strings will no longer intentionally generate duplicate characters
  by default. You now have to tell it to do so.

- Methods `Combinators.withBuilder(builderSupplier)` and 
  `Combinators.withBuilder(arbitrary)` are now deprecated.

#### Bug Fixes

- Addressed performance issues raised in https://github.com/jlink/jqwik/issues/206

- Fixed some type resolution and type usage bug found through
  https://github.com/mihxil/math.

### 1.5.3

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.5.3/user-guide.html">User guide</a>
and
<a href="/docs/1.5.3/javadoc/index.html">javadoc</a>
</p>

#### New and Enhanced Features

- You can now influence the random distribution of 
  [the size of generated multi-value containers](/docs/1.5.3/user-guide.html#size-of-multi-value-containers) 
  (sets, lists, arrays, streams, iterators and maps).

- You can now influence the random distribution of 
  [the size of generated strings](/docs/1.5.3/user-guide.html#string-size).

- All container-based arbitraries (e.g. List, Sets, Strings) now generate containers 
  without duplicated elements with a higher probability even when they have many elements. 

- String arbitraries now generate duplicate chars and series of chars with a higher probability. 

- Added `CharacterArbitrary.numeric()` and `CharacterArbitrary.alpha()`.

#### Breaking Changes

- `ActionSequenceArbitrary` no longer extends `SizableArbitrary`.

- `ActionSequenceArbitrary.ofMinSize()` and `ActionSequenceArbitrary.ofMaxSize()`
  is now deprecated.

- `CharacterArbitrary.digit()` is now deprecated.

#### Bug Fixes

- Module name now correctly set for testing, web and time modules.
  See https://github.com/jlink/jqwik/issues/201.

- No longer throwing ConcurrentModificationException when using `sample()` within another generator.
  This happened in Java >= 11 only.
  See https://github.com/jlink/jqwik/issues/205


### 1.5.2

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.5.2/user-guide.html">User guide</a>
and
<a href="/docs/1.5.2/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Added new base class 
  [`DomainContextBase`](/docs/1.5.2/javadoc/net/jqwik/api/domains/DomainContextBase.html), which changes the way 
  how you typically [provide domain contexts](/docs/1.5.2/user-guide.html#domain-and-domain-context).
  
  That's the reason why 
  [`AbstractDomainContextBase`](/docs/1.5.2/javadoc/net/jqwik/api/domains/AbstractDomainContextBase.html) 
  is now _deprecated_.

- Provider Methods annotated with `@Provide` now support
  [implicit flat mapping](/docs/1.5.2/user-guide.html#implicit-flat-mapping).

- More progress on 
  [generation of DateTimes](/docs/1.5.2/user-guide.html#generation-of-datetimes)

- Upgrade to JUnit Platform 1.7.2


#### Breaking Changes

- Removed `leapYears(boolean withLeapyear)` from all date generating arbitraries.

- Changed `Arbitrary<T> Arbitraries.oneOf(List<T> choices)` to
  `Arbitrary<T> Arbitraries.oneOf(Collection<? extends T> choices)`

#### Bug Fixes

- Fixed bug in edge case generation: 
  https://github.com/jlink/jqwik/issues/180

- Fixed bug when running inner tests of an extended test container:
  https://github.com/jlink/jqwik/issues/179

- Fixed bug in Tuple.toString():
  https://github.com/jlink/jqwik/issues/187

### 1.5.1

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.5.1/user-guide.html">User guide</a>
and
<a href="/docs/1.5.1/javadoc/index.html">javadoc</a>
</p>

#### New and Enhanced Features

- Implemented `@StatisticsReport.label` as suggested in 
  https://github.com/jlink/jqwik/issues/146.

- Time module
  - [Generation of Times](/docs/1.5.1/user-guide.html#generation-of-times)
  - [Generation of DateTimes](/docs/1.5.1/user-guide.html#generation-of-datetimes)
    got its first rudimentary support.

- Added `StringArbitrary.excludeChars(char ... toExclude)`. 
  See https://github.com/jlink/jqwik/issues/167.

#### Breaking Changes

- Trying to add a _numerical_ edge case that is outside the arbitrary's
  allowed range will now throw an `IllegalArgumentException`.

#### Bug Fixes

- Fixed memory leakage introduced in 1.5.0.

- Shrinking of flat mapped values would sometimes never end.

### 1.5.0

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.5.0/user-guide.html">User guide</a>
and
<a href="/docs/1.5.0/javadoc/index.html">javadoc</a>
</p>

#### New and Enhanced Features

- Added constraint annotation `@NotBlank` for String parameters

- Generated email addresses get a few more edge cases

- Shrinking of long Strings (length > 100) is faster now

#### Breaking Changes

- Removed `Arbitrary.unique()` which had been deprecated in 1.4.0

- Removed annotation `@Unique` which had been deprecated in 1.4.0

- Removed `Arbitraries.constant(..)` which had been deprecated in 1.3.2

#### Bug Fixes

- Fixed [degraded generation performance](https://github.com/jlink/jqwik/issues/166) 
  introduced in version [1.4.0](#140). 

- `Arbitrary.withoutEdgeCases()` did not really get rid of all edge case generation.
  Now it does.

- Some arbitrary types, e.g. `Arbitraries.lazyOf()` could not be used in sampling.
  Now all should work.
  
- Bounded shrinking could previously result in an `OutsideJqwikException`.
  

## 1.4.0

Unless you don't have the time for migrating from `Arbitrary.unique()` to
[the new uniqueness approach](/docs/1.4.0/user-guide.html#uniqueness-constraints)
but still need some of the newly added features, you should directly upgrade to
version [1.5.0](#150).

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.4.0/user-guide.html">User guide</a>
and
<a href="/docs/1.4.0/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Upgrade to JUnit Platform 1.7.1

- Using `@Property(edgeCases = NONE)` will now also suppress the generation of 
  edge cases in embedded arbitraries.

- You now have the capability to configure an 
  [arbitrary's edge case generation](/docs/1.4.0/user-guide.html#configuring-edge-cases-themselves).

- There is a new annotation attribute `@Property.whenSeedFixed` and a new
  [configuration parameter](/docs/1.4.0/user-guide.html#jqwik-configuration)
  `jqwik.seeds.whenfixed`
  to warn or even fail when a property has been given a fixed random seed. 
  See [this issued](https://github.com/jlink/jqwik/issues/138) for more details.
  Many thanks to [osi](https://github.com/osi) for this contribution.

- jqwik's approach to specify uniqueness of generated objects has been completely revamped:

    - `Arbitrary.unique()` and the annotation `@Unique` are now deprecated and will
      be removed in version 1.5.
    - [Uniqueness constraints](/docs/1.4.0/user-guide.html#uniqueness-constraints) 
      are now handled by the elements' container.
    - The new [annotation to require uniqueness](/docs/1.4.0/user-guide.html#unique-elements) 
      is called `@UniqueElements` and applied to the container's type.

- Arbitraries modified through `Arbitrary.unique()`, which is deprecated anyway,
  no longer generate edge cases. This is actually a bug fix since the
  generated edge were not considered for uniqueness.

- There's a new [jqwik module `jqwik-time`](/docs/1.4.0/user-guide.html#time-module)
  which simplifies the generation of dates (and times in a future release).
  Many thanks to [zinki97](https://github.com/zinki97) for this contribution.

- There's a new [jqwik module `jqwik-web`](/docs/1.4.0/user-guide.html#web-module)
  which is currently home of email addresses generation.

- Configuration parameters are now loaded via JUnit's
  [Configuration Parameters](https://junit.org/junit5/docs/current/user-guide/#running-tests-config-params) mechanism.
  Parameters specified in `jqwik.properties` continue to work, but are considered deprecated.
  Log messages will be emitted for any used properties with their new name.
  Some [parameter names](/docs/1.4.0/user-guide.html#jqwik-configuration) have also changed.
  Many thanks to [osi](https://github.com/osi) for this contribution.

- Using internal Kotlin methods as properties will now produce the correct name
  as seen in the Kotlin source code.

- The User Guide has been restructured.

- There will now be a warning log entry when a property with more than 1 try has no
  '@ForAll' parameters.

- Promoted APIs from `EXPERIMENTAL` to `MAINTAINED`
    - Mostly everything in package `net.jqwik.api.lifecycle`
    - Method `Arbitrary.dontShrink()`
    - Method `Combinators.CombinableBuilder.inSetter(..)`
    - Class `PropertyDefaults`
    - Class `Reporter`
    - Class `SampleReportingFormat`
    - Method `Tuple.of()`
    - Method `Tuple.empty()`
    - Method `BigDecimalArbitrary.shrinkTowards(..)`
    - Method `BigIntegerArbitrary.shrinkTowards(..)`
    - Method `ByteArbitrary.shrinkTowards(..)`
    - Method `DoubleArbitrary.shrinkTowards(..)`
    - Method `FloatArbitrary.shrinkTowards(..)`
    - Method `IntegerArbitrary.shrinkTowards(..)`
    - Method `LongArbitrary.shrinkTowards(..)`
    - Method `ShortArbitrary.shrinkTowards(..)`
    - Method `SetArbitrary.mapEach(..)`
    - Method `SetArbitrary.flatMapEach(..)`
    - Method `ListArbitrary.mapEach(..)`
    - Method `ListArbitrary.flatMapEach(..)`
    - Method `ActionSequence.withInvariant(..)`
    - Method `Statistics.coverage(..)`
    - Method `StatisticsCollector.coverage(..)`
    - Class `StatisticsCoverage`
    - Class `StatisticsEntry`
    
#### Breaking Changes

- `Arbitraries.emails()` has been moved to `net.jqwik.web.api.Web.emails()` in new 
  [Web module](/docs/1.4.0/user-guide.html#web-module).

- `@net.jqwik.api.constraints.Email` annotation has been moved to 
  `@net.jqwik.web.api.Email` in new 
  [Web module](/docs/1.4.0/user-guide.html#web-module).

- Parameters annotated with `@Email` will by default only generate
  "standard" email addresses.

- Removed deprecated APIs
    - `Arbitrary.samples(..)`
    - Interface `FalsificationResult`
    - `SampleReportingFormat.reportJavaBean(..)`
    - `Shrinkable.shrink(Falsifier<T> falsifier)`
    - Interface `ShrinkingSequence`
    - `CharacterArbitary.with(Arbitrary<Character> characterArbitrary)`
    - `StringArbitary.withChars(Arbitrary<Character> characterArbitrary)`

- Unconstrained wildcards are no longer handled in a special way
  but just like unconstrained type variables.
  
- `Arbitrary.array(..)` now returns `ArrayArbitrary` instead of `StreamableArbitrary`

#### Bug Fixes

- Fixed bug that could lead to strange edge case generation behaviour 
  when `Arbitrary.edgeCases(edgeCasesConfig)` was used.

## 1.3.x

### 1.3.10

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.10/user-guide.html">User guide</a>
and
<a href="/docs/1.3.10/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Introduced `JavaBeanReportingFormat` for simpler and configurable reporting
  of Java beans.

#### Breaking Changes

- `SampleReportingFormat.reportJavaBean(..)` has been deprecated. 
  Will be removed in version 1.4.0.

#### Bug Fixes

- `Arbitraries.lazy()`
  [did not allow exhaustive generation](https://github.com/jlink/jqwik/issues/137)
  but does now.

- `SampleReportingFormat.reportJavaBean(..)` can now handle methods named
  `is` or `get`.

- `SampleReportingFormat.reportJavaBean(..)` can now handle properties
  that return `Optional<T>`.

### 1.3.9

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.9/user-guide.html">User guide</a>
and
<a href="/docs/1.3.9/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- [Generating email addresses](/docs/1.3.9/user-guide.html#email-address-generation)
  has now configuration options. 

- Generated email addresses now 
  [have fewer edge cases](https://github.com/jlink/jqwik/issues/133). 

- Added constraint annotation
  [`@Email`](/docs/1.3.9/javadoc/net/jqwik/api/constraints/Email.html)
  for generating valid email addresses.
  
- Experimental support for 
  [changing an arbitrary's edge cases](/docs/1.3.9/javadoc/net/jqwik/api/Arbitrary.html#edgeCases(java.util.function.Consumer)).

- Constraint annotation `@StringLength` now works for any arbitrary that generates a `String`.

- Range annotations (`@Byte|Short|Int|Long|Float|Double|BigRange`) do work now when applied
  to any arbitrary that generates the appropriate numeric type.

#### Breaking Changes

- Email arbitrary no longer generates domain hosts without top level domain

#### Bug Fixes

- [Reporting of null values in failed properties](https://github.com/jlink/jqwik/issues/132) 
  no longer fails with NPE.

- Sampling arbitraries with null values 
  [no longer fails](https://github.com/jlink/jqwik/pull/136).

### 1.3.8

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.8/user-guide.html">User guide</a>
and
<a href="/docs/1.3.8/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- You can now 
  [generate valid email addresses](/docs/1.3.8/user-guide.html#email-address-generation). 

- The header of the label column in 
  [histograms and number range histograms](/docs/1.3.8/user-guide.html#histograms) 
  can now be changed.

#### Breaking Changes

- The distribution of char groups when generating `Character`s or `String`s
  is now weighted by the number of chars in each group so that each possible
  char has the same probability of being generated - except for edge cases.

- `StringArbitrary.withChars(Arbitrary<Character>)` was deprecated

- `CharacterArbitrary.with(Arbitrary<Character>)` was deprecated

#### Bug Fixes

No known bugs.
                                 

### 1.3.7

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.7/user-guide.html">User guide</a>
and
<a href="/docs/1.3.7/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Added two edge cases for generation of integral numbers.

- Reporting of parameters that changed during execution of property is now better
  at detecting real changes.
  
#### Breaking Changes

- _Examples_ with a failing assumption - i.e. throwing a `TestAbortedException` -
  will now be reported as being skipped. See https://github.com/jlink/jqwik/issues/122
  for motivation and reasoning.

#### Bug Fixes

- Using some arbitraries with `Arbitrary.sample()` outside of a jqwik context 
  [no longer worked](https://github.com/jlink/jqwik/issues/125). 
  Now it does again.

- The default character generator was erroneously considered to produce unique
  characters. Resulted in a strange to analyze 
  [bug when generating functions](https://github.com/jlink/jqwik/issues/126)
                                 

### 1.3.6

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.6/user-guide.html">User guide</a>
and
<a href="/docs/1.3.6/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Shrinking behaviour of arbitraries created with
  `Combinators.withBuilder(..)` is now _much_ better, especially _much_ faster.

- Upgrade to JUnit Platform 1.7.0

#### Breaking Changes

- A maximum of 1000 (instead of 10000) edge cases is generated _per arbitrary_.

- Arbitraries that allow nullables through `Arbitrary.injectNull()` or 
  annotation `@WithNull` will now shrink their values to `null` if possible.

#### Bug Fixes

- With a lot of edge cases sometimes _only_ edge cases were generated. 
  Now the minimum ratio is 1 edge case in 3 generating steps.

- Warning about combinatorial explosion of edge cases generation is
  now [logged only once](https://github.com/jlink/jqwik/issues/119). 

- Edge cases for `oneOf(..)` and `frequencyOf(..)` generators are now correctly being shrunk.

### 1.3.5

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.5/user-guide.html">User guide</a>
and
<a href="/docs/1.3.5/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Added `Tuple.of()` and `Tuple.empty()` to create an empty tuple.

- The time out for bounded shrinking can now be changed in `jqwik.properties`

- Sample reporting will now report changes to parameters during property execution

- Added some convenience to use POJOs as builders:

  - `BuilderCombinator.build()`: Return arbitrary of builder itself
  - `CombinableBuilder.inSetter(..)`: Set a builder's property and go on using it

- Added `SampleReportingFormat.reportJavaBean(Object bean)`

#### Breaking Changes

- Shrinking is no longer bound by number of shrinking attempts, but by time with a 10 seconds default.
  The reason is that counting shrinking attempts in a consistent manner was difficult, and
  shrinking could take very very long despite being in `BOUNDED` mode.

#### Bug Fixes

- edge cases generation will be stopped when 10000 edge cases have been found.
  See https://github.com/jlink/jqwik/issues/113


### 1.3.4

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.4/user-guide.html">User guide</a>
and
<a href="/docs/1.3.4/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- `Arbitraries.lazyOf(Supplier<Arbitrary<T>> ...)` is now the method of choice for 
  recursive value generation. It has much better shrinking behaviour than the
  more generic `lazy()` combined with `oneOf()`. 
  Consult [the user guide](/docs/1.3.4/user-guide.html#probabilistic-recursion)
  for an example.

- Added `PropertyLifecycleContext.attributes()`, which allows to query, set and change
  a property method's attributes like number of tries and seed within a
 `AroundPropertyHook`  lifecycle hook.

- Added `@PropertyDefaults` annotation which allows to 
  [set the defaults](/docs/1.3.4/user-guide.html#setting-defaults-for-property-attributes) 
  of all property methods in a container.

#### Breaking Changes

- No known breaking changes

#### Bug Fixes

- Made loading of services thread-safe to allow use of jqwik generators 
  in parallel JUnit runs: https://github.com/jlink/jqwik/pull/117.
  Thank you, https://github.com/Danny02!
  

### 1.3.3

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.3/user-guide.html">User guide</a>
and
<a href="/docs/1.3.3/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Shrinking has been re-implemented from scratch. Should be more predictable and 
  shrink to smaller samples in most cases.

- Shrinking now has stronger equivalence requirements for falsification:
  - Same type of assertion error or other exception
  - Assertion error or exception thrown from the same code location

- Shrinking `@AlphaChars` will now shrink to uppercase letters (if possible)
  since their Unicode codepoint is smaller.
  
- Negative numbers are shrunk to their positive counterpart if possible

- Changes in property result reporting: 
    - Shrunk samples have now header line "Shrunk Sample (<n> steps)"
    - If no shrinking took place samples have header line "Sample"
    - Original samples now also report the original error
    - Action sequences in stateful properties got `their own reporting format

- `Arbitraries.frequencyOf() now takes covariant arbitrary types

- Made loading of services thread-safe to allow use of jqwik generators 
  in parallel JUnit runs: https://github.com/jlink/jqwik/pull/117.
  Thank you, https://github.com/Danny02!
  
- Added `PropertyLifecycleContext.attributes()`, which allows to query, set and change
  a property method's attributes like number of tries and seed within a
 `AroundPropertyHook`  lifecycle hook.


#### Breaking Changes

- Exceeding shrinking bound is now logged as warning instead of being reported
  through JUnit platform reporting
  
- Deprecated interface `net.jqwik.api.ShrinkingSequence`. Throw all your implementations away!

- Deprecated class `net.jqwik.api.FalsificationResult`. No longer used anywhere.

- Deprecated method `Shrinkable.shrink(Falsifier<T> falsifier)`. 
  It's no longer used anywhere.

#### Bug Fixes

- Reporting an exhausted property had been broken since `1.2.4` and nobody noticed.
  Works again. 

### 1.3.2

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.2/user-guide.html">User guide</a>
and
<a href="/docs/1.3.2/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Added `Arbitrary.dontShrink()`

- Added `SetArbitrary.mapEach()`

- Added `SetArbitrary.flatMapEach()`

- Added `ListArbitrary.mapEach()`

- Added `ListArbitrary.flatMapEach()`

- Added `Arbitraries.just(aConstant)` and deprecated `Arbitraries.constant(aConstant)`.
  Most other PBT libraries seem to use `just` for this functionality. 
  
- Added programmatic access to 
  [JUnit 5 platform reporting](/docs/1.3.2/user-guide.html#platform-reporting-with-reporter-object)

- Added `Tuple.Tuple5<T,T,T,T,T> Arbitrary.tuple5()`

#### Breaking Changes

- Introduced `StreamableArbitrary` hierarchy:
    - `Arbitrary.set()` now has return type `net.jqwik.api.arbitraries.SetArbitrary`
    - `Arbitrary.list()` now has return type `net.jqwik.api.arbitraries.ListArbitrary`
    - `Arbitrary.stream()` now has return type `net.jqwik.api.arbitraries.StreamArbitrary`
    - `Arbitrary.iterator()` now has return type `net.jqwik.api.arbitraries.IteratorArbitrary`
    
- `Arbitraries.maps(...)` now has return type `net.jqwik.api.arbitraries.MapArbitrary`

- `net.jqwik.api.lifecycle.Reporter` moved to `net.jqwik.api.Reporter`

#### Bug Fixes

- Reporting samples with circular dependencies [does no longer throw
  an exception](https://github.com/jlink/jqwik/issues/111)

- Reporting test failure due to exception without message 
  no longer leads to ignored test
  
- Reporting shrunk samples now report the actual sample and not a freshly generated one

### 1.3.1

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.1/user-guide.html">User guide</a>
and
<a href="/docs/1.3.1/javadoc/index.html">javadoc</a>
</p>


#### New and Enhanced Features

- Reporting of falsified, shrunk and  generated samples 
  [has been completely reworked](/docs/1.3.1/user-guide.html#failure-reporting).
  See [this issue](https://github.com/jlink/jqwik/issues/85) for more details.

- Added `Arbitrary.ignoreException(exceptionType)` which allows to
  [ignore exceptions during value generation](/docs/1.3.1/user-guide.html#ignoring-exceptions-during-generation).

#### Breaking Changes

- `Arbitraries.of(List<T>)` replaced with `Arbitraries.of(Collection<T>)`

- `Arbitraries.ofSuppliers(List<Supplier<T>>)` replaced with `Arbitraries.of(Collection<Supplier<T>>)`

#### Bug Fixes

No open bugs had been reported.

### 1.3.0

<p style="padding-left:1em;font-size:larger">
<a href="/docs/1.3.0/user-guide.html">User guide</a>
and
<a href="/docs/1.3.0/javadoc/index.html">javadoc</a>
</p>

#### New and Enhanced Features

- Added user-guide documentation for [Lifecycle Hooks](/docs/1.3.0/user-guide.html#lifecycle-hooks)

- Added new statistics report formats: 
  [Histogram and NumberRangeHistogram](/docs/1.3.0/user-guide.html#histograms)

- Improved shrinking of dependent parameters.

- Added `Arbitraries.ofSuppliers(..)` to choose among a set of mutable objects.

- You can now influence the 
  [distribution of random number generation](/docs/1.3.0/user-guide.html#random-numeric-distribution): 
  All numeric arbitraries now support `withDistribution(RandomDistribution)`
  to choose between `RandomDistribution.biased()` (default),
   `RandomDistribution.uniform()` and `RandomDistribution.gaussian(borderSigma)`. 
  
- Default number generation has now a much higher bias towards numbers
  that are closer to the shrinking target of a number range.

- Using a faster implementation of `java.util.Random` under the hood

- Massively improved and enhanced 
  [generation of edge cases](/docs/1.3.0/user-guide.html#generation-of-edge-cases)

- Edge Cases Mode is now being reported per property

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

- The evaluation order of `SkipExecutionHook` hooks can no longer be influenced.

#### Bug Fixes

- An `OutOfMemoryError` will go through to the top. Fix for
  [bug report from jqwik-spring](https://github.com/jlink/jqwik-spring/issues/5). 
  
- `Arbitraries.of(listOfValues)` would break when list does not allow null values. 

- Generated functions now handle default methods correctly

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