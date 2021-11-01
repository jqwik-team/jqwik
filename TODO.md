- 1.6.0

    - Kotlin Support

    - Release
      - Generate Documentation
      - Set link in docs/current/kdoc/index.html
      - Write blog entry about Kotlin support


- 1.6.x

    - Running an abstract test container (abstract, enum, sealed) will run all concrete subclasses.

    - Make ProvidePropertyInstanceHook a useful hook for other purposes.
      Implementors should be able to wrap default hook.
      Implementors should be able to resolve potential parameters.

    - JqwikSession:
        - setRandomSessionSeed(), getRandomSessionSeed()

    - Verify size settings for all Collection Arbitraries

    - Arbitraries.forType(Class<T> targetType)
      - Recursive use
        - forType(Class<T> targetType, int depth)
          - @UseType(depth = 1)
      - See https://github.com/jlink/jqwik/issues/191

    - Allow specification of provider class in `@ForAll` and `@From`
      see https://github.com/jlink/jqwik/issues/91

    - Allow state machine / model specification with temporal logic.
      See https://wickstrom.tech/programming/2021/05/03/specifying-state-machines-with-temporal-logic.html

    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
        - Maybe change AroundTryHook to allow replacement of `Random` source
        - Or: Introduce ProvideGenerationSourceHook

    - Allow to add frequency to chars for String and Character arbitraries eg.
      StringArbitrary.alpha(5).numeric(5).withChars("-", 1)

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


