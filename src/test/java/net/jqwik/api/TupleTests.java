package net.jqwik.api;

import static org.assertj.core.api.Assertions.*;

import net.jqwik.api.Tuple.*;

class TupleTests {

	@Example
	void tupleOfTwo() {
		Tuple2<String, Integer> tuple2 = Tuple.of("hallo", 42);
		assertThat(tuple2.size()).isEqualTo(2);

		assertThat(tuple2.get1()).isEqualTo("hallo");
		assertThat(tuple2.get2()).isEqualTo(42);

		assertThat(tuple2.equals(Tuple.of("hallo", 42))).isTrue();
		assertThat(tuple2.equals(Tuple.of("hello", 41))).isFalse();

		assertThat(tuple2.hashCode()).isEqualTo(Tuple.of("hallo", 42).hashCode());

		assertThat(tuple2.toString()).isEqualTo("(hallo,42)");
	}
}
