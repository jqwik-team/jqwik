package net.jqwik.engine.execution.lifecycle;

import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.properties.*;

public interface ExtendedPropertyExecutionResult extends PropertyExecutionResult {

	Optional<PropertyCheckResult> checkResult();
}
