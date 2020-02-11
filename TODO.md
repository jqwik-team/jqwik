- 1.2.4

    - ParameterInjector: Allow injection of parameters not annotated with `@ForAll`

    - Container lifecycle
        - @PerContainer

    - Around try lifecycle
      - TryLifecycle objects
        try(TryLifecycle lc = new ...) {...}
      - @PerTry(Class<? extends TryLifecycle>)

    - Around property lifecycle
      - @PerProperty(Class<? extends PropertyLifecycle>)
      - Get and set random seed

    - Engine Lifecycle
        - BeforeEngineHook
        - AfterEngineHook
        - Register as Service Provider

- 1.2.x
  
    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

    - Spring/Boot Testing in its own module

    - Documentation for lifecycle API in user guide
