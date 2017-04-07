### General

- Allow custom names for containers and test methods
- Allow Fixture parameters to examples and properties

### Properties

- Handle error 
  - if more than one generator applies
  - if generic type is a bounded type
  - if assumption doesn't return a boolean
  - if more than one assumption applies
- Default Generators for
  - chars, floats and All kinds of Numbers
  - javaslang lists and streams
- Generators/Arbitraries
  - Configure option: provideNull
  - Enrich with fixed examples: Requires deterministic arbitrary
  - Deterministic generators for finite set of values
- Constrain tries to max possible number of values (e.g. Enum.values().length)
  - Can that be done without change in javaslang?
- Create child test with previous seed if property fails so that it can be run from runner with a click:
  _Code is commented out because missing support from IDEA and bug in JUnit5 platform_


### Change requests to javaslang test:

- Get rid of size parameter for Arbitraries. Maybe introduce generic config object.
- Replace implies with assume (or add it): A failing assumption will trigger the creation of new set of values 
  - Reporting is false IMO: Report only number of runs with precondition is true
  - Report property failure if precondition never holds
- Allow switching off of print to System.out ("OK...", "Falsified...") 
- Shrinking!
- Allow Arbitraries to fail/timeout, e.g. if they cannot generate additional values or if they are wrongly configured
  - See: Arbitrary.distinct can hang forever.
- Introduce a before-each-hook to clean up after single property check
- Introduce full data creation if possible (e.g. all combinations of an enum and a ranged number)
  - with and without random order
