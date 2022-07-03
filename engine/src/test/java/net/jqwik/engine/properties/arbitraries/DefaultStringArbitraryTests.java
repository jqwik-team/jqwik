package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.api.statistics.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.testing.TestingSupport.*;

@StatisticsReport(onFailureOnly = true)
class DefaultStringArbitraryTests implements GenericEdgeCasesProperties, GenericGenerationProperties {

	@Override
	public Arbitrary<Arbitrary<?>> arbitraries() {
		return Arbitraries.of(
			new DefaultStringArbitrary(),
			new DefaultStringArbitrary().ofMinLength(5).ofMaxLength(55),
			new DefaultStringArbitrary().withCharRange('a', 'z'),
			new DefaultStringArbitrary().whitespace().numeric().alpha()
		);
	}

	StringArbitrary arbitrary = new DefaultStringArbitrary();

	@Example
	void currentlyNoCodepointsAboveAllowedMaxAreCreated(@ForAll Random random) {
		assertAllGenerated(arbitrary.generator(10, true), random, s -> {
			for (int i = 0; i < s.length(); i++) {
				Assertions.assertThat(s.codePointAt(i)).isLessThanOrEqualTo(Character.MAX_CODE_POINT);
			}
		});
	}

	@Example
	void perDefaultNoNoncharactersAndNoPrivateUseCharactersAreCreated(@ForAll Random random, @ForAll int i) {
		checkAllGenerated(arbitrary.generator(10000, true), random, s -> {
			return s.chars().allMatch(c -> {
				if (DefaultCharacterArbitrary.isNoncharacter(c))
					return false;
				return !DefaultCharacterArbitrary.isPrivateUseCharacter(c);
			});
		});
	}

