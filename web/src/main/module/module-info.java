module net.jqwik.web {

	exports net.jqwik.web.api;

	requires transitive net.jqwik.api;
	requires org.opentest4j;

	provides net.jqwik.api.providers.ArbitraryProvider with
		net.jqwik.web.EmailArbitraryProvider,
		net.jqwik.web.WebDomainArbitraryProvider;
}
