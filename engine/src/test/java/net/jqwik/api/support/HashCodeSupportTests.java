package net.jqwik.api.support;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;
import net.jqwik.testing.*;

import static org.assertj.core.api.Assertions.*;

@PropertyDefaults(tries = 100)
@SuppressLogging
class HashCodeSupportTests {

	@Property
	<T1> void oneElement(@ForAll @WithNull T1 o1) {
		assertThat(HashCodeSupport.hash(o1))
			.isEqualTo(Objects.hash(o1));
	}

	@Property
	<T1, T2, T3> void twoElements(
		@ForAll @WithNull T1 o1,
		@ForAll @WithNull T2 o2
	) {
		assertThat(HashCodeSupport.hash(o1, o2))
			.isEqualTo(Objects.hash(o1, o2));
	}

	@Property
	<T1, T2, T3> void threeElements(
		@ForAll @WithNull T1 o1,
		@ForAll @WithNull T2 o2,
		@ForAll @WithNull T3 o3
	) {
		assertThat(HashCodeSupport.hash(o1, o2, o3))
			.isEqualTo(Objects.hash(o1, o2, o3));
	}

	@Property
	<T1, T2, T3, T4, T5, T6, T7, T8> void eightElements(
		@ForAll @WithNull T1 o1,
		@ForAll @WithNull T2 o2,
		@ForAll @WithNull T3 o3,
		@ForAll @WithNull T4 o4,
		@ForAll @WithNull T5 o5,
		@ForAll @WithNull T6 o6,
		@ForAll @WithNull T7 o7,
		@ForAll @WithNull T8 o8
	) {
		assertThat(HashCodeSupport.hash(o1, o2, o3, o4, o5, o6, o7, o8))
			.isEqualTo(Objects.hash(o1, o2, o3, o4, o5, o6, o7, o8));
	}
}
