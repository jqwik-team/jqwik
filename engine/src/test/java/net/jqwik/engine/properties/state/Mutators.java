package net.jqwik.engine.properties.state;

import java.util.function.*;

import net.jqwik.api.state.*;

class Mutators {

	static <S> Mutator<S> withName(Function<S, S> mutator, String name) {
		return new Mutator<S>() {
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
