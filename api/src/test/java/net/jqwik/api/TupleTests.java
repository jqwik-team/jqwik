package net.jqwik.api;

import java.math.*;

import net.jqwik.api.Tuple.*;

import static org.assertj.core.api.Assertions.*;

class TupleTests {

	@Example
	void tupleOfOne() {
		Tuple1<String> tuple1 = Tuple.of("hallo");
		assertThat(tuple1.size()).isEqualTo(1);

		assertThat(tuple1.get1()).isEqualTo("hallo");
		assertThat(tuple1.items()).containsExactly("hallo");

		assertThat(tuple1.equals(Tuple.of("hallo"))).isTrue();
		assertThat(tuple1.equals(Tuple.of("hello"))).isFalse();

		assertThat(tuple1.hashCode()).isEqualTo(Tuple.of("hallo").hashCode());

		assertThat(tuple1.toString()).isEqualTo("(hallo)");
	}

	@Example
	void tupleOfTwo() {
		Tuple2<String, Integer> tuple2 = Tuple.of("hallo", 42);
		assertThat(tuple2.size()).isEqualTo(2);

		assertThat(tuple2.get1()).isEqualTo("hallo");
		assertThat(tuple2.get2()).isEqualTo(42);
		assertThat(tuple2.items()).containsExactly("hallo", 42);

		assertThat(tuple2.equals(Tuple.of("hallo", 42))).isTrue();
		assertThat(tuple2.equals(Tuple.of("hello", 41))).isFalse();

		assertThat(tuple2.hashCode()).isEqualTo(Tuple.of("hallo", 42).hashCode());

		assertThat(tuple2.toString()).isEqualTo("(\"hallo\",42)");
	}

	@Example
	void tupleOfThree() {
		Tuple3<String, Integer, Boolean> tuple3 = Tuple.of("hallo", 42, true);
		assertThat(tuple3.size()).isEqualTo(3);

		assertThat(tuple3.get1()).isEqualTo("hallo");
		assertThat(tuple3.get2()).isEqualTo(42);
		assertThat(tuple3.get3()).isEqualTo(true);
		assertThat(tuple3.items()).containsExactly("hallo", 42, true);

		assertThat(tuple3.equals(Tuple.of("hallo", 42, true))).isTrue();
		assertThat(tuple3.equals(Tuple.of("hallo", 42, false))).isFalse();

		assertThat(tuple3.hashCode()).isEqualTo(Tuple.of("hallo", 42, true).hashCode());

		assertThat(tuple3.toString()).isEqualTo("(\"hallo\",42,true)");
	}

	@Example
	void tupleOfFour() {
		Tuple4<String, Integer, Boolean, RoundingMode> tuple4 = Tuple.of("hallo", 42, true, RoundingMode.CEILING);
		assertThat(tuple4.size()).isEqualTo(4);

		assertThat(tuple4.get1()).isEqualTo("hallo");
		assertThat(tuple4.get2()).isEqualTo(42);
		assertThat(tuple4.get3()).isEqualTo(true);
		assertThat(tuple4.get4()).isEqualTo(RoundingMode.CEILING);
		assertThat(tuple4.items()).containsExactly("hallo", 42, true, RoundingMode.CEILING);

		assertThat(tuple4.equals(Tuple.of("hallo", 42, true, RoundingMode.CEILING))).isTrue();
		assertThat(tuple4.equals(Tuple.of("hallo", 42, true, RoundingMode.FLOOR))).isFalse();

		assertThat(tuple4.hashCode()).isEqualTo(Tuple.of("hallo", 42, true, RoundingMode.CEILING).hashCode());

		assertThat(tuple4.toString()).isEqualTo("(\"hallo\",42,true,CEILING)");
	}

	@Example
	void tupleOfFive() {
		Tuple5<String, Integer, Boolean, RoundingMode, Double> tuple5 = Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5);
		assertThat(tuple5.size()).isEqualTo(5);

