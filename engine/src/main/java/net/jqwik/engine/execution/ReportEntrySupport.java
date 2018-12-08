package net.jqwik.engine.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

import net.jqwik.engine.properties.*;

import static net.jqwik.engine.properties.CheckResultReportEntry.*;

class ReportEntrySupport {
	public static void printToStdout(TestDescriptor testDescriptor, ReportEntry entry) {
		// System.out.println(testDescriptor.getUniqueId());
		String formattedEntry = isCheckResultEntry(entry) ? formatCheckResultReportEntry(entry) : formatStandardEntry(entry);
		System.out.println(formattedEntry);
	}

	private static String formatStandardEntry(ReportEntry entry) {
		List<String> stringEntries = new ArrayList<>();
		stringEntries.add(String.format("timestamp = %s", entry.getTimestamp()));
		for (Map.Entry<String, String> keyValue : entry.getKeyValuePairs().entrySet()) {
			stringEntries.add(String.format("%s = %s", keyValue.getKey(), keyValue.getValue()));
		}
		return String.join(", ", stringEntries) + String.format("%n");
	}

	private static boolean isCheckResultEntry(ReportEntry entry) {
		return entry.getKeyValuePairs().containsKey(CheckResultReportEntry.TRIES_KEY);
	}

	private static String formatCheckResultReportEntry(ReportEntry entry) {
		List<String> stringEntries = new ArrayList<>();
		stringEntries.add(String.format("timestamp = %s%n", entry.getTimestamp()));

		List<String> sortedKeys = sortKeys(entry);
		for (String key : sortedKeys) {
			String value = entry.getKeyValuePairs().get(key);
			stringEntries.add(String.format("    %s = %s%n", key, value));
		}

		return String.join("", stringEntries);
	}

	private static ArrayList<String> sortKeys(ReportEntry entry) {
		ArrayList<String> keys = new ArrayList<>(entry.getKeyValuePairs().keySet());
		sortToStart(keys, SAMPLE_KEY, ORIGINAL_REPORT_KEY, SEED_KEY, AFTER_FAILURE_KEY, GENERATION_KEY, CHECKS_KEY, TRIES_KEY);
		return keys;
	}

	private static void sortToStart(ArrayList<String> keys, String ... keysToStartWith) {
		for (String firstKey : keysToStartWith) {
			if (keys.remove(firstKey)) {
				keys.add(0, firstKey);
			}
		}
	}

}
