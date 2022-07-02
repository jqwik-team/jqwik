package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;
import net.jqwik.engine.support.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.ShrinkingSupport.*;
import static net.jqwik.testing.TestingFalsifier.*;

@Group
@Label("ShrinkableString")
public class ShrinkableStringTests {

	@Example
	void creation() {
		Shrinkable<String> shrinkable = createShrinkableString("abcd", 0);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.value()).isEqualTo("abcd");
	}

	@Group
	class Shrinking {

		@Property(tries = 100)
		void longStrings(@ForAll @CharRange(from = 'a', to = 'z') @StringLength(min = 1000, max = Short.MAX_VALUE) String any) {
			Shrinkable<String> shrinkable = createShrinkableString(any, 1);
			String shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo("a");
		}

		@Example
		void downAllTheWay() {
			Shrinkable<String> shrinkable = createShrinkableString("abc", 0);
			String shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEmpty();
		}

		@Example
		void downToMinSize() {
			Shrinkable<String> shrinkable = createShrinkableString("aaaaa", 2);
			String shrunkValue = shrink(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo("aa");
		}

		@Example
		void downToNonEmpty() {
			Shrinkable<String> shrinkable = createShrinkableString("abcd", 0);
			String shrunkValue = shrink(shrinkable, falsifier(String::isEmpty), null);
			assertThat(shrunkValue).isEqualTo("a");
		}

		@Example
		void alsoShrinkCharacters() {
			Shrinkable<String> shrinkable = createShrinkableString("bbb", 0);
			TestingFalsifier<String> falsifier = aString -> aString.length() <= 1;
			String shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo("aa");
		}

		@Example
		void withFilterOnStringLength() {
			Shrinkable<String> shrinkable = createShrinkableString("cccc", 0);

			TestingFalsifier<String> falsifier = ignore -> false;
			Falsifier<String> filteredFalsifier = falsifier.withFilter(aString -> aString.length() % 2 == 0);

			String shrunkValue = shrink(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo("");
		}

		@Example
		void withFilterOnStringContents() {
			Shrinkable<String> shrinkable = createShrinkableString("ddd", 0);

			TestingFalsifier<String> falsifier = ignore -> false;
			Falsifier<String> filteredFalsifier =
					falsifier.withFilter(aString -> aString.startsWith("d") || aString.startsWith("b"));

			String shrunkValue = shrink(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo("b");
		}

		@Example
		void shrinkCharacterPairsTogether() {
			Shrinkable<String> shrinkable = createShrinkableString("xxxx", 2);

			TestingFalsifier<String> falsifier =
					string -> {
						Set<Integer> usedLetters = string.chars().boxed().collect(CollectorsSupport.toLinkedHashSet());
						return usedLetters.size() != 1;
					};

			String shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo("aa");
		}

		@Example
		void shrinkToSortedString() {
			Shrinkable<String> shrinkable = createShrinkableString("cdab", 4);

			TestingFalsifier<String> falsifier =
					string -> {
						int sum = string.chars().map(c -> c - 'a').sum();
						return sum < 6;
					};

			String shrunkValue = shrink(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo("abcd");
		}

		@Example
		void longString() {
			List<Shrinkable<Character>> elementShrinkables =
					IntStream.range(0, 1000)
							 .mapToObj(aChar -> new OneStepShrinkable(aChar, 0, 100))
							 .map(shrinkableInt -> shrinkableInt.map(anInt -> (char) (int) anInt))
							 .collect(Collectors.toList());

			Shrinkable<String> shrinkable = new ShrinkableString(elementShrinkables, 5, 1000);
			String shrunkValue = shrink(shrinkable, (TestingFalsifier<String>) String::isEmpty, null);
			assertThat(shrunkValue).hasSize(5);
		}

	}

	public static Shrinkable<String> createShrinkableString(String aString, int minSize) {
		List<Shrinkable<Character>> elementShrinkables =
				aString
						.chars()
						.mapToObj(aChar -> new OneStepShrinkable(aChar, 'a', 'z'))
						.map(shrinkable -> shrinkable.map(anInt -> (char) (int) anInt))
						.collect(Collectors.toList());

		return new ShrinkableString(elementShrinkables, minSize, aString.length());
	}

}
