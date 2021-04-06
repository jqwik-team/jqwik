package net.jqwik.time.internal.properties.arbitraries.valueRanges;

public class WithLeapYears {

	private boolean withLeapYears = true;

	public void set(boolean withLeapYears) {
		this.withLeapYears = withLeapYears;
	}

	public boolean get() {
		return withLeapYears;
	}

}
