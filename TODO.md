- Arbitrary.forEachValue(Consumer<? super T> action)

  ```
  arbitrary.allValues().ifPresent(
  				stream -> stream.forEach(value -> action.apply(value)));
  if (!arbitrary.allValues().isPresent()) 
      throw AssertionError("Cannot generate all values")
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

- Shrink correctly: (Maybe Integer.MAX_VALUE/2, 1)
  	@Property
  	boolean sumOfTwoIntegersAlwaysGreaterThanEach(
  			@ForAll @Positive int positive1, //
  			@ForAll @Positive int positive2
  	) {
  		int sum = positive1 + positive2;
  		return sum > positive1 && sum > positive2;
  	}
