- Case-based branching with statistical reporting:

  ```
  Cases.of("name")
    .match(condition1, "label1", () -> {})
    .match(condition2, "label2", () -> {})
    .noMatch();
  ``` 
  
  - specify minimum coverage for each case
  
- Arbitraries.series(n -> prime(n)[, maxN])

- Arbitraries.fromStream(aStream[, maxLength])

- Arbitrary.describe() for all built-in arbitraries

- Default Arbitraries, Generators and Shrinking for
  - Map
  - Functional interfaces and SAM types

- Spring/Boot Testing

