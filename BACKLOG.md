### Bugs

- Bug: Arbitrary method resolution is sometimes too loose.
  E.g. return type `@Provide ActionSequenceArbitrary<Integer>` would be accepted
  for parameter of type `@ForAll Arbitrary<ActionSequence<String>>` which will lead
  to class cast exception on property evaluation. See TODO in TypeUsage.canBeAssignedTo()
  
### Missing Tests

- Add tests for RandomGenerators
- Tests for TestRunDatabase
- Tests for TestRunData
- Tests for JqwikProperties and its use in JqwikTestEngine

### General

- Use org.junit.platform.engine.support.discovery.* for discovery

- PackageDescriptor e.g.
  @Group
  @Label("mypackage")
  @AddHook(...)
  in package-info.java

- Classifiers as in John Hughes' talk: 
  https://www.youtube.com/watch?v=NcJOiQlzlXQ&list=PLvL2NEhYV4ZvCRCVlXTfB6-d09K3r0Sxa

  - Classifiers.label(boolean condition, String label)
  - Classifiers.checkCoverage(boolean condition, String label, double minCoverage)

- @AddExample[s] annotation like @FromData but additional to generated data

- Store regressions (samples once failed) in dedicated database
  https://hypothesis.readthedocs.io/en/latest/database.html

- Automatically generate nulls for types annotated as nullable
  See https://github.com/pholser/junit-quickcheck/pull/210

- Allow Fixture parameters to examples and properties

- Lifecycle Hooks
  - AroundAllHook
  - AroundTryHook
  - AroundContainerHook
  - Tests for AroundPropertyHook

- Parallel test execution:
  - Across single property with annotation @Parallel 
  - Across Properties: Does it make sense with non working IntelliJ support?
  - For ActionSequences

- Configuration:
  - Switch to JUnit platform configuration
  - Find a way to set config params through command line or env variable

    `request.getConfigurationParameters().get("jqwik.tries.default", Integer::valueOf)`

    plus:

    ```
    test {
        useJUnitPlatform {}
        systemProperties = [
            "jqwik.tries.default": 100
        ]
    }
    ```

    would do the trick.

### Reporting

- Reporting.ARBITRARIES: report for each property which arbitraries are used.
  - Requires Arbitray.describe() or something similar

- Reporter.report(key, value)

- Additional reporting data, e.g. 
  - Typical runtimes: ~ 1m 
  - Fraction of time spent in data generation: ~ 12%

- Record/report generated values by parameter name,
  plus allow label for generated parameter like that: @ForAll(label = "first“)


### Properties

- Stateless Properties:
  - Let action generation access the model state?
    E.g. to use a name that’s already been added to a store.
    - Will require involved version of ActionGenerator, e.g. to take
      Arbitrary<Function<T, Arbitrary<Action<T>>>>
    - Will shrinking work on that?
    - Are there other ways to reach the same goal?
  - Parallel execution of action sequences (see Proper book)
  - Special support for FSMs (finite state machines)

- Arbitraries.fromSize(Function<Integer, Arbitrary> f) : Arbitrary
  Use current size to influence arbitrary generation

- SizableArbitrary.averageSize(50)

- StringArbitrary.blacklist(char … chars)

- Arbitraries.function(Arbitrary outputArbitrary, Class inputType)

- Arbitrary.share(Arbitrary, String key) 
  [see here](https://hypothesis.readthedocs.io/en/latest/data.html#hypothesis.strategies.shared)

- Arbitraries.emails()

- @Property(timeout=500) msecs to timeout a property run

- Targeted data generation
  Simulated annealing with an additional target function in property
  (see Proper book)

- Provide alternative shrinking targets for arbitraries,
  eg Arbitraries.integers().shrinkTowards(42, 110)

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

- Automatic Provider detection/loading
  - E.g. as in junit-quickcheck. If there is a
    mypackage.MyDomainArbitraryProvider for mypackage.MyDomain then load it

- Provider methods can take params e.g.
  - @Provided(value="otherProviderMethod") Arbitrary<String> aString
  Does that really help since there is Arbitraries.defaultFor()

- Default Arbitraries, Generators and Shrinking for
  - Map
  - Functional interfaces and SAM types
  - Tuples.Tuple1/2/3/4/5/6/7/8
  - Dates and times (LocalDateTime, Date, Calendar, etc.)
  - Files, Paths etc.
  - Arrays of Arrays

- Arbitraries and Generators
  - Add Arbitrary.describe() to optionally describe elements in sample output
  - functions/methods (whose output parameter can be generated)
  - @Regex(RegularExpression value)
  - Constrain charset for String and Char generation through @Charset(String charset) constraint

### Contracts / Specifications / Domain objects

see example in package `net.jqwik.docs.contracts.eurocalc`

- Allow specification of consumer and provider contract in test class
- Allow spec annotations in domain classes a la clojure-spec
- Support domain object generation guided by spec annotations
  Have a look at https://github.com/benas/random-beans for inspiration 
