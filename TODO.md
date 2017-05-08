### Bugs

- Explicit Property.seed value should overwrite previous seed even after failure

### Tests

- Add / rework tests for all Shrinkables 
- Tests for TestRunDatabase
- Tests for TestRunData
- Tests for JqwikProperties and its use in JqwikTestEngine

### General

- Allow custom names for containers and test methods: @Label

- Allow Fixture parameters to examples and properties

- LifeCycles
  - PerClassLifeCycle
  - PerMethodLifeCycle
  - PerCheckLifeCycle

- Warn if jqwik tests are annotated with Jupiter/JUnit4 annotations

- Parallel test execution: Does it make sense with non working IntelliJ support?

### Properties

- Shrinking
  - Make it configurable (default on/off)
  - Shrinker for Collections
  - Shrinker for Longs
  - Shrinker for Strings
  - Shrinker for Filters
  - Shrinker for Maps
  - Shrinker for Combinators
  - Shrinker for Arrays

- @ForAll can take `providerClass` parameter (with or without value param)

- Handle error 
  - if more than one generator applies
  - if generic type is a bounded type

- Default Arbitraries and Generators for
  - BigInteger
  - chars, short, byte
  - float, double, BigDecimal
  
- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

### Contracts

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
