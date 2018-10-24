- Exhaustive Generation bug: Should generate all but does not

  ```
  	// Will generate 9^9 different matrices
  	@Property(generation = GenerationMode.EXHAUSTIVE)
  	void matrix3times3(@ForAll("sudokus") List<List<Integer>> sudoku) {
  		System.out.println(format(sudoku));
  	}

  	@Provide
  	Arbitrary<List<List<Integer>>> sudokus() {
  		return Arbitraries.integers().between(1, 9)
  						  .list().ofSize(3)
  						  .list().ofSize(3)
  						  .filter(this::hasNoDuplicates);
  	}

  	private boolean hasNoDuplicates(List<List<Integer>> matrix) {
  		List<Integer> all = matrix.stream().flatMap(List::stream).collect(Collectors.toList());
  		return all.size() == new HashSet<>(all).size();
  	}

  	;

  	private String format(List<List<Integer>> matrix) {
  		return String.format("%s%n%s%n%s%n", matrix.get(0), matrix.get(1), matrix.get(2));
  	}
  ```

- PackageDescriptor e.g.
  @Label("mypackage")
  @AddHook(...)
  in package-info.java

- Make reporting configurable
  - Use System.out directly instead of using JUnit 5 reporter
  - jqwik.properties: useJunitPlatformReporter=false