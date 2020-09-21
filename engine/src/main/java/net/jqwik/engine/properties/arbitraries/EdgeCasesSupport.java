package net.jqwik.engine.properties.arbitraries;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apiguardian.api.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.shrinking.*;
import net.jqwik.engine.support.*;

import static org.apiguardian.api.API.Status.*;

public class EdgeCasesSupport {

	public static <T> EdgeCases<T> fromSuppliers(final List<Supplier<Shrinkable<T>>> suppliers) {
		return new EdgeCases<T>() {
			@Override
			public List<Supplier<Shrinkable<T>>> suppliers() {
				return suppliers;
			}

			@Override
			public String toString() {
				String edgeCases =
					suppliers
						.stream()
						.map(Supplier::get)
						.map(Shrinkable::value)
						.map(JqwikStringSupport::displayString)
						.collect(Collectors.joining(", "));
				return String.format("EdgeCases[%s]", edgeCases);
			}
		};
	}

	public static <T> EdgeCases<T> choose(final List<T> values) {
		List<Shrinkable<T>> shrinkables = new ArrayList<>();
		if (values.size() > 0) {
			shrinkables.add(new ChooseValueShrinkable<>(values.get(0), values));
		}
		if (values.size() > 1) {
			int lastIndex = values.size() - 1;
			shrinkables.add(new ChooseValueShrinkable<>(values.get(lastIndex), values));
		}
		try {
			if (values.contains(null)) {
				shrinkables.add(Shrinkable.unshrinkable(null));
			}
		} catch (NullPointerException someListsDoNotAllowNullValues) { }
		return EdgeCasesSupport.fromShrinkables(shrinkables);
	}

	public static <T> EdgeCases<T> concat(List<EdgeCases<T>> edgeCases) {
		if (edgeCases.isEmpty()) {
			return EdgeCases.none();
		}
		List<Supplier<Shrinkable<T>>> concatenatedSuppliers = new ArrayList<>();
		for (EdgeCases<T> edgeCase : edgeCases) {
			if (edgeCase.isEmpty()) {
				continue;
			}
			concatenatedSuppliers.addAll(edgeCase.suppliers());
		}
		return EdgeCasesSupport.fromSuppliers(concatenatedSuppliers);
	}

	@SafeVarargs
	@API(status = INTERNAL)
	static <T> EdgeCases<T> concat(EdgeCases<T>... rest) {
		return EdgeCasesSupport.concat(Arrays.asList(rest));
	}

	@API(status = INTERNAL)
	public static <T> EdgeCases<T> fromShrinkables(List<Shrinkable<T>> shrinkables) {
		return () -> shrinkables
						 .stream()
						 .map(shrinkable -> (Supplier<Shrinkable<T>>) () -> shrinkable)
						 .collect(Collectors.toList());
	}

}
