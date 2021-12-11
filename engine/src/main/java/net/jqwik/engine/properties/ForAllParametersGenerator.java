package net.jqwik.engine.properties;

import java.util.*;

import net.jqwik.api.*;

public interface ForAllParametersGenerator extends Iterator<List<Shrinkable<Object>>> {

	default int edgeCasesTotal() {
		return 0;
	}

	default int edgeCasesTried() {
		return 0;
	}

}
