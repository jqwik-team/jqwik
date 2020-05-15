package net.jqwik.api.statistics;

import java.util.*;
import java.util.stream.*;

public class Histogram implements StatisticsReportFormat {
	@Override
	public List<String> formatReport(List<StatisticsEntry> entries) {
		if (entries.isEmpty()) {
			throw new IllegalArgumentException("Entries must not be empty");
		}
		try {
			entries.sort(comparator());
			List<Bucket> buckets = collectBuckets(entries);
			return generateHistogram(entries, buckets);
		} catch (Exception exception) {
			return Collections.singletonList("Cannot draw histogram: " + exception.toString());
		}
	}


	/**
	 * Determine how many block characters are maximally used to draw the distribution.
	 * The more you have the further the histogram extends to the right.
	 *
	 * <p>
	 * Can be overridden.
	 * </p>
	 *
	 * @return A positive number. Default is 80.
	 */
	protected int maxDrawRange() {
		return 80;
	}

	/**
	 * Determine how entries are being sorted from top to bottom.
	 *
	 * <p>
	 * Can be overridden.
	 * </p>
	 *
	 * @return A comparator instance.
	 */
	protected Comparator<? super StatisticsEntry> comparator() {
		return (left, right) -> {
			Comparable leftFirst = (Comparable) left.values().get(0);
			Comparable rightFirst = (Comparable) right.values().get(0);
			return leftFirst.compareTo(rightFirst);
		};
	}

	/**
	 * Determine how entries are being labelled in the histogram.
	 *
	 * <p>
	 * Can be overridden.
	 * </p>
	 *
	 * @param entry
	 * @return A non-null string
	 */
	protected String label(final StatisticsEntry entry) {
		return entry.name();
	}

	private List<String> generateHistogram(final List<StatisticsEntry> entries, final List<Bucket> buckets) {
		int labelWidth = calculateLabelWidth(entries);
		int maxCount = buckets.stream().mapToInt(bucket1 -> bucket1.count).max().orElse(0);
		int countWidth = calculateCountWidth(maxCount);
		double scale = Math.max(1.0, maxCount / (double) maxDrawRange());

		List<String> lines = new ArrayList<>();
		String headerFormat = "%1$4s | %2$" + labelWidth + "s | %3$" + countWidth + "s | %4$s";
		String bucketFormat = "%1$4s | %2$" + labelWidth + "s | %3$" + countWidth + "d | %4$s";

		lines.add(header(headerFormat));
		lines.add(ruler(headerFormat));
		for (int i = 0; i < buckets.size(); i++) {
			Bucket bucket = buckets.get(i);
			lines.add(bucketLine(bucketFormat, i, scale, bucket));
		}
		return lines;
	}

	private int calculateCountWidth(final int maxCount) {
		int decimals = (int) Math.max(1, Math.floor(Math.log10(maxCount)) + 1);
		return Math.max(5, decimals);
	}

	private int calculateLabelWidth(final List<StatisticsEntry> entries) {
		int maxLabelLength = entries.stream().mapToInt(entry -> label(entry).length()).max().orElse(0);
		return Math.max(5, maxLabelLength);
	}

	private List<Bucket> collectBuckets(final List<StatisticsEntry> entries) {
		return entries
				   .stream()
				   .map(entry -> new Bucket(label(entry), entry.count()))
				   .collect(Collectors.toList());
	}

	private String bucketLine(String format, int index, final double scale, Bucket bucket) {
		String bars = bars(bucket.count, scale);
		return String.format(format, index, bucket.label, bucket.count, bars);
	}

	private String bars(int num, double scale) {
		StringBuilder builder = new StringBuilder();
		int weight = (int) (num / scale);
		for (int j = 0; j < weight; j++) {
			builder.append('■');
		}
		return builder.toString();
	}

	private String header(String format) {
		return String.format(format, "#", "label", "count", "");
	}

	private String ruler(String format) {
		return String.format(
			format,
			"",
			"",
			"",
			"--------------------------------------------------------------------------------"
		).replace(" ", "-");
	}

	static public class Bucket {
		private final String label;
		private final int count;

		public Bucket(String label, int count) {
			if (label == null) {
				throw new IllegalArgumentException("label must not be null");
			}
			this.label = label;
			this.count = count;
		}
	}
}
