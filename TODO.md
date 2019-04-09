- "All parameters must have @ForAll annotation."
  Fail test (instead of ignore it)

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

