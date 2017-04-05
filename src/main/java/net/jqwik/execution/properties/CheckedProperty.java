package net.jqwik.execution.properties;

public interface CheckedProperty {
	PropertyExecutionResult check();

	int getTries();
}
