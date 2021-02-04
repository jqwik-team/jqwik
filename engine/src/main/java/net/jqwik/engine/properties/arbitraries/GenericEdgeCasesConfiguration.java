package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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

	@SafeVarargs
	@Override
	public final EdgeCases.Config<T> add(T... edgeCases) {
		additionalEdgeCases.addAll(Arrays.asList(edgeCases));
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

		List<Supplier<Shrinkable<T>>> suppliers = new ArrayList<>(configuredEdgeCases.suppliers());
		for (Predicate<T> filter : filters) {
			suppliers = suppliers.stream().filter(s -> filter.test(s.get().value())).collect(Collectors.toList());
		}
		for (T additionalEdgeCase : additionalEdgeCases) {
			suppliers.add(() -> createShrinkable(additionalEdgeCase));
		}
		return EdgeCasesSupport.fromSuppliers(suppliers);
	}

	protected Shrinkable<T> createShrinkable(T additionalEdgeCase) {
		return Shrinkable.unshrinkable(additionalEdgeCase);
	}
}
