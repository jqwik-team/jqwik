package jqwik.experiments;

@Spec("A simple Specification")
public class SimpleSpec implements ForEachLifeCycle {

	@Override
	public void beforeEach() {
		System.out.println("before each");
	}

	@Override
	public void afterEach() {
		System.out.println("after each");
	}

	@Fact("A String is a String")
	boolean aStringIsAString() {
		return "aString" instanceof String;
	}

	@Fact
	boolean aNameIsAString(Object aName, Object other) {
		return aName instanceof String;
	}

	@Fixture
	Object aName() {
		return "Johannes";
	}

	@Fixture(referBy = "other")
	Object otherName() {
		return "Marcus";
	}

}
