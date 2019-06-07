- Get rid of dependencies to internal junit5 classes
  e.g. https://github.com/jlink/jqwik/issues/59
  
- Don't use ClasspathScanningSupport anymore (deprecated in platform 1.5)
  Use org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver instead

- Case-based branching with statistical reporting:

  ```
  Cases.of("name")
    .match(condition1, "label1", () -> {})
    .match(condition2, "label2", () -> {})
    .noMatch();
  ``` 
  
  - specify minimum coverage for each case
  
- Arbitrary.describe() for all built-in arbitraries

- Default Arbitraries, Generators and Shrinking for
  - Map
  - Functional interfaces and SAM types

- Spring/Boot Testing

