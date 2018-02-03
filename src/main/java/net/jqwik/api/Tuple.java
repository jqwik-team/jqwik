package net.jqwik.api;

import java.io.*;

public interface Tuple<T extends Tuple> extends Serializable, Cloneable {
	int size();

	static <T1, T2> Tuple2<T1, T2> of(T1 v1, T2 v2) {
		return new Tuple2<>(v1, v2);
	}

	class Tuple2<T1, T2> implements Tuple<Tuple2> {
		private final T1 v1;
		private final T2 v2;

		public Tuple2(T1 v1, T2 v2) {
			this.v1 = v1;
			this.v2 = v2;
		}

		@Override
		public int size() {
			return 2;
		}

		public T1 get1() {
			return v1;
		}

		public T2 get2() {
			return v2;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Tuple2<?, ?> tuple2 = (Tuple2<?, ?>) o;

			if (v1 != null ? !v1.equals(tuple2.v1) : tuple2.v1 != null) return false;
			return v2 != null ? v2.equals(tuple2.v2) : tuple2.v2 == null;
		}

		@Override
		public int hashCode() {
			int result = v1 != null ? v1.hashCode() : 0;
			result = 31 * result + (v2 != null ? v2.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return String.format("(%s,%s)", v1, v2);
		}
	}
}



