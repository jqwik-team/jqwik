module net.jqwik.api {

	exports net.jqwik.api;
	exports net.jqwik.api.arbitraries;
	exports net.jqwik.api.configurators;
	exports net.jqwik.api.constraints;
	exports net.jqwik.api.domains;
	// hide net.jqwik.api.facades;
	exports net.jqwik.api.footnotes;
	exports net.jqwik.api.lifecycle;
	exports net.jqwik.api.providers;
	exports net.jqwik.api.sessions;
	exports net.jqwik.api.stateful;
	exports net.jqwik.api.statistics;

	requires org.junit.platform.commons;
	requires org.opentest4j;

	// TODO provides ?

	// TODO uses *Facade
}
