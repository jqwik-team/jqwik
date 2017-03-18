package experiments;

import net.jqwik.api.properties.ForAll;
import net.jqwik.api.properties.Property;

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