	@Example
	void allAlsoAllowsNoncharactersAndPrivateUseCharacters(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.all();
		checkAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c >= Character.MIN_VALUE && c <= Character.MAX_VALUE);
		});
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.chars().anyMatch(DefaultCharacterArbitrary::isNoncharacter)
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.chars().anyMatch(DefaultCharacterArbitrary::isPrivateUseCharacter)
		);
	}

	@Example
	void withCharRange(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withCharRange('\u0222', '\u0333');
		checkAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c >= '\u0222' && c <= '\u0333');
		});
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('\u0222'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('\u0333'))
		);
	}

	@Example
	void withTwoCharRanges(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withCharRange('\u0222', '\u0333').withCharRange('A', 'Z');
		checkAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> (c >= '\u0222' && c <= '\u0333') || (c >= 'A' && c <= 'Z'));
		});
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('\u0222'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('\u0333'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('A'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('Z'))
		);
	}

	@Example
	void withChars(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withChars('a', 'm', 'x');
		checkAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c == 'a' || c == 'm' || c == 'x');
		});
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('a'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('m'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('x'))
		);
	}

	@Example
	void withCharsFromCharSequence(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withChars("amx");
		checkAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c == 'a' || c == 'm' || c == 'x');
		});
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('a'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('m'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('x'))
		);
	}

	@Example
	void withCharsAndCharRange(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withCharRange('\u0222', '\u0333').withChars('a', 'm', 'x');
		checkAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> (c == 'a' || c == 'm' || c == 'x') || (c >= '\u0222' && c <= '\u0333'));
		});
	}

	@Example
	void lengthRange(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.ofMinLength(3).ofMaxLength(10);
		checkAllGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.length() >= 3 && s.length() <= 10
		);
	}

	@Example
	void ofLength(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.ofLength(17);
		checkAllGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.length() == 17
		);
	}

	@Example
	void withLengthDistribution(@ForAll Random random) {
		StringArbitrary arbitrary = this.arbitrary.ofMaxLength(100)
												  .withLengthDistribution(RandomDistribution.uniform());

		RandomGenerator<String> generator = arbitrary.generator(1, false);

		for (int i = 0; i < 10000; i++) {
			String string = generator.next(random).value();
			Statistics.collect(string.length());
		}

		Statistics.coverage(checker -> {
			for (int length = 0; length <= 100; length++) {
				checker.check(length).percentage(p -> p >= 0.4);
			}
		});

	}

	@Example
	void ascii(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.ascii();
		checkAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c <= DefaultCharacterArbitrary.MAX_ASCII_CODEPOINT);
		});
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString((char) DefaultCharacterArbitrary.MAX_ASCII_CODEPOINT))
		);
	}

	@Example
	void alpha(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.alpha();
		checkAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
		});
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('a'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('z'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('A'))
		);
		TestingSupport.checkAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('Z'))
		);
	}

	@Example
	void numeric(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.numeric();
		checkAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c >= '0' && c <= '9');
		});
	}

	@Example
	void whitespace(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.whitespace();

		RandomGenerator<String> generator = stringArbitrary.generator(10, true);
		for (char c : DefaultCharacterArbitrary.WHITESPACE_CHARS) {
			TestingSupport.checkAtLeastOneGenerated(generator, random, s -> s.contains(Character.toString(c)));
		}
	}

	@Example
	void excludeChars(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.numeric()
														.excludeChars('0', '9');

		assertAllGenerated(
			stringArbitrary.generator(10, true), random,
			s -> {
				Assertions.assertThat(s.chars()).allMatch(c -> c >= '1' && c <= '8');
			}
		);
	}

	@Group
	class Coverage {
		@Property
		void randomStringsShouldContainZeroChar(@ForAll @StringLength(min = 1, max = 20) String aString) {
			Statistics.label("contains 0")
					  .collect(aString.contains("\u0000"))
					  .coverage(checker -> checker.check(true).count(c -> c > 10));
			Statistics.label("0 at last position")
					  .collect(aString.charAt(aString.length() - 1) == '\u0000')
					  .coverage(checker -> checker.check(true).count(c -> c > 10));
		}

		@Property(edgeCases = EdgeCasesMode.NONE)
		void evenLongStringsShouldSometimesGenerateNoDuplicates(@ForAll @StringLength(500) String aString) {
			Statistics.label("duplicates")
					  .collect(hasDuplicate(aString))
					  .coverage(checker -> checker.check(false).percentage(p -> p >= 4));

			Statistics.label("repetition")
					  .collect(hasRepetition(aString));
		}

		@Property(edgeCases = EdgeCasesMode.NONE)
		void generateNoDuplicatesWorksIfPossibleCharsAreLessThanMaxStringLength(
			@ForAll @CharRange(from = 'a', to = 'z') @StringLength(max = 500) String aString
		) {
			Statistics.label("duplicates")
					  .collect(hasDuplicate(aString))
					  .coverage(checker -> checker.check(false).percentage(p -> p >= 4));

			Statistics.label("repetition")
					  .collect(hasRepetition(aString));
		}

		private boolean hasDuplicate(String aString) {
			return aString.chars().distinct().count() != aString.length();
		}

		private boolean hasRepetition(String aString) {
			for (int i = 0; i < aString.length(); i++) {
				if (i > 0) {
					char current = aString.charAt(i);
					char previous = aString.charAt(i - 1);
					if (current == previous) {
						return true;
					}
				}
			}
			return false;
		}

		@Property(generation = GenerationMode.RANDOMIZED, edgeCases = EdgeCasesMode.NONE)
		void stringFromSeveralCharacterGroups_HaveRandomDistributionBySize(@ForAll("a to z, 1 to 3") String aString) {
			char onlyChar = aString.charAt(0);

			// The distribution should be 9:1 (27:3)

			Statistics.label("a to z")
					  .collect(onlyChar >= 'a' && onlyChar <= 'z')
					  .coverage(coverage -> coverage.check(true).percentage(p -> p > 80));

			Statistics.label("1 to 3")
					  .collect(onlyChar >= '1' && onlyChar <= '3')
					  .coverage(coverage -> coverage.check(true).percentage(p -> p < 20));
		}

		@Provide("a to z, 1 to 3")
		Arbitrary<String> aToZand1to3() {
			return new DefaultStringArbitrary()
				.withCharRange('a', 'z')
				.withCharRange('1', '3')
				.ofLength(1);
		}

		@Property(tries = 2000, edgeCases = EdgeCasesMode.NONE)
		void randomStringsWithRepeatedCharsShouldGenerateDuplicatesAndRepetitions(
			@ForAll("withRepeatedChars") @StringLength(min = 10, max = 20) String aString
		) {
			Statistics.label("duplicates")
					  .collect(hasDuplicate(aString))
					  .coverage(checker -> checker.check(true).percentage(p -> p > 10));
			Statistics.label("repetition")
					  .collect(hasRepetition(aString))
					  .coverage(checker -> checker.check(true).percentage(p -> p > 1));
		}

		@Provide
		Arbitrary<String> withRepeatedChars() {
			return Arbitraries.strings().repeatChars(0.01);
		}

	}

	@Group
	@PropertyDefaults(tries = 100)
	class InvalidValues {

		@Property
		void minLengthOutOfRange(@ForAll int minLength) {
			Assume.that(minLength < 0);
			assertThatThrownBy(
				() -> Arbitraries.strings().ofMinLength(minLength)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void maxLengthOutOfRange(@ForAll int maxLength) {
			Assume.that(maxLength < 0);
			assertThatThrownBy(
				() -> Arbitraries.strings().ofMaxLength(maxLength)
			).isInstanceOf(IllegalArgumentException.class);
		}

		@Property
		void minLargerThanMax(@ForAll @IntRange(max = 2147483647) int minLength, @IntRange(min = 1) @ForAll int maxLength) {
			Assume.that(maxLength < minLength);
			assertThatThrownBy(
				() -> Arbitraries.strings().ofMinLength(minLength).ofMaxLength(maxLength)
			).isInstanceOf(IllegalArgumentException.class);
		}
	}
}
