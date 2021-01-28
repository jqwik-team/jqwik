package net.jqwik.engine.properties.arbitraries;

import org.assertj.core.api.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.constraints.*;
import net.jqwik.api.statistics.*;

import static net.jqwik.api.ArbitraryTestHelper.*;

class DefaultStringArbitraryTests {

	StringArbitrary arbitrary = new DefaultStringArbitrary();

	@Example
	void currentlyNoCodepointsAboveAllowedMaxAreCreated() {
		assertAllGenerated(arbitrary.generator(10, true), s -> {
			for (int i = 0; i < s.length(); i++) {
				Assertions.assertThat(s.codePointAt(i)).isLessThanOrEqualTo(Character.MAX_CODE_POINT);
			}
		});
	}

	@Example
	void perDefaultNoNoncharactersAndNoPrivateUseCharactersAreCreated(@ForAll int i) {
		assertAllGenerated(arbitrary.generator(10000, true), s -> {
			return s.chars().allMatch(c -> {
				if (DefaultCharacterArbitrary.isNoncharacter(c))
					return false;
				return !DefaultCharacterArbitrary.isPrivateUseCharacter(c);
			});
		});
	}

	@Example
	void allAlsoAllowsNoncharactersAndPrivateUseCharacters() {
		StringArbitrary stringArbitrary = this.arbitrary.all();
		assertAllGenerated(stringArbitrary.generator(10, true), s -> {
			return s.chars().allMatch(c -> c >= Character.MIN_VALUE && c <= Character.MAX_VALUE);
		});
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.chars().anyMatch(DefaultCharacterArbitrary::isNoncharacter));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.chars().anyMatch(DefaultCharacterArbitrary::isPrivateUseCharacter));
	}

	@Example
	void withCharRange() {
		StringArbitrary stringArbitrary = this.arbitrary.withCharRange('\u0222', '\u0333');
		assertAllGenerated(stringArbitrary.generator(10, true), s -> {
			return s.chars().allMatch(c -> c >= '\u0222' && c <= '\u0333');
		});
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('\u0222')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('\u0333')));
	}

	@Example
	void withTwoCharRanges() {
		StringArbitrary stringArbitrary = this.arbitrary.withCharRange('\u0222', '\u0333').withCharRange('A', 'Z');
		assertAllGenerated(stringArbitrary.generator(10, true), s -> {
			return s.chars().allMatch(c -> (c >= '\u0222' && c <= '\u0333') || (c >= 'A' && c <= 'Z'));
		});
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('\u0222')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('\u0333')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('A')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('Z')));
	}

	@Example
	void withChars() {
		StringArbitrary stringArbitrary = this.arbitrary.withChars('a', 'm', 'x');
		assertAllGenerated(stringArbitrary.generator(10, true), s -> {
			return s.chars().allMatch(c -> c == 'a' || c == 'm' || c == 'x');
		});
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('a')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('m')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('x')));
	}

	@Example
	void withCharsFromCharSequence() {
		StringArbitrary stringArbitrary = this.arbitrary.withChars("amx");
		assertAllGenerated(stringArbitrary.generator(10, true), s -> {
			return s.chars().allMatch(c -> c == 'a' || c == 'm' || c == 'x');
		});
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('a')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('m')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('x')));
	}

	@Example
	void withCharsAndCharRange() {
		StringArbitrary stringArbitrary = this.arbitrary.withCharRange('\u0222', '\u0333').withChars('a', 'm', 'x');
		assertAllGenerated(stringArbitrary.generator(10, true), s -> {
			return s.chars().allMatch(c -> (c == 'a' || c == 'm' || c == 'x') || (c >= '\u0222' && c <= '\u0333'));
		});
	}

	@Example
	void lengthRange() {
		StringArbitrary stringArbitrary = this.arbitrary.ofMinLength(3).ofMaxLength(10);
		assertAllGenerated(stringArbitrary.generator(10, true), s -> s.length() >= 3 && s.length() <= 10);
	}

	@Example
	void ofLength() {
		StringArbitrary stringArbitrary = this.arbitrary.ofLength(17);
		assertAllGenerated(stringArbitrary.generator(10, true), s -> s.length() == 17);
	}

	@Example
	void ascii() {
		StringArbitrary stringArbitrary = this.arbitrary.ascii();
		assertAllGenerated(stringArbitrary.generator(10, true), s -> {
			return s.chars().allMatch(c -> c <= DefaultCharacterArbitrary.MAX_ASCII_CODEPOINT);
		});
		assertAtLeastOneGenerated(
				stringArbitrary.generator(10, true),
				s -> s.contains(Character.toString((char) DefaultCharacterArbitrary.MAX_ASCII_CODEPOINT))
		);
	}

	@Example
	void alpha() {
		StringArbitrary stringArbitrary = this.arbitrary.alpha();
		assertAllGenerated(stringArbitrary.generator(10, true), s -> {
			return s.chars().allMatch(c -> (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
		});
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('a')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('z')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('A')));
		assertAtLeastOneGenerated(stringArbitrary.generator(10, true), s -> s.contains(Character.toString('Z')));
	}

	@Example
	void numeric() {
		StringArbitrary stringArbitrary = this.arbitrary.numeric();
		assertAllGenerated(stringArbitrary.generator(10, true), s -> {
			return s.chars().allMatch(c -> c >= '0' && c <= '9');
		});
	}

	@Example
	void whitespace() {
		StringArbitrary stringArbitrary = this.arbitrary.whitespace();

		RandomGenerator<String> generator = stringArbitrary.generator(10, true);
		for (char c : DefaultCharacterArbitrary.WHITESPACE_CHARS) {
			assertAtLeastOneGenerated(generator, s -> s.contains(Character.toString(c)));
		}
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
