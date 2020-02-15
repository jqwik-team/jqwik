- 1.2.4

    - Get rid of static PropertyLifecycle methods
        - Implement @PropertyLifecycle with before/after/onSuccess/onFailure methods
        - Remove static PropertyLifecycle methods

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
        - TryLifecycle objects
            try(TryLifecycle lc = new ...) {...}


- 1.2.x

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

    - Spring/Boot Testing in its own module
    
    
- 1.3.0

    - Documentation for lifecycle API in user guide
