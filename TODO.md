### Bugs

### General

- Allow custom names for containers and test methods: @Label

- Allow Fixture parameters to examples and properties

- LifeCycles
  - PerClassLifeCycle
  - PerMethodLifeCycle
  - PerCheckLifeCycle

### Properties

- Save last state in local database and run all failing properties with 
  latest seed

- Handle error 
  - if more than one generator applies
  - if generic type is a bounded type

- Default Arbitraries and Generators for
  - BigInteger
  - chars, short, byte
  - float, double, BigDecimal
  
- Create child test with previous seed if property fails so that it can be run from runner with a click:
  _Code is commented out because missing support from IDEA and bug in JUnit5 platform_

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

### Contracts

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
