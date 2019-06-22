- 1.1.6

  - Release

- 1.2.0

  - Remove deprecated stuff
  
  - Promote some experimental stuff
  
  - Function generation

    - Functions.function(Class<F> functionalType).returns(Arbitrary<R> resultArbitrary))
    
      - generate non constant functions
      
      - allow returns depending on input parameter conditions
      
      - user guide entry
      
    - Default mapping for SAM types and @Functional types

  - Lifecycle
    - Around container
    - Around try
    - Around engine
  
  - Spring/Boot Testing in its own module

  - Arbitrary.describe() for all built-in arbitraries


