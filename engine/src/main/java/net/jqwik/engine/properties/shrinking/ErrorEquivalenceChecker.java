package net.jqwik.engine.properties.shrinking;

import java.lang.reflect.*;
import java.util.*;

class ErrorEquivalenceChecker {

	private final Method targetMethod;

	ErrorEquivalenceChecker(final Method targetMethod) {
		this.targetMethod = targetMethod;
	}

	/**
	 * Equivalence of falsified property:
	 * - Either both exceptions are null
	 * - Or both exceptions have the same type and their stack trace ended in same location
	 */
	boolean areEquivalent(Optional<Throwable> optionalOriginal, Optional<Throwable> optionalCurrent) {
		if (!optionalOriginal.isPresent()) {
			return !optionalCurrent.isPresent();
		}
		if (!optionalCurrent.isPresent()) {
			return false;
		}
		Throwable originalError = optionalOriginal.get();
		Throwable currentError = optionalCurrent.get();
		if (!originalError.getClass().equals(currentError.getClass())) {
			return false;
		}
		Optional<StackTraceElement> firstOriginal = firstRelevantStackTraceElement(originalError);
		Optional<StackTraceElement> firstCurrent = firstRelevantStackTraceElement(currentError);
		return firstOriginal.equals(firstCurrent);
	}

	private Optional<StackTraceElement> firstRelevantStackTraceElement(Throwable error) {
		StackTraceElement[] stackTrace = error.getStackTrace();
		if (stackTrace == null) {
			// Although you might think this cannot happen, it can.
			// See https://github.com/jqwik-team/jqwik/issues/283 for the discussion
			return Optional.empty();
		}
		return Arrays.stream(stackTrace)
					 .filter(this::belongsToTargetPropertyMethod)
					 .findFirst();
	}

	private boolean belongsToTargetPropertyMethod(StackTraceElement stackTraceElement) {
		if (targetMethod == null) {
			// Should only happen when shrinking is done outside normal property lifecycle
			return true;
		}
		return stackTraceElement.getClassName().equals(targetMethod.getDeclaringClass().getName())
				   && stackTraceElement.getMethodName().equals(targetMethod.getName());
	}

}
