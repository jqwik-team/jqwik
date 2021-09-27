package net.jqwik.api.statistics;

import java.lang.annotation.*;
import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * This annotation can be used to influence statistics reporting. You can either
 * annotate a property method to change reporting for this property only
 * or a container class to change reporting for all properties in this class
 * or nested subclasses.
 * <p>
 * There are three usage scenarios:
 * <ul>
 *     <li>
 *          Use {@code @StatisticsReport(STANDARD)} to enable the standard reporting.
 *  		This is the default anyway.
 *  	</li>
 *  	<li>
 * 			Use {@code @StatisticsReport(OFF)} to disable statistics reporting.
 *  	</li>
 *  	<li>
 * 			Use {@code @StatisticsReport(format = YourReportFormat.class)} to plug in your own format.
 *  	</li>
 * </ul>
 *
 * <p>
 *     This annotation is repeatable when used on property methods.
 *     In this case use {@linkplain #label()} to specify for which statistics collection the given report format shall be used.
 * </p>
 *
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(StatisticsReportList.class)
@API(status = MAINTAINED, since = "1.2.3")
public @interface StatisticsReport {

	@API(status = INTERNAL)
	class None implements StatisticsReportFormat {
		@Override
		public List<String> formatReport(List<StatisticsEntry> entries) {
			throw new UnsupportedOperationException("This format should never be used");
		}
	}

	enum StatisticsReportMode {
		/**
		 * No statistics report
		 */
		OFF,
		/**
		 * Standard statistics report format
		 */
		STANDARD,
		/**
		 * Plug in your own format. Must be set with {@linkplain StatisticsReport#format()}.
		 */
		PLUG_IN
	}

	String ALL_LABELS = "";

	StatisticsReportMode value() default StatisticsReportMode.PLUG_IN;

	/**
	 * The format to be used for publishing statistics reports
	 * in the annotated property.
	 */
	Class<? extends StatisticsReportFormat> format() default None.class;

	/**
	 * The statistics label to which the format in this annotation should be applied.
	 */
	@API(status = MAINTAINED, since = "1.5.1")
	String label() default ALL_LABELS;

	/**
	 * Set to true when only failing properties should report their statistics
	 */
	@API(status = EXPERIMENTAL, since = "1.5.5")
	boolean onFailureOnly() default false;
}
