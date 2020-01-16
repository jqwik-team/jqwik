package net.jqwik.engine.execution.lifecycle;

import org.junit.platform.engine.*;

import net.jqwik.api.*;

public class CannotFindStoreException extends JqwikException {
	public CannotFindStoreException(String name, TestDescriptor retriever) {
		super(createMessage(name, retriever));
	}

	static String createMessage(String name, TestDescriptor retriever) {
		return String.format("Cannot find store with name [%s] for [%s]", name, retriever.getUniqueId());
	}
}
