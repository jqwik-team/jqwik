package net.jqwik.engine.execution;

import java.lang.reflect.*;
import java.util.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

public class DefaultParameterInjectionContext implements ParameterResolutionContext {
	private MethodParameter parameter;

	public DefaultParameterInjectionContext(MethodParameter parameter) {this.parameter = parameter;}

	@Override
	public Parameter parameter() {
		return parameter.getRawParameter();
	}

	@Override
	public TypeUsage typeUsage() {
		return TypeUsageImpl.forParameter(parameter);
	}

	@Override
	public int index() {
		return parameter.getIndex();
	}

	@Override
	public Optional<Method> optionalMethod() {
		Executable declaringExecutable = parameter().getDeclaringExecutable();
		if (declaringExecutable instanceof Method) {
			return Optional.of((Method) declaringExecutable);
		}
		return Optional.empty();
	}
}
