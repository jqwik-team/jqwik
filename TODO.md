- 1.3.0

    - Introduce Arbitrary.edgeCases() and combinatorial execution of edge cases
        - Implement edgeCases() for
            - Optional, Map 
            - Combinators
            - Functions
        - Configure default in jqwik.properties
        - Move internals from EdgeCases class to EdgeCasesFacade or to engine module
        - Document edge cases

    - Get rid of Arbitraries.samples()

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

