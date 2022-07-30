package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.api.support.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

import static java.util.Arrays.*;

public class DefaultStringArbitrary extends TypedCloneable implements StringArbitrary {

	private CharacterArbitrary characterArbitrary = new DefaultCharacterArbitrary();

	private int minLength = 0;
	private Integer maxLength = null;
	private Set<Character> excludedChars = new LinkedHashSet<>();
	private RandomDistribution lengthDistribution = null;
	private double repeatChars = 0.0;

	@Override
	public RandomGenerator<String> generator(int genSize) {
		long maxUniqueChars = characterArbitrary.exhaustive(maxLength()).map(ExhaustiveGenerator::maxCount).orElse((long) maxLength());
		return RandomGenerators.strings(randomCharacterGenerator(), minLength, maxLength(), maxUniqueChars, genSize, lengthDistribution);
	}

	private int maxLength() {
		return RandomGenerators.collectionMaxSize(minLength, maxLength);
	}

	@Override
	public Optional<ExhaustiveGenerator<String>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.strings(
			effectiveCharacterArbitrary(),
			minLength,
			maxLength(),
			maxNumberOfSamples
		);
	}

	@Override
	public EdgeCases<String> edgeCases(int maxEdgeCases) {
		// Optimization. Already handled by EdgeCases.concat(..)
		if (maxEdgeCases <= 0) {
			return EdgeCases.none();
		}

		EdgeCases<String> emptyStringEdgeCases =
			hasEmptyStringEdgeCase() ? emptyStringEdgeCase() : EdgeCases.none();

		int effectiveMaxEdgeCases = maxEdgeCases - emptyStringEdgeCases.size();
		EdgeCases<String> singleCharEdgeCases =
			hasSingleCharEdgeCases() ? fixedSizedEdgeCases(1, effectiveMaxEdgeCases) : EdgeCases.none();

		effectiveMaxEdgeCases = effectiveMaxEdgeCases - singleCharEdgeCases.size();
		EdgeCases<String> fixedSizeEdgeCases =
			hasMultiCharEdgeCases() ? fixedSizedEdgeCases(minLength, effectiveMaxEdgeCases) : EdgeCases.none();

		return EdgeCasesSupport.concat(asList(singleCharEdgeCases, emptyStringEdgeCases, fixedSizeEdgeCases), maxEdgeCases);
	}

	private boolean hasEmptyStringEdgeCase() {
		return minLength <= 0;
	}

	private boolean hasMultiCharEdgeCases() {
		return minLength <= maxLength() && minLength > 1;
	}

	private boolean hasSingleCharEdgeCases() {
		return minLength <= 1 && maxLength() >= 1;
	}

	private EdgeCases<String> emptyStringEdgeCase() {
		return EdgeCases.fromSupplier(() -> new ShrinkableString(Collections.emptyList(), minLength, maxLength()));
	}

	private EdgeCases<String> fixedSizedEdgeCases(int fixedSize, int maxEdgeCases) {
		return EdgeCasesSupport.mapShrinkable(
			effectiveCharacterArbitrary().edgeCases(maxEdgeCases),
			shrinkableChar -> {
				List<Shrinkable<Character>> shrinkableChars = new ArrayList<>(Collections.nCopies(fixedSize, shrinkableChar));
				return new ShrinkableString(shrinkableChars, minLength, maxLength());
			}
		);
	}

	@Override
	public StringArbitrary ofMinLength(int minLength) {
		if (minLength < 0) {
			String message = String.format("minLength (%s) must be between 0 and 2147483647", minLength);
			throw new IllegalArgumentException(message);
		}
		DefaultStringArbitrary clone = typedClone();
		clone.minLength = minLength;
		return clone;
	}

	@Override
	public StringArbitrary ofMaxLength(int maxLength) {
		if (maxLength < 0) {
			String message = String.format("maxLength (%s) must be between 0 and 2147483647", maxLength);
			throw new IllegalArgumentException(message);
		}
		if (maxLength < minLength) {
			String message = String.format("minLength (%s) must not be larger than maxLength (%s)", minLength, maxLength);
			throw new IllegalArgumentException(message);
		}

		DefaultStringArbitrary clone = typedClone();
		clone.maxLength = maxLength;
		return clone;
	}

	@Override
	public StringArbitrary withLengthDistribution(RandomDistribution distribution) {
		DefaultStringArbitrary clone = typedClone();
		clone.lengthDistribution = distribution;
		return clone;
	}

	@Override
	public StringArbitrary repeatChars(double repeatProbability) {
		if (repeatProbability < 0 || repeatProbability >= 1) {
			throw new IllegalArgumentException("repeatProbability must be between 0 (included) and 1 (excluded)");
		}
		DefaultStringArbitrary clone = typedClone();
		clone.repeatChars = repeatProbability;
		return clone;
	}

	@Override
	public StringArbitrary withChars(char... chars) {
		DefaultStringArbitrary clone = typedClone();
		clone.characterArbitrary = clone.characterArbitrary.with(chars);
		return clone;
	}

	@Override
	public StringArbitrary withChars(CharSequence chars) {
		DefaultStringArbitrary clone = typedClone();
		clone.characterArbitrary = clone.characterArbitrary.with(chars);
		return clone;
	}

	@Override
	public StringArbitrary withCharRange(char from, char to) {
		DefaultStringArbitrary clone = typedClone();
		clone.characterArbitrary = clone.characterArbitrary.range(from, to);
		return clone;
	}

	@Override
	public StringArbitrary ascii() {
		DefaultStringArbitrary clone = typedClone();
		clone.characterArbitrary = characterArbitrary.ascii();
		return clone;
	}

	@Override
	public StringArbitrary alpha() {
		DefaultStringArbitrary clone = typedClone();
		clone.characterArbitrary = clone.characterArbitrary.alpha();
		return clone;
	}

	@Override
	public StringArbitrary numeric() {
		DefaultStringArbitrary clone = typedClone();
		clone.characterArbitrary = clone.characterArbitrary.numeric();
		return clone;
	}

	@Override
	public StringArbitrary whitespace() {
		DefaultStringArbitrary clone = typedClone();
		clone.characterArbitrary = clone.characterArbitrary.whitespace();
		return clone;
	}

	@Override
	public StringArbitrary all() {
		return this.withCharRange(Character.MIN_VALUE, Character.MAX_VALUE);
	}

	@Override
	public StringArbitrary excludeChars(char... charsToExclude) {
		DefaultStringArbitrary clone = typedClone();
		Set<Character> excludedChars = new LinkedHashSet<>(this.excludedChars);
		for (char c : charsToExclude) {
			excludedChars.add(c);
		}
		clone.excludedChars = excludedChars;
		return clone;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultStringArbitrary that = (DefaultStringArbitrary) o;
		if (minLength != that.minLength) return false;
		if (!Objects.equals(maxLength, that.maxLength)) return false;
		if (Double.compare(that.repeatChars, repeatChars) != 0) return false;
		if (!characterArbitrary.equals(that.characterArbitrary)) return false;
		if (!excludedChars.equals(that.excludedChars)) return false;
		return Objects.equals(lengthDistribution, that.lengthDistribution);
	}

	@Override
	public int hashCode() {
		return HashCodeSupport.hash(characterArbitrary, minLength, maxLength, repeatChars, excludedChars, lengthDistribution);
	}

	private RandomGenerator<Character> randomCharacterGenerator() {
		RandomGenerator<Character> characterGenerator = effectiveCharacterArbitrary().generator(1, false);
		if (repeatChars > 0) {
			return characterGenerator.injectDuplicates(repeatChars);
		} else {
			return characterGenerator;
		}
	}

	private Arbitrary<Character> effectiveCharacterArbitrary() {
		Arbitrary<Character> characterArbitrary = this.characterArbitrary;
		if (!excludedChars.isEmpty()) {
			characterArbitrary = characterArbitrary.filter(c -> !excludedChars.contains(c));
		}
		return characterArbitrary;
	}

}
