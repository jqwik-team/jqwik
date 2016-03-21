
package jqwik.samples;

import net.jqwik.api.Property;

class IntegerProperties {

	@Property
    boolean allAreSmallerEqualOrBiggerThanZero(int aNumber) {
	    return aNumber > 0 || aNumber < 0 || aNumber == 0;
	}

	@Property
    boolean failWithUnresolvableType(Object anObject) {
	    return true;
	}

}
