package net.jqwik.engine.properties.shrinking;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

import static net.jqwik.testing.ShrinkingSupport.*;

@PropertyDefaults(tries = 100, shrinking = ShrinkingMode.FULL)
class CombinatorsShrinkingTests {

	@Property
	void shrinkCombineWithoutCondition(@ForAll Random random) {
		Arbitrary<String> as =
			Combinators
				.combine(Arbitraries.integers(), Arbitraries.strings().alpha().ofMinLength(1))
				.as((i, s) -> i + s);

		String shrunkValue = falsifyThenShrink(as, random);

		Assertions.assertThat(shrunkValue).isIn("0A", "0a");
	}

	@Property
	void shrinkCombineWithCondition(@ForAll Random random) {
		Arbitrary<String> as =
			Combinators
				.combine(Arbitraries.integers(), Arbitraries.strings().alpha().ofMinLength(1))
				.as((i, s) -> i + s);

		Falsifier<String> falsifier = aString -> aString.length() >= 3
														 ? TryExecutionResult.falsified(null)
														 : TryExecutionResult.satisfied();
		String shrunkValue = falsifyThenShrink(as, random, falsifier);

		Assertions.assertThat(shrunkValue).isIn("0AA", "10a", "10A", "-1a", "-1A");
	}

	@Property
	void shrinkBuilderWithoutCondition(@ForAll Random random) {
		Arbitrary<Integer> as =
			Combinators
				.withBuilder(() -> 0)
				.use(Arbitraries.integers()).in(Integer::sum)
				.use(Arbitraries.integers().greaterOrEqual(10)).in(Integer::sum)
				.use(Arbitraries.integers().lessOrEqual(10)).in(Integer::sum)
				.use(Arbitraries.integers().greaterOrEqual(100)).in(Integer::sum)
				.use(Arbitraries.integers().greaterOrEqual(1000)).in(Integer::sum)
				.use(Arbitraries.integers().greaterOrEqual(10000)).in(Integer::sum)
				.use(Arbitraries.integers().greaterOrEqual(100000)).in(Integer::sum)
				.use(Arbitraries.integers().greaterOrEqual(1000000)).in(Integer::sum)
				.use(Arbitraries.integers().greaterOrEqual(10000000)).in(Integer::sum)
				.build();

		int shrunkValue = falsifyThenShrink(as, random);

		Assertions.assertThat(shrunkValue).isEqualTo(11111110);
	}

	@Property
	void shrinkBuilderWithCondition(@ForAll Random random) {

		Arbitrary<Thing> things =
			Combinators.withBuilder(Thing.Builder::new)
					   .use(Arbitraries.integers()).in(Thing.Builder::setA)
					   .use(Arbitraries.integers()).in(Thing.Builder::setB)
					   .use(Arbitraries.integers()).in(Thing.Builder::setC)
					   .use(Arbitraries.integers()).in(Thing.Builder::setD)
					   .use(Arbitraries.integers()).in(Thing.Builder::setE)
					   .use(Arbitraries.integers()).in(Thing.Builder::setF)
					   .use(Arbitraries.integers()).in(Thing.Builder::setG)
					   .use(Arbitraries.integers()).in(Thing.Builder::setH)
					   .use(Arbitraries.integers()).in(Thing.Builder::setI)
					   .build(Thing.Builder::build);

		Falsifier<Thing> falsifier = aThing -> {
			if (aThing.a >= 42 && aThing.g >= 100) {
				return TryExecutionResult.falsified(null);
			}
			return TryExecutionResult.satisfied();
		};
		// Fails when generating with edge cases :-(
		Thing shrunkValue = falsifyThenShrink(things.generator(100), random, falsifier);

		Assertions.assertThat(shrunkValue.a).isEqualTo(42);
		Assertions.assertThat(shrunkValue.b).isEqualTo(0);
		Assertions.assertThat(shrunkValue.c).isEqualTo(0);
		Assertions.assertThat(shrunkValue.d).isEqualTo(0);
		Assertions.assertThat(shrunkValue.e).isEqualTo(0);
		Assertions.assertThat(shrunkValue.f).isEqualTo(0);
		Assertions.assertThat(shrunkValue.g).isEqualTo(100);
		Assertions.assertThat(shrunkValue.h).isEqualTo(0);
		Assertions.assertThat(shrunkValue.i).isEqualTo(0);
	}

	private static class Thing {
		private final int a;
		private final int b;
		private final int c;
		private final int d;
		private final int e;
		private final int f;
		private final int g;
		private final int h;
		private final int i;

		private Thing(final Thing.Builder builder) {
			a = builder.a;
			b = builder.b;
			c = builder.c;
			d = builder.d;
			e = builder.e;
			f = builder.f;
			g = builder.g;
			h = builder.h;
			i = builder.i;
		}

		@Override
		public String toString() {
			return String.format(
				"Thing{a=%d, b=%d, c=%d, d=%d, e=%d, f=%d, g=%d, h=%d, i=%d}",
				a, b, c, d, e, f, g, h, i
			);
		}

		private static class Builder {
			private int a;
			private int b;
			private int c;
			private int d;
			private int e;
			private int f;
			private int g;
			private int h;
			private int i;

			Thing.Builder setA(final int a) {
				this.a = a;
				return this;
			}

			Thing.Builder setB(final int b) {
				this.b = b;
				return this;
			}

			Thing.Builder setC(final int c) {
				this.c = c;
				return this;
			}

			Thing.Builder setD(final int d) {
				this.d = d;
				return this;
			}

			Thing.Builder setE(final int e) {
				this.e = e;
				return this;
			}

			Thing.Builder setF(final int f) {
				this.f = f;
				return this;
			}

			Thing.Builder setG(final int g) {
				this.g = g;
				return this;
			}

			Thing.Builder setH(final int h) {
				this.h = h;
				return this;
			}

			Thing.Builder setI(final int i) {
				this.i = i;
				return this;
			}

			Thing build() {
				return new Thing(this);
			}
		}
	}

}
