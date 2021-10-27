module net.jqwik.kotlin {

	exports net.jqwik.kotlin.api;

	requires transitive net.jqwik.api;
	requires org.opentest4j;

	provides net.jqwik.api.lifcycle.LifecycleHook with
		net.jqwik.kotlin.internal.SuspendedPropertyMethodsHook;

	provides net.jqwik.api.configurators.ArbitraryConfigurator with
		net.jqwik.kotlin.internal.KotlinIntRangeConfigurator,
		net.jqwik.kotlin.internal.KotlinUniqueElementsConfigurator;

	provides net.jqwik.api.providers.ArbitraryProvider with
		net.jqwik.kotlin.internal.IntRangeArbitraryProvider,
		net.jqwik.kotlin.internal.SequenceArbitraryProvider,
		net.jqwik.kotlin.internal.PairArbitraryProvider,
		net.jqwik.kotlin.internal.TripleArbitraryProvider;

	provides net.jqwik.api.providers.TypeUsage.Enhancer with
		net.jqwik.kotlin.internal.KTypeEnhancer,
		net.jqwik.kotlin.internal.NullabilityEnhancer,
		net.jqwik.kotlin.internal.ParameterAnnotationEnhancer;

}
