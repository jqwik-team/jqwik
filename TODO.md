### General

- Allow Fixture parameters to examples and properties

### Properties

- Tests for Combinators
- Introduce Assumptions/Implications (PropertyX.implies)
- Tests for @ForAll effectiveSize parameter
- Handle error if more than one generator applies
- Default Generators for
  - chars, floats and Big*
  - Sets, Streams
  - javaslang lists and streams
  - All kinds of Numbers
  - Strings
- Constrain tries to max possible number of values (e.g. Enum.values().length)
- Encapsulate Generators into jqwik-owned interface(s) 
  and factory methods