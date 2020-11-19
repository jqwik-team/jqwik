package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;

import static org.assertj.core.api.Assertions.*;

import static net.jqwik.engine.execution.reporting.ParameterChangesDetector.*;

class ParameterChangesDetectorTests {

	@Example
	void emptyParametersAreUnchanged() {
		assertThat(haveParametersChanged(Collections.emptyList(), Collections.emptyList())).isFalse();
	}

	@Example
	void identicalListsAreUnchanged() {
		List<Object> before = Arrays.asList(new Object(), "a String", 42, null);
		assertThat(haveParametersChanged(before, before)).isFalse();
	}

	@Example
	void copiedListsAreUnchanged() {
		List<Object> before = Arrays.asList(new Object(), "a String", 42, null);
		List<Object> after = new ArrayList<>(before);
		assertThat(haveParametersChanged(before, after)).isFalse();
	}

	@Example
	void changedObjectDoesNotCountAsChange() {
		List<Object> before = Arrays.asList(new Object(), "a String", 42, null);
		List<Object> after = Arrays.asList(new Object(), "a String", 42, null);
		assertThat(haveParametersChanged(before, after)).isFalse();
	}

	@Example
	void oneParameterLess() {
		List<Object> before = Arrays.asList(new Object(), "a String", 42, null);
		List<Object> after = Arrays.asList(new Object(), "a String", 41);
		assertThat(haveParametersChanged(before, after)).isTrue();
		assertThat(haveParametersChanged(after, before)).isTrue();
	}

	@Example
	void oneParameterChanged() {
		List<Object> before = Arrays.asList(new Object(), "a String", 42);
		List<Object> after = Arrays.asList(new Object(), "a String", 41);
		assertThat(haveParametersChanged(before, after)).isTrue();
	}

	@Example
	void nullParameterChanged() {
		List<Object> before = Arrays.asList(new Object(), "a String", null);
		List<Object> after = Arrays.asList(new Object(), "a String", 41);
		assertThat(haveParametersChanged(before, after)).isTrue();
		assertThat(haveParametersChanged(after, before)).isTrue();
	}
}
