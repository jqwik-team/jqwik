- "All parameters must have @ForAll annotation." 
  Fail test (instead of ignore it)
  https://github.com/jlink/jqwik/issues/54

- Shrink correctly: (Maybe Integer.MAX_VALUE/2, 1)
  	@Property
  	boolean sumOfTwoIntegersAlwaysGreaterThanEach(
  			@ForAll @Positive int positive1, //
  			@ForAll @Positive int positive2
  	) {
  		int sum = positive1 + positive2;
  		return sum > positive1 && sum > positive2;
  	}
  	
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

