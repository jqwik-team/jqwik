package net.jqwik.api.providers;

import net.jqwik.api.*;
import org.assertj.core.api.*;

import java.util.*;

class GenericTypeTests {

	@Example
	void primitiveTypes() {
		Assertions.assertThat(new GenericType(int.class).isCompatibleWith(Integer.class)).isTrue();
		Assertions.assertThat(new GenericType(int.class).isCompatibleWith(int.class)).isTrue();
		Assertions.assertThat(new GenericType(Integer.class).isCompatibleWith(Integer.class)).isTrue();

		Assertions.assertThat(new GenericType(Integer.class).isCompatibleWith(int.class)).isFalse();
		Assertions.assertThat(new GenericType(int.class).isCompatibleWith(float.class)).isFalse();
	}

	@Example
	void rawTypes() {
		Assertions.assertThat(new GenericType(Object.class).isCompatibleWith(Object.class)).isTrue();
		Assertions.assertThat(new GenericType(Object.class).isCompatibleWith(String.class)).isFalse();
	}

	@Example
	void genericTypesToRawTypes() {
		GenericType target = new GenericType(List.class, new GenericType(String.class));

		Assertions.assertThat(target.isCompatibleWith(List.class)).isTrue();
		Assertions.assertThat(target.isCompatibleWith(Collection.class)).isFalse();
		Assertions.assertThat(target.isCompatibleWith(Set.class)).isFalse();
	}

	@Example
	void genericTypesAmongEachOther() {
		GenericType target = new GenericType(List.class, new GenericType(String.class));
		GenericType provided = new GenericType(List.class, new GenericType(String.class));

		Assertions.assertThat(target.isCompatibleWith(provided)).isTrue();

		Assertions.assertThat(target.isCompatibleWith(new GenericType(List.class, new GenericType(Object.class)))).isFalse();
		Assertions.assertThat(new GenericType(List.class, new GenericType(Object.class)).isCompatibleWith(provided)).isFalse();
	}

}
