- 1.2.3

    - Statistics
        .coverageOf(false).checkPercentage("at least 5%", p -> p > 5.0)
        .coverageOf(true).checkCount(all, p -> p > 0)

    - Documentation for statistics coverage checking

    - Lifecycle
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

    - Documentation for lifecycle API in user guide
