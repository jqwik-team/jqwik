package net.jqwik.testing;

import java.util.*;

import org.apiguardian.api.*;

import net.jqwik.api.lifecycle.*;

import static org.apiguardian.api.API.Status.*;

@API(status = EXPERIMENTAL, since = "1.5.1")
public class ReportMemoryHook implements AroundPropertyHook {

	private static final double BYTES_PER_MBYTE = 1048576.0;

	@Override
	public PropertyExecutionResult aroundProperty(PropertyLifecycleContext context, PropertyExecutor property) throws Throwable {
		double usedMemoryBefore = usedMemInMB();
		PropertyExecutionResult result = property.execute();
		double usedMemoryAfter = usedMemInMB();
		Map<String, Object> memoryReport = new LinkedHashMap<>();
		memoryReport.put("used memory before", String.format("%s MB", usedMemoryBefore));
		memoryReport.put("used memory after", String.format("%s MB", usedMemoryAfter));
		context.reporter().publishReport(context.extendedLabel(), memoryReport);
		return result;
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	private long totalMem() {
		return Runtime.getRuntime().totalMemory();
	}

	private double usedMemInMB() {
		System.gc();
		double usedMB = (totalMem() - freeMem()) / BYTES_PER_MBYTE;
		return Math.round(usedMB * 100) / 100.0;
	}

	private long freeMem() {
		return Runtime.getRuntime().freeMemory();
	}
}
