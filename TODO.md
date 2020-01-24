- 1.2.3

    - Statistics.coverage(..)
      - check that is only called once per collector and try.
        Might require per try lifecycle.

    - Documentation
      - statistics coverage checking
      - statistics reportMode

    - Probabilistic assertions

- 1.2.x
  
    - Upgrade to JUnit platform 1.6
      https://junit.org/junit5/docs/5.6.0/release-notes/#deprecations-and-breaking-changes

    - @StatisticsReport
        - jqwik.properties, OFF|STANDARD|<MyReportFormatClass>, default = STANDARD

    - Lifecycle
        - Allow to specify proximity/order in `@AddLifecycleHook`

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
