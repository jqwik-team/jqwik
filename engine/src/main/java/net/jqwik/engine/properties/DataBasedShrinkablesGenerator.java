package net.jqwik.engine.properties;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.support.*;
import net.jqwik.engine.support.types.*;

public class DataBasedShrinkablesGenerator implements ForAllParametersGenerator {

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
	public List<Shrinkable<Object>> next() {
		Tuple tuple = iterator.next();
		checkCompatibility(tuple);
		return tuple.items().stream().map(Shrinkable::unshrinkable).collect(Collectors.toList());
	}

	private void checkCompatibility(Tuple tuple) {
		if (tuple.size() != forAllParameters.size()) {
			throw new IncompatibleDataException(createIncompatibilityMessage(tuple));
		}
		for (int i = 0; i < tuple.items().size(); i++) {
			TypeUsage valueType = TypeUsage.of(tuple.items().get(i).getClass());
			TypeUsage parameterType = TypeUsageImpl.forParameter(forAllParameters.get(i));
			if (!valueType.canBeAssignedTo(parameterType)) {
				throw new IncompatibleDataException(createIncompatibilityMessage(tuple));
			}
		}
	}

	private String createIncompatibilityMessage(Tuple tuple) {
		List<TypeUsage> parameterTypes =
			this.forAllParameters
				.stream()
				.map(TypeUsageImpl::forParameter)
				.collect(Collectors.toList());

		return String.format(
			"Data tuple %s is not compatible with parameters %s",
			tuple,
			JqwikStringSupport.displayString(parameterTypes)
		);
	}
}
