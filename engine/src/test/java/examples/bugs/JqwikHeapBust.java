package examples.bugs;

import net.jqwik.api.*;

// see problem in https://github.com/jqwik-team/jqwik/issues/113
// feature issue: https://github.com/jqwik-team/jqwik/issues/114
class JqwikHeapBust {

	@Property//(edgeCases = EdgeCasesMode.NONE)
	public void my_property(@ForAll("things") final Thing thing) {
		// Runs out of heap before getting here.
	}

	@Provide
	public Arbitrary<Thing> things() {
		return Builders.withBuilder(Thing.Builder::new)
						  .use(Arbitraries.integers()).in(Thing.Builder::setA)
						  .use(Arbitraries.integers()).in(Thing.Builder::setB)
						  .use(Arbitraries.integers()).in(Thing.Builder::setC)
						  .use(Arbitraries.integers()).in(Thing.Builder::setD)
						  .use(Arbitraries.integers()).in(Thing.Builder::setE)
						  .use(Arbitraries.integers()).in(Thing.Builder::setF)
						  // Starting to get slowly with 7th building step
						  // .use(Arbitraries.integers()).in(Thing.Builder::setG)
						  // .use(Arbitraries.integers()).in(Thing.Builder::setH)
						  // .use(Arbitraries.integers()).in(Thing.Builder::setI)
						  .build(Thing.Builder::build);
	}
}

class Thing {

	private final int a;
	private final int b;
	private final int c;
	private final int d;
	private final int e;
	private final int f;
	private final int g;
	private final int h;
	private final int i;

	private Thing(final Builder builder) {
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

		public Thing build() {
			return new Thing(this);
		}
	}
}
