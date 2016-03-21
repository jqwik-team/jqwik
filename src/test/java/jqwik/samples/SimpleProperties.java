
package jqwik.samples;

import net.jqwik.api.Property;

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
