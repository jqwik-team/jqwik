- 1.4.0

    - Uniqueness constraints
        - User guide entry for uniqueness constraints
          - Remove user guide sections for old uniqueness approach
        - Release notes for new uniqueness concept  

    - Time and Date Generation Module
        - https://github.com/jlink/jqwik/issues/140
        - DateArbitrary.shrinkTowards(date)

    - Default for Email-Generation should be "unquoted" local part and "domain" host.

    - Edge Cases

        - Restrict number of generated edge cases to number of tries
          - For embedded/individual use of generators only use a max of 100 edge cases
        
        - Arbitrary.edgeCases(Consumer<Config>):
          - Special handling possible? for:
              - Numeric Arbitraries
              - CharacterArbitrary
              - Arbitrary.of() arbitraries
              - Collections
              - Combinators
        
        - Mixin edge cases in random order (https://github.com/jlink/jqwik/issues/101)
    
        - Add documentation for Arbitrary.edgeCases(configuration)

    - Warn on fixed seed depending on configuration
      https://github.com/jlink/jqwik/issues/138

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


