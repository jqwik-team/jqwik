package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.execution.lifecycle.*;
import net.jqwik.engine.support.*;

public class SampleReportingFormats {

	private static final LazyServiceLoaderCache<SampleReportingFormat> serviceCache = new LazyServiceLoaderCache<>(SampleReportingFormat.class);

	public static Collection<SampleReportingFormat> getReportingFormats() {
		Set<SampleReportingFormat> formats = new LinkedHashSet<>();
		formats.addAll(getRegisteredReportingFormats());
		formats.addAll(getReportingFormatsFromCurrentDomainContext());

		return Collections.unmodifiableSet(formats);
	}

	private static Collection<SampleReportingFormat> getReportingFormatsFromCurrentDomainContext() {
		return CurrentDomainContext.get().getReportingFormats();
	}

	private static Collection<SampleReportingFormat> getRegisteredReportingFormats() {
		return serviceCache.getServices();
	}
}
