# Survey about the usage of the not-so-popular features of jqwik

Rate as many features as you like. The more the better.

Rating scale:
- 0: I didn't know about this feature
- 1: I know about this feature, but I never used it
- 2: I used this feature once or twice, but I could easily live without it
- 3: I use this feature regularly, and I would miss it if it was gone
- 4: I use this feature all the time, and it's crucial for my work

## Features

- Have a test container class implement `AutoCloseable`

- Tagging properties with `@Tag` annotation

- Using injected reporter `Reporter reporter` parameter

- Using footnotes through injected `Footnotes footnotes` parameter

- Setting `maxDiscardRatio` for a property

- Using data-driven properties with `@Data` and `@FromData` annotations

- `@ForAll(supplier=<ArbitrarySupplierClass>.class)`: Alternative to `@ForAll("providerMethod")`

- `@From("providerMethod")`: Alternative to `@ForAll("providerMethod")`

- Using `Arbitraries.randomValue(Function<Random, T> randomFunction)` to create custom objects

- Using `Arbitraries.fromGenerator(RandomGenerator generator)` to create custom objects

- Implementing `Arbitrary` interface

- Implementing `RandomGenerator` directly

- Generating functional types (SAM types), e.g. `@ForAll Function<String, Integer> myFunction`

- Changing edge case generation through `Arbitrary.configureEdgeCases(..)`

- Changing `genSize` with `Arbitrary.fixGenSize(int)`.

- Changing the shrinking target of numeric types, e.g. `IntegerArbitrary.shrinkTowards(int target)`

- Changing the random distribution of numeric types, e.g. `IntegerArbitrary.withDistribution(RandomDistribution distribution)`

- Changing the length distribution of collections or strings, 
  e.g. `ListArbitrary.withSizeDistribution(RandomDistribution distribution)`

- Ignoring exceptions during value generation
  - with annotation attribute `@ForAll(ignoreExceptions=<ExceptionType>.class)`  
  - with `Arbitrary.ignoreException(Class<? extends Throwable> exceptionType)`

- Combining arbitraries with Builder API

- Deterministic recursive data generation with `Arbitraries.recursive(..)`.

- Using arbitraries outside Jqwik lifecycle, e.g. to generate test data in example based tests
  - Using `JqwikSession` API 

- Stateful properties
  - Using the old API in package `net.jqwik.api.stateful` 
  - Using the new API in package `net.jqwik.api.state`

- Lifecycle Hooks
  - Writing your own custom lifecycle hooks 
  - Lifecycle storage using `Store` API
  - `@BeforeTry` annotation on member variables
  - `@PerProperty` to specify lifecycle adaptations for individual properties


- Kotlin module

- Time module

- Web module