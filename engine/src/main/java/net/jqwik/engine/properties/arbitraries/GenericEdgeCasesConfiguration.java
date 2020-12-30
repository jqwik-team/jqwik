package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;

public class GenericEdgeCasesConfiguration<T> implements EdgeCases.Config<T> {
	private boolean none;
	private final List<Predicate<T>> filters = new ArrayList<>();
	private final List<T> additionalEdgeCases = new ArrayList<>();

	@Override
	public EdgeCases.Config<T> none() {
		none = true;
		return this;
	}

	@Override
	public EdgeCases.Config<T> filter(Predicate<T> filter) {
		filters.add(filter);
		return this;
	}

	@Override
	public EdgeCases.Config<T> add(T edgeCase) {
		additionalEdgeCases.add(edgeCase);
		return this;
	}

	@SafeVarargs
	@Override
	public final EdgeCases.Config<T> includeOnly(T... includedValues) {
		List<T> values = Arrays.asList(includedValues);
		return filter(values::contains);
	}

	public EdgeCases<T> configure(Consumer<EdgeCases.Config<T>> configurator, EdgeCases<T> defaultEdgeCases) {
		configurator.accept(this);
		EdgeCases<T> configuredEdgeCases = defaultEdgeCases;
		if (none) {
			configuredEdgeCases = EdgeCases.none();
		}
		for (Predicate<T> filter : filters) {
			configuredEdgeCases = EdgeCasesSupport.filter(configuredEdgeCases, filter);
		}

		List<Supplier<Shrinkable<T>>> suppliers = new ArrayList<>(configuredEdgeCases.suppliers());
		for (T additionalEdgeCase : additionalEdgeCases) {
			suppliers.add(() -> Shrinkable.unshrinkable(additionalEdgeCase));
		}
		return EdgeCasesSupport.fromSuppliers(suppliers);
	}
}
