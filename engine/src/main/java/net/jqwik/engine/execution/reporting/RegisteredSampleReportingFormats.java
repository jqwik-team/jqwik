package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;

public class RegisteredSampleReportingFormats {

	private static List<SampleReportingFormat> registeredFormats;

	public static List<SampleReportingFormat> getReportingFormats() {
		if (null == registeredFormats) {
			loadSampleReportingFormats();
		}
		return Collections.unmodifiableList(new ArrayList<>(registeredFormats));
	}

	private static void loadSampleReportingFormats() {
		registeredFormats = new ArrayList<>();
		Iterable<SampleReportingFormat> providers = ServiceLoader.load(SampleReportingFormat.class);
		for (SampleReportingFormat provider : providers) {
			register(provider);
		}
	}

	public static void register(SampleReportingFormat reportingFormat) {
		if (getReportingFormats().contains(reportingFormat)) {
			return;
		}
		registeredFormats.add(0, reportingFormat);
	}
}
