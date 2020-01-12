- 1.2.3

    - Arbitrary.duplicates(probability)
      - User guide entry: "Inject Duplicate Values"

    - Lifecycle
        - Storing values
            - API: Store
                    get
                    update
                    - static:
                        create(visibility, lifespan, name, initializer)
                        get(name, type)

        - Around property
            - Statistics.count()/percentage()
            - PerProperty.after/finally/variable
        - Around container
            - @BeforeContainer
            - @AfterContainer

- 1.2.x
  
    - Spring/Boot Testing in its own module
 
    - Around try
      - @BeforeTry
      - @AfterTry
      - Allow premature success/failure

    - Around engine
      - Register through Java Service Registration
