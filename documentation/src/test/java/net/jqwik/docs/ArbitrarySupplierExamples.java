package net.jqwik.docs;

import java.util.*;

import net.jqwik.api.*;

class ArbitrarySupplierExamples {

	@Property
	boolean concatenatingStringWithInt(
		@ForAll(supplier = ShortStrings.class) String aShortString,
		@ForAll(supplier = TenTo99.class) int aNumber
	) {
		String concatenated = aShortString + aNumber;
		return concatenated.length() > 2 && concatenated.length() < 11;
	}

	class ShortStrings implements ArbitrarySupplier<String> {
		@Override
		public Arbitrary<String> get() {
			return Arbitraries.strings().withCharRange('a', 'z')
							  .ofMinLength(1).ofMaxLength(8);
		}
	}

	class TenTo99 implements ArbitrarySupplier<Integer> {
		@Override
		public Arbitrary<Integer> get() {
			return Arbitraries.integers().between(10, 99);
		}
	}

	@Property
	boolean joiningListOfStrings(@ForAll List<@From(supplier=ShortStrings.class) String> listOfStrings) {
		String concatenated = String.join("", listOfStrings);
		return concatenated.length() <= 8 * listOfStrings.size();
	}

}
