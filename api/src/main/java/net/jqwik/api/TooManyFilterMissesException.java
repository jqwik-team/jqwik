package net.jqwik.api;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public class TooManyFilterMissesException extends JqwikException {
	public TooManyFilterMissesException(String message) {
		super(message);
	}
}
