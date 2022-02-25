package net.jqwik.engine.properties.state;

import java.util.function.*;

import net.jqwik.api.state.*;

class ChainsTestingHelper {

	static <S> Transformer<S> transformer(Function<S, S> mutator, String description) {
		return new Transformer<S>() {
			@Override
			public S apply(S s) {
				return mutator.apply(s);
			}

			@Override
			public String transformation() {
				return description;
			}
		};

	}

}
