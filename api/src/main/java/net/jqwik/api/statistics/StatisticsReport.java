package net.jqwik.api.statistics;

import java.lang.annotation.*;
import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Use {@code @StatisticsReport(OFF)} to disable statistics reporting.
 * <p>
 * Use {@code @StatisticsReport(STANDARD)} to enable the standard reporting.
 * This is the default anyway.
 * <p>
 * Use {@code @StatisticsReport(format = YourReportFormat.class)} to use your own format.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@API(status = EXPERIMENTAL, since = "1.2.3")
public @interface StatisticsReport {

	@API(status = INTERNAL)
	class None implements StatisticsReportFormat {
		@Override
		public List<String> formatReport(List<StatisticsEntry> entries) {
			throw new UnsupportedOperationException("This format should never be used");
		}
	}

	enum StatisticsReportMode {
		OFF, STANDARD, PLUG_IN
	}

	StatisticsReportMode value() default StatisticsReportMode.PLUG_IN;

	/**
	 * The format to be used for publishing statistics reports
	 * in the annotated property.
	 */
	Class<? extends StatisticsReportFormat> format() default None.class;

}
