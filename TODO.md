- "All parameters must have @ForAll annotation." 
  Fail test (instead of ignore it)
  https://github.com/jlink/jqwik/issues/54

- Case-based branching with statistical reporting:

  ```
  Case
    .of(condition1, "label1", () -> {})
    .of(condition2, "label2", () -> {})
    .of(true, "labelDefault", () -> {});
  ``` 

- Arbitrary.describe() for all built-in arbitraries

- Default Arbitraries, Generators and Shrinking for
  - Map
  - Functional interfaces and SAM types

- Spring/Boot Testing

