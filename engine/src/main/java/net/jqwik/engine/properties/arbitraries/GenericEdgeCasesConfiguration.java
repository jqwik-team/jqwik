package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

import org.jspecify.annotations.*;

public class GenericEdgeCasesConfiguration<T extends @Nullable Object> implements EdgeCases.Config<T> {
	private boolean none;
	private final List<Predicate<? super T>> filters = new ArrayList<>();
	private final List<T> additionalEdgeCases = new ArrayList<>();

	@Override
	public EdgeCases.Config<T> none() {
		none = true;
		return this;
	}

	@Override
	public EdgeCases.Config<T> filter(Predicate<? super T> filter) {
		filters.add(filter);
		return this;
	}

	@SafeVarargs
	@Override
	public final EdgeCases.Config<T> add(T... edgeCases) {
		for (T edgeCase : edgeCases) {
			checkEdgeCaseIsValid(edgeCase);
			additionalEdgeCases.add(edgeCase);
		}
		return this;
	}

	// Override in subclasses if there is anything to check
	protected void checkEdgeCaseIsValid(T edgeCase) {
	}

	@SafeVarargs
	@Override
	public final EdgeCases.Config<T> includeOnly(T... includedValues) {
		List<T> values = Arrays.asList(includedValues);
		return filter(values::contains);
	}

	public EdgeCases<T> configure(Consumer<? super EdgeCases.Config<T>> configurator, Function<? super Integer, ? extends EdgeCases<T>> edgeCasesCreator, int maxEdgeCases) {
		configurator.accept(this);

		EdgeCases<T> configuredEdgeCases;
		if (none) {
			configuredEdgeCases = EdgeCases.none();
		} else if (filters.isEmpty()) {
			configuredEdgeCases = edgeCasesCreator.apply(maxEdgeCases);
		} else {
			configuredEdgeCases = edgeCasesCreator.apply(Integer.MAX_VALUE);
		}

		List<Supplier<Shrinkable<T>>> suppliers = configuredEdgeCases.suppliers();
		for (Predicate<? super T> filter : new ArrayList<>(filters)) {
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
