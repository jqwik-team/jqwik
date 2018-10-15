package net.jqwik.properties;

import java.util.*;

import org.junit.platform.engine.reporting.ReportEntry;

import net.jqwik.support.JqwikStringSupport;

public class CheckResultReportEntry {

	public static final String SEED_REPORT_KEY = "seed";
	public static final String GENERATION_REPORT_KEY = "generation-mode";
	public static final String TRIES_REPORT_KEY = "tries";
	public static final String CHECKS_REPORT_KEY = "checks";
	public static final String SAMPLE_REPORT_KEY = "sample";
	public static final String ORIGINAL_SAMPLE_REPORT_KEY = "originalSample";

	public static ReportEntry from(PropertyCheckResult checkResult) {
		Map<String, String> entries = new HashMap<>();
		entries.put(SEED_REPORT_KEY, checkResult.randomSeed());
		entries.put(TRIES_REPORT_KEY, Integer.toString(checkResult.countTries()));
		entries.put(CHECKS_REPORT_KEY, Integer.toString(checkResult.countChecks()));
		entries.put(GENERATION_REPORT_KEY, checkResult.generation().name());
		checkResult.sample().ifPresent(sample -> {
			if (!sample.isEmpty())
				entries.put(SAMPLE_REPORT_KEY, JqwikStringSupport.displayString(sample));
		});
		checkResult.originalSample().ifPresent(sample -> {
			if (!sample.isEmpty())
				entries.put(ORIGINAL_SAMPLE_REPORT_KEY, JqwikStringSupport.displayString(sample));
		});
		return ReportEntry.from(entries);
	}

}
