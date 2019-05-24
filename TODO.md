- Shrinking towards target

    - [Float|Double|BigDecimal]Arbitrary.shrinkTowards(T extends Number target)
    
    - @[Number]Range(shrinkingTarget=target)

    - shrinkTowards(target2, target2...) 
      e.g. implement as oneOf(shrinkTowards(target1), shrinkTowards(target2))`

    - CharArbitrary.shrinkTowards(Character target)
    
- Statistics.keyFigures(String label, Number variable)

- Arbitraries.series(n -> prime(n)[, maxN])

- Arbitraries.fromStream(aStream[, maxLength])

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

