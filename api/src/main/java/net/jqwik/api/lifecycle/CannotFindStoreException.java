package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * Experimental feature. Not ready for public usage yet.
 */
@API(status = EXPERIMENTAL, since = "1.2.3")
@NonNullApi
public class CannotFindStoreException extends JqwikException {
	public CannotFindStoreException(Object identifier, String retrieverId) {
		super(createMessage(identifier, retrieverId));
	}

	private static String createMessage(Object identifier, String retrieverId) {
		return String.format(
			"Cannot find store with identifier [%s] for [%s]",
			identifier.toString(),
			retrieverId
		);
	}
}
