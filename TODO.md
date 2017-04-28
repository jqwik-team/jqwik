### Bugs

### Missing Tests

- Tests for TestRunDatabase
- JqwikExecutor: test that failed tests from previous run are executed first
- TestRunData

### General

- Make preempted running of failed tests configurable

- Allow custom names for containers and test methods: @Label

- Allow Fixture parameters to examples and properties

- LifeCycles
  - PerClassLifeCycle
  - PerMethodLifeCycle
  - PerCheckLifeCycle

- Parallel test execution

### Properties

- Run failing tests first in next test run

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
