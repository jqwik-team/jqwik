### General

- Allow Fixture parameters to examples and properties

### Properties

- Tests for Combinators
- Introduce Assumptions/Implications (PropertyX.implies)
- Tests for @ForAll effectiveSize parameter
- Handle error if more than one generator applies
- Default Generators for
  - chars, floats and All kinds of Numbers
  - Streams
  - javaslang lists and streams
- Constrain tries to max possible number of values (e.g. Enum.values().length)
  - Can that be done without change in javaslang?


### Change requests to javaslang test:

- Get rid of size parameter for Arbitraries. Maybe introduce generic config object.
- Shrinking!
- Expose Random seed, so that a subsequent test run can exactly reproduce a previous one
- Introduce a before-each-hook to clean up after single property check
- Introduce full data creation if possible (e.g. all combinations of an enum and a ranged number)
  - with and without random order