- Case-based branching with statistical reporting:

  ```
  Cases.of("name")
    .match(condition1, "label1", () -> {})
    .match(condition2, "label2", () -> {})
    .noMatch();
  ``` 

- Arbitrary.describe() for all built-in arbitraries

- Default Arbitraries, Generators and Shrinking for
  - Map
  - Functional interfaces and SAM types

- Spring/Boot Testing

