package net.jqwik.engine.properties.arbitraries;

import net.jqwik.api.*;

public abstract class TypedCloneable implements Cloneable {

	protected <A extends Arbitrary<?>> A typedClone() {
		try {
			//noinspection unchecked
			return (A) this.clone();
		} catch (CloneNotSupportedException e) {
			throw new JqwikException(e.getMessage());
		}
	}

}
