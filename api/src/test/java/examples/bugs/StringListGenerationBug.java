package examples.bugs;

import net.jqwik.api.*;

import java.util.*;

class StringListGenerationBug {

	@Property @Report(Reporting.GENERATED)
	void runsForever(@ForAll List<String> aList) {
	}
}
