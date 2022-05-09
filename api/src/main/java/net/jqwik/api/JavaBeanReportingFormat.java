package net.jqwik.api;

import java.util.*;
import java.util.function.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.3.10")
public abstract class JavaBeanReportingFormat implements SampleReportingFormat {

	@API(status = INTERNAL)
	abstract public static class JavaBeanReportingFormatFacade {
		private static final JavaBeanReportingFormatFacade implementation;

		static {
			implementation = FacadeLoader.load(JavaBeanReportingFormatFacade.class);
		}

		public abstract Object reportJavaBean(
				Object bean,
				boolean reportNulls,
				Collection<String> excludeProperties,
				Function<List<String>, List<String>> sortProperies
		);

	}

	protected abstract Collection<Class<?>> beanTypes();

	protected Collection<String> excludeProperties() {
		return Collections.emptySet();
	}

	protected List<String> sortProperties(List<String> properties) {
		return properties;
	}

	protected boolean reportNulls() {
		return false;
	}

	@Override
	public Optional<String> label(Object value) {
		return Optional.of(value.getClass().getSimpleName());
	}

	@Override
	public boolean appliesTo(final Object value) {
		if (value == null) {
			return false;
		}
		return beanTypes().stream().anyMatch(beanType -> beanType.isAssignableFrom(value.getClass()));
	}

	@Override
	public final Object report(Object value) {
		return JavaBeanReportingFormatFacade.implementation.reportJavaBean(
				value,
				reportNulls(),
				excludeProperties(),
				this::sortProperties
		);
	}

}
