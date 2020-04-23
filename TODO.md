- 1.3.0

    - Introduce Arbitrary.edgeCases()
        - LazyArbitrary?
        - DefaultActionSequenceArbitrary?
        - Configure default in jqwik.properties
        - Document edge cases

    - Offer Arbitrary.addEdgeCase(value)|.clearEdgeCases() for 
        - Numeric Arbitraries
        - CharacterArbitrary
        - Arbitrary.of() arbitraries

    - Get rid of Arbitraries.samples() in jqwik code
    
    - Unify all Arbitraries.of() implementations

    - Scrutinize unique() behaviour of IteratorArbitrary, StreamArbitrary, ArrayArbitrary
        - When @Unique is a constraint
      
    - Change signature Arbitrary.exhaustive() -> ExhaustiveGenerator

    - Check TODOs in code
    
    - Documentation for lifecycle hooks API in user guide
    
    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

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

