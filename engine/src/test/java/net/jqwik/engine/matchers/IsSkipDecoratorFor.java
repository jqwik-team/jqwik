package net.jqwik.engine.matchers;

import org.mockito.*;

import net.jqwik.engine.descriptor.*;

class IsSkipDecoratorFor implements ArgumentMatcher<SkipExecutionDecorator> {

	private final Class<?> containerClass;
	private final String methodName;

	IsSkipDecoratorFor(Class<?> containerClass, String methodName) {
		this.containerClass = containerClass;
		this.methodName = methodName;
	}

	@Override
	public boolean matches(SkipExecutionDecorator descriptor) {
		if (methodName != null) {
			PropertyMethodDescriptor wrapped = (PropertyMethodDescriptor) descriptor.getWrapped();
			return wrapped.getContainerClass().equals(containerClass) //
					   && wrapped.getTargetMethod().getName().equals(methodName);
		} else {
			ContainerClassDescriptor wrapped = (ContainerClassDescriptor) descriptor.getWrapped();
			return wrapped.getContainerClass().equals(containerClass);
		}
	}

}
