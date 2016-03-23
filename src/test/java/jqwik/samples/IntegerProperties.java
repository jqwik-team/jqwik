
package jqwik.samples;

import net.jqwik.api.Constraints;
import net.jqwik.api.Property;

class IntegerProperties {

	@Property
    boolean allAreSmallerEqualOrBiggerThanZero(int aNumber) {
	    return aNumber > 0 || aNumber < 0 || aNumber == 0;
	}

	@Property
    boolean allArePositive(int aNumber) {
	    return aNumber >= 0;
	}

	@Property
    boolean allAreGreaterThanMinus1000(int aNumber) {
	    return aNumber > -1000;
	}

	@Property
    boolean allAreSmallerThan1000(int aNumber) {
	    return aNumber <= 1000;
	}

	@Property
    boolean firstAlwaysBiggerThanSecond(int first, int second) {
	    return first > second;
	}

	@Property
    boolean failWithUnresolvableType(Object anObject) {
	    return true;
	}

	@Property
	boolean positiveNumbersAreGreaterThanZero(Integer aNumber) {
		Constraints.require(aNumber > 0);
		return aNumber >= 0;
	}

}
