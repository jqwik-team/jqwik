package net.jqwik.engine.discovery;

import org.junit.platform.engine.*;
import org.junit.platform.engine.support.discovery.*;

import net.jqwik.engine.descriptor.*;
import net.jqwik.engine.discovery.predicates.*;

public class JqwikDiscoverySelectorResolver {

	private static final EngineDiscoveryRequestResolver<JqwikEngineDescriptor> resolver =
		EngineDiscoveryRequestResolver.<JqwikEngineDescriptor>builder()
			.addClassContainerSelectorResolver(new IsTestContainer())
			.addSelectorResolver(context -> new TopLevelClassResolver(context.getClassNameFilter()))
			.addSelectorResolver(context -> new GroupClassResolver())
			.addSelectorResolver(context -> new MethodSelectorResolver(
				context.getEngineDescriptor().getTestRunData(),
				context.getEngineDescriptor().getPropertyDefaultValues()
			))
			.addTestDescriptorVisitor(context -> TestDescriptor::prune)
			.build();

	public void resolveSelectors(EngineDiscoveryRequest request, JqwikEngineDescriptor engineDescriptor) {
		resolver.resolve(request, engineDescriptor);
	}

}
