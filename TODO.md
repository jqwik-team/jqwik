- 1.3.3

    - Re-implement shrinking
        - Possibility of shrinking one element and growing another
          - Allow numbers to grow
          - Allow collections to grow
        - Review shrinking related TODOs
        - Remove duplication in shrink methods (especially one-after-the-other)
        
        
- 1.3.4

    - Add abstract method DomainContextBase.registrations()
    
    - Hook: @PropertyDefaults(....)

    - Allow specification of provider class in `@ForAll` and `@From`
      see https://github.com/jlink/jqwik/issues/91

    - UniqueShrinkable should be able to shrink to previously tried values

    - Arbitrary.uniqueBy(Predicate<T> uniqueCondition)
    
    - Use derived Random object for generation of each parameter.
      Will that somehow break a random byte provider in guided generation?
      - Remember the random seed in Shrinkable

- 1.3.x

    - `@Report(reportOnlyFailures = false)`

    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      - Maybe change AroundTryHook to allow replacement of `Random` source
      - Or: Introduce ProvideGenerationSourceHook
      
    - Edge Cases
        - Stream edge cases on the fly instead of creating all upfront:
           - https://github.com/jlink/jqwik/issues/114
           - examples.bugs.JqwikHeapBust as test case
    
        - Arbitrary.withoutEdgeCases() 
            - should also work for individual generators
            - Maybe introduce ArbitraryDecorator or something like that
        
        - Arbitrary.addEdgeCase(value) 
            - Make shrinkable variants for
                - Numeric Arbitraries
                - CharacterArbitrary
                - Arbitrary.of() arbitraries
                - Collections
                - Combinators
            - Mixin edge cases in random order (https://github.com/jlink/jqwik/issues/101)

    - Property runtime statistics (https://github.com/jlink/jqwik/issues/100)

    - Support more RandomDistribution modes, e.g. Log, PowerLaw
        https://en.wikipedia.org/wiki/Inverse_transform_sampling
        https://en.wikipedia.org/wiki/Ziggurat_algorithm
        https://github.com/jeffhain/jafaran/blob/master/src/main/java/net/jafaran/Ziggurat.java

    - @ResolveParameter method
        - Returns `Optional<MyType>` | `Optional<ParameterSupplier<MyType>>`
        - Optional Parameters: TypeUsage, LifecycleContext
        - static and non-static

    - PerProperty.Lifecycle
        - void beforeTry(TryLifecycleContext, parameters)
        - void afterTry(TryLifecycleContext, TryExecutionResult)
        - void onSatisfiedTry()
        - TryExecutionResult onFalsifiedTry(TryExecutionResult)

    - @StatisticsReportFormat
        - label=<statistics label> to specify for which statistics to use
        - Make it repeatable
