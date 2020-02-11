package net.jqwik.engine.execution;

import java.lang.reflect.*;

import net.jqwik.api.lifecycle.*;
import net.jqwik.api.providers.*;
import net.jqwik.engine.facades.*;
import net.jqwik.engine.support.*;

public class DefaultParameterInjectionContext implements ParameterInjectionContext {
	private MethodParameter parameter;

	public DefaultParameterInjectionContext(MethodParameter parameter) {this.parameter = parameter;}

	@Override
	public Parameter parameter() {
		return parameter.getRawParameter();
	}

	@Override
	public TypeUsage usage() {
		return TypeUsageImpl.forParameter(parameter);
	}
}
