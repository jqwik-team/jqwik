### Bugs

### Missing Tests

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

- Parallel test execution: Does it make sense with non working IntelliJ support?

### Properties

- Shrinking
  - simple approach first to check mechanism
  - Internal Shrinking for all Arbitrary classes

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
