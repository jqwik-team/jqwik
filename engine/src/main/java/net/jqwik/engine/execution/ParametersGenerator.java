package net.jqwik.engine.execution;

import java.util.*;

import net.jqwik.api.*;

public interface ParametersGenerator {

	boolean hasNext();

	List<Shrinkable<Object>> next();
}
