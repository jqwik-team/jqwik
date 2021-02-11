- 1.4.1
  
    - Scrutinize performance with nested BuilderCombinators.
      See p3 testing library. May be related to uniqueElements or to arbitrary configurator.
    
    - Create possibility/annotation to suppress reporting inside of a test/property but not lose
      reporting of property's results. May require a new lifecycle hook.
        - Apply annotation wherever reporting is test collateral

    - @StatisticsReportFormat
      https://github.com/jlink/jqwik/issues/146
        - label=<statistics label> to specify for which statistics to use
        - Make it repeatable
    
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

    - Time Module:
        - [LocalDate|Calendar|DateArbitrary].shrinkTowards(date)
        - Generate Instant, LocalTime, ZonedTime etc.
    
    - Web Module:
        - Web.ipv4Addresses()|ipv6Addresses()|domains()|urls()

    - StringArbitrary.notBlank() + `@NotBlank`

    - EdgeCases.Configuration.withProbability(double injectProbability)

- 1.4.x

      - Arbitraries.forType(Class<T> targetType) or Beans.forType/from(...)
      https://github.com/jlink/jqwik/issues/121
        - useBeanProperties()
            - are considered nullable
            - with optional spec: Map<String, Arbitrary> to map
              a property to a certain arbitrary

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
    

