package net.jqwik.execution;

import java.util.*;

import org.junit.platform.engine.reporting.*;

import net.jqwik.properties.*;

public class CheckResultReportEntry {

	public static final String SEED_REPORT_KEY = "seed";
	public static final String TRIES_REPORT_KEY = "tries";
	public static final String CHECKS_REPORT_KEY = "checks";
	public static final String SAMPLE_REPORT_KEY = "sample";

	public static ReportEntry from(PropertyCheckResult checkResult) {
		Map<String, String> entries = new HashMap<>();
		entries.put(SEED_REPORT_KEY, Long.toString(checkResult.randomSeed()));
		entries.put(TRIES_REPORT_KEY, Integer.toString(checkResult.countTries()));
		entries.put(CHECKS_REPORT_KEY, Integer.toString(checkResult.countChecks()));
		checkResult.sample().ifPresent(sample -> {
			if (!sample.isEmpty())
				entries.put(SAMPLE_REPORT_KEY, sample.toString());
		});
		return ReportEntry.from(entries);
	}

}
