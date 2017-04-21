package net.jqwik.execution.providers;

public interface GenericArbitraryProvider extends ArbitraryProvider {

	default boolean isGenericallyTyped() {
		return true;
	}
}
