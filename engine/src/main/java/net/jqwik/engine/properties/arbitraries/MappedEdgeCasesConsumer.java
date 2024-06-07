package net.jqwik.engine.properties.arbitraries;

import java.util.function.*;

import net.jqwik.api.*;

class MappedEdgeCasesConsumer<T, U> implements Consumer<EdgeCases.Config<U>> {

	private final Consumer<EdgeCases.Config<T>> tConfigurator;
	private final Function<U, T> utMapper;
	private final Function<T, U> tuMapper;

	MappedEdgeCasesConsumer(
			Consumer<EdgeCases.Config<T>> tConfigurator,
			Function<U, T> utMapper,
			Function<T, U> tuMapper
	) {
		this.tConfigurator = tConfigurator;
		this.utMapper = utMapper;
		this.tuMapper = tuMapper;
	}

	@Override
	public void accept(EdgeCases.Config<U> uConfig) {

		EdgeCases.Config<T> tConfig = new EdgeCases.Config<T>() {
			@Override
			public EdgeCases.Config<T> none() {
				uConfig.none();
				return this;
			}

			@Override
			public EdgeCases.Config<T> filter(Predicate<? super T> filter) {
				uConfig.filter(u -> filter.test(utMapper.apply(u)));
				return this;
			}

			@SuppressWarnings("unchecked")
			@SafeVarargs
			@Override
			public final EdgeCases.Config<T> add(T... edgeCases) {
				for (T edgeCase : edgeCases) {
					uConfig.add(tuMapper.apply(edgeCase));
				}
				return this;
			}

			@SuppressWarnings("unchecked")
			@Override
			public EdgeCases.Config<T> includeOnly(T... includedValues) {
				Object[] includedBigIntegers = new Object[includedValues.length];
				for (int i = 0; i < includedValues.length; i++) {
					includedBigIntegers[i] = tuMapper.apply(includedValues[i]);
				}
				uConfig.includeOnly((U[]) includedBigIntegers);
				return this;
			}
		};
		tConfigurator.accept(tConfig);
	}
}
