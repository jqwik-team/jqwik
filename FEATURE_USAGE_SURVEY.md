# Survey about the usage of the not-so-popular features of jqwik

Rate as many features as you like. The more the better.

Rating scale:

- 0: I didn't know this feature existed
- 1: I know about this feature, but I never used it
- 2: I used this feature once or twice, but I could easily live without it
- 3: I use this feature regularly, and I would miss it if it was gone
- 4: I use this feature regularly, and it's crucial for my work

## Features

### General Features

- Check `API.status()` for new API methods that you use or intend to use

- Tag properties with `@Tag` annotation

- Use injected parameter `Reporter reporter` to report values during property execution

- Use footnotes through injected `Footnotes footnotes` parameter

- Set `maxDiscardRatio` for a property

- Use data-driven properties with `@Data` and `@FromData` annotations


### Arbitraries and Generators

- `@ForAll(supplier=<ArbitrarySupplierClass>.class)` as alternative
  to `@ForAll("providerMethod")`

- Use `Arbitraries.randomValue(Function<Random, T> randomFunction)`

- Use `Arbitraries.fromGenerator(RandomGenerator generator)`

- Use `Arbitraries.traverse(Class<T> targetType, Traverser traverser)`

- Implement `Arbitrary` interface directly

- Subclass from `ArbitraryDecorator` to create custom arbitrary interfaces

- Implement `RandomGenerator` directly

- Implement and register your own `ArbitraryProvider`

- Implement and register your own `ArbitraryConfigurator`

- Generate functional types (SAM types), e.g. with `@ForAll Function<String, Integer> myFunction`

- Change edge case generation through `Arbitrary.configureEdgeCases(..)`

- Change `genSize` with `Arbitrary.fixGenSize(int)`.

- Change the shrinking target of numeric types,
  e.g. `IntegerArbitrary.shrinkTowards(int target)`

- Change the random distribution of numeric types,
  e.g. `IntegerArbitrary.withDistribution(RandomDistribution distribution)`

- Change the length distribution of collections or strings,
  e.g. `ListArbitrary.withSizeDistribution(RandomDistribution distribution)`

- Ignore exceptions during value generation with `@ForAll(ignoreExceptions=<ExceptionType>.class)`
  or `Arbitrary.ignoreException(Class<? extends Throwable> exceptionType)`

- Combine arbitraries with Builder API `Builders.withBuilder(..)`

- Deterministic recursive data generation with `Arbitraries.recursive(..)`.


### Lifecycle and Lifecycle Hooks

- Have a test container class implement `AutoCloseable`
- Implement your own custom lifecycle hooks
- Use lifecycle storage API on `Store` type 
- Annotate member variables with `@BeforeTry`
- Annotate property methods with `@PerProperty` to specify lifecycle adaptations


### Domains

- Define your own domain by implementing `DomainContext` or extending `DomainContextBase`
- Using `@Domain` annotation to include a domain for value generation


### Using arbitraries outside Jqwik lifecycle, e.g. to generate test data in example based tests

- Use `Arbitrary.sample()` or `Arbitrary.sampleStream()`
- Use `JqwikSession` API
- Use `Arbitrary.allValues()`
- Use `Arbitrary.forEachValue(..)`


### Statistics

- Collect statistics with label: `Statistics.label(String label).collect(..)`
- Report statistics using `Histogram` or `NumberRangeHistogram`
- Implement your own `StatisticsReportFormat`
- Check the coverage of generated data with `Statistics.collect(..).coverage(..)`


### Stateful Testing

- Use the old API in package `net.jqwik.api.stateful`
- Use the new API in package `net.jqwik.api.state`


### Modules and Extensions

- Kotlin module

- Time module

- Web module

- jqwik Spring Support

- jqwik Micronaut Support

- jqwik Testcontainers Support

- jqwik Vavr Support
