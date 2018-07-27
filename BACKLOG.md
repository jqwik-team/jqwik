### Bugs

- Bug: Arbitrary method resolution is sometimes too loose.
  E.g. return type `@Provide ActionSequenceArbitrary<Integer>` would be accepted
  for parameter of type `@ForAll Arbitrary<ActionSequence<String>>` which will lead
  to class cast exception on property evaluation. See TODO in TypeUsage.canBeAssignedTo()
  
### Tests

- Add tests for RandomGenerators
- Tests for TestRunDatabase
- Tests for TestRunData
- Tests for JqwikProperties and its use in JqwikTestEngine

### Refactoring

- Introduce PropertyExecutionListener and build all reporting/results on top of it

### General

- `@Disabled("reason")` annotation

- Allow reporting to be configured to also go to stdtout 
  (to work around missing reporting in Gradle's useJunitPlatform() )

- Switch to gradle library plugin: 
  https://docs.gradle.org/current/userguide/java_library_plugin.html

- Allow Fixture parameters to examples and properties

- Use apiguardian annotations (starting version 1.0)

- LifeCycles
  - PerTryLifeCycle
  - PerTestRunLifeCycle
  - PerClassLifeCycle

- Parallel test execution:
  - Across single property with annotation @Parallel 
  - Across Properties: Does it make sense with non working IntelliJ support?
  - For ActionSequences

### Properties

- Use every combination of generated shrinkables only once

- Provide arbitraries for classes with single constructor with parameters
  that can be provided

- Check arbitrary providers for numbers that @Range annotations fit, e.g.
  `@IntRange @ForAll long aNumber` should result in a warning

- Optionally report for each property which arbitraries are used.

- @ForAll 
  - can be used in parameter types to choose provider method
  - can take `providerClass` parameter (but no value parameter) 
    to specify ArbitraryProvider implementation
  - Alternative: Allow `@ForAll @From(MyProvider.class) MyType myObject`


- Provider methods can take params e.g.
  - @Provided(value="otherProviderMethod") Arbitrary<String> aString

- Default Arbitraries, Generators and Shrinking for
  - Tuples.Tuple2/3/4
  - Map
  - Functional interfaces and SAM types
  - Dates and times (LocalDateTime, Date, Calendar, etc.)
  - Files, Paths etc.
  - Arrays of Arrays

- Arbitraries and Generators
  - Add Arbitrary.describe() to optionally describe elements in sample output
  - functions/methods (whose output parameter can be generated)
  - @Regex(RegularExpression value)
  - Constrain charset for String and Char generation through @Charset(String charset) constraint

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

- Group properties, e.g. @Property for classes and individual methods with preconditions

### Contracts / Specifications / Domain objects

see example in package `examples.docs.contracts.eurocalc`

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
- Support domain object generation guided by spec annotations
  Have a look at https://github.com/benas/random-beans for inspiration 
