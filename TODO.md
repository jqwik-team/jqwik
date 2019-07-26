- 1.2.0

  – @From("providerMethod") annotation
  
    - Alternative to @ForAll("providerMethod")
    - Allow in type parameters, 
      e.g. @ForAll List<@From("zip codes") String> listOfZips)
    - Warn if there are conflicting @From/@ForAll annotations
  
- 1.2.1

  - Lifecycle
    - Around container
    - Around try
    - Around engine
    - Storing values
  
  - Spring/Boot Testing in its own module
 

