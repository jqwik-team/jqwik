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

	public ShrinkingResult shrink(Falsifier<List> forAllFalsifier, Throwable originalError) {
		return new ShrinkingResult(parameters, 0, originalError);
	}
}
