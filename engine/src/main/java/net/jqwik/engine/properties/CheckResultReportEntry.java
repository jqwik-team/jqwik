package net.jqwik.engine.properties;

import java.util.*;

import org.junit.platform.engine.reporting.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

import static net.jqwik.engine.properties.PropertyCheckResult.Status.*;

public class CheckResultReportEntry {

	private static final String TRIES_KEY = "tries";
	private static final String CHECKS_KEY = "checks";
	private static final String GENERATION_KEY = "generation-mode";
	private static final String AFTER_FAILURE_KEY = "after-failure";
	private static final String SEED_KEY = "seed";
	private static final String SAMPLE_KEY = "sample";
	private static final String ORIGINAL_REPORT_KEY = "original-sample";

	public static ReportEntry from(String propertyName, PropertyCheckResult checkResult, AfterFailureMode afterFailureMode) {
		return buildJqwikReport(propertyName, checkResult, afterFailureMode);
	}

	private static ReportEntry buildJqwikReport(
		String propertyName,
		PropertyCheckResult checkResult,
		AfterFailureMode afterFailureMode
	) {
		StringBuilder reportLines = new StringBuilder();

		appendThrowableMessage(reportLines, checkResult);
		appendFixedSizedProperties(reportLines, checkResult, afterFailureMode);
		appendSamples(reportLines, checkResult);

		return ReportEntry.from(propertyName, reportLines.toString());
	}

	private static void appendSamples(StringBuilder reportLines, PropertyCheckResult checkResult) {
		checkResult.sample().ifPresent(shrunkSample -> {
			if (!shrunkSample.isEmpty()) {
				reportLines.append(String.format("%s%n", buildProperty(
					SAMPLE_KEY,
					JqwikStringSupport.displayString(shrunkSample)
				)));
			}
		});

		checkResult.originalSample().ifPresent(originalSample -> {
			if (!originalSample.isEmpty()) {
				reportLines
					.append(String.format("%s%n", buildProperty(
						ORIGINAL_REPORT_KEY,
						JqwikStringSupport.displayString(originalSample)
					)));
			}
		});
	}

	private static void appendFixedSizedProperties(
		StringBuilder reportLines,
		PropertyCheckResult checkResult,
		AfterFailureMode afterFailureMode
	) {
		List<String> propertiesLines = new ArrayList<>();
		appendProperty(propertiesLines, TRIES_KEY, Integer.toString(checkResult.countTries()), "# of calls to property");
		appendProperty(propertiesLines, CHECKS_KEY, Integer.toString(checkResult.countChecks()), "# of not rejected calls");
		appendProperty(propertiesLines, GENERATION_KEY, checkResult.generation().name(), helpGenerationMode(checkResult.generation()));
		if (afterFailureMode != AfterFailureMode.NOT_SET) {
			appendProperty(propertiesLines, AFTER_FAILURE_KEY, afterFailureMode.name(), helpAfterFailureMode(afterFailureMode));
		}
		appendProperty(propertiesLines, SEED_KEY, checkResult.randomSeed(), "random seed to reproduce generated values");

		int halfBorderLength =
			(propertiesLines.stream().mapToInt(String::length).max().orElse(50) - 37) / 2 + 1;
		String halfBorder = String.join("", Collections.nCopies(halfBorderLength, "-"));

		reportLines.append(String.format("%n"));
		reportLines.append(buildLine("", "|" + halfBorder + "jqwik" + halfBorder));
		propertiesLines.forEach(reportLines::append);

	}

	private static void appendThrowableMessage(StringBuilder reportLines, PropertyCheckResult checkResult) {
		if (checkResult.status() != SATISFIED) {
			Throwable throwable = checkResult.toExecutionResult().getThrowable().get();
			String assertionClass = throwable.getClass().getName();
			String assertionMessage = throwable.getMessage();
			reportLines.append(String.format("%n%n%s: ", assertionClass));
			reportLines.append(String.format("%s%n", assertionMessage));
		}
	}

	private static String helpAfterFailureMode(AfterFailureMode afterFailureMode) {
		switch (afterFailureMode) {
			case RANDOM_SEED:
				return "use a new random seed";
			case PREVIOUS_SEED:
				return "use the previous seed";
			case SAMPLE_ONLY:
				return "only try the previously failed sample";
			case SAMPLE_FIRST:
				return "try previously failed sample, then previous seed";
			default:
				return "RANDOM_SEED, PREVIOUS_SEED or SAMPLE_FIRST";
		}
	}

	private static String helpGenerationMode(GenerationMode generation) {
		switch (generation) {
			case RANDOMIZED:
				return "parameters are randomly generated";
			case EXHAUSTIVE:
				return "parameters are exhaustively generated";
			case DATA_DRIVEN:
				return "parameters are taken from data provider";
			default:
				return "RANDOMIZED, EXHAUSTIVE or DATA_DRIVEN";
		}
	}

	private static void appendProperty(List<String> propertiesLines, String triesKey, String value, String s) {
		propertiesLines.add(buildPropertyLine(triesKey, value, s));
	}

	private static String buildPropertyLine(String key, String value, String help) {
		return buildLine(buildProperty(key, value), String.format("| %s", help));
	}

	private static String buildProperty(String key, String value) {
		return String.format("%s = %s", key, value);
	}

	private static String buildLine(String body, String helpString) {
		return String.format("%-30s%s%n", body, helpString);
	}

}
