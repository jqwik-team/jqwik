
package jqwik.experiments;

@Spec
public class SpecWithFixtureClasses implements ForAllLifeCycle {

	private final String aString;

	public SpecWithFixtureClasses() {
		aString = "a new string";
	}

	@Fact
	boolean test(Connection aConnection) {
		return aString.equalsIgnoreCase("A NEW STRING") && aConnection.url.startsWith("http:");
	}

	@Fixture(dependsOn = OtherFixture.class)
	class Connection implements ForEachLifeCycle {
		private final String url;

		public Connection(OtherFixture other) {
			url = "http://somewhere.net";
		}

		@Override
		public void beforeEach() {
			System.out.println("before each");
		}

		@Override
		public void afterEach() {
			System.out.println("after each");
		}

	}

	@Fixture
	class OtherFixture implements ForEachLifeCycle {
		@Override
		public void beforeEach() {
			System.out.println("other before each");
		}

		@Override
		public void afterEach() {
			System.out.println("other after each");
		}

	}

	@Override
	public void beforeAll() {
		System.out.println("before all");
	}

	@Override
	public void afterAll() {
		System.out.println("after all");
	}
}
