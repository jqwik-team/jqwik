### Bugs

- ListShrinkCandidates.distance() should be scaled down to distribute equally over 0 to Integer.MAX
  - Alternative 1: change distance to be of type BigInteger!
  - Alternative 2: Make distance a structured value object with higher position compared first, 
    e.g. [distanceOfList, distanceOfListValues] or [distanceOfToFlatMap, distanceOfEmbedded]
- Rerunning failures first does not work in all cases, e.g. try in pbt-java project
  
### Tests

- Add tests for RandomGenerators
- Add / rework tests for all Shrinkables 
  - especially Sets
- Tests for distance of Shrinkables -> They must not overflow! E.g. ListShrinkCandidates.distance()
- Tests for TestRunDatabase
- Tests for TestRunData
- Tests for JqwikProperties and its use in JqwikTestEngine

### Refactoring

- Introduce PropertyExecutionListener and build all reporting/results on top of it

### General

- Allow reporting to be configured to also go to stdtout

- Switch to gradle library plugin: 
  https://docs.gradle.org/current/userguide/java_library_plugin.html

- Allow Fixture parameters to examples and properties

- Use apiguardian annotations

- LifeCycles
  - PerTestRunLifeCycle
  - PerClassLifeCycle
  - PerMethodLifeCycle
  - PerTryLifeCycle

- Parallel test execution:
  - Across single property with annotation @Parallel 
  - Across Properties: Does it make sense with non working IntelliJ support?

### Properties

- @ForAll 
  - can be used in parameter types to choose provider method
  - can take `providerClass` parameter (but no value parameter) 
    to specify ArbitraryProvider implementation

- ArbitraryProvider: Add priority to provider registration to allow more specific providers.
  Currently the order of registration is decisive - last registered provider wins.

- Provider methods can take params e.g.
  - @Provided(value="otherProviderMethod") Arbitrary<String> aString

- Generator/Arbitrary for sequences of method/function calls 

- Shrinking
  - Time limit (100ms default?) for shrinking
    - Make it configurable
  - Make it configurable (default on/off)

- Evaluate properties in parallel (max tries worker thread per property)

- Handle error
  - if more than one generator applies
  - if generic type is a bounded type

- Default Arbitraries, Generators and Shrinking for
  - Tuples.Tuple2/3/4
  - Map
  - Functional interfaces and SAM types
  - Dates and times (LocalDateTime, Date, Calendar, etc.)
  - Files, Paths etc.
  - Arrays of Arrays

- Report the samples in the state BEFORE execution of property method

- Arbitraries and Generators
  - Add Arbitrary.describe() to optionally describe elements in sample output
  - functions/methods (whose output parameter can be generated)
  - @Regex(RegularExpression value)
  - Constrain charset for String and Char generation through @Charset(String charset) constraint

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

- Group properties, e.g. @Property for classes and individual methods with preconditions

### Contracts / Specifications / Domain objects

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
- Support domain object generation guided by spec annotations
  Have a look at https://github.com/benas/random-beans for inspiration 
