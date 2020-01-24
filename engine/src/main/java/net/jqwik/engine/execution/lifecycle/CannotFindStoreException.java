package net.jqwik.engine.execution.lifecycle;

import org.junit.platform.engine.*;

import net.jqwik.api.*;

public class CannotFindStoreException extends JqwikException {
	public CannotFindStoreException(Object identifier, TestDescriptor retriever) {
		super(createMessage(identifier, retriever));
	}

	static String createMessage(Object identifier, TestDescriptor retriever) {
		return String.format(
			"Cannot find store with identifier [%s] for [%s]",
			identifier.toString(),
			retriever.getUniqueId()
		);
	}
}
