package net.jqwik.api;

import org.apiguardian.api.*;

import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public class CannotFindArbitraryException extends JqwikException {

	public CannotFindArbitraryException(TypeUsage typeUsage) {
		super(createMessage(typeUsage, ""));
	}

	public CannotFindArbitraryException(TypeUsage typeUsage, ForAll forAll) {
		super(createMessage(typeUsage, forAll));
	}

	private static String createMessage(TypeUsage typeUsage, ForAll forAll) {
		String forAllValue = forAll == null ? "" : forAll.value();
		return createMessage(typeUsage, forAllValue);
	}

	private static String createMessage(TypeUsage typeUsage, String forAllValue) {
		if (forAllValue.isEmpty())
			return String.format("Cannot find an Arbitrary for Parameter of type [%s]", typeUsage);
		else
			return String.format("Cannot find an Arbitrary [%s] for Parameter of type [%s]", forAllValue, typeUsage);
	}

}
