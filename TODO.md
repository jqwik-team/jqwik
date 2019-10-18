- Bug:

    <X extends HashMap<@IntRange(min = 1, max = 10) Integer, @AlphaChars Character>> void genericVars(@ForAll X callable) 

    does not constrain the generated ints and chars

- 1.2.1

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
 

