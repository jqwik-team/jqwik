package net.jqwik.engine.properties.arbitraries;

import java.math.*;
import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.*;

public class IntegralEdgeCasesConfiguration implements EdgeCases.Config<BigInteger> {
	private final Range<BigInteger> range;
	private final BigInteger shrinkingTarget;
	private boolean none;
	private final List<Predicate<BigInteger>> filters = new ArrayList<>();
	private final List<BigInteger> additionalEdgeCases = new ArrayList<>();

	public IntegralEdgeCasesConfiguration(Range<BigInteger> range, BigInteger shrinkingTarget) {
		this.range = range;
		this.shrinkingTarget = shrinkingTarget;
	}

	@Override
	public EdgeCases.Config<BigInteger> none() {
		none = true;
		return this;
	}

	@Override
	public EdgeCases.Config<BigInteger> filter(Predicate<BigInteger> filter) {
		filters.add(filter);
		return this;
	}

	@Override
	public EdgeCases.Config<BigInteger> add(BigInteger edgeCase) {
		additionalEdgeCases.add(edgeCase);
		return this;
	}

	@Override
	public final EdgeCases.Config<BigInteger> includeOnly(BigInteger... includedValues) {
		List<BigInteger> values = Arrays.asList(includedValues);
		return filter(values::contains);
	}

	public EdgeCases<BigInteger> configure(Consumer<EdgeCases.Config<BigInteger>> configurator, EdgeCases<BigInteger> defaultEdgeCases) {
		configurator.accept(this);
		EdgeCases<BigInteger> configuredEdgeCases = defaultEdgeCases;
		if (none) {
			configuredEdgeCases = EdgeCases.none();
		}
		for (Predicate<BigInteger> filter : filters) {
			configuredEdgeCases = EdgeCasesSupport.filter(configuredEdgeCases, filter);
		}

		List<Supplier<Shrinkable<BigInteger>>> suppliers = new ArrayList<>(configuredEdgeCases.suppliers());
		for (BigInteger additionalEdgeCase : additionalEdgeCases) {
			suppliers.add(() -> new ShrinkableBigInteger(additionalEdgeCase, range, shrinkingTarget));
		}
		return EdgeCasesSupport.fromSuppliers(suppliers);
	}
}
