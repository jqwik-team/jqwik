package net.jqwik.api.providers;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.*;

class GenericTypeTests {

	@Example
	void nonGenericTypesAssignability() {
		assertAssignable(new GenericType(int.class), new GenericType(Integer.class));
		assertAssignable(new GenericType(double.class), new GenericType(Integer.class));
		assertAssignable(new GenericType(List.class), new GenericType(List.class));
		assertAssignable(new GenericType(List.class), new GenericType(ArrayList.class));
		assertNotAssignable(new GenericType(ArrayList.class), new GenericType(List.class));
	}

	@Example
	void identicalGenericTypesAreAssignable() {
		GenericType target = new GenericType(List.class, new GenericType(String.class));
		GenericType provided = new GenericType(List.class, new GenericType(String.class));
		assertAssignable(target, provided);
	}

	@Example
	void genericTypeIsAssignableFromRawType() {
		GenericType target = new GenericType(List.class, new GenericType(String.class));
		GenericType provided = new GenericType(List.class);
		assertAssignable(target, provided);
	}

	@Example
	void genericTypeIsNotAssignableFromSameRawTypeWithOtherTypeArgument() {
		GenericType target = new GenericType(List.class, new GenericType(String.class));
		GenericType provided = new GenericType(List.class, new GenericType(Integer.class));
		assertNotAssignable(target, provided);
	}

	private void assertAssignable(GenericType target, GenericType provided) {
		Assertions.assertThat(target.isAssignableFrom(provided));
	}

	private void assertNotAssignable(GenericType target, GenericType provided) {
		Assertions.assertThat(target.isAssignableFrom(provided)).isFalse();
	}

}
