package examples.bugs;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;

class ExhaustiveGenerationBug {

	// Should throw TooManyFilterMissesException
	@Property(generation = GenerationMode.EXHAUSTIVE)
	void sudokuMatrix(@ForAll("sudokus") List<List<Integer>> sudoku) {
		System.out.println(format(sudoku));
	}

	@Provide
	Arbitrary<List<List<Integer>>> sudokus() {
		return Arbitraries.integers().between(1, 9)
						  .list().ofSize(3)
						  .list().ofSize(3)
						  .filter(matrix -> hasNoDuplicates(matrix));
	}

	private boolean hasNoDuplicates(List<List<Integer>> matrix) {
		List<Integer> all = matrix.stream().flatMap(List::stream).collect(Collectors.toList());
		HashSet<Integer> allSet = new LinkedHashSet<>(all);
		return all.size() == allSet.size();
	}

	private String format(List<List<Integer>> matrix) {
		return String.format("%s%n%s%n%s%n", matrix.get(0), matrix.get(1), matrix.get(2));
	}

}
