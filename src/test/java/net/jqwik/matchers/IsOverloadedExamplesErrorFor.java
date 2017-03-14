package net.jqwik.matchers;

import org.hamcrest.Description;
import org.mockito.ArgumentMatcher;

import net.jqwik.descriptor.OverloadedMethodsErrorDescriptor;

public class IsOverloadedExamplesErrorFor extends ArgumentMatcher<OverloadedMethodsErrorDescriptor> {

	private final Class<?> containerClass;
	private final String overloadedMethodName;

	public IsOverloadedExamplesErrorFor(Class<?> containerClass, String overloadedMethodName) {
		this.containerClass = containerClass;
		this.overloadedMethodName = overloadedMethodName;
	}

	@Override
	public boolean matches(Object argument) {
		if (argument.getClass() != OverloadedMethodsErrorDescriptor.class)
			return false;
		OverloadedMethodsErrorDescriptor descriptor = (OverloadedMethodsErrorDescriptor) argument;
		return descriptor.getContainerClass() == containerClass && descriptor.getOverloadedMethodName().equals(overloadedMethodName);
	}

	@Override
	public void describeTo(Description description) {
		description.appendText("is OverloadedExamplesError for " + containerClass.toString() + "::" +overloadedMethodName);
	}

}
