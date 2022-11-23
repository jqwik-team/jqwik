module net.jqwik.engine {

	opens net.jqwik.engine.properties.configurators to org.junit.platform.commons;

	requires java.logging;
	requires net.jqwik.api;
	requires org.junit.platform.engine;

	uses net.jqwik.api.SampleReportingFormat;
	uses net.jqwik.api.configurators.ArbitraryConfigurator;
	uses net.jqwik.api.lifecycle.LifecycleHook;
	uses net.jqwik.api.providers.ArbitraryProvider;
	uses net.jqwik.api.providers.TypeUsage.Enhancer;

	provides net.jqwik.api.facades.ReflectionSupportFacade with net.jqwik.engine.facades.ReflectionSupportFacadeImpl;
	provides net.jqwik.api.facades.ShrinkingSupportFacade with net.jqwik.engine.facades.ShrinkingSupportFacadeImpl;
	provides net.jqwik.api.facades.TestingSupportFacade with net.jqwik.engine.facades.TestingSupportFacadeImpl;

	provides net.jqwik.api.Arbitraries.ArbitrariesFacade with net.jqwik.engine.facades.ArbitrariesFacadeImpl;
	provides net.jqwik.api.Arbitrary.ArbitraryFacade with net.jqwik.engine.facades.ArbitraryFacadeImpl;
	provides net.jqwik.api.Combinators.CombinatorsFacade with net.jqwik.engine.facades.CombinatorsFacadeImpl;
	provides net.jqwik.api.EdgeCases.EdgeCasesFacade with net.jqwik.engine.facades.EdgeCasesFacadeImpl;
	provides net.jqwik.api.ExhaustiveGenerator.ExhaustiveGeneratorFacade with net.jqwik.engine.facades.ExhaustiveGeneratorFacadeImpl;
	provides net.jqwik.api.Functions.FunctionsFacade with net.jqwik.engine.facades.FunctionsFacadeImpl;
	provides net.jqwik.api.JavaBeanReportingFormat.JavaBeanReportingFormatFacade with net.jqwik.engine.facades.JavaBeanReportingFormatFacadeImpl;
	provides net.jqwik.api.RandomDistribution.RandomDistributionFacade with net.jqwik.engine.facades.RandomDistributionFacadeImpl;
	provides net.jqwik.api.RandomGenerator.RandomGeneratorFacade with net.jqwik.engine.facades.RandomGeneratorFacadeImpl;
	provides net.jqwik.api.Shrinkable.ShrinkableFacade with net.jqwik.engine.facades.ShrinkableFacadeImpl;
	provides net.jqwik.api.domains.DomainContext.DomainContextFacade with net.jqwik.engine.facades.DomainContextFacadeImpl;
	provides net.jqwik.api.lifecycle.Store.StoreFacade with net.jqwik.engine.facades.StoreFacadeImpl;
	provides net.jqwik.api.providers.TypeUsage.TypeUsageFacade with net.jqwik.engine.facades.TypeUsageFacadeImpl;
	provides net.jqwik.api.sessions.JqwikSession.JqwikSessionFacade with net.jqwik.engine.facades.JqwikSessionFacadeImpl;
	provides net.jqwik.api.statistics.Statistics.StatisticsFacade with net.jqwik.engine.facades.StatisticsFacadeImpl;
	provides net.jqwik.api.state.Chain.ChainFacade with net.jqwik.engine.facades.ChainFacadeImpl;
	provides net.jqwik.api.state.ActionChain.ActionChainFacade with net.jqwik.engine.facades.ActionChainFacadeImpl;

	provides net.jqwik.api.providers.ArbitraryProvider with
		net.jqwik.engine.providers.BigDecimalArbitraryProvider,
		net.jqwik.engine.providers.BigIntegerArbitraryProvider,
		net.jqwik.engine.providers.BooleanArbitraryProvider,
		net.jqwik.engine.providers.ByteArbitraryProvider,
		net.jqwik.engine.providers.CharacterArbitraryProvider,
		net.jqwik.engine.providers.DoubleArbitraryProvider,
		net.jqwik.engine.providers.FloatArbitraryProvider,
		net.jqwik.engine.providers.IntegerArbitraryProvider,
		net.jqwik.engine.providers.LongArbitraryProvider,
		net.jqwik.engine.providers.ObjectArbitraryProvider,
		net.jqwik.engine.providers.RandomArbitraryProvider,
		net.jqwik.engine.providers.ShortArbitraryProvider,
		net.jqwik.engine.providers.StringArbitraryProvider;

	provides net.jqwik.api.lifecycle.LifecycleHook with
		net.jqwik.engine.hooks.DisabledHook,
		net.jqwik.engine.hooks.ResolveReporterHook,
		net.jqwik.engine.hooks.lifecycle.AutoCloseableHook,
		net.jqwik.engine.hooks.lifecycle.ContainerLifecycleMethodsHook,
		net.jqwik.engine.hooks.lifecycle.PropertyLifecycleMethodsHook,
		net.jqwik.engine.hooks.lifecycle.TryLifecycleMethodsHook,
		net.jqwik.engine.hooks.statistics.StatisticsHook;

	provides net.jqwik.api.configurators.ArbitraryConfigurator with
		net.jqwik.engine.properties.configurators.BigDecimalRangeConfigurator,
		net.jqwik.engine.properties.configurators.BigIntegerRangeConfigurator,
		net.jqwik.engine.properties.configurators.ByteRangeConfigurator,
		net.jqwik.engine.properties.configurators.CharsConfigurator,
		net.jqwik.engine.properties.configurators.DoubleRangeConfigurator,
		net.jqwik.engine.properties.configurators.FloatRangeConfigurator,
		net.jqwik.engine.properties.configurators.IntRangeConfigurator,
		net.jqwik.engine.properties.configurators.LongRangeConfigurator,
		net.jqwik.engine.properties.configurators.NegativeConfigurator,
		net.jqwik.engine.properties.configurators.NotBlankConfigurator,
		net.jqwik.engine.properties.configurators.PositiveConfigurator,
		net.jqwik.engine.properties.configurators.ScaleConfigurator,
		net.jqwik.engine.properties.configurators.ShortRangeConfigurator,
		net.jqwik.engine.properties.configurators.StringLengthConfigurator,
		net.jqwik.engine.properties.configurators.WhitespaceConfigurator;

	provides net.jqwik.api.SampleReportingFormat with
		net.jqwik.engine.execution.reporting.ArrayReportingFormat,
		net.jqwik.engine.execution.reporting.OptionalReportingFormat,
		net.jqwik.engine.execution.reporting.StreamReportingFormat,
		net.jqwik.engine.properties.stateful.ActionSequenceReportingFormat;

	provides org.junit.platform.engine.TestEngine with
		net.jqwik.engine.JqwikTestEngine;

}
