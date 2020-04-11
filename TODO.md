- 1.2.7

    - Allow include/exclude for decimal ranges
        - FloatArbitrary.between(0.1f, false, 10.0f, false))
        - FloatRange.min/maxIncluded

- 1.3.0

    - Introduce Arbitrary.edgeCases() and combinatorial execution of edge cases

    - Remove deprecated APIs
    
    - Make some experimental API "maintained"

    - Documentation for lifecycle hooks API in user guide
    
    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

    - ProvideArbitraryHook
        - Let domains use that hook
        - Let ArbitraryProviders use that hook
    
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
        - TryExecutionResult onFalsifiedTry(TrExecutionResult)

    - Around property hooks
        - Get and set random seed

    - `@Report(reportOnlyFailures = false)`

