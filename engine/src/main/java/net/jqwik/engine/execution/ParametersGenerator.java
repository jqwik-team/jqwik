package net.jqwik.engine.execution;

import java.util.*;
import java.util.function.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.api.lifecycle.*;

public interface ParametersGenerator {

	boolean hasNext();

	Tuple2<TryLifecycleContext, List<Shrinkable<Object>>> next(Supplier<TryLifecycleContext> contextSupplier);

	int edgeCasesTotal();

	int edgeCasesTried();

	int generationIndex();
}
