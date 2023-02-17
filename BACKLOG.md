### Bugs

### Missing Tests

- Tests for TestRunDatabase
- Tests for TestRunData

### General

- Allow usage of simple Jupiter extensions:
  Requires a jqwik extension with set of lifecycle hooks.
  Additional module, e.g. jqwik-jupiter-extensions-adapter

- Guided Generation
  https://github.com/jqwik-team/jqwik/issues/84
    - Maybe change AroundTryHook to allow replacement of `Random` source
    - Or: Introduce ProvideGenerationSourceHook
    
- Use TestDiscovery from JUnit platform 1.5:
https://junit.org/junit5/docs/5.5.0/api/org/junit/platform/engine/support/discovery/package-summary.html

- PackageDescriptor e.g.
  @Group
  @Label("mypackage")
  @AddHook(...)
  in package-info.java

- Classifiers as in John Hughes' talk:
  https://www.youtube.com/watch?v=NcJOiQlzlXQ&list=PLvL2NEhYV4ZvCRCVlXTfB6-d09K3r0Sxa

  - Classifiers.label(boolean condition, String label)

  - Classifiers.checkCoverage(boolean condition, String label, double minCoverage)

  - Alternative: Case-based branching with statistical reporting:

    ```
    Cases.of("name")
      .match(condition1, "label1", () -> {})
      .match(condition2, "label2", () -> {})
      .noMatch();
    ```

    - specify minimum coverage for each case

- @AddExample[s] annotation like @FromData but additional to generated data

- Automatically generate nulls for types annotated as nullable
  See https://github.com/pholser/junit-quickcheck/pull/210

- Parallel test execution:
  - Across single property with annotation @Parallel
  - Across Properties: Does it make sense with non working IntelliJ support?
  
### Lifecycle Hooks

- Make ProvidePropertyInstanceHook a useful hook for other purposes.
  Implementors should be able to wrap default hook.
  Implementors should be able to resolve potential parameters.

- ProvideArbitraryHook
    - Let domains use that hook
    - Let ArbitraryProviders use that hook

- PerProperty.Lifecycle
    - void beforeTry(TryLifecycleContext, parameters)
    - void afterTry(TryLifecycleContext, TryExecutionResult)
    - void onSatisfiedTry()
    - TryExecutionResult onFalsifiedTry(TryExecutionResult)

### Reporting

- `@Report(reportOnlyFailures = false)`

- Reporting.ARBITRARIES|GENERATORS: report for each property which arbitraries are used.
    - Requires Arbitrary.describe() or something similar

- Property runtime statistics (https://github.com/jqwik-team/jqwik/issues/100)
    - Additional reporting data, e.g.
      - Typical runtimes: ~ 1m
      - Fraction of time spent in data generation: ~ 12%


### Properties

- Add capability to easily generate java beans with Builders
  (if that really makes sense).
  ```
  Builders.forBeanType(...)
       .excludeProperties(...)
       .use().forProperties(...)
  ```

- Time Module:
    - <timebased>Arbitrary.shrinkTowards(date|time|dateTime)
    - Generate java.util.Date
    - Generate java.util.Calendar

- Web Module:
    - Web.ipv4Addresses()|ipv6Addresses()|urls()

- EdgeCases.Configuration.withProbability(double injectProbability)

- Allow to add frequency to chars for String and Character arbitraries eg.
  StringArbitrary.alpha(5).numeric(5).withChars("-", 1)

- Lib to generate Json from JsonSchema as in
  https://github.com/Zac-HD/hypothesis-jsonschema
  - https://github.com/schemathesis/schemathesis 

- Generator / value sharing:
    - Arbitrary.shareValue(Arbitrary, String key)
      [see here](https://hypothesis.readthedocs.io/en/latest/data.html#hypothesis.strategies.shared)

- Support more RandomDistribution modes, e.g. Log, PowerLaw
    https://en.wikipedia.org/wiki/Inverse_transform_sampling
    https://en.wikipedia.org/wiki/Ziggurat_algorithm
    https://github.com/jeffhain/jafaran/blob/master/src/main/java/net/jafaran/Ziggurat.java

- Stateful Properties:
  - Special support for FSMs (finite state machines)
  - Concurrent execution of Chains and ActionChains.
    See https://www.youtube.com/watch?v=r5i_OiZw6Sw for inspiration

- @Property(timeout=500) msecs to timeout a property run

- Guided data generation
  see https://github.com/jqwik-team/jqwik/issues/84
  - also see: Targeted generation, Simulated annealing with an additional target
    function in property (see Proper book)

- Shrinking targets in annotations
    - @[Number]Range(shrinkingTarget=target)

- Reimplement String generation based on Unicode codepoints, not on characters
  Maybe consider this: https://github.com/quicktheories/QuickTheories/issues/54

- Check arbitrary providers for numbers that @Range annotations fit, e.g.
  `@IntRange @ForAll long aNumber` should result in a warning

- Data-driven properties: Warnings if method parameters have
  additional annotations to @ForAll

- Exhaustive Generators:
  - Better error messages when exhaustive generation not possible:
    Tell when the number of combinations is too high
    or which arbitrary does not provide exhaustive generation
  - Decimal generation with restricted scale

- Default Arbitraries, Generators and Shrinking for
  - Tuples.Tuple1/2/3/4/5/6/7/8
  - Files, Paths etc.
  - Arrays of Arrays

- Arbitraries and Generators
  - Add Arbitrary.describe() to optionally describe elements in sample output
  - functions/methods (whose output parameter can be generated)
  - @Regex(RegularExpression value) or composable RegexStringArbitrary
    see https://github.com/jqwik-team/jqwik/issues/68
  - Constrain charset for String and Char generation through @Charset(String charset) constraint

- Implement grow() for more shrinkables
    - CombinedShrinkable: grow each leg
    - CollectShrinkable: grow each element


### Kotlin

- Add ParameterResolver for combined contexts
  see https://www.47deg.com/blog/effects-contexts/


### Contracts / Specifications / Domain objects

see example in package `net.jqwik.docs.contracts.eurocalc`

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
- Support domain object generation guided by spec annotations
  Have a look at https://github.com/benas/random-beans for inspiration
