package net.jqwik.engine.properties.shrinking;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.engine.properties.*;
import net.jqwik.engine.support.*;

public class ShrinkableString extends ShrinkableContainer<String, Character> {

	public static final Set<FeatureExtractor<Character>> UNIQUE_CHARS_EXTRACTOR = Collections.singleton(FeatureExtractor.identity());

	public ShrinkableString(
		List<Shrinkable<Character>> elements, int minSize, int maxSize,
		Arbitrary<Character> characterArbitrary,
		boolean uniqueChars
	) {
		this(elements, minSize, maxSize, uniquenessExtractors(uniqueChars), characterArbitrary);
	}

	private ShrinkableString(
		List<Shrinkable<Character>> elements, int minSize, int maxSize,
		Collection<FeatureExtractor<Character>> uniquenessExtractors,
		Arbitrary<Character> characterArbitrary
	) {
		super(elements, minSize, maxSize, uniquenessExtractors, characterArbitrary);
	}

	private static Collection<FeatureExtractor<Character>> uniquenessExtractors(boolean uniqueChars) {
		return uniqueChars ? UNIQUE_CHARS_EXTRACTOR : Collections.emptySet();
	}

	@Override
	String createValue(List<Shrinkable<Character>> shrinkables) {
		// Using loop instead of stream to make stack traces more readable
		StringBuilder builder = new StringBuilder(shrinkables.size());
		for (Shrinkable<Character> shrinkable : shrinkables) {
			builder.appendCodePoint(shrinkable.value());
		}
		return builder.toString();
	}

	@Override
	Shrinkable<String> createShrinkable(List<Shrinkable<Character>> shrunkElements) {
		return new ShrinkableString(shrunkElements, minSize, maxSize, uniquenessExtractors, elementArbitrary);
	}

	@Override
	public Stream<Shrinkable<String>> shrink() {
		if (elements.size() > 100) {
			return JqwikStreamSupport.concat(
				shrinkSizeAggressively(),
				shrinkSizeOfList(),
				shrinkElementsOneAfterTheOther(100)
			);
		}
		return JqwikStreamSupport.concat(
			shrinkSizeOfList(),
			shrinkElementsOneAfterTheOther(0),
			shrinkPairsOfElements(),
			sortElements()
		);
	}
}
