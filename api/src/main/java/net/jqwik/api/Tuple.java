package net.jqwik.api;

import java.io.*;
import java.util.*;

public interface Tuple extends Serializable, Cloneable {

	int size();

	default List<Object> items() {return Collections.emptyList();}

	static <T1> Tuple1<T1> of(T1 v1) {
		return new Tuple1<>(v1);
	}

	static <T1, T2> Tuple2<T1, T2> of(T1 v1, T2 v2) {
		return new Tuple2<>(v1, v2);
	}

	static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 v1, T2 v2, T3 v3) {
		return new Tuple3<>(v1, v2, v3);
	}

	static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> of(T1 v1, T2 v2, T3 v3, T4 v4) {
		return new Tuple4<>(v1, v2, v3, v4);
	}

	static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> of(T1 v1, T2 v2, T3 v3, T4 v4, T5 v5) {
		return new Tuple5<>(v1, v2, v3, v4, v5);
	}

	static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> of(T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6) {
		return new Tuple6<>(v1, v2, v3, v4, v5, v6);
	}

	static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> of(T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7) {
		return new Tuple7<>(v1, v2, v3, v4, v5, v6, v7);
	}

	static <T1, T2, T3, T4, T5, T6, T7, T8> Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> of(
		T1 v1,
		T2 v2,
		T3 v3,
		T4 v4,
		T5 v5,
		T6 v6,
		T7 v7,
		T8 v8
	) {
		return new Tuple8<>(v1, v2, v3, v4, v5, v6, v7, v8);
	}

	class Tuple1<T1> implements Tuple {
		final T1 v1;

		private Tuple1(T1 v1) {
			this.v1 = v1;
		}

		@Override
		public int size() {
			return 1;
		}

		public T1 get1() {
			return v1;
		}

		@Override
		public List<Object> items() {
			return Arrays.asList(get1());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Tuple1<?> tuple = (Tuple1<?>) o;
			return Objects.equals(v1, tuple.v1);
		}

		@Override
		public int hashCode() {
			return Objects.hash(v1);
		}

		@Override
		public String toString() {
			return String.format("(%s)", v1);
		}
	}

	class Tuple2<T1, T2> extends Tuple1<T1> {
		final T2 v2;

		// TODO: Make private as soon as Tuples.Tuple2 has been removed
		Tuple2(T1 v1, T2 v2) {
			super(v1);
			this.v2 = v2;
		}

		@Override
		public int size() {
			return 2;
		}

		public T2 get2() {
			return v2;
		}

		@Override
		public List<Object> items() {
			return Arrays.asList(get1(), get2());
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

	class Tuple3<T1, T2, T3> extends Tuple2<T1, T2> {
		final T3 v3;

		// TODO: Make private as soon as Tuples.Tuple3 has been removed
		Tuple3(T1 v1, T2 v2, T3 v3) {
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
		public List<Object> items() {
			return Arrays.asList(get1(), get2(), get3());
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

	class Tuple4<T1, T2, T3, T4> extends Tuple3<T1, T2, T3> {
		final T4 v4;

		// TODO: Make private as soon as Tuples.Tuple4 has been removed
		Tuple4(T1 v1, T2 v2, T3 v3, T4 v4) {
			super(v1, v2, v3);
			this.v4 = v4;
		}

		@Override
		public int size() {
			return 4;
		}

		public T4 get4() {
			return v4;
		}

		@Override
		public List<Object> items() {
			return Arrays.asList(get1(), get2(), get3(), get4());
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

	class Tuple5<T1, T2, T3, T4, T5> extends Tuple4<T1, T2, T3, T4> {
		final T5 v5;

		private Tuple5(T1 v1, T2 v2, T3 v3, T4 v4, T5 v5) {
			super(v1, v2, v3, v4);
			this.v5 = v5;
		}

		@Override
		public int size() {
			return 5;
		}

		public T5 get5() {
			return v5;
		}

		@Override
		public List<Object> items() {
			return Arrays.asList(get1(), get2(), get3(), get4(), get5());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Tuple5<?, ?, ?, ?, ?> tuple = (Tuple5<?, ?, ?, ?, ?>) o;
			return Objects.equals(v1, tuple.v1) //
				&& Objects.equals(v2, tuple.v2) //
				&& Objects.equals(v3, tuple.v3) //
				&& Objects.equals(v4, tuple.v4) //
				&& Objects.equals(v5, tuple.v5);
		}

		@Override
		public int hashCode() {
			return Objects.hash(v1, v2, v3, v4, v5);
		}

		@Override
		public String toString() {
			return String
				.format("(%s,%s,%s,%s,%s)", v1, v2, v3, v4, v5);
		}
	}

	class Tuple6<T1, T2, T3, T4, T5, T6> extends Tuple5<T1, T2, T3, T4, T5> {
		final T6 v6;

		private Tuple6(T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6) {
			super(v1, v2, v3, v4, v5);
			this.v6 = v6;
		}

		@Override
		public int size() {
			return 6;
		}

		public T6 get6() {
			return v6;
		}

		@Override
		public List<Object> items() {
			return Arrays.asList(get1(), get2(), get3(), get4(), get5(), get6());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Tuple6<?, ?, ?, ?, ?, ?> tuple = (Tuple6<?, ?, ?, ?, ?, ?>) o;
			return Objects.equals(v1, tuple.v1) //
				&& Objects.equals(v2, tuple.v2) //
				&& Objects.equals(v3, tuple.v3) //
				&& Objects.equals(v4, tuple.v4) //
				&& Objects.equals(v5, tuple.v5) //
				&& Objects.equals(v6, tuple.v6);
		}

		@Override
		public int hashCode() {
			return Objects.hash(v1, v2, v3, v4, v5, v6);
		}

		@Override
		public String toString() {
			return String
				.format("(%s,%s,%s,%s,%s,%s)", v1, v2, v3, v4, v5, v6);
		}
	}

	class Tuple7<T1, T2, T3, T4, T5, T6, T7> extends Tuple6<T1, T2, T3, T4, T5, T6> {
		final T7 v7;

		private Tuple7(T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7) {
			super(v1, v2, v3, v4, v5, v6);
			this.v7 = v7;
		}

		@Override
		public int size() {
			return 7;
		}

		public T7 get7() {
			return v7;
		}

		@Override
		public List<Object> items() {
			return Arrays.asList(get1(), get2(), get3(), get4(), get5(), get6(), get7());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Tuple7<?, ?, ?, ?, ?, ?, ?> tuple = (Tuple7<?, ?, ?, ?, ?, ?, ?>) o;
			return Objects.equals(v1, tuple.v1) //
				&& Objects.equals(v2, tuple.v2) //
				&& Objects.equals(v3, tuple.v3) //
				&& Objects.equals(v4, tuple.v4) //
				&& Objects.equals(v5, tuple.v5) //
				&& Objects.equals(v6, tuple.v6) //
				&& Objects.equals(v7, tuple.v7);
		}

		@Override
		public int hashCode() {
			return Objects.hash(v1, v2, v3, v4, v5, v6, v7);
		}

		@Override
		public String toString() {
			return String.format("(%s,%s,%s,%s,%s,%s,%s)", v1, v2, v3, v4, v5, v6, v7);
		}
	}

	class Tuple8<T1, T2, T3, T4, T5, T6, T7, T8> extends Tuple7<T1, T2, T3, T4, T5, T6, T7> {
		final T8 v8;

		private Tuple8(T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8) {
			super(v1, v2, v3, v4, v5, v6, v7);
			this.v8 = v8;
		}

		@Override
		public int size() {
			return 8;
		}

		public T8 get8() {
			return v8;
		}

		@Override
		public List<Object> items() {
			return Arrays.asList(get1(), get2(), get3(), get4(), get5(), get6(), get7(), get8());
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Tuple8<?, ?, ?, ?, ?, ?, ?, ?> tuple = (Tuple8<?, ?, ?, ?, ?, ?, ?, ?>) o;
			return Objects.equals(v1, tuple.v1) //
				&& Objects.equals(v2, tuple.v2) //
				&& Objects.equals(v3, tuple.v3) //
				&& Objects.equals(v4, tuple.v4) //
				&& Objects.equals(v5, tuple.v5) //
				&& Objects.equals(v6, tuple.v6) //
				&& Objects.equals(v7, tuple.v7) //
				&& Objects.equals(v8, tuple.v8);
		}

		@Override
		public int hashCode() {
			return Objects.hash(v1, v2, v3, v4, v5, v6, v7, v8);
		}

		@Override
		public String toString() {
			return String.format("(%s,%s,%s,%s,%s,%s,%s,%s)", v1, v2, v3, v4, v5, v6, v7, v8);
		}
	}
}



