package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.properties.shrinking.ShrinkableTypesForTest.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.api.NEW_ShrinkingTestHelper.*;

@Group
@Label("ShrinkableString")
public class NEW_ShrinkableStringTests {

	@Example
	void creation() {
		Shrinkable<String> shrinkable = createShrinkableString("abcd", 0);
		assertThat(shrinkable.distance()).isEqualTo(ShrinkingDistance.of(4, 6));
		assertThat(shrinkable.createValue()).isEqualTo("abcd");
	}

	@Group
	class Shrinking {

		@Example
		void downAllTheWay() {
			Shrinkable<String> shrinkable = createShrinkableString("abc", 0);
			String shrunkValue = shrinkToMinimal(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEmpty();
		}

		@Example
		void downToMinSize() {
			Shrinkable<String> shrinkable = createShrinkableString("aaaaa", 2);
			String shrunkValue = shrinkToMinimal(shrinkable, alwaysFalsify(), null);
			assertThat(shrunkValue).isEqualTo("aa");
		}

		@Example
		void downToNonEmpty() {
			Shrinkable<String> shrinkable = createShrinkableString("abcd", 0);
			String shrunkValue = shrinkToMinimal(shrinkable, falsifier(String::isEmpty), null);
			assertThat(shrunkValue).isEqualTo("a");
		}

		@Example
		void alsoShrinkCharacters() {
			Shrinkable<String> shrinkable = createShrinkableString("bbb", 0);
			TestingFalsifier<String> falsifier = aString -> aString.length() <= 1;
			String shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo("aa");
		}

		@Example
		void withFilterOnStringLength() {
			Shrinkable<String> shrinkable = createShrinkableString("cccc", 0);

			TestingFalsifier<String> falsifier = ignore -> false;
			Falsifier<String> filteredFalsifier = falsifier.withFilter(aString -> aString.length() % 2 == 0);

			String shrunkValue = shrinkToMinimal(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo("");
		}

		@Example
		void withFilterOnStringContents() {
			Shrinkable<String> shrinkable = createShrinkableString("ddd", 0);

			TestingFalsifier<String> falsifier = ignore -> false;
			Falsifier<String> filteredFalsifier =
				falsifier.withFilter(aString -> aString.startsWith("d") || aString.startsWith("b"));

			String shrunkValue = shrinkToMinimal(shrinkable, filteredFalsifier, null);
			assertThat(shrunkValue).isEqualTo("b");
		}

		@Example
		void shrinkCharacterPairsTogether() {
			Shrinkable<String> shrinkable = createShrinkableString("xxxx", 2);

			TestingFalsifier<String> falsifier =
				string -> {
					Set<Integer> usedLetters = string.chars().boxed().collect(Collectors.toSet());
					return usedLetters.size() != 1;
				};

			String shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
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

			String shrunkValue = shrinkToMinimal(shrinkable, falsifier, null);
			assertThat(shrunkValue).isEqualTo("abcd");
		}

		@Example
		void longString() {
			List<Shrinkable<Character>> elementShrinkables =
				IntStream.range(0, 1000)
						 .mapToObj(aChar -> new OneStepShrinkable(aChar, 0))
						 .map(shrinkableInt -> shrinkableInt.map(anInt -> (char) (int) anInt))
						 .collect(Collectors.toList());

			Shrinkable<String> shrinkable = new ShrinkableString(elementShrinkables, 5);
			String shrunkValue = shrinkToMinimal(shrinkable, (TestingFalsifier<String>) String::isEmpty, null);
			assertThat(shrunkValue).hasSize(5);
		}

	}

	public static Shrinkable<String> createShrinkableString(String aString, int minSize) {
		List<Shrinkable<Character>> elementShrinkables =
			aString
				.chars()
				.mapToObj(aChar -> new OneStepShrinkable(aChar, 'a'))
				.map(shrinkable -> shrinkable.map(anInt -> (char) (int) anInt))
				.collect(Collectors.toList());

		return new ShrinkableString(elementShrinkables, minSize);
	}

}
