
package net.jqwik;

import static java.util.Arrays.asList;

import org.opentest4j.AssertionFailedError;

public class PropertyVerificationFailure extends AssertionFailedError {

	private final String displayName;
	private final Object[] args;

	public PropertyVerificationFailure(String displayName, Object[] args) {
		this(displayName, args, null);
	}

	public PropertyVerificationFailure(String displayName, Object[] args, Throwable cause) {
		super(null, cause);
		this.displayName = displayName;
		this.args = args;
	}

	@Override
	public String getMessage() {
		return String.format("Property '%s' falsified for args %s", displayName, asList(args));
	}

	public Object[] getArgs() {
		return args;
	}

	public String getDisplayName() {
		return displayName;
	}
}
