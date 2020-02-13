- 1.2.4

    - RegistrationHook or other way to register hook instances

    - Engine Lifecycle
        - BeforeEngineHook
        - AfterEngineHook
        - Register as Service Provider

    - Container lifecycle
        - @BeforeContainer methods
        - @AfterContainer methods

    - Around property lifecycle
        - @BeforeProperty methods
        - @AfterProperty methods
        - @PerProperty(Class<? extends PropertyLifecycle>)
        - Get and set random seed

    - Around try lifecycle
        - @BeforeTry methods
        - @AfterTry methods
        - TryLifecycle objects
            try(TryLifecycle lc = new ...) {...}


- 1.2.x
  
    - Guided Generation
      https://github.com/jlink/jqwik/issues/84
      
    - Global ResolveReporterHook

    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

    - Spring/Boot Testing in its own module
    
    
- 1.3.0

    - Documentation for lifecycle API in user guide
