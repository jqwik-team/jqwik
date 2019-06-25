- 1.2.0

  - Function generation

    - Functions.function(Class<F> functionalType).returns(Arbitrary<R> resultArbitrary))
    
      - generate non constant functions
      
      - when(Predicate<List<Object>> parameterCondition).answer(Function<List, Object> answer)
      
      - user guide entry
      
    - Default mapping for SAM types and @Functional types

  - Arbitrary.describe() for all built-in arbitraries
  
    - @Report(GENERATORS or ARBITRARIES)
    
    
  - Lifecycle
    - Around container
    - Around try
    - Around engine
  
  - Spring/Boot Testing in its own module


