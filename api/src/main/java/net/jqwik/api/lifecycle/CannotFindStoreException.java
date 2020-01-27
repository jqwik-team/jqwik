package net.jqwik.api.lifecycle;

import net.jqwik.api.*;

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
