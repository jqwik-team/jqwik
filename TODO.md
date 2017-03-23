### General

- Switch to non static nested classes for @Group
- Allow Fixture parameters to examples and properties

### Properties

- Introduce Combinator(s) for Arbitraries
- Introduce Assumptions/Implications (PropertyX.implies)
- Tests for @ForAll size parameter
- Handle error if more than one generator applies
- Default Generators for
  - Lists, Sets
  - All kinds of Numbers
  - Strings
- Constrain tries to max possible number of values (e.g. Enum.values().length)
- Encapsulate Generators into jqwik-owned interface(s) 
  and factory methods