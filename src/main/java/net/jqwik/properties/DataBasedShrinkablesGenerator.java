package net.jqwik.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.support.*;

public class DataBasedShrinkablesGenerator implements ShrinkablesGenerator {

	private final List<MethodParameter> forAllParameters;
	private final Iterator<? extends Tuple> iterator;

	public DataBasedShrinkablesGenerator(List<MethodParameter> forAllParameters, Iterable<? extends Tuple> data) {
		this.forAllParameters = forAllParameters;
		this.iterator = data.iterator();
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public List<Shrinkable> next() {
		Tuple tuple = iterator.next();
		checkCompatibility(tuple);
		return tuple.items().stream().map(Shrinkable::unshrinkable).collect(Collectors.toList());
	}

	private void checkCompatibility(Tuple tuple) {
		if (tuple.size() != forAllParameters.size()) {
			String message = String.format(
				"Data tuple %s is not compatible with parameters %s",
				tuple,
				JqwikStringSupport.displayString(forAllParameters)
			);
			throw new IncompatibleDataException(message);
		}
	}
}
