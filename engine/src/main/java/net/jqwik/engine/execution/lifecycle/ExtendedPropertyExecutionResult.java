package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;

public interface ExtendedPropertyExecutionResult extends PropertyExecutionResult {

	boolean isExtended();

	String randomSeed();

	Optional<List<Object>> originalSample();

	GenerationMode generation();

	EdgeCasesMode edgeCases();

}
