- 1.2.3

    - Lifecycle
        - Around property
            - PropertyLifecycle
               .after((PropertyExecutionResult) -> PER)
               .onSuccess(() ->)
               .onFailure((Throwable) ->)
              - user guide after with Statistics percentage check

        - Around try
          - @BeforeTry
            - For methods
            - For variables
          - @AfterTry for methods
          - Allow premature success/failure

- 1.2.x
  
    - Around container
        - @BeforeContainer
        - @AfterContainer

    - Spring/Boot Testing in its own module
 
    - Around engine
      - Register through Java Service Registration
