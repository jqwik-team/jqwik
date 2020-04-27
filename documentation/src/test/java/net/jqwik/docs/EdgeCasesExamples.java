package net.jqwik.docs;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

class EdgeCasesExamples {

	@Example
	void printEdgeCases() {
		System.out.println(Arbitraries.integers().edgeCases());
		System.out.println(Arbitraries.strings().withCharRange('a', 'z').edgeCases());
		System.out.println(Arbitraries.floats().list().edgeCases());
	}

	@Property(edgeCases = EdgeCasesMode.FIRST)
	void combinedEdgeCasesOfTwoParameters(
		@ForAll List<Integer> intList,
		@ForAll @IntRange(min = -100, max = 0) int anInt
	) {
		String parameters = String.format("%s, %s", intList, anInt);
		System.out.println(parameters);
	}
}
