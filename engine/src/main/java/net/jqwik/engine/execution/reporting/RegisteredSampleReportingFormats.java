package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class RegisteredSampleReportingFormats {

	private static final LazyServiceLoaderCache<SampleReportingFormat> serviceCache = new LazyServiceLoaderCache<>(SampleReportingFormat.class);

	public static List<SampleReportingFormat> getReportingFormats() {
		return Collections.unmodifiableList(new ArrayList<>(serviceCache.getServices()));
	}

	public static void register(SampleReportingFormat reportingFormat) {
		if (serviceCache.getServices().contains(reportingFormat)) {
			return;
		}
		serviceCache.getServices().add(0, reportingFormat);
	}
}
