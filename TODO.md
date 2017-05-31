### Bugs

### Tests

- Add / rework tests for all Shrinkables 
- Tests for TestRunDatabase
- Tests for TestRunData
- Tests for JqwikProperties and its use in JqwikTestEngine

### General

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
  - const
  - frequency: pairs of generator and frequency weight
  - oneOf: randomly select one of two generators
  - posNum, negNum
  - alpha[Upper|Lower|Num]Char
  - numStr, alphaStr, identifier

- Default Arbitraries and Generators for
  - float, double, BigDecimal
  - char, short, byte
  - Map
  
- Arbitrary.chain(..) to use result of one generator for creating next arbitrary

- Introduce Arbitrary.deterministicGenerator and Property.Mode.EXHAUSTIVE

- Group properties, e.g. @Property for classes and individual methods with preconditions

### Contracts / Specifications

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
