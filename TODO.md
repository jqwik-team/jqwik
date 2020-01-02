- 1.2.2

  - Save random seed instead of serialized objects to recreate specific test data
    when rerunning failed properties

  - Change signature of Arbitraries.oneOf() to allow covariant arbitrary types, e.g.
  
    return Arbitraries.oneOf(
			Arbitraries.strings(),
			Arbitraries.strings().list(),
			Arbitraries.integers()
		);
		
	If not possible introduce Arbitraries.anyOf() as a type unsafe wrapper.

  - Allow to annotate type variables with @From

- 1.2.3

  - Lifecycle
    - Around property
    - Around try
      - @BeforeTry
      - @AfterTry      
      - Allow premature success/failure
    - Around container
      - @BeforeContainer
      - @AfterContainer
    - Around engine
      - Register through Java Service Registration

- 1.2.x
  
  - Storing values
  - Spring/Boot Testing in its own module
 

