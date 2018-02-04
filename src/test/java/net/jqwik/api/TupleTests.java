package net.jqwik.api;

import net.jqwik.api.Tuples.*;

import java.math.*;

import static org.assertj.core.api.Assertions.*;

class TupleTests {

	@Example
	void tupleOfTwo() {
		Tuple2<String, Integer> tuple2 = Tuples.tuple("hallo", 42);
		assertThat(tuple2.size()).isEqualTo(2);

		assertThat(tuple2.get1()).isEqualTo("hallo");
		assertThat(tuple2.get2()).isEqualTo(42);

		assertThat(tuple2.equals(Tuples.tuple("hallo", 42))).isTrue();
		assertThat(tuple2.equals(Tuples.tuple("hello", 41))).isFalse();

		assertThat(tuple2.hashCode()).isEqualTo(Tuples.tuple("hallo", 42).hashCode());

		assertThat(tuple2.toString()).isEqualTo("(hallo,42)");
	}

	@Example
	void tupleOfThree() {
		Tuple3<String, Integer, Boolean> tuple3 = Tuples.tuple("hallo", 42, true);
		assertThat(tuple3.size()).isEqualTo(3);

		assertThat(tuple3.get1()).isEqualTo("hallo");
		assertThat(tuple3.get2()).isEqualTo(42);
		assertThat(tuple3.get3()).isEqualTo(true);

		assertThat(tuple3.equals(Tuples.tuple("hallo", 42, true))).isTrue();
		assertThat(tuple3.equals(Tuples.tuple("hallo", 42, false))).isFalse();

		assertThat(tuple3.hashCode()).isEqualTo(Tuples.tuple("hallo", 42, true).hashCode());

		assertThat(tuple3.toString()).isEqualTo("(hallo,42,true)");
	}

	@Example
	void tupleOfFour() {
		Tuple4<String, Integer, Boolean, RoundingMode> tuple4 = Tuples.tuple("hallo", 42, true, RoundingMode.CEILING);
		assertThat(tuple4.size()).isEqualTo(4);

		assertThat(tuple4.get1()).isEqualTo("hallo");
		assertThat(tuple4.get2()).isEqualTo(42);
		assertThat(tuple4.get3()).isEqualTo(true);
		assertThat(tuple4.get4()).isEqualTo(RoundingMode.CEILING);

		assertThat(tuple4.equals(Tuples.tuple("hallo", 42, true, RoundingMode.CEILING))).isTrue();
		assertThat(tuple4.equals(Tuples.tuple("hallo", 42, true, RoundingMode.FLOOR))).isFalse();

		assertThat(tuple4.hashCode()).isEqualTo(Tuples.tuple("hallo", 42, true, RoundingMode.CEILING).hashCode());

		assertThat(tuple4.toString()).isEqualTo("(hallo,42,true,CEILING)");
	}
}
