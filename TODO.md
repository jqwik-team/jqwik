- 1.6.1

    - @UseType(allowRecursion=true)
        - See https://github.com/jlink/jqwik/issues/191
        - Rework documentation in generation-from-type.md
        - what about recursive types (e.g. Composite Pattern)?

    - Improve shrinking of (duplicate) emails
      - Maybe have a "simple emails" config with restricted length and restricted domains

    - Allow specification of provider class in `@ForAll` and `@From`
      see https://github.com/jlink/jqwik/issues/91

    - Tags should be reported in standard property report


- 1.6.x

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

