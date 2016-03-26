
package jqwik.samples;

import com.pholser.junit.quickcheck.Property;

class SimpleProperties {

	@Property
    boolean succeedingProperty() {
	    return true;
	}

	@Property
    static boolean succeedingStaticProperty() {
	    return true;
	}

	@Property
    Boolean succeedingPropertyWithBoxedBoolean() {
	    return Boolean.TRUE;
	}

	@Property
	boolean failingProperty() {
		return false;
	}

	@Property
	private boolean notAPropertyBecausePrivate() {
		return false;
	}

	@Property
	void notAPropertyBecauseVoid() {
	}

	@Property
	Object notAPropertyBecauseWrongReturnType() {
		return new Object();
	}

}