		assertThat(tuple5.get1()).isEqualTo("hallo");
		assertThat(tuple5.get2()).isEqualTo(42);
		assertThat(tuple5.get3()).isEqualTo(true);
		assertThat(tuple5.get4()).isEqualTo(RoundingMode.CEILING);
		assertThat(tuple5.get5()).isEqualTo(0.5);
		assertThat(tuple5.items()).containsExactly("hallo", 42, true, RoundingMode.CEILING, 0.5);

		assertThat(tuple5.equals(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5))).isTrue();
		assertThat(tuple5.equals(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.9))).isFalse();

		assertThat(tuple5.hashCode()).isEqualTo(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5).hashCode());

		assertThat(tuple5.toString()).isEqualTo("(\"hallo\",42,true,CEILING,0.5)");
	}

	@Example
	void tupleOfSix() {
		Tuple6<String, Integer, Boolean, RoundingMode, Double, String> tuple6 = Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six");
		assertThat(tuple6.size()).isEqualTo(6);

		assertThat(tuple6.get1()).isEqualTo("hallo");
		assertThat(tuple6.get2()).isEqualTo(42);
		assertThat(tuple6.get3()).isEqualTo(true);
		assertThat(tuple6.get4()).isEqualTo(RoundingMode.CEILING);
		assertThat(tuple6.get5()).isEqualTo(0.5);
		assertThat(tuple6.get6()).isEqualTo("six");
		assertThat(tuple6.items()).containsExactly("hallo", 42, true, RoundingMode.CEILING, 0.5, "six");

		assertThat(tuple6.equals(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six"))).isTrue();
		assertThat(tuple6.equals(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "sixx"))).isFalse();

		assertThat(tuple6.hashCode()).isEqualTo(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six").hashCode());

		assertThat(tuple6.toString()).isEqualTo("(\"hallo\",42,true,CEILING,0.5,\"six\")");
	}

	@Example
	void tupleOfSeven() {
		Tuple7<String, Integer, Boolean, RoundingMode, Double, String, Integer> tuple7 =
			Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 7);
		assertThat(tuple7.size()).isEqualTo(7);

		assertThat(tuple7.get1()).isEqualTo("hallo");
		assertThat(tuple7.get2()).isEqualTo(42);
		assertThat(tuple7.get3()).isEqualTo(true);
		assertThat(tuple7.get4()).isEqualTo(RoundingMode.CEILING);
		assertThat(tuple7.get5()).isEqualTo(0.5);
		assertThat(tuple7.get6()).isEqualTo("six");
		assertThat(tuple7.get7()).isEqualTo(7);
		assertThat(tuple7.items()).containsExactly("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 7);

		assertThat(tuple7.equals(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 7))).isTrue();
		assertThat(tuple7.equals(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 8))).isFalse();

		assertThat(tuple7.hashCode()).isEqualTo(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 7).hashCode());

		assertThat(tuple7.toString()).isEqualTo("(\"hallo\",42,true,CEILING,0.5,\"six\",7)");
	}

	@Example
	void tupleOfEight() {
		Tuple8<String, Integer, Boolean, RoundingMode, Double, String, Integer, Boolean> tuple8 =
			Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 7, false);
		assertThat(tuple8.size()).isEqualTo(8);

		assertThat(tuple8.get1()).isEqualTo("hallo");
		assertThat(tuple8.get2()).isEqualTo(42);
		assertThat(tuple8.get3()).isEqualTo(true);
		assertThat(tuple8.get4()).isEqualTo(RoundingMode.CEILING);
		assertThat(tuple8.get5()).isEqualTo(0.5);
		assertThat(tuple8.get6()).isEqualTo("six");
		assertThat(tuple8.get7()).isEqualTo(7);
		assertThat(tuple8.get8()).isEqualTo(false);
		assertThat(tuple8.items()).containsExactly("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 7, false);

		assertThat(tuple8.equals(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 7, false))).isTrue();
		assertThat(tuple8.equals(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 7, true))).isFalse();

		assertThat(tuple8.hashCode()).isEqualTo(Tuple.of("hallo", 42, true, RoundingMode.CEILING, 0.5, "six", 7, false).hashCode());

		assertThat(tuple8.toString()).isEqualTo("(\"hallo\",42,true,CEILING,0.5,\"six\",7,false)");
	}
}
