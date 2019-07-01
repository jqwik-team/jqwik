- 1.2.0

  - Update to JUnit platform 1.5:
    https://junit.org/junit5/docs/5.5.0/release-notes/#release-notes-5.5.0-junit-platform
    
  - Function generation

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
 

  - Use TestDiscovery from JUnit platform 1.5:
    https://junit.org/junit5/docs/5.5.0/api/org/junit/platform/engine/support/discovery/package-summary.html

