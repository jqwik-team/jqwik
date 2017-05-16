package net.jqwik.newArbitraries;

import java.util.*;
import java.util.function.*;

public class NCombinators {

	public static <T1, T2> Combinator2<T1, T2> combine(NArbitrary<T1> a1, NArbitrary<T2> a2) {
		return new Combinator2<T1, T2>(a1, a2);
	}

	public static class Combinator2<T1, T2> {
		private final NArbitrary<T1> a1;
		private final NArbitrary<T2> a2;

		private Combinator2(NArbitrary<T1> a1, NArbitrary<T2> a2) {
			this.a1 = a1;
			this.a2 = a2;
		}

		public <R> NArbitrary<R> as(F2<T1, T2, R> combinator) {
			return (tries) -> {
				NShrinkableGenerator<T1> g1 = a1.generator(tries);
				NShrinkableGenerator<T2> g2 = a2.generator(tries);
				return random -> {
					List<NShrinkable<?>> shrinkables = new ArrayList<>();
					shrinkables.add(g1.next(random));
					shrinkables.add(g2.next(random));
					Function<List<?>, R> combineFunction = params -> combinator.apply(params.get(0), params.get(1));

					return new NCombinedShrinkable<>(shrinkables, combineFunction);
				};
			};
		}
	}

	@FunctionalInterface
	public interface F2<T1, T2, R> {
		R apply(Object t1, Object t2);
	}

}
