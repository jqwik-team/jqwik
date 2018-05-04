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
@Label("ShrinkableString")
class ShrinkableStringTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;
	private Consumer<String> reporter = mock(Consumer.class);

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


//	@Example
//	@Label("report all falsified on the way")
//	void reportFalsified() {
//		NShrinkable<String> shrinkable = createShrinkableString("bcd", 0);
//
//		ShrinkingSequence<String> sequence = shrinkable.shrink(String::isEmpty);
//
//		assertThat(sequence.next(count, reporter)).isTrue();
//		assertThat(sequence.current().value()).isEqualTo("bc");
//		verify(reporter).accept("bc");
//
//		assertThat(sequence.next(count, reporter)).isTrue();
//		assertThat(sequence.current().value()).isEqualTo("b");
//		verify(reporter).accept("b");
//
//		assertThat(sequence.next(count, reporter)).isTrue();
//		assertThat(sequence.current().value()).isEqualTo("a");
//		verify(reporter).accept("a");
//
//		assertThat(sequence.next(count, reporter)).isFalse();
//		verifyNoMoreInteractions(reporter);
//	}


//	@Group
//	class Shrinking {
//
//		@Example
//		void downAllTheWay() {
//			NShrinkable<String> shrinkable = createShrinkableString("abc", 0);
//
//			ShrinkingSequence<String> sequence = shrinkable.shrink(aString -> false);
//
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(2);
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(1);
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(0);
//			assertThat(sequence.next(count, reporter)).isFalse();
//
//			assertThat(counter.get()).isEqualTo(3);
//		}
//
//		@Example
//		void downToMinSize() {
//			NShrinkable<String> shrinkable = createShrinkableString("aaaaa", 2);
//
//			ShrinkingSequence<String> sequence = shrinkable.shrink(aString -> false);
//
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(4);
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(3);
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(2);
//			assertThat(sequence.next(count, reporter)).isFalse();
//
//			assertThat(counter.get()).isEqualTo(3);
//		}
//
//		@Example
//		void downToNonEmpty() {
//			NShrinkable<String> shrinkable = createShrinkableString("abcd", 0);
//
//			ShrinkingSequence<String> sequence = shrinkable.shrink(String::isEmpty);
//
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(3);
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(2);
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(1);
//			assertThat(sequence.next(count, reporter)).isFalse();
//
//			assertThat(counter.get()).isEqualTo(3);
//		}
//
//		@Example
//		void alsoShrinkElements() {
//
//			NShrinkable<String> shrinkable = createShrinkableString("bbb", 0);
//
//			ShrinkingSequence<String> sequence = shrinkable.shrink(aString -> aString.length() <= 1);
//
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("bb");
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(2);
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value().length()).isEqualTo(2);
//			assertThat(sequence.next(count, reporter)).isFalse();
//			assertThat(sequence.current().value()).isEqualTo("aa");
//
//			assertThat(counter.get()).isEqualTo(3);
//		}
//
//		@Example
//		void withFilterOnStringLength() {
//			NShrinkable<String> shrinkable = createShrinkableString("cccc", 0);
//
//			Falsifier<String> falsifier = ignore -> false;
//			Falsifier<String> filteredFalsifier = falsifier.withFilter(aString -> aString.length() % 2 == 0);
//
//			ShrinkingSequence<String> sequence = shrinkable.shrink(filteredFalsifier);
//
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("cccc");
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("cc");
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("cc");
//			assertThat(sequence.next(count, reporter)).isTrue();
//			assertThat(sequence.current().value()).isEqualTo("");
//			assertThat(sequence.next(count, reporter)).isFalse();
//
//			assertThat(counter.get()).isEqualTo(4);
//		}
//
//		@Example
//		void withFilterOnStringContents() {
//			NShrinkable<String> shrinkable = createShrinkableString("ddd", 0);
//
//			Falsifier<String> falsifier = String::isEmpty;
//			Falsifier<String> filteredFalsifier = falsifier //
//				.withFilter(aString -> aString.startsWith("d") || aString.startsWith("b"));
//			ShrinkingSequence<String> sequence = shrinkable.shrink(filteredFalsifier);
//
//			while (sequence.next(count, reporter)) {
//			}
//			assertThat(sequence.current().value()).isEqualTo("b");
//
//			assertThat(counter.get()).isEqualTo(6);
//		}
//
//		@Example
//		void longString() {
//			List<NShrinkable<Character>> elementShrinkables =
//				IntStream.range(0, 1000) //
//						 .mapToObj(aChar -> new OneStepShrinkable(aChar, 0)) //
//						 .map(shrinkableInt -> shrinkableInt.map(anInt -> (char) (int) anInt)) //
//						 .collect(Collectors.toList());
//
//			NShrinkable<String> shrinkable = new ShrinkableString(elementShrinkables, 5);
//
//			ShrinkingSequence<String> sequence = shrinkable.shrink(String::isEmpty);
//
//			while (sequence.next(count, reporter)) {
//			}
//			assertThat(sequence.current().value()).hasSize(5);
//
//			assertThat(counter.get()).isEqualTo(21);
//		}
//
//	}


	private NShrinkable<BigInteger> createShrinkableBigInteger(long number, Range<Long> longRange) {
		Range<BigInteger> bigIntegerRange = longRange.map(BigInteger::valueOf);
		return new ShrinkableBigInteger(BigInteger.valueOf(number), bigIntegerRange);
	}

}
