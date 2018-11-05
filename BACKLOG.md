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

- ArchitectureTests: Use @ArchTest annotations

### Refactoring

- Introduce PropertyExecutionListener and build all reporting/results on top of it

### General

- `@Disabled("reason")` annotation

- Allow Fixture parameters to examples and properties

- Use apiguardian annotations (starting version 1.0)

- Lifecycle Hooks
  - AroundTryHook
  - AroundContainerHook
  - AroundAllHook
  - Tests for AroundPropertyHook

- Parallel test execution:
  - Across single property with annotation @Parallel 
  - Across Properties: Does it make sense with non working IntelliJ support?
  - For ActionSequences

- Configuration:
  - reportOnlyFailures = false
  - Find a way to set config params through command line or env variable

### Properties

- Reimplement String generation based on Unicode codepoints, not on characters

- Provide arbitraries for classes with single constructor with parameters
  that can be provided

- Check arbitrary providers for numbers that @Range annotations fit, e.g.
  `@IntRange @ForAll long aNumber` should result in a warning

- Data-driven properties: Warnings if method parameters have
  other annotations than @ForAll

- Reporting.ARBITRARIES: report for each property which arbitraries are used.
  - Requires Arbitray.describe() or something similar

- Exhaustive Generators:
  - Better error messages when exhaustive generation not possible:
    Tell when the number of combinations is too high
    or which arbitrary does not provide exhaustive generation
  - Make default GenerationMode configurable
  - Decimal generation with restricted scale

- @From(String methodName)
  - can be used in parameter types to choose provider method
    as replacement for @ForAll.value
  - can have alternatively `Class provider` attribute (but no value parameter)
    to specify ArbitraryProvider implementation
  - @ForAll.value should still be allowed as shortcut

- Provider methods can take params e.g.
  - @Provided(value="otherProviderMethod") Arbitrary<String> aString
  Does that really help since there is Arbitraries.defaultFor()

- Default Arbitraries, Generators and Shrinking for
  - Tuples.Tuple2/3/4/5/6/7/8
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

- Group properties, e.g. @Property for classes and individual methods with preconditions

### Contracts / Specifications / Domain objects

see example in package `examples.docs.contracts.eurocalc`

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
- Support domain object generation guided by spec annotations
  Have a look at https://github.com/benas/random-beans for inspiration 
