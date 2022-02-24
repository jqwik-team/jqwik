package net.jqwik.engine.properties.state;

import java.util.function.*;

import net.jqwik.api.state.*;

class ChainsTestingHelper {

	static <S> Step<S> withName(Function<S, S> mutator, String name) {
		return new Step<S>() {
			@Override
			public S apply(S s) {
				return mutator.apply(s);
			}

			@Override
			public String toString() {
				return name;
			}
		};

	}

}
