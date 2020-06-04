package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.engine.support.*;

class SampleReporter {
	private final String headline;
	private final List<Object> sample;

	public SampleReporter(String headline, List<Object> sample) {
		this.headline = headline;
		this.sample = sample;
	}

	public void reportTo(StringBuilder builder) {
		builder.append(String.format("%n"));
		reportHeadline(builder);
		String line = JqwikStringSupport.displayString(sample);
		reportLine(builder, 1, line);
	}

	private void reportLine(StringBuilder builder, int indent, String line) {
		String indentation = multiply(' ', indent * 2);
		builder.append(String.format("%s%s%n", indentation, line));
	}

	private void reportHeadline(StringBuilder builder) {
		builder.append(String.format("%s%n", headline));
		builder.append(String.format("%s%n", multiply('-', headline.length())));
	}

	private String multiply(char c, int times) {
		StringBuilder builder = new StringBuilder();
		for (int j = 0; j < times; j++) {
			builder.append(c);
		}
		return builder.toString();
	}

}
