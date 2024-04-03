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


## Issue Text for GitHub Repos

Jqwik Usage Survey

Hi! I noticed you're using the jqwik library in your code base.
You might be interested in participating in jqwik's usage survey, which will be the foundation for jqwik version 2:
[jqwik Usage Survey](https://docs.google.com/forms/d/e/1FAIpQLSeG8-A-QeatayR51p41-PIad8wxPCjlF42TGji4OLqlcUFvHg/viewform?usp=pp_url&entry.444445538=0&entry.1176932837=0&entry.433792307=0&entry.362899335=0&entry.1051484453=0&entry.1308403628=0&entry.1157551046=0&entry.1218216631=0&entry.1884348691=0&entry.1664582984=0&entry.1395243731=0&entry.637599713=0&entry.214390401=0&entry.1023619256=0&entry.2103198793=0&entry.1262440461=0&entry.642227431=0&entry.824407284=0&entry.945992025=0&entry.688647886=0&entry.892125303=0&entry.1318595707=0&entry.1651231167=0&entry.949481904=0&entry.1909153108=0&entry.1310182120=0&entry.1591038055=0&entry.95811223=0&entry.1183762376=0&entry.983463514=0&entry.2030231907=0&entry.1650069733=0&entry.1356824859=0&entry.1217556164=0&entry.1171969955=0&entry.1882369745=0&entry.337847346=0&entry.432565656=0&entry.126087813=0&entry.308155936=0&entry.1308530978=0&entry.1359750459=0&entry.894210534=0&entry.1285593899=0&entry.795916190=0&entry.1039917490=0&entry.1201586198=0&entry.550102822=0&entry.499364845=0&usp=embed_facebook)

Many thanks for considering to participate!