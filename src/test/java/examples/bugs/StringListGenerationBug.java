package examples.bugs;

import net.jqwik.api.*;

import java.util.*;

class StringListGenerationBug {

	@Property(reporting = Reporting.GENERATED)
	void runsForever(@ForAll List<String> aList) {
	}
}
