package net.jqwik.execution.providers;

public interface GenericArbitraryProvider extends ArbitraryProvider {

	default boolean needsSubtypeProvider() {
		return true;
	}
}
