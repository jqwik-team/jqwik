package examples.bugs;

import net.jqwik.api.*;

import java.util.*;

class StringListGenerationBug {

	@Property(reporting = ReportingMode.GENERATED)
	void runsForever(@ForAll List<String> aList) {
	}
}
