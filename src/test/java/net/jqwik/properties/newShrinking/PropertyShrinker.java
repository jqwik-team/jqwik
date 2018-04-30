package net.jqwik.properties.newShrinking;

import net.jqwik.api.*;
import org.junit.platform.engine.reporting.*;

import java.util.*;
import java.util.function.*;

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

	public PropertyShrinkingResult shrink(Falsifier<List> forAllFalsifier, Throwable originalError) {
//		ElementsShrinkingSequence sequence = new ElementsShrinkingSequence(parameters, forAllFalsifier);
//
//		List<NShrinkable> current = parameters;
//		while(sequence.next(() -> {}, ignore -> {})) {
//			current = (List<NShrinkable>) sequence.current().value();
//		}
//		return new ShrinkingResult(current, 0, originalError);
		return new PropertyShrinkingResult(parameters, 0, originalError);
	}
}
