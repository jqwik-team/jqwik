package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.engine.support.*;

class SampleReporter {
	private final String headline;
	private final List<Object> sample;
	private final List<String> parameterNames;

	public SampleReporter(String headline, List<Object> sample, List<String> parameterNames) {
		if (sample.size() != parameterNames.size()) {
			throw new IllegalArgumentException("Number of sample parameters must be equal to number of parameter names");
		}
		this.headline = headline;
		this.sample = sample;
		this.parameterNames = parameterNames;
	}

	public void reportTo(StringBuilder builder) {
		builder.append(String.format("%n"));
		reportHeadline(builder);
		reportParameters(builder);
	}

	private void reportParameters(final StringBuilder builder) {
		for (int i = 0; i < parameterNames.size(); i++) {
			String parameterName = parameterNames.get(i);
			Object parameterValue = sample.get(i);
			String line = String.format("%s: %s", parameterName, JqwikStringSupport.displayString(parameterValue));
			reportLine(builder, 1, line);
		}
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
