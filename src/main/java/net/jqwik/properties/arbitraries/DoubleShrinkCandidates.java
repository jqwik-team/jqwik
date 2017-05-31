package net.jqwik.properties.arbitraries;

import java.util.*;

public class DoubleShrinkCandidates implements ShrinkCandidates<Double> {
	public DoubleShrinkCandidates(double min, double max, int precision) {}

	@Override
	public Set<Double> nextCandidates(Double value) {
		return Collections.emptySet();
	}
}
