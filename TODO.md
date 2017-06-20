### Bugs

### Tests

- Add / rework tests for all Shrinkables
- Tests for TestRunDatabase
- Tests for TestRunData
- Tests for JqwikProperties and its use in JqwikTestEngine

### General

- Add documentation

- Allow custom names for containers and test methods: @Label

- Allow Fixture parameters to examples and properties

- LifeCycles
  - PerTestRunLifeCycle
  - PerClassLifeCycle
  - PerMethodLifeCycle
  - PerCheckLifeCycle

- Warn (or skip? or fail?) if jqwik tests are annotated with Jupiter/JUnit4 annotations

- Parallel test execution: Does it make sense with non working IntelliJ support?

### Properties

- Evaluate properties in parallel (max tries worker thread per property)

- Make default tries configurable
- Introduce maxDiscardRatio: How many failed assumptions are allowed per check
  - Default: 5
  - Make it configurable
  - Make it configurable in @Property

- Shrinking
  - Can be switched off in @Properties annotation. For objects that take long to shrink.
  - Configurable time limit (100ms default?)
  - Reporting:
    - collected statistics (similar to ScalaCheck's collect-feature)
  - Make it configurable (default on/off)

- @ForAll can take `providerClass` parameter (with or without value param)

- Handle error
  - if more than one generator applies
  - if generic type is a bounded type

- Arbitraries and Generators
  - Dates and times (Date, Calendar, etc.)
  - const
  - functions/methods (whose output parameter can be generated)
  - frequency: pairs of generator and frequency weight
  - oneOf: randomly select one of two generators
  - Arbitrary.chain(..) to use result of one generator for creating next arbitrary
  - posNum, negNum
  - alpha[Upper|Lower|Num]Char
  - numStr, alphaStr, identifier

- Default Arbitraries, Generators and Shrinking for
  - char, short, byte

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

- Group properties, e.g. @Property for classes and individual methods with preconditions

### Contracts / Specifications

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
