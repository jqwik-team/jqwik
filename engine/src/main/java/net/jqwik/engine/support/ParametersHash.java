package net.jqwik.engine.support;

import java.io.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class ParametersHash implements Serializable {

	private final int hash;

	public ParametersHash(Method method) {
		this(calculateHash(method));
	}

	public ParametersHash(int hash) {
		this.hash = hash;
	}

	// Public for testing purposes
	public static int calculateHash(Method method) {
		Object[] values = Arrays.stream(method.getParameters())
								.map(ParametersHash::hashParameter)
								.toArray();
		return Objects.hash(values);
	}

	private static int hashParameter(Parameter parameter) {
		int annotationHash = hashAnnotations(JqwikAnnotationSupport.findAllAnnotations(parameter));
		return Objects.hash(
			parameter.getType(),
			annotationHash
		);
	}

	private static int hashAnnotations(List<Annotation> annotations) {
		Object[] values = annotations.stream()
									 .map(annotation -> annotation.hashCode())
									 .toArray();
		return Objects.hash(values);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ParametersHash that = (ParametersHash) o;
		return hash == that.hash;
	}

	@Override
	public int hashCode() {
		return hash;
	}

	@Override
	public String toString() {
		return String.format("ParametersHash(%s)", hash);
	}

	public boolean matchesMethod(Method method) {
		return hash == calculateHash(method);
	}
}
