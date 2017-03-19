package net.jqwik.discovery.predicates;

import static net.jqwik.support.JqwikReflectionSupport.isAbstract;
import static net.jqwik.support.JqwikReflectionSupport.isPrivate;

public class PotentialContainerDiscoverySpec implements DiscoverySpec<Class<?>> {

	@Override
	public boolean shouldBeDiscovered(Class<?> candidate) {
		if (isAbstract(candidate))
			return false;
		if (isPrivate(candidate))
			return false;
		if (candidate.isLocalClass())
			return false;
		if (candidate.isAnonymousClass())
			return false;
		return true;
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
