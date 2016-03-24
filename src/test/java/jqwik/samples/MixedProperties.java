
package jqwik.samples;

import net.jqwik.api.Max;
import net.jqwik.api.Min;
import net.jqwik.api.Property;

class MixedProperties {

	@Property
    boolean manyValues(boolean b1, Boolean b2, int number) {
		return true;
	}

	@Property
    boolean allValues(boolean b1, Boolean b2, @Min(-5) @Max(5) int number) {
		return true;
	}

	@Property
    boolean booleansAreShrinkedToTrue(boolean b1, Boolean b2, int number) {
		return number >= 0;
	}

}
