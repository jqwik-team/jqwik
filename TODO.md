- DefaultCharacterArbitrary: Support more than one range.

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
  	
- Arbitrary.describe() for all built-in arbitraries

- Case-based branching with statistical reporting:

  ```
  Case
    .of(condition1, "label1", () -> {})
    .of(condition2, "label2", () -> {})
    .of(true, "labelDefault", () -> {});
  ``` 

- Class-based Property like this:
  
  ```
	@Property/Group/PropertyGroup?
	class NewBoard {

		private final Board board;

		public NewBoard(@ForAll Board board) {
			this.board = board;
		}

		@Property
		void all_holes_of_new_board_contain_pegs_except_center(
				@ForAll("validCoordinate")  int x,
				@ForAll("validCoordinate") int y
		) {
			Assume.that(x != board.center() || y != board.center());
			assertThat(board.hole(x, y)).isEqualTo(Hole.PEG);
		}

		@Provide
		Arbitrary<Integer> validCoordinate() {
			return Arbitraries.integers().between(1, board.size());
		}

	}
  ```


- Default Arbitraries, Generators and Shrinking for
  - Map
  - Functional interfaces and SAM types

- Spring/Boot Testing

