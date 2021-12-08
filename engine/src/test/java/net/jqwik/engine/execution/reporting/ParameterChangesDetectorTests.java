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
	void nestedFieldHasChanged() {
		Node node1 = new Node(1);
		Node node2 = new Node(2);
		node1.next = node2;
		Node node11 = new Node(1);
		Node node22 = new Node(2);
		node11.next = node22;

		List<Object> before = Arrays.asList(node1);
		List<Object> after = Arrays.asList(node11);
		assertThat(haveParametersChanged(before, after)).isFalse();

		node22.value = 3;
		assertThat(haveParametersChanged(before, after)).isTrue();
	}

	@Example
	void nullParameterChanged() {
		List<Object> before = Arrays.asList(new Object(), "a String", null);
		List<Object> after = Arrays.asList(new Object(), "a String", 41);
		assertThat(haveParametersChanged(before, after)).isTrue();
		assertThat(haveParametersChanged(after, before)).isTrue();
	}

	private static class Node {
		int value;
		Node next = null;

		private Node(int value) {
			this.value = value;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Node node = (Node) o;
			return value == node.value && Objects.equals(next, node.next);
		}

		@Override
		public int hashCode() {
			return Objects.hash(value, next);
		}
	}
}
