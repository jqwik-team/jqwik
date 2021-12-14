- 1.6.2

    - Make falsified samples reproducible in other way than serialization
      - Add shrinking sequence to GeneratorInfo and previous sample generation
      - Test and optimize serialization of TestRun/GeneratorInfo with up to 1500 elements in shrinking sequence

- 1.6.x

    - Allow annotation @BeforeTry on member variables of tests to reinitialize them before each try.

    - Allow specification of provider class in `@ForAll` and `@From`
      see https://github.com/jlink/jqwik/issues/91

    - Make ProvidePropertyInstanceHook a useful hook for other purposes.
      Implementors should be able to wrap default hook.
      Implementors should be able to resolve potential parameters.

    - Running an abstract test container (abstract, enum, sealed) will run all concrete subclasses.

    - JqwikSession:
        - setRandomSessionSeed(), getRandomSessionSeed()

    - Allow state machine / model specification with temporal logic.
      See https://wickstrom.tech/programming/2021/05/03/specifying-state-machines-with-temporal-logic.html

    - Add capability to easily generate java beans with Builders
      (if that really makes sense).
      ```
      Beans.ofType(...)
           .excludeProperties(...)
           .use().forProperties(...)
      ```

    - Time Module:
        - <timebased>Arbitrary.shrinkTowards(date|time|dateTime)

    - Web Module:
        - Web.ipv4Addresses()|ipv6Addresses()|urls()

    - EdgeCases.Configuration.withProbability(double injectProbability)

    - Kotlin:
        - Add ParameterResolver for combined contexts
          see https://www.47deg.com/blog/effects-contexts/

