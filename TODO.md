- 1.2.4

    - PerProperty.Lifecycle
        - beforeTry(TryContext, parameters)
        - afterTry(TryExecutionResult)

    - Store.onChange()
    
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
    
    - `@Report(reportOnlyFailures = false)`

    - Somehow allow to parameterize hook registrations, e.g.
      @AddLifecycleHook(MyHook.class, configuration = { })

    - ResolveParameterHook
        - `@ResolveParameter` method
    
    - ProvideArbitraryHook
        - Let domains use that hook
        - Let ArbitraryProviders use that hook
    
    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      
    - Global ResolveReporterHook

    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

    
- 1.3.0

    - Documentation for lifecycle hooks API in user guide
