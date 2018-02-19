package examples.docs;

import java.io.*;
import java.util.*;

import net.jqwik.api.*;

class VariableTypedPropertyExamples {

	@Property(reporting = Reporting.GENERATED)
	<T> boolean unboundedGenericTypesAreResolved(@ForAll List<T> items, @ForAll T newItem) {
		items.add(newItem);
		return items.contains(newItem);
	}

	@Property(reporting = Reporting.GENERATED)
	void wildcardTypesAreResolved(@ForAll List<? extends Serializable> items) {
	}

	@Property
	<T extends Serializable & Comparable> void boundedGenericTypesCannotBeResolved(@ForAll List<T> items, @ForAll T newItem) {
	}

}
