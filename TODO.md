### General

- Use DiscoverySpec for container discovery
- Switch to non static nested classes for @Group
- Allow Fixture parameters to examples and properties

### Properties

- Introduce Assumptions/Implications (PropertyX.implies)
- Tests for @ForAll size parameter
- Handle error if more than one generator applies
- Default Generators for
  - Enums
  - Lists, Sets
  - All kinds of Numbers
  - Strings
- Encapsulate Generators into jqwik-owned interface(s) 
  and factory methods