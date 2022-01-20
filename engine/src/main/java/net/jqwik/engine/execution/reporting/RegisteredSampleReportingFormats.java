package net.jqwik.engine.execution.reporting;

import java.util.*;

import net.jqwik.api.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

public class RegisteredSampleReportingFormats {

	private static final LazyServiceLoaderCache<SampleReportingFormat> serviceCache = new LazyServiceLoaderCache<>(SampleReportingFormat.class);

	public static List<SampleReportingFormat> getReportingFormats() {
		System.out.println("#### " + DomainContextFacadeImpl.getCurrentContext());
		return Collections.unmodifiableList(new ArrayList<>(serviceCache.getServices()));
	}
}
