package net.jqwik.api;

import java.io.*;
import java.util.*;

public class Tuples {

	public interface Tuple extends Serializable, Cloneable {
		int size();
	}

	public static <T1, T2> Tuple2<T1, T2> tuple(T1 v1, T2 v2) {
		return new Tuple2<>(v1, v2);
	}

	public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(T1 v1, T2 v2, T3 v3) {
		return new Tuple3<>(v1, v2, v3);
	}

	public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tuple(T1 v1, T2 v2, T3 v3, T4 v4) {
		return new Tuple4<>(v1, v2, v3, v4);
	}

	static class Tuple2<T1, T2> implements Tuple {
		protected final T1 v1;
		protected final T2 v2;

		private Tuple2(T1 v1, T2 v2) {
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
			Tuple2<?, ?> tuple = (Tuple2<?, ?>) o;
			return Objects.equals(v1, tuple.v1) //
				&& Objects.equals(v2, tuple.v2);
		}

		@Override
		public int hashCode() {
			return Objects.hash(v1, v2);
		}

		@Override
		public String toString() {
			return String.format("(%s,%s)", v1, v2);
		}
	}

	public static class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {
		protected final T3 v3;

		private Tuple3(T1 v1, T2 v2, T3 v3) {
			super(v1, v2);
			this.v3 = v3;
		}

		@Override
		public int size() {
			return 3;
		}

		public T3 get3() {
			return v3;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Tuple3<?, ?, ?> tuple = (Tuple3<?, ?, ?>) o;
			return Objects.equals(v1, tuple.v1) //
				&& Objects.equals(v2, tuple.v2) //
				&& Objects.equals(v3, tuple.v3);
		}

		@Override
		public int hashCode() {
			return Objects.hash(v1, v2, v3);
		}

		@Override
		public String toString() {
			return String.format("(%s,%s,%s)", v1, v2, v3);
		}
	}

	static class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {
		protected final T4 v4;

		private Tuple4(T1 v1, T2 v2, T3 v3, T4 v4) {
			super(v1, v2, v3);
			this.v4 = v4;
		}

		@Override
		public int size() {
			return 4;
		}

		public T1 get1() {
			return v1;
		}

		public T2 get2() {
			return v2;
		}

		public T3 get3() {
			return v3;
		}

		public T4 get4() {
			return v4;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Tuple4<?, ?, ?, ?> tuple = (Tuple4<?, ?, ?, ?>) o;
			return Objects.equals(v1, tuple.v1) //
				&& Objects.equals(v2, tuple.v2) //
				&& Objects.equals(v3, tuple.v3) //
				&& Objects.equals(v4, tuple.v4);
		}

		@Override
		public int hashCode() {
			return Objects.hash(v1, v2, v3, v4);
		}

		@Override
		public String toString() {
			return String.format("(%s,%s,%s,%s)", v1, v2, v3, v4);
		}
	}
}



