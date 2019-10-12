- 1.2.1

  - TypeUsage.canBeAssigned/isAssignableFrom to handle 
    - `X extends XYZ`
    - `X super XYZ` 
  
  - Introduce recursive use of Arbitraries.forType(Class<T> targetType)
    - forType(Class<T> targetType, int depth)
    - @UseType(depth = 1)
    
- 1.2.2

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
  
- 1.2.3

  - Storing values
  - Spring/Boot Testing in its own module
 

