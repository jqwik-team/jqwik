### Bugs

- Generating List of Strings can run forever, see. examples.bugs.StringListGenerationBug
- Arbitrary.filter can result in endless loop if the filter criterion never matches.
- ListShrinkCandidates.distance() should be scaled down to distribute equally over 0 to Integer.MAX
  - Alternative 1: change distance to be of type BigInteger!
  - Alternative 2: Make distance a structured value object with higher position compared first, 
    e.g. [distanceOfList, distanceOfListValues] or [distanceOfToFlatMap, distanceOfEmbedded]
- Combining two Arbitraries of Character to Array of Character
  throws NullPointerException.
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

- Use JUnit-Config-Params for configuration:
  http://junit.org/junit5/docs/5.0.0/user-guide/#running-tests-config-params

- Switch to gradle library plugin: 
  https://docs.gradle.org/current/userguide/java_library_plugin.html

- Add documentation

- Allow custom stereotype for test methods, e.g. @Example has stereotype "Example" 
  so that Failure message is "Example [xyz123] was falsified..."

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

- Add ReportingMode.FALSIFIED, GENERATED_AND_FALSIFIED: Report only, also falsified values, also during shrinking

- Report the samples in the state BEFORE execution of property method

- Filter out duplicates in generated sets

- Generator/Arbitrary for sequences of method/function calls 

- Test shrinking with combined arbitraries, especially Arbitrary.withSamples(...)

- Shrinking
  - Time limit (100ms default?) for shrinking
    - Make it configurable
  - Make it configurable (default on/off)

- Evaluate properties in parallel (max tries worker thread per property)

- Make maxDiscardRatio configurable

- Make default tries configurable

- @ForAll can take `providerClass` parameter (with or without value param)

- Handle error
  - if more than one generator applies
  - if generic type is a bounded type

- Default Arbitraries, Generators and Shrinking for
  - short, byte
  - Dates and times (LocalDateTime, Date, Calendar, etc.)
  - Files, Paths etc.
  - Arrays of Arrays

- Arbitraries and Generators
  - Introduce Tuples to allow return of mapped and flatMapped values!
  - Generators.defaultFor(Class<?> targetType)
  - const
  - functions/methods (whose output parameter can be generated)
  - frequency: pairs of generator and frequency weight
  - oneOf: randomly select one of many generators and sample from one
  - mutate: like map, but with a random parameter added, see mutate in QuickTheories
  - Recursive generators, like in 
    http://propertesting.com/book_custom_generators.html#_recursive_generators
  - @Regex(RegularExpression value)
  - @AllChars(String charset)
  - BigIntegers: Should also generate numbers beyond Long.MAX. @Positive should also exceed Long.MAX
  - Integrals of all kind: With upper limit, change distribution towards smaller numbers according to tries

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

- Group properties, e.g. @Property for classes and individual methods with preconditions

### Contracts / Specifications

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
