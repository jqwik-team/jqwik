package net.jqwik.api.edgeCases;

import net.jqwik.api.*;

class EdgeCasesExceedLimitTests {

	@Property(edgeCases = EdgeCasesMode.MIXIN)
	public void doesNotBurstHeap(@ForAll("things") final Thing thing) {
		// System.out.println(thing);
	}

	@Provide
	public Arbitrary<Thing> things() {
		return Combinators.withBuilder(Thing.Builder::new)
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
	}

	static class Thing {
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

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer("Thing{");
			sb.append("a=").append(a);
			sb.append(", b=").append(b);
			sb.append(", c=").append(c);
			sb.append(", d=").append(d);
			sb.append(", e=").append(e);
			sb.append(", f=").append(f);
			sb.append(", g=").append(g);
			sb.append(", h=").append(h);
			sb.append(", i=").append(i);
			sb.append('}');
			return sb.toString();
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
}
