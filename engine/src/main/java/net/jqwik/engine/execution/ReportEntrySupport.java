package net.jqwik.engine.execution;

import java.util.*;

import org.junit.platform.engine.*;
import org.junit.platform.engine.reporting.*;

class ReportEntrySupport {
	public static void printToStdout(TestDescriptor testDescriptor, ReportEntry entry) {
		// System.out.println(testDescriptor.getUniqueId());
		String formattedEntry = formatStandardEntry(entry);
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
}
