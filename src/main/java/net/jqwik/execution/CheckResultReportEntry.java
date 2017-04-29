package net.jqwik.execution;

import net.jqwik.properties.*;
import org.junit.platform.engine.reporting.*;

import java.util.*;

public class CheckResultReportEntry {

	public static final String SEED_REPORT_KEY = "seed";
	public static final String TRIES_REPORT_KEY = "tries";
	public static final String CHECKS_REPORT_KEY = "checks";

	public static ReportEntry from(PropertyCheckResult checkResult) {
		Map<String, String> entries = new HashMap<>();
		entries.put(SEED_REPORT_KEY, Long.toString(checkResult.randomSeed()));
		entries.put(TRIES_REPORT_KEY, Integer.toString(checkResult.countTries()));
		entries.put(CHECKS_REPORT_KEY, Integer.toString(checkResult.countChecks()));
		checkResult.sample().ifPresent(sample -> entries.put("sample", sample.toString()));
		checkResult.throwable().ifPresent(throwable -> entries.put("throwable", throwable.toString()));
		return ReportEntry.from(entries);
	}

}
