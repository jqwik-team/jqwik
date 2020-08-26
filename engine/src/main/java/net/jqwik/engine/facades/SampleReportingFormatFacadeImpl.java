package net.jqwik.engine.facades;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.support.*;

public class SampleReportingFormatFacadeImpl extends SampleReportingFormat.SampleReportingFormatFacade {

	@Override
	public Object reportJavaBean(Object bean) {
		Map<Object, Object> report = new LinkedHashMap<>();
		Arrays.stream(bean.getClass().getMethods())
			  .filter(method -> !method.getName().equals("getClass"))
			  .filter(method -> method.getName().startsWith("get") ||
									method.getName().startsWith("is"))
			  .map(method -> Tuple.of(extractPropertyName(method), method))
			  .sorted(Comparator.comparing(Tuple1::get1))
			  .forEach(tuple -> appendGetterToReport(report, tuple, bean));
		return report;
	}

	private void appendGetterToReport(Map<Object, Object> report, Tuple2<String, Method> method, Object bean) {
		appendIfNotNull(report, method.get1(), () -> {
			try {
				return ReflectionSupport.invokeMethod(method.get2(), bean);
			} catch (Throwable e) {
				JqwikExceptionSupport.rethrowIfBlacklisted(e);
				return "<illegal access>";
			}
		});
	}

	private String extractPropertyName(Method method) {
		String methodName = method.getName();
		String name = methodName.startsWith("get")
						  ? methodName.substring(3)
						  : methodName.startsWith("is")
								? methodName.substring(2)
								: methodName;
		name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
		return name;
	}

	private void appendIfNotNull(
		Map<Object, Object> report,
		String name,
		Supplier<Object> supplier
	) {
		Object value = supplier.get();
		if (value != null) {
			report.put(SampleReportingFormat.plainLabel(name), value);
		}
	}

}
