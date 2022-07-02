module net.jqwik.api {

	exports net.jqwik.api;
	exports net.jqwik.api.arbitraries;
	exports net.jqwik.api.configurators;
	exports net.jqwik.api.constraints;
	exports net.jqwik.api.domains;
	exports net.jqwik.api.facades;
	exports net.jqwik.api.footnotes;
	exports net.jqwik.api.lifecycle;
	exports net.jqwik.api.providers;
	exports net.jqwik.api.sessions;
	exports net.jqwik.api.stateful;
	exports net.jqwik.api.statistics;
	exports net.jqwik.api.support;

	requires java.logging;
	requires org.junit.platform.commons;
	requires org.opentest4j;

	uses net.jqwik.api.facades.ReflectionSupportFacade;
	uses net.jqwik.api.facades.ShrinkingSupportFacade;
	uses net.jqwik.api.facades.TestingSupportFacade;

	uses net.jqwik.api.Arbitraries.ArbitrariesFacade;
	uses net.jqwik.api.Arbitrary.ArbitraryFacade;
	uses net.jqwik.api.Combinators.CombinatorsFacade;
	uses net.jqwik.api.EdgeCases.EdgeCasesFacade;
	uses net.jqwik.api.ExhaustiveGenerator.ExhaustiveGeneratorFacade;
	uses net.jqwik.api.Functions.FunctionsFacade;
	uses net.jqwik.api.JavaBeanReportingFormat.JavaBeanReportingFormatFacade;
	uses net.jqwik.api.RandomDistribution.RandomDistributionFacade;
	uses net.jqwik.api.RandomGenerator.RandomGeneratorFacade;
	uses net.jqwik.api.Shrinkable.ShrinkableFacade;
	uses net.jqwik.api.domains.DomainContext.DomainContextFacade;
	uses net.jqwik.api.lifecycle.Store.StoreFacade;
	uses net.jqwik.api.providers.TypeUsage.TypeUsageFacade;
	uses net.jqwik.api.sessions.JqwikSession.JqwikSessionFacade;
	uses net.jqwik.api.statistics.Statistics.StatisticsFacade;
}
