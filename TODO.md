- 1.5.1

    - @StatisticsReportFormat
      https://github.com/jlink/jqwik/issues/146
        - label=<statistics label> to specify for which statistics to use
        - Make it repeatable
    
    - StringArbitrary.excludeChars() or s.t. like that
      https://github.com/jlink/jqwik/issues/167

    - Document that provider methods can have arguments (TypeUsage and SubtypeResolver)

    - Time Module:
        - Times. See https://github.com/jlink/jqwik/issues/154
    
    - Web Module:
        - Web.ipv4Addresses()|ipv6Addresses()|domains()|urls()

    - Builders.startWith(..).use()|maybe(0.5).use().in()|inSetter()
      to replace Combinators.withBuilder() but target cannot change across
      build steps (-> much better performance and shrinking)
        - Add capability to easily generate java beans (if that really makes sense)


- 1.5.x

    - Domains
        - Deprecate AbstractDomainContextBase
            - Introduce DomainContextBase
            - Allow @Provide methods in DomainContextBase subclasses
            - Allow @ForAll parameters in @Provide methods
            - Allow Arbitrary<T> parameters in @Provide methods
            - @Configure method for configurators?

        - Hand in property execution context to domains when being created.
          E.g. to get annotation values from method
          DomainContext.prepare(PropertyExecutionContext context)

    - EdgeCases.Configuration.withProbability(double injectProbability)

    - Time Module:
      - DateTimes. See https://github.com/jlink/jqwik/issues/???
      - Generate Instant, LocalTime, ZonedTime etc.
      - [LocalDate|Calendar|DateArbitrary].shrinkTowards(date)

    - Create possibility/annotation to suppress reporting inside of a test/property but not lose
      reporting of property's results. May require a new lifecycle hook.
        - Apply annotation wherever reporting is test collateral
        - Make it configurable, e.g. `jqwik.reporting.statistics.onfailureonly`
      
    - Arbitraries.forType(Class<T> targetType)
        - Recursive use
            - forType(Class<T> targetType, int depth)
            - @UseType(depth = 1)

    - `@Repeat(42)`: Repeat a property 42 times

    - Implement grow() for more shrinkables
        - CombinedShrinkable: grow each leg
        - CollectShrinkable: grow each element

    - Allow specification of provider class in `@ForAll` and `@From`
      see https://github.com/jlink/jqwik/issues/91

    - Use derived Random object for generation of each parameter.
      Will that somehow break a random byte provider in guided generation?
      - Remember the random seed in Shrinkable

    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      - Maybe change AroundTryHook to allow replacement of `Random` source
      - Or: Introduce ProvideGenerationSourceHook
      
    - Allow to add frequency to chars for String and Character arbitraries
      eg. StringArbitrary.alpha(5).numeric(5).withChars("-", 1)

    - Mixin edge cases in random order (https://github.com/jlink/jqwik/issues/101)
    

