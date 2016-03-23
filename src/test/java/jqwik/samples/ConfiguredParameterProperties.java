
package jqwik.samples;

import net.jqwik.api.Max;
import net.jqwik.api.Min;
import net.jqwik.api.Property;

class ConfiguredParameterProperties {

	@Property
    boolean allAreSmallerEqualOrBiggerThanZero(@Min(0) @Max(20) int aNumber) {
	    return aNumber >= 0 && aNumber <= 20;
	}

	@Property(trials = 1000)
    boolean allAreSmallerEqualOrBiggerThanZero(@Min(-10) @Max(0) int first, @Min(0) @Max(10) int second) {
	    return first < second;
	}

}
