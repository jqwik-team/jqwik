package net.jqwik.engine.properties;

import java.util.*;

import org.junit.platform.engine.reporting.ReportEntry;

import net.jqwik.api.*;
import net.jqwik.engine.support.JqwikStringSupport;

public class CheckResultReportEntry {

	public static final String TRIES_KEY = "tries";
	public static final String CHECKS_KEY = "checks";
	public static final String GENERATION_KEY = "generation-mode";
	public static final String AFTER_FAILURE_KEY = "after-failure";
	public static final String SEED_KEY = "seed";
	public static final String SAMPLE_KEY = "sample";
	public static final String ORIGINAL_REPORT_KEY = "original-sample";

	public static ReportEntry from(PropertyCheckResult checkResult, AfterFailureMode afterFailureMode) {
		Map<String, String> entries = new HashMap<>();
		entries.put(TRIES_KEY, Integer.toString(checkResult.countTries()));
		entries.put(CHECKS_KEY, Integer.toString(checkResult.countChecks()));
		entries.put(GENERATION_KEY, checkResult.generation().name());
		if (afterFailureMode != AfterFailureMode.NOT_SET) {
			entries.put(AFTER_FAILURE_KEY, afterFailureMode.name());
		}
		entries.put(SEED_KEY, checkResult.randomSeed());
		checkResult.sample().ifPresent(sample -> {
			if (!sample.isEmpty())
				entries.put(SAMPLE_KEY, JqwikStringSupport.displayString(sample));
		});
		checkResult.originalSample().ifPresent(sample -> {
			if (!sample.isEmpty())
				entries.put(ORIGINAL_REPORT_KEY, JqwikStringSupport.displayString(sample));
		});
		return ReportEntry.from(entries);
	}

}
