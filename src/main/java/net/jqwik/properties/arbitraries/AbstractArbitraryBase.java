package net.jqwik.properties.arbitraries;

import net.jqwik.*;
import net.jqwik.api.*;

public abstract class AbstractArbitraryBase implements Cloneable {

	@SuppressWarnings("unchecked")
	protected <A extends Arbitrary> A typedClone() {
		try {
			return (A) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new JqwikException(e.getMessage());
		}
	}

}
