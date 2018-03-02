### Bugs

- Arbitrary.filter can result in endless loop if the filter criterion never matches.
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

### General

- Switch to gradle library plugin: 
  https://docs.gradle.org/current/userguide/java_library_plugin.html

- Allow custom names for containers and test methods: @Label?

- Allow Fixture parameters to examples and properties

- Use apiguardian annotations

- LifeCycles
  - PerTestRunLifeCycle
  - PerClassLifeCycle
  - PerMethodLifeCycle
  - PerTryLifeCycle

- Warn (or skip? or fail?) if jqwik tests are annotated with Jupiter/JUnit4 annotations

- Parallel test execution: Does it make sense with non working IntelliJ support?

### Properties

- ArbitraryProvider: Add priority to provider registration to allow more specific providers.
  Currently the order of registration is decisive - last registered provider wins.

- Provider methods can take params e.g.
  - @Provided(value="otherProviderMethod") Arbitrary<String> aString

- Filter out duplicates in generated sets

- Generator/Arbitrary for sequences of method/function calls 

- Shrinking
  - Time limit (100ms default?) for shrinking
    - Make it configurable
  - Make it configurable (default on/off)

- Evaluate properties in parallel (max tries worker thread per property)

- @ForAll can take `providerClass` parameter (with or without value param)

- Handle error
  - if more than one generator applies
  - if generic type is a bounded type

- Default Arbitraries, Generators and Shrinking for
  - Tuples.Tuple2/3/4
  - Map
  - Function and SAM types
  - Dates and times (LocalDateTime, Date, Calendar, etc.)
  - Files, Paths etc.
  - Arrays of Arrays

- Report the samples in the state BEFORE execution of property method

- Arbitraries and Generators
  - Add Arbitrary.describe() to optionally describe elements in sample output
  - Find syntax to use provider methods for subtypes, e.g. @ForAll List<@ProvidedBy("myString") String> aList
  - functions/methods (whose output parameter can be generated)
  - frequency: pairs of generator and frequency weight
  - mutate: like map, but with a random parameter added, see mutate in QuickTheories
  - Recursive generators, like in 
    http://propertesting.com/book_custom_generators.html#_recursive_generators
  - @Regex(RegularExpression value)
  - Constrain charset for String and Char generation through @Charset(String charset) constraint

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

- Group properties, e.g. @Property for classes and individual methods with preconditions

### Contracts / Specifications / Domain objects

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
- Support domain object generation guided by spec annotations
  Have a look at https://github.com/benas/random-beans for inspiration 
