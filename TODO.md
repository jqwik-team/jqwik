### General

- Introduce DiscoverySelector (discover, butAbort, butSkip)
- Switch to non static nested classes for @Group
- Allow Fixture parameters to examples and properties

### Properties

- Tests for @ForAll tries parameters
- Handle error if more than one generator applies
- Default Generators for
  - Enums
  - Lists, Sets
  - All kinds of Numbers
  - Strings
- Introduce Assumptions/Implications (PropertyX.implies)
- Encapsulate Generators into jqwik-owned interface(s) 
  and factory methods