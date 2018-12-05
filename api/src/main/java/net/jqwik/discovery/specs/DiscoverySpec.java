package net.jqwik.discovery.specs;

import org.junit.platform.engine.support.hierarchical.Node.*;

import java.lang.reflect.*;

public interface DiscoverySpec<T extends AnnotatedElement> {

	boolean shouldBeDiscovered(T candidate);

	SkipResult shouldBeSkipped(T candidate);
}
