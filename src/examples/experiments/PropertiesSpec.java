package experiments;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;

public class PropertiesSpec {

	@Property
	@Assume("shorter than 3")
	boolean isTrue(@ForAll String aShortString) {
		return true;
	}

	@Assumption
	boolean shorterThan3(String aShortString) {
		return aShortString.length() < 10;
	}
}
