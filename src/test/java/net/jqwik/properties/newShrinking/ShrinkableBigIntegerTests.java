package net.jqwik.properties.newShrinking;

import net.jqwik.*;
import net.jqwik.api.*;
import net.jqwik.properties.arbitraries.*;

import java.math.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("ShrinkableBigInteger")
class ShrinkableBigIntegerTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	private Consumer<BigInteger> reporter = mock(Consumer.class);

	@Example
	void creation() {
		NShrinkable<BigInteger> shrinkable = createShrinkableBigInteger(25, Range.of(-100l, 100l));
		assertThat(shrinkable.value()).isEqualTo(BigInteger.valueOf(25));
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(25));
	}

	@Example
	void cannotCreateValueOutsideRange() {
		assertThatThrownBy( //
			() -> createShrinkableBigInteger(25, Range.of(50l, 100l))) //
			.isInstanceOf(JqwikException.class);
	}

	@Example
	void shrinkingDistanceIsDistanceToShrinkingTarget() {
		assertThat( //
			createShrinkableBigInteger(25, Range.of(-100l, 100l)).distance()) //
			.isEqualTo(ShrinkingDistance.of(25));
		assertThat( //
			createShrinkableBigInteger(-25, Range.of(-100l, 100l)).distance()) //
			.isEqualTo(ShrinkingDistance.of(25));
		assertThat( //
			createShrinkableBigInteger(25, Range.of(5l, 100l)).distance()) //
			.isEqualTo(ShrinkingDistance.of(20));
		assertThat( //
			createShrinkableBigInteger(-25, Range.of(-100l, -5l)).distance()) //
			.isEqualTo(ShrinkingDistance.of(20));
	}


	@Example
	@Label("report all falsified on the way")
	void reportFalsified() {
		NShrinkable<BigInteger> shrinkable = createShrinkableBigInteger(30, Range.of(-100l, 100l));

		ShrinkingSequence<BigInteger> sequence = shrinkable.shrink(aBigInteger -> aBigInteger.compareTo(BigInteger.valueOf(10)) < 0);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(BigInteger.valueOf(13));
		verify(reporter).accept(BigInteger.valueOf(13));

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo(BigInteger.valueOf(10));
		verify(reporter).accept(BigInteger.valueOf(10));

		assertThat(sequence.next(count, reporter)).isFalse();
		verifyNoMoreInteractions(reporter);
	}


	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			NShrinkable<BigInteger> shrinkable = createShrinkableBigInteger(100000, Range.of(5l, 500000l));

			ShrinkingSequence<BigInteger> sequence = shrinkable.shrink(aBigInteger -> aBigInteger.compareTo(BigInteger.valueOf(1000)) <= 0);

			while (sequence.next(count, reporter)) {
			}

			assertThat(sequence.current().value()).isEqualTo(BigInteger.valueOf(1001));
			assertThat(counter.get()).isEqualTo(7);
		}

		@Example
		void withFilter() {
			NShrinkable<BigInteger> shrinkable = createShrinkableBigInteger(100000, Range.of(0l, 1000000l));

			Falsifier<BigInteger> falsifier = aBigInteger -> aBigInteger.intValueExact() < 99;
			Falsifier<BigInteger> filteredFalsifier = falsifier.withFilter(aBigInteger -> aBigInteger.intValueExact() % 2 == 0);

			ShrinkingSequence<BigInteger> sequence = shrinkable.shrink(filteredFalsifier);

			while (sequence.next(count, reporter)) {
				System.out.println(sequence.current().shrinkable());
			}

			assertThat(sequence.current().value()).isEqualTo(100);
			assertThat(counter.get()).isEqualTo(8);
		}

	}

	private NShrinkable<BigInteger> createShrinkableBigInteger(long number, Range<Long> longRange) {
		Range<BigInteger> bigIntegerRange = longRange.map(BigInteger::valueOf);
		return new ShrinkableBigInteger(BigInteger.valueOf(number), bigIntegerRange);
	}

}
