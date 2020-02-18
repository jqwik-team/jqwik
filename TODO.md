- 1.2.4

    - Container lifecycle
        - @BeforeContainer methods
        - @AfterContainer methods

    - Around property lifecycle
        - @BeforeProperty methods
        - @AfterProperty methods
        - Get and set random seed

    - Around try lifecycle
        - @BeforeTry methods
        - @AfterTry methods

- 1.2.x

    - Spring/Boot Testing in its own module
    
    - ProvideArbitraryHook
        - Let domains use that hook
        - Let ArbitraryProviders use that hook
    
    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      
    - ResolveReporterHook: Inject Reporter instance into property methods

    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

- 1.3.0

    - Documentation for lifecycle hooks API in user guide

    - ResolveParameterHook
        - @ResolveParameter method

    - PerProperty.Lifecycle
        - void beforeTry(TryContext, parameters)
        - void afterTry(TryExecutionResult)
        - void onSatisfiedTry()
        - TryExecutionResult onFalsifiedTry(TrExecutionResult)

    - Store
        - onUpdate()
    
    - `@Report(reportOnlyFailures = false)`

