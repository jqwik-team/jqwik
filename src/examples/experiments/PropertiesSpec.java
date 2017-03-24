package experiments;

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
