package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.engine.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Group
@Label("ShrinkableString")
public class ShrinkableStringTests {

	private AtomicInteger counter = new AtomicInteger(0);
	private Runnable count = counter::incrementAndGet;

	@SuppressWarnings("unchecked")
	private Consumer<String> valueReporter = mock(Consumer.class);
	private Consumer<FalsificationResult<String>> reporter = result -> valueReporter.accept(result.value());

	@Example
	void creation() {
		Shrinkable<String> shrinkable = createShrinkableString("abcd", 0);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.value()).isEqualTo("abcd");
	}


	@Example
	@Label("report all falsified on the way")
	void reportFalsified() {
		Shrinkable<String> shrinkable = createShrinkableString("bcd", 0);

		ShrinkingSequence<String> sequence = shrinkable.shrink(String::isEmpty);

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("bc");
		verify(valueReporter).accept("bc");

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("b");
		verify(valueReporter).accept("b");

		assertThat(sequence.next(count, reporter)).isTrue();
		assertThat(sequence.current().value()).isEqualTo("a");
		verify(valueReporter).accept("a");

		assertThat(sequence.next(count, reporter)).isFalse();
		verifyNoMoreInteractions(valueReporter);
	}


	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<String> shrinkable = createShrinkableString("abc", 0);

			ShrinkingSequence<String> sequence = shrinkable.shrink(aString -> false);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(1);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(0);
			assertThat(sequence.next(count, reporter)).isFalse();

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void downToMinSize() {
			Shrinkable<String> shrinkable = createShrinkableString("aaaaa", 2);

			ShrinkingSequence<String> sequence = shrinkable.shrink(aString -> false);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(4);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(3);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isFalse();

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void downToNonEmpty() {
			Shrinkable<String> shrinkable = createShrinkableString("abcd", 0);

			ShrinkingSequence<String> sequence = shrinkable.shrink(String::isEmpty);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(3);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(1);
			assertThat(sequence.next(count, reporter)).isFalse();

			assertThat(counter.get()).isEqualTo(3);
		}

		@Example
		void alsoShrinkElements() {

			Shrinkable<String> shrinkable = createShrinkableString("bbb", 0);

			ShrinkingSequence<String> sequence = shrinkable.shrink(aString -> aString.length() <= 1);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo("bb");
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value().length()).isEqualTo(2);
			assertThat(sequence.next(count, reporter)).isFalse();
			assertThat(sequence.current().value()).isEqualTo("aa");

			assertThat(counter.get()).isEqualTo(2);
		}

		@Example
		void shrinkingResultHasValueAndThrowable() {
			Shrinkable<String> shrinkable = createShrinkableString("bbb", 0);


			ShrinkingSequence<String> sequence = shrinkable.shrink(string -> {
				if (string.length() > 1) throw new IllegalArgumentException("my reason");
				return true;
			});

			while(sequence.next(count, reporter)) {}

			assertThat(sequence.current().value()).isEqualTo("aa");
			assertThat(sequence.current().throwable()).isPresent();
			assertThat(sequence.current().throwable().get()).isInstanceOf(IllegalArgumentException.class);
			assertThat(sequence.current().throwable().get()).hasMessage("my reason");
		}


		@Example
		void withFilterOnStringLength() {
			Shrinkable<String> shrinkable = createShrinkableString("cccc", 0);

			Falsifier<String> falsifier = ignore -> false;
			Falsifier<String> filteredFalsifier = falsifier.withFilter(aString -> aString.length() % 2 == 0);

			ShrinkingSequence<String> sequence = shrinkable.shrink(filteredFalsifier);

			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo("cccc");
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo("cc");
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo("cc");
			assertThat(sequence.next(count, reporter)).isTrue();
			assertThat(sequence.current().value()).isEqualTo("");
			assertThat(sequence.next(count, reporter)).isFalse();

			assertThat(counter.get()).isEqualTo(4);
		}

		@Example
		void withFilterOnStringContents() {
			Shrinkable<String> shrinkable = createShrinkableString("ddd", 0);

			Falsifier<String> falsifier = String::isEmpty;
			Falsifier<String> filteredFalsifier = falsifier //
				.withFilter(aString -> aString.startsWith("d") || aString.startsWith("b"));
			ShrinkingSequence<String> sequence = shrinkable.shrink(filteredFalsifier);

			while (sequence.next(count, reporter));
			assertThat(sequence.current().value()).isEqualTo("b");

			assertThat(counter.get()).isEqualTo(7);
		}

		@Example
		void longString() {
			List<Shrinkable<Character>> elementShrinkables =
				IntStream.range(0, 1000) //
						 .mapToObj(aChar -> new OneStepShrinkable(aChar, 0)) //
						 .map(shrinkableInt -> shrinkableInt.map(anInt -> (char) (int) anInt)) //
						 .collect(Collectors.toList());

			Shrinkable<String> shrinkable = new ShrinkableString(elementShrinkables, 5);

			ShrinkingSequence<String> sequence = shrinkable.shrink(String::isEmpty);

			while (sequence.next(count, reporter));
			assertThat(sequence.current().value()).hasSize(5);

			assertThat(counter.get()).isEqualTo(21);
		}

	}


	public static Shrinkable<String> createShrinkableString(String aString, int minSize) {
		List<Shrinkable<Character>> elementShrinkables = aString //
																 .chars() //
																 .mapToObj(aChar -> new OneStepShrinkable(aChar, 'a')) //
																 .map(shrinkable -> shrinkable.map(anInt -> (char) (int) anInt)) //
																 .collect(Collectors.toList());

		return new ShrinkableString(elementShrinkables, minSize);
	}

}
