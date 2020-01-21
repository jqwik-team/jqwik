- 1.2.3

    - Statistics.coverage(..)
      - check(Predicate<List<Object>> query)

    - Documentation for statistics coverage checking

    - @StatisticsReport(OFF|STANDARD)
      - jqwik.properties, default = STANDARD
      - Documentation

    - Upgrade to JUnit platform 1.6
      https://junit.org/junit5/docs/5.6.0/release-notes/#deprecations-and-breaking-changes

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
