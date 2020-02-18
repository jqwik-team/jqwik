- 1.2.4

    - Around property lifecycle
        - @BeforeProperty methods
        - @AfterProperty methods

    - Around try lifecycle
        - @BeforeTry methods
        - @AfterTry methods

- 1.2.x

    - Spring/Boot Testing in its own module
    
    - Support resolved parameters in:
      - `@BeforeProperty` and `@AfterProperty` methods
      - `@BeforeTry` and `@AfterTry` methods

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

    - Around property hooks
        - Get and set random seed

    - Store
        - onUpdate()
    
    - `@Report(reportOnlyFailures = false)`

