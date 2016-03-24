
package jqwik.samples;

import net.jqwik.api.Constraints;
import net.jqwik.api.Max;
import net.jqwik.api.Min;
import net.jqwik.api.Property;

class ConfiguredParameterProperties {

//	@Property
    boolean allAreSmallerEqualOrBiggerThanZero(@Min(0) @Max(20) int aNumber) {
	    return aNumber >= 0 && aNumber <= 20;
	}

//	@Property(trials = 1000)
    boolean allAreSmallerEqualOrBiggerThanZero(@Min(-10) @Max(0) int first, @Min(0) @Max(10) int second) {
	    return first < second;
	}

	@Property(trials = 25)
	boolean shouldExecuteAllPossibilities(@Min(-10) @Max(10) int aNumber) {
		return true;
	}

	@Property(trials = 25)
	boolean shouldExecuteAllConstrainedPossibilities(@Min(-10) @Max(10) int aNumber) {
		Constraints.require(aNumber >= 0);
		System.out.println(aNumber);
		return true;
	}

}
