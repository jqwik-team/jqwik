### Bugs

### General

- Get rid of javaslang (Function2,3,4)

- Allow custom names for containers and test methods
- Allow Fixture parameters to examples and properties

### Properties

- Report all data from PropertyCheckResult (seed, countChecks, countTries, sample)
- Generators/Arbitraries
  - Configurations: MaxSize
- Handle error 
  - if more than one generator applies
  - if generic type is a bounded type
- Default Generators for
  - chars, floats and All kinds of Numbers
- Create child test with previous seed if property fails so that it can be run from runner with a click:
  _Code is commented out because missing support from IDEA and bug in JUnit5 platform_

- Save last state in local database and run all failing properties with 
  latest seed

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE
