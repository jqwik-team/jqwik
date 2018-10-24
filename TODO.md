- <T> Arbitrary<List<T>> Arbitraries.shuffle(T...values)
  - RandomGenerator
  - ExhaustiveGenerator

- PackageDescriptor e.g.
  @Label("mypackage")
  @AddHook(...)
  in package-info.java

- Make reporting configurable
  - Use System.out directly instead of using JUnit 5 reporter
  - jqwik.properties: useJunitPlatformReporter=false