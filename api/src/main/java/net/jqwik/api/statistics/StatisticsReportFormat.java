package net.jqwik.api.statistics;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An implementation of this interface is responsible for creating
 * a formatted statistics report.
 *
 * <p>
 * Implementation of this class must have a public default constructor
 * to be usable in {@linkplain StatisticsReport#format()}
 * </p>
 *
 * @see StatisticsReport
 */
@API(status = EXPERIMENTAL, since = "1.2.3")
public interface StatisticsReportFormat {

	/**
	 * Return a list of report lines. Often, one line will represent one entry
	 * but that must not necessarily be the case.
	 *
	 * @return the report lines without trailing new lines
	 *
	 * @see StatisticsEntry
	 */
	List<String> formatReport(List<StatisticsEntry> entries);
}
