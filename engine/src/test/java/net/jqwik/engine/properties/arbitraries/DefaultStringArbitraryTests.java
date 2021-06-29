package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.edgeCases.*;
import net.jqwik.api.statistics.*;

import static net.jqwik.testing.TestingSupport.*;

class DefaultStringArbitraryTests implements GenericEdgeCasesProperties {

	@Override
	public Arbitrary<Arbitrary<?>> arbitraries() {
		return Arbitraries.of(arbitrary);
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
		assertAllGenerated(arbitrary.generator(10000, true), random, s -> {
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
		assertAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c >= Character.MIN_VALUE && c <= Character.MAX_VALUE);
		});
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.chars().anyMatch(DefaultCharacterArbitrary::isNoncharacter)
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.chars().anyMatch(DefaultCharacterArbitrary::isPrivateUseCharacter)
		);
	}

	@Example
	void withCharRange(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withCharRange('\u0222', '\u0333');
		assertAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c >= '\u0222' && c <= '\u0333');
		});
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('\u0222'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('\u0333'))
		);
	}

	@Example
	void withTwoCharRanges(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withCharRange('\u0222', '\u0333').withCharRange('A', 'Z');
		assertAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> (c >= '\u0222' && c <= '\u0333') || (c >= 'A' && c <= 'Z'));
		});
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('\u0222'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('\u0333'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('A'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('Z'))
		);
	}

	@Example
	void withChars(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withChars('a', 'm', 'x');
		assertAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c == 'a' || c == 'm' || c == 'x');
		});
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('a'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('m'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('x'))
		);
	}

	@Example
	void withCharsFromCharSequence(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withChars("amx");
		assertAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c == 'a' || c == 'm' || c == 'x');
		});
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('a'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('m'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('x'))
		);
	}

	@Example
	void withCharsAndCharRange(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.withCharRange('\u0222', '\u0333').withChars('a', 'm', 'x');
		assertAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> (c == 'a' || c == 'm' || c == 'x') || (c >= '\u0222' && c <= '\u0333'));
		});
	}

	@Example
	void lengthRange(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.ofMinLength(3).ofMaxLength(10);
		assertAllGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.length() >= 3 && s.length() <= 10
		);
	}

	@Example
	void ofLength(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.ofLength(17);
		assertAllGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.length() == 17
		);
	}

	@Example
	void ascii(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.ascii();
		assertAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c <= DefaultCharacterArbitrary.MAX_ASCII_CODEPOINT);
		});
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString((char) DefaultCharacterArbitrary.MAX_ASCII_CODEPOINT))
		);
	}

	@Example
	void alpha(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.alpha();
		assertAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
		});
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('a'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('z'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('A'))
		);
		assertAtLeastOneGenerated(
			stringArbitrary.generator(10, true), random,
			s -> s.contains(Character.toString('Z'))
		);
	}

	@Example
	void numeric(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.numeric();
		assertAllGenerated(stringArbitrary.generator(10, true), random, s -> {
			return s.chars().allMatch(c -> c >= '0' && c <= '9');
		});
	}

	@Example
	void whitespace(@ForAll Random random) {
		StringArbitrary stringArbitrary = this.arbitrary.whitespace();

		RandomGenerator<String> generator = stringArbitrary.generator(10, true);
		for (char c : DefaultCharacterArbitrary.WHITESPACE_CHARS) {
			assertAtLeastOneGenerated(generator, random, s -> s.contains(Character.toString(c)));
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

		@Property(tries = 2000, edgeCases = EdgeCasesMode.NONE)
		void randomStringsShouldSometimesGenerateDuplicatesAndRepetitions(@ForAll @StringLength(min = 10, max = 20) String aString) {
			Statistics.label("duplicates")
					  .collect(hasDuplicate(aString))
					  .coverage(checker -> checker.check(true).percentage(p -> p > 10));
			Statistics.label("repetition")
					  .collect(hasRepetition(aString))
					  .coverage(checker -> checker.check(true).count(p -> p > 5));
		}

		@Property(edgeCases = EdgeCasesMode.NONE)
		void evenLongStringsShouldSometimesGenerateNoDuplicates(@ForAll @StringLength(500) String aString) {
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

	}

}
