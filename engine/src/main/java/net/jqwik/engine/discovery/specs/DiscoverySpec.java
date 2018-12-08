package net.jqwik.engine.discovery.specs;

import java.lang.reflect.*;

import org.junit.platform.engine.support.hierarchical.Node.*;

public interface DiscoverySpec<T extends AnnotatedElement> {

	boolean shouldBeDiscovered(T candidate);

	SkipResult shouldBeSkipped(T candidate);
}
