package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.support.*;

public class RegisteredSampleReportingFormats {

	private static final LazyServiceLoaderCache<SampleReportingFormat> serviceCache = new LazyServiceLoaderCache<>(SampleReportingFormat.class);

	public static List<SampleReportingFormat> getReportingFormats() {
		// TODO: Add formats from current domain context
		// System.out.println(String.format("####[%s] %s", Thread.currentThread(), DomainContextFacadeImpl.getCurrentContext()));
		return Collections.unmodifiableList(new ArrayList<>(serviceCache.getServices()));
	}
}
