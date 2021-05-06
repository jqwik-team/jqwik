package net.jqwik.api;

import java.lang.reflect.*;

import org.apiguardian.api.*;

import net.jqwik.api.providers.*;

import static org.apiguardian.api.API.Status.*;

@API(status = MAINTAINED, since = "1.0")
public class CannotFindArbitraryException extends JqwikException {

	public CannotFindArbitraryException(TypeUsage typeUsage) {
		this(typeUsage, null);
	}

	public CannotFindArbitraryException(TypeUsage typeUsage, ForAll forAll) {
		this(typeUsage, forAll, null);
	}

	public CannotFindArbitraryException(TypeUsage typeUsage, ForAll forAll, Method method) {
		super(createMessage(typeUsage, forAll, method));
	}

	private static String createMessage(TypeUsage typeUsage, ForAll forAll, Method method) {
		String forAllValue = forAll == null ? "" : forAll.value();
		return createMessage(typeUsage, forAllValue, method);
	}

	private static String createMessage(TypeUsage typeUsage, String forAllValue, Method method) {
		String methodMessage = method == null ? "" : String.format(" in method [%s]", method);
		if (forAllValue.isEmpty())
			return String.format("Cannot find an Arbitrary for Parameter of type [%s]%s", typeUsage, methodMessage);
		else
			return String.format("Cannot find an Arbitrary [%s] for Parameter of type [%s]%s", forAllValue, typeUsage, methodMessage);
	}

}
