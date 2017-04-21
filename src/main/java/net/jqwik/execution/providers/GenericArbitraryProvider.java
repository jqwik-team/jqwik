package net.jqwik.execution.providers;

public interface GenericArbitraryProvider extends TypedArbitraryProvider {

	default boolean isGenericallyTyped() {
		return true;
	}
}
