### General

- Allow custom names for containers and test methods
- Allow Fixture parameters to examples and properties

### Properties

- Tests for Combinators
- Introduce Assumptions/Implications (PropertyX.implies)
  - Wait till assumptions work as expected in javaslang
- Tests for @ForAll effectiveSize parameter
- Handle error if more than one generator applies
- Default Generators for
  - chars, floats and All kinds of Numbers
  - javaslang lists and streams
- Constrain tries to max possible number of values (e.g. Enum.values().length)
  - Can that be done without change in javaslang?


### Change requests to javaslang test:

- Get rid of size parameter for Arbitraries. Maybe introduce generic config object.
- Replace implies with assume (or add it): A failing assumption will trigger the creation of new set of values 
- Shrinking!
- Allow Arbitraries to fail/timeout, e.g. if they cannot generate additional values or if they are wrongly configured
  - See: Arbitrary.distinct can hang forever.
- Expose Random seed, so that a subsequent test run can exactly reproduce a previous one
- Introduce a before-each-hook to clean up after single property check
- Introduce full data creation if possible (e.g. all combinations of an enum and a ranged number)
  - with and without random order