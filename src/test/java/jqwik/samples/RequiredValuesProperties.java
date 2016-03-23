
package jqwik.samples;

import net.jqwik.api.Constraints;
import net.jqwik.api.Property;

class RequiredValuesProperties {

	@Property
    boolean allAreSmallerEqualOrBiggerThanZero(int aNumber) {
		Constraints.require(aNumber > Integer.MIN_VALUE && aNumber < 100);
	    return aNumber > 0 || aNumber < 0 || aNumber == 0;
	}

	@Property
    boolean willFailWithTooManyTries(int aNumber) {
		Constraints.require(aNumber > 100 && aNumber < 100);
	    return true;
	}

}
