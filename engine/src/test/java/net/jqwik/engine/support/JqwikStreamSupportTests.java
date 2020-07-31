package net.jqwik.engine.support;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

class JqwikStreamSupportTests {

	@Example
	void concatenateStreams() {
		AtomicInteger countCalls = new AtomicInteger(0);
		Consumer<Integer> peeker = i -> countCalls.incrementAndGet();

		Stream<Integer> s1 = Stream.of(1, 2, 3, 4, 5)
								   .peek(peeker);
		Stream<Integer> s2 = Stream.of(6, 7, 8, 9, 10)
								   .peek(peeker);
		Stream<Integer> s3 = Stream.of(11, 12, 13, 14, 15)
								   .peek(peeker);

		Stream<Integer> stream = JqwikStreamSupport.concat(s1, s2);

		Optional<Integer> anInt = stream
									  .filter(i -> i % 2 == 0)
									  .findFirst();

		assertThat(anInt.get()).isEqualTo(2);
		assertThat(countCalls.get()).isEqualTo(2);
	}
}
