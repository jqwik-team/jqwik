package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;

public interface TryLifecycleExecutor {

	TryExecutionResult execute(TryLifecycleContext tryLifecycleContext, List<Object> parameters);
}
