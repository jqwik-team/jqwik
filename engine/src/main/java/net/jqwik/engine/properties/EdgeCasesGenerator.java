package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

import static java.lang.Math.*;

public class EdgeCasesGenerator implements Iterator<List<Shrinkable<Object>>> {

	// TODO: This may not be the best place for this shared functionality
	// Caveat: Always make sure that the number is greater than 1.
	// Otherwise only edge cases will be generated
	public static int calculateBaseToEdgeCaseRatio(int genSize, int countEdgeCases) {
		return min(
			max(genSize / countEdgeCases, 3),
			20
		);
	}


	private final List<EdgeCases<Object>> edgeCases;
	private final Iterator<List<Shrinkable<Object>>> iterator;

	EdgeCasesGenerator(List<EdgeCases<Object>> edgeCases) {
		this.edgeCases = edgeCases;
		this.iterator = createIterator();
	}

	private Iterator<List<Shrinkable<Object>>> createIterator() {
		if (this.edgeCases.isEmpty()) {
			return Collections.emptyIterator();
		}
		List<Iterable<Shrinkable<Object>>> iterables =
			edgeCases
				.stream()
				.map(edge -> (Iterable<Shrinkable<Object>>) edge)
				.collect(Collectors.toList());
		return Combinatorics.combine(iterables);
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public List<Shrinkable<Object>> next() {
		return iterator.next();
	}
}
