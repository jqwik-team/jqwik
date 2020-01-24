package net.jqwik.api.statistics;

import java.util.*;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * An implementation of this interface is responsible for creating
 * a formatted statistics report.
 *
 */
@API(status = EXPERIMENTAL, since = "1.2.3")
public interface StatisticsReportFormat {

	StatisticsReportFormat OFF = entries -> Optional.empty();

	/**
	 * Return a list of report lines. Often, one line will represent one entry
	 * but that must not necessarily be the case.
	 *
	 * @return the report lines or {@code Optional.empty()} if no report is produced
	 */
	Optional<List<String>> formatReport(List<StatisticsEntry> entries);
}
