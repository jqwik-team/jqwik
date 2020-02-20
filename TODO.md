- 1.2.5

    - Report checked value for failing statistics coverage check, e.g.
      "Count of 0 _for value ["abc"]_ does not fulfill condition"

    - Spring/Boot Testing in its own module
    
    - Support resolved parameters in:
      - `@BeforeProperty` and `@AfterProperty` methods
      - `@BeforeTry` and `@AfterTry` methods

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

    - Documentation for lifecycle hooks API in user guide

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

