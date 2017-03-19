package net.jqwik.discovery.specs;

import net.jqwik.discovery.predicates.IsPotentialTestContainer;

import java.util.function.Predicate;

public class PotentialContainerDiscoverySpec implements DiscoverySpec<Class<?>> {

	private static final Predicate<Class<?>> isPotentialTestContainer = new IsPotentialTestContainer();

	@Override
	public boolean shouldBeDiscovered(Class<?> candidate) {
		return isPotentialTestContainer.test(candidate);
	}

	@Override
	public boolean butSkippedOnExecution(Class<?> candidate) {
		return false;
	}

	@Override
	public String skippingReason(Class<?> candidate) {
		return null;
	}
}
