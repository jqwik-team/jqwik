- 1.2.6

- 1.2.7

    - Get rid of LifecycleHook.prepareFor() or make sure it's always called
      - LifecycleContext.optionalParent() might be necessary
    
    - ResolveParameterHook
        - @ResolveParameter method

    - ProvideArbitraryHook
        - Let domains use that hook
        - Let ArbitraryProviders use that hook
    
    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      
    - ResolveReporterHook: Inject Reporter instance into property methods
    
    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

- 1.3.0

    - Remove deprecated APIs
    
    - Make some experimental API "maintained"

    - Documentation for lifecycle hooks API in user guide

    - Allow include/exclude for decimal ranges, e.g.
      - BigDecimalArbitrary.within(Range.from(0.1, true, 10.0, false))
      - `BigRange.min/maxIncluded`
    
    - PerProperty.Lifecycle
        - void beforeTry(TryContext, parameters)
        - void afterTry(TryExecutionResult)
        - void onSatisfiedTry()
        - TryExecutionResult onFalsifiedTry(TrExecutionResult)

    - Around property hooks
        - Get and set random seed

    - Store
        - onUpdate()
    
    - `@Report(reportOnlyFailures = false)`

