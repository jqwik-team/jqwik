package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import org.junit.platform.engine.reporting.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.stream.*;

public class PropertyShrinker {

	private final List<NShrinkable> parameters;
	private final ShrinkingMode shrinkingMode;
	private final Consumer<ReportEntry> reporter;
	private final Reporting[] reporting;

	public PropertyShrinker(List<NShrinkable> parameters, ShrinkingMode shrinkingMode, Consumer<ReportEntry> reporter, Reporting[] reporting) {
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

		ElementsShrinkingSequence sequence = new ElementsShrinkingSequence(parameters, originalError, forAllFalsifier);

		AtomicInteger counter = new AtomicInteger(0);
		while (sequence.next(counter::incrementAndGet, ignore -> {})) { }
		FalsificationResult<List> current = sequence.current();
		return new PropertyShrinkingResult(current.value(), counter.get(), current.throwable().orElse(null));
	}

	private List toValues(List<NShrinkable> shrinkables) {
		return shrinkables.stream().map(NShrinkable::value).collect(Collectors.toList());
	}
}
