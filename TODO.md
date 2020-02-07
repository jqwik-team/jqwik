- 1.2.4

    - Improve Sample Reporting
      https://github.com/jlink/jqwik/issues/85

    - Lifecycle
        - Bind stores to member variables, e.g.
          @Store(lifespan = RUN|PROPERTY|TRY)

        - Around try
          - TryLifecycle objects
            try(TryLifecycle lc = new ...) {...}
          - @PerTry(Class<? extends TryLifecycle>)

        - Around property
          - @PerProperty(Class<? extends TryLifecycle>)

        - Container lifecycle
            - BeforeContainerHook
            - AfterContainerHook
            - @PerContainer

        - Engine Lifecycle
            - BeforeEngineHook
            - AfterEngineHook
            - Register as Service Provider

- 1.2.x
  
    - Spring/Boot Testing in its own module

    - Documentation for lifecycle API in user guide
