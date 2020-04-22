package net.jqwik.engine.properties.arbitraries;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.engine.properties.arbitraries.exhaustive.*;
import net.jqwik.engine.properties.arbitraries.randomized.*;
import net.jqwik.engine.properties.shrinking.*;

public class DefaultStringArbitrary extends AbstractArbitraryBase implements StringArbitrary {

	private CharacterArbitrary characterArbitrary = new DefaultCharacterArbitrary();

	private int minLength = 0;
	private int maxLength = RandomGenerators.DEFAULT_COLLECTION_SIZE;

	@Override
	public RandomGenerator<String> generator(int genSize) {
		final int cutoffLength = RandomGenerators.defaultCutoffSize(minLength, maxLength, genSize);
		return RandomGenerators.strings(randomCharacterGenerator(), minLength, maxLength, cutoffLength).withEdgeCases(genSize, edgeCases());
	}

	@Override
	public Optional<ExhaustiveGenerator<String>> exhaustive(long maxNumberOfSamples) {
		return ExhaustiveGenerators.strings(
			effectiveCharacterArbitrary(),
			minLength,
			maxLength,
			maxNumberOfSamples
		);
	}

	@Override
	public EdgeCases<String> edgeCases() {

		EdgeCases<String> emptyStringEdgeCases =
			minLength != 0 ? EdgeCases.none()
				: EdgeCases.fromSupplier(() -> Shrinkable.unshrinkable(""));
		EdgeCases<String> singleCharEdgeCases =
			minLength > 1 ? EdgeCases.none() : fixedSizedEdgeCases(1);

		// TODO: What if character arbitrary is unique? Can that happen?
		EdgeCases<String> fixedSizeEdgeCases =
			minLength == maxLength && minLength > 1 ? fixedSizedEdgeCases(minLength) : EdgeCases.none();

		return EdgeCases.concat(singleCharEdgeCases, emptyStringEdgeCases, fixedSizeEdgeCases);
	}

	protected EdgeCases<String> fixedSizedEdgeCases(int fixedSize) {
		return effectiveCharacterArbitrary()
				   .edgeCases()
				   .mapShrinkable(shrinkableChar -> {
					   List<Shrinkable<Character>> chars = new ArrayList<>(Collections.nCopies(fixedSize, shrinkableChar));
					   return new ShrinkableString(chars, minLength);
				   });
	}

	@Override
	public StringArbitrary ofMinLength(int minLength) {
		DefaultStringArbitrary clone = typedClone();
		clone.minLength = minLength;
		return clone;
	}

	@Override
	public StringArbitrary ofMaxLength(int maxLength) {
		DefaultStringArbitrary clone = typedClone();
		clone.maxLength = maxLength;
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
		clone.characterArbitrary = clone.characterArbitrary
									   .range('A', 'Z')
									   .range('a', 'z');
		return clone;
	}

	@Override
	public StringArbitrary numeric() {
		DefaultStringArbitrary clone = typedClone();
		clone.characterArbitrary = clone.characterArbitrary.range('0', '9');
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

	private RandomGenerator<Character> randomCharacterGenerator() {
		return effectiveCharacterArbitrary().generator(1);
	}

	private Arbitrary<Character> effectiveCharacterArbitrary() {
		return characterArbitrary;
	}

}
