package net.jqwik.engine.facades;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.junit.platform.commons.support.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;
import net.jqwik.engine.support.*;

public class JavaBeanReportingFormatFacadeImpl extends JavaBeanReportingFormat.JavaBeanReportingFormatFacade {

	@Override
	public Object reportJavaBean(
			Object bean,
			boolean reportNulls,
			Collection<String> excludeProperties,
			Function<List<String>, List<String>> sortProperties
	) {
		Map<Object, Object> report = new LinkedHashMap<>();
		List<Tuple2<String, Method>> properties =
				Arrays.stream(bean.getClass().getMethods())
					  .filter(method -> !JqwikReflectionSupport.isStatic(method))
					  .filter(method -> method.getParameters().length == 0)
					  .filter(method -> !method.getName().equals("getClass"))
					  .filter(method -> !method.getName().equals("get"))
					  .filter(method -> !method.getName().equals("is"))
					  .filter(method -> method.getName().startsWith("get") ||
												method.getName().startsWith("is"))
					  .map(method -> Tuple.of(extractPropertyName(method), method))
					  .sorted(Comparator.comparing(Tuple1::get1))
					  .filter(tuple -> !excludeProperties.contains(tuple.get1()))
					  .collect(Collectors.toList());

		resort(properties, sortProperties).forEach(tuple -> appendGetterToReport(report, reportNulls, tuple, bean));
		return report;
	}

	private Iterable<Tuple2<String, Method>> resort(List<Tuple2<String, Method>> properties, Function<List<String>, List<String>> nameSorter) {
		List<String> unsortedNames = properties.stream().map(p -> p.get1()).collect(Collectors.toList());
		List<String> sortedNames = nameSorter.apply(unsortedNames);

		List<Tuple2<String, Method>> sortedProperties = new ArrayList<>();
		Map<String, Tuple2<String, Method>> propertyMap = toMap(properties);
		for (String name : sortedNames) {
			Tuple2<String, Method> entry = propertyMap.get(name);
			if (entry != null) {
				sortedProperties.add(entry);
			}
		}

		return sortedProperties;
	}

	private Map<String, Tuple2<String, Method>> toMap(List<Tuple2<String, Method>> properties) {
		HashMap<String, Tuple2<String, Method>> map = new LinkedHashMap<>();
		for (Tuple2<String, Method> entry : properties) {
			map.put(entry.get1(), entry);
		}
		return map;
	}

	private void appendGetterToReport(Map<Object, Object> report, boolean reportNulls, Tuple2<String, Method> method, Object bean) {
		maybeAppend(report, reportNulls, method.get1(), () -> {
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

	private void maybeAppend(
			Map<Object, Object> report,
			boolean reportNulls,
			String name,
			Supplier<Object> supplier
	) {
		Object value = supplier.get();
		boolean isNullOrEmpty = value != null && !value.equals(Optional.empty());
		if (reportNulls || isNullOrEmpty) {
			report.put(SampleReportingFormat.plainLabel(name), value);
		}
	}

}
