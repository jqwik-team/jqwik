- 1.2.5

    - Invariants for ActionSequence can take a describing name

    - Spring/Boot Testing in its own module
    
    - Aliases `@BeforeExample` and `@AfterExample`
    
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

    - Remove deprecated APIs
    
    - Make some experimental API "maintained"

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

