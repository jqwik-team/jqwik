
package jqwik.samples;

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
    boolean allAreGreaterThan1000(int aNumber) {
	    return aNumber > 1000;
	}

	@Property
    boolean allAreSmallerThan1000(int aNumber) {
	    return aNumber < 1000;
	}

	@Property
    boolean firstBiggerThanSecond(int first, int second) {
	    return first < second;
	}

	@Property
    boolean failWithUnresolvableType(Object anObject) {
	    return true;
	}

}
