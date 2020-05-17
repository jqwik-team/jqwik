- 1.3.0

    - Histogram()
        - Document Histogram class

    - Documentation for lifecycle hooks API in user guide
    
    - Edge Case number reporting (https://github.com/jlink/jqwik/issues/101)
    
    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85
      - Extract description before running the property?
        e.g. to be able to display a generated Stream
        
- 1.3.1

    - Does exhaustive generation for mutable objects work or will muted objects be injected
      in other parameter sets?

    - Edge Cases
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

    - Change signature Arbitrary.exhaustive() -> ExhaustiveGenerator
    
    - Property runtime statistics (https://github.com/jlink/jqwik/issues/100)

    - ProvideArbitraryHook
        - Let domains use that hook
        - Let ArbitraryProviders use that hook
        
    - AroundPropertyHook
        - Add parameter PropertyConfiguration
            - tries()
            - afterFailureMode()
            - generationMode()
            - shrinkingMode()
            - randomSeed()
        - Allow configuration attributes to be changed
        - Alternative: Introduce PropertyConfigurationHook
    
    - Support more RandomDistribution modes, e.g. Gaussian, Log, PowerLaw
        https://en.wikipedia.org/wiki/Inverse_transform_sampling
        https://en.wikipedia.org/wiki/Ziggurat_algorithm
        https://github.com/jeffhain/jafaran/blob/master/src/main/java/net/jafaran/Ziggurat.java

    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      - Maybe change AroundTryHook to allow replacement of `Random` source
      - Or: Introduce ProvideGenerationSourceHook
      
    - @ResolveParameter method
        - Returns `Optional<MyType>` | `Optional<ParameterSupplier<MyType>>`
        - Optional Parameters: TypeUsage, LifecycleContext
        - static and non-static

    - PerProperty.Lifecycle
        - void beforeTry(TryLifecycleContext, parameters)
        - void afterTry(TryLifecycleContext, TryExecutionResult)
        - void onSatisfiedTry()
        - TryExecutionResult onFalsifiedTry(TryExecutionResult)

    - `@Report(reportOnlyFailures = false)`

    - @StatisticsReportFormat
        - label=<statistics label> to specify for which statistics to use
        - Make it repeatable
