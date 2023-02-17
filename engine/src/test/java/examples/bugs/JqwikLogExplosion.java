package examples.bugs;

import java.util.*;

import net.jqwik.api.*;

// see https://github.com/jqwik-team/jqwik/issues/119
class JqwikLogExplosion {

	@Property(edgeCases = EdgeCasesMode.MIXIN)
	public void my_property(@ForAll("things") final List<Thing2> thing) {
		// Spams the following warning, despite "EdgeCasesMode.NONE"
		//   Aug 30, 2020 2:23:01 PM net.jqwik.engine.facades.EdgeCasesFacadeImpl logTooManyEdgeCases
		//   WARNING: Combinatorial explosion of edge case generation. Stopped creating more after 10000 generated cases
	}

	@Provide
	public Arbitrary<List<Thing2>> things() {
		return Builders.withBuilder(Thing2.Builder::new)
						  .use(Arbitraries.integers()).in(Thing2.Builder::setA)
						  .use(Arbitraries.integers()).in(Thing2.Builder::setB)
						  .use(Arbitraries.integers()).in(Thing2.Builder::setC)
						  .use(Arbitraries.integers()).in(Thing2.Builder::setD)
						  .use(Arbitraries.integers()).in(Thing2.Builder::setE)
						  .use(Arbitraries.integers()).in(Thing2.Builder::setF)
						  .use(Arbitraries.integers()).in(Thing2.Builder::setG)
						  .use(Arbitraries.integers()).in(Thing2.Builder::setH)
						  .use(Arbitraries.integers()).in(Thing2.Builder::setI)
						  .build(Thing2.Builder::build).list();
	}
}

class Thing2 {

	private final int a;
	private final int b;
	private final int c;
	private final int d;
	private final int e;
	private final int f;
	private final int g;
	private final int h;
	private final int i;

	private Thing2(final Builder builder) {
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

	public static class Builder {
		private int a;
		private int b;
		private int c;
		private int d;
		private int e;
		private int f;
		private int g;
		private int h;
		private int i;

		public Builder setA(final int a) {
			this.a = a;
			return this;
		}

		public Builder setB(final int b) {
			this.b = b;
			return this;
		}

		public Builder setC(final int c) {
			this.c = c;
			return this;
		}

		public Builder setD(final int d) {
			this.d = d;
			return this;
		}

		public Builder setE(final int e) {
			this.e = e;
			return this;
		}

		public Builder setF(final int f) {
			this.f = f;
			return this;
		}

		public Builder setG(final int g) {
			this.g = g;
			return this;
		}

		public Builder setH(final int h) {
			this.h = h;
			return this;
		}

		public Builder setI(final int i) {
			this.i = i;
			return this;
		}

		public Thing2 build() {
			return new Thing2(this);
		}
	}
}