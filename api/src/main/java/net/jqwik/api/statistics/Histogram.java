package net.jqwik.api.statistics;

import java.util.*;
import java.util.stream.*;

import net.jqwik.api.*;
import net.jqwik.api.Tuple.*;

public class Histogram implements StatisticsReportFormat {
	@Override
	public List<String> formatReport(final List<StatisticsEntry> entries) {
		Tuple3<List<Bucket>, Integer, Integer> bucketsLabelWidthDecimals = calculateBuckets(entries);

		List<Bucket> buckets = bucketsLabelWidthDecimals.get1();
		int labelWidth = Math.max(5, bucketsLabelWidthDecimals.get2());
		int decimals = Math.max(5, bucketsLabelWidthDecimals.get3());
		int maxCount = buckets.stream().mapToInt(Bucket::count).max().orElse(0);

		List<String> lines = new ArrayList<>();
		String headerFormat = "%1$4s | %2$" + labelWidth + "s | %3$" + decimals + "s | %4$s";
		String bucketFormat = "%1$4s | %2$" + labelWidth + "s | %3$" + decimals + "d | %4$s";
		double scale = Math.max(1.0, maxCount / 80.0);

		lines.add(header(headerFormat));
		lines.add(ruler(headerFormat));
		for (int i = 0; i < buckets.size(); i++) {
			Bucket bucket = buckets.get(i);
			lines.add(bucketLine(bucketFormat, i, scale, bucket));
		}
		return lines;
	}

	private String bucketLine(String format, int index, final double scale, Bucket bucket) {
		String bars = bars(bucket.count(), scale);
		return String.format(format, index, bucket.label(), bucket.count(), bars);
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

	private Tuple3<List<Bucket>, Integer, Integer> calculateBuckets(final List<StatisticsEntry> entries) {
		OptionalInt optionalMaxNameLength = entries.stream().mapToInt(entry -> entry.name().length()).max();
		int maxNameLength = optionalMaxNameLength.orElse(0);
		Comparator<? super StatisticsEntry> comparator = (left, right) -> {
			String leftName = padLeft(left.name(), maxNameLength);
			String rightName = padLeft(right.name(), maxNameLength);
			return leftName.compareTo(rightName);
		};
		entries.sort(comparator);

		int maxCount = entries.stream().mapToInt(StatisticsEntry::count).max().orElse(0);
		int decimals = (int) Math.max(1, Math.floor(Math.log10(maxCount)) + 1);

		List<Bucket> buckets = entries
								   .stream()
								   .map(entry -> new Bucket(padLeft(entry.name(), maxNameLength), entry.count()))
								   .collect(Collectors.toList());

		return Tuple.of(buckets, maxNameLength, decimals);
	}

	private String padLeft(String name, int length) {
		return String.format("%1$" + length + "s", name);
	}

	static public class Bucket {
		private final String label;
		private final int count;

		public Bucket(String label, int count) {
			this.label = label;
			this.count = count;
		}

		public String label() {
			return label;
		}

		public int count() {
			return count;
		}

	}
}
