package jqwik.experiments;

public class SpecWithLifecycle {

	private final String aString;

	public SpecWithLifecycle() {
		aString = "a new string";
	}

	@Fact
	boolean test(Connection aConnection) {
		return aString.equalsIgnoreCase("A NEW STRING") && aConnection.url.startsWith("http:");
	}

	@Fixture
	class Connection implements ForEachLifeCycle {
		private final String url;

		public Connection() {
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

		@RequireFixture
		OtherFixture other;
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

	@Fixture
	static class AStaticFixture implements ForAllLifeCycle {
		@Override
		public void beforeAll() {
			System.out.println("before all");
		}

		@Override
		public void afterAll() {
			System.out.println("after all");
		}
	}
}
