package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public interface ParametersGenerator {

	boolean hasNext();

	List<Shrinkable<Object>> next(TryLifecycleContext context);
}
