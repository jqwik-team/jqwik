### Bugs

### Tests

- Add / rework tests for all Shrinkables
- Tests for TestRunDatabase
- Tests for TestRunData
- Tests for JqwikProperties and its use in JqwikTestEngine

### General

- Use JUnit-Config-Params for configuration:
  http://junit.org/junit5/docs/5.0.0/user-guide/#running-tests-config-params

- Add documentation

- Allow custom names for containers and test methods: @Label

- Allow Fixture parameters to examples and properties

- Use apiguardian annotations

- LifeCycles
  - PerTestRunLifeCycle
  - PerClassLifeCycle
  - PerMethodLifeCycle
  - PerCheckLifeCycle

- Warn (or skip? or fail?) if jqwik tests are annotated with Jupiter/JUnit4 annotations

- Parallel test execution: Does it make sense with non working IntelliJ support?

### Properties

- Test shrinking with combined arbitraries, especially Arbitrary.withSamples(...)

- Shrinking
  - Can be switched off in @Properties annotation. For properties that take very long to shrink.
  - Time limit (100ms default?) for shrinking
    - Make it configurable
  - Reporting:
    - collected statistics (similar to ScalaCheck's collect-feature)
  - Make it configurable (default on/off)

- Evaluate properties in parallel (max tries worker thread per property)

- Make maxDiscardRatio configurable

- Make default tries configurable

- @ForAll can take `providerClass` parameter (with or without value param)

- Handle error
  - if more than one generator applies
  - if generic type is a bounded type

- Default Arbitraries, Generators and Shrinking for
  - char, short, byte
  - Dates and times (LocalDateTime, Date, Calendar, etc.)
  - Arrays of Arrays

- Arbitraries and Generators
  - Generators.defaultFor(Class<?> targetType)
  - const
  - functions/methods (whose output parameter can be generated)
  - frequency: pairs of generator and frequency weight
  - oneOf: randomly select one of two generators
  - Arbitrary.chain(..) to use result of one generator for creating next arbitrary
  - posNum, negNum
  - alpha[Upper|Lower|Num]Char
  - numStr, alphaStr, identifier

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

- Group properties, e.g. @Property for classes and individual methods with preconditions

### Contracts / Specifications

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
