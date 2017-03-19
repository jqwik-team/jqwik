package net.jqwik.discovery.predicates;

import java.lang.reflect.AnnotatedElement;

public interface DiscoverySpec<T extends AnnotatedElement> {

	boolean discover(T candidate);

	boolean butSkip(T candidate);

	String skippingReason(T candidate);
}
