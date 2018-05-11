package net.jqwik.properties.shrinking;

import net.jqwik.api.*;
import net.jqwik.support.*;
import org.junit.platform.engine.reporting.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

public class PropertyShrinker {

	private final static int BOUNDED_SHRINK_STEPS = 1000;

	private final List<Shrinkable> parameters;
	private final ShrinkingMode shrinkingMode;
	private final Consumer<ReportEntry> reporter;
	private final Reporting[] reporting;

	public PropertyShrinker(List<Shrinkable> parameters, ShrinkingMode shrinkingMode, Consumer<ReportEntry> reporter, Reporting[] reporting) {
		this.parameters = parameters;
		this.shrinkingMode = shrinkingMode;
		this.reporter = reporter;
		this.reporting = reporting;
	}

	@SuppressWarnings("unchecked")
	public PropertyShrinkingResult shrink(Falsifier<List> forAllFalsifier, Throwable originalError) {
		if (shrinkingMode == ShrinkingMode.OFF) {
			return new PropertyShrinkingResult(toValues(parameters), 0 , originalError);
		}

		Function<List<Shrinkable<Object>>, ShrinkingDistance> distanceFunction = ShrinkingDistance::combine;
		ElementsShrinkingSequence sequence = new ElementsShrinkingSequence(parameters, originalError, forAllFalsifier, distanceFunction);

		Consumer falsifiedReporter = isFalsifiedReportingOn() ? this::reportFalsifiedParams : ignore -> {};

		AtomicInteger shrinkingStepsCounter = new AtomicInteger(0);
		while (sequence.next(shrinkingStepsCounter::incrementAndGet, falsifiedReporter)) {
			if (shrinkingMode == ShrinkingMode.BOUNDED && shrinkingStepsCounter.get() >= BOUNDED_SHRINK_STEPS) {
				reportShrinkingBoundReached(shrinkingStepsCounter.get(), toValues(parameters), sequence.current().value());
				break;
			}
		}
		FalsificationResult<List> current = sequence.current();
		return new PropertyShrinkingResult(current.value(), shrinkingStepsCounter.get(), current.throwable().orElse(null));
	}

	private boolean isFalsifiedReportingOn() {
		return Reporting.FALSIFIED.containedIn(reporting);
	}

	private List toValues(List<Shrinkable> shrinkables) {
		return shrinkables.stream().map(Shrinkable::value).collect(Collectors.toList());
	}

	private void reportFalsifiedParams(Object effectiveParams) {
		ReportEntry falsifiedEntry = ReportEntry.from("falsified", JqwikStringSupport.displayString(effectiveParams));
		reporter.accept(falsifiedEntry);
	}

	private void reportShrinkingBoundReached(int steps, Object originalValue, Object bestShrunkValue) {
		String value = String.format(
			"%n    steps : %s" +
				"%n    original parameters : %s" +
				"%n    shrunk parameters   : %s" +
				"%nYou can switch on full shrinking with '@Property(shrinking = ShrinkingMode.FULL)'",
			steps,
			JqwikStringSupport.displayString(originalValue),
			JqwikStringSupport.displayString(bestShrunkValue)
		);
		reporter.accept(ReportEntry.from("shrinking bound reached", value));
	}

}
