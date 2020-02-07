- 1.2.4

    - Container lifecycle
        - BeforeContainerHook
        - AfterContainerHook
        - @PerContainer

    - Lifecycle Storage: Bind stores to member variables, e.g.
      @Store(lifespan = RUN|PROPERTY|TRY)

    - Around try lifecycle
      - TryLifecycle objects
        try(TryLifecycle lc = new ...) {...}
      - @PerTry(Class<? extends TryLifecycle>)

    - Around property lifecycle
      - @PerProperty(Class<? extends PropertyLifecycle>)

    - Engine Lifecycle
        - BeforeEngineHook
        - AfterEngineHook
        - Register as Service Provider

- 1.2.x
  
    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

    - Spring/Boot Testing in its own module

    - Documentation for lifecycle API in user guide
