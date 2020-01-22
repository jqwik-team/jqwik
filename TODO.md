- 1.2.3

    - @ExpectFailure(ExpectedThrowableClass.class)
      - Add optional Throwable class
      - Tests
      - Documentation

    - Statistics.coverage(..)
      - check that is only called once per collector and try.
        Might require per try lifecycle.
      - check(Predicate<List<Object>> query)

    - Documentation for statistics coverage checking

    - @StatisticsReport(OFF|STANDARD)
      - StatisticsCollector.reportMode(OFF|STANDARD)
      - jqwik.properties, default = STANDARD
      - Documentation

    - Probabilistic assertions

- 1.2.x
  
    - Upgrade to JUnit platform 1.6
      https://junit.org/junit5/docs/5.6.0/release-notes/#deprecations-and-breaking-changes

    - Lifecycle
        - Around try
          - @BeforeTry
            - For methods
            - For variables
          - @AfterTry for methods
          - Allow premature success/failure

    - Around container
        - @BeforeContainer
        - @AfterContainer

    - Spring/Boot Testing in its own module
 
    - Around engine
      - Register through Java Service Registration

    - Documentation for lifecycle API in user guide
