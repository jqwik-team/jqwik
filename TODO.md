### Bugs

### General

- Allow custom names for containers and test methods
- Allow Fixture parameters to examples and properties

### Properties

- Generators/Arbitraries
  - Configurations: MaxSize for collections 
- Handle error 
  - if more than one generator applies
  - if generic type is a bounded type
- Default Generators for
  - Strings, chars, floats and All kinds of Numbers
- Create child test with previous seed if property fails so that it can be run from runner with a click:
  _Code is commented out because missing support from IDEA and bug in JUnit5 platform_

- Save last state in local database and run all failing properties with 
  latest seed

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

### Contracts

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
