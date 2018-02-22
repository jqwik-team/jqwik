package net.jqwik.properties.arbitraries;

import static net.jqwik.properties.ArbitraryTestHelper.*;

import net.jqwik.api.*;
import net.jqwik.api.arbitraries.*;
import net.jqwik.properties.*;

class DefaultCharacterArbitraryTests {

	CharacterArbitrary arbitrary = new DefaultCharacterArbitrary();

	@Example
	void perDefaultOnlyZeroIsCreated() {
		assertAllGenerated(arbitrary.generator(1000), c -> c == 0);
	}

	@Example
	void all() {
		CharacterArbitrary all = this.arbitrary.all();
		assertAllGenerated(all.generator(1000), c -> c >= Character.MIN_VALUE && c <= Character.MAX_VALUE);
		ArbitraryTestHelper.assertAtLeastOneGenerated(all.generator(1000), c -> c == Character.MIN_VALUE);
		ArbitraryTestHelper.assertAtLeastOneGenerated(all.generator(1000), c -> c == Character.MAX_VALUE);
	}

	@Example
	void between() {
		char min = '\u0010';
		char max = '\u1000';
		CharacterArbitrary all = this.arbitrary.between(min, max);
		assertAllGenerated(all.generator(1000), c -> c >= min && c <= max);
		ArbitraryTestHelper.assertAtLeastOneGenerated(all.generator(1000), c -> c == min);
		ArbitraryTestHelper.assertAtLeastOneGenerated(all.generator(1000), c -> c == max);
	}

	@Example
	void digit() {
		CharacterArbitrary all = this.arbitrary.digit();
		assertAllGenerated(all.generator(1000), c -> c >= '0' && c <= '9');
		ArbitraryTestHelper.assertAtLeastOneGenerated(all.generator(1000), c -> c == '0');
		ArbitraryTestHelper.assertAtLeastOneGenerated(all.generator(1000), c -> c == '9');
	}

	@Example
	void ascii() {
		CharacterArbitrary all = this.arbitrary.ascii();
		assertAllGenerated(all.generator(1000), c -> c >= 0 && c <= 127);
		ArbitraryTestHelper.assertAtLeastOneGenerated(all.generator(1000), c -> c == 0);
		ArbitraryTestHelper.assertAtLeastOneGenerated(all.generator(1000), c -> c == 127);
	}

}
