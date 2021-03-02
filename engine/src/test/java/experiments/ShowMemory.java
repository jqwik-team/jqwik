package experiments;

import net.jqwik.api.lifecycle.*;

public class ShowMemory implements AroundPropertyHook {

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		PropertyExecutionResult result = property.execute();
		System.gc();
		System.err.printf("# %s: ## Used memory: %s%n", context.extendedLabel(), usedMem());
		return result;
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	public long totalMem() {
		return Runtime.getRuntime().totalMemory();
	}

	public double usedMem() {
		long l = totalMem() - Runtime.getRuntime().freeMemory();
		return l / 1048576.0;
	}
}
