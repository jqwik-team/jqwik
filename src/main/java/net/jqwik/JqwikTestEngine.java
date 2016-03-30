
package net.jqwik;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;
import java.util.logging.Logger;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.internal.GeometricDistribution;
import com.pholser.junit.quickcheck.internal.generator.GeneratorRepository;
import com.pholser.junit.quickcheck.internal.generator.ServiceLoaderGeneratorSource;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import org.junit.gen5.commons.util.AnnotationUtils;
import org.junit.gen5.commons.util.ReflectionUtils;
import org.junit.gen5.engine.EngineDiscoveryRequest;
import org.junit.gen5.engine.ExecutionRequest;
import org.junit.gen5.engine.TestDescriptor;
import org.junit.gen5.engine.UniqueId;
import org.junit.gen5.engine.discovery.ClassSelector;
import org.junit.gen5.engine.discovery.MethodSelector;
import org.junit.gen5.engine.discovery.UniqueIdSelector;
import org.junit.gen5.engine.support.descriptor.JavaSource;
import org.junit.gen5.engine.support.hierarchical.HierarchicalTestEngine;
import org.slf4j.LoggerFactory;

public class JqwikTestEngine extends HierarchicalTestEngine<JqwikExecutionContext> {

	private static final Logger LOG = Logger.getLogger(JqwikTestEngine.class.getName());

	private static final String ENGINE_ID = "jqwik";

	public static final String SEGMENT_TYPE_CLASS = "jqwik-class";
	public static final String SEGMENT_TYPE_METHOD = "jqwik-method";
	public static final String SEGMENT_TYPE_SEED = "jqwik-seed";

	private final GeneratorRepository repo;
	private final GeometricDistribution distro;
	private final org.slf4j.Logger seedLog;

	// Test runs should produce the same results for one instantiation of the test engine
	private long seed = new Random().nextLong();
	private final SourceOfRandomness generatorRepositoryRandom;

	public JqwikTestEngine() {
		generatorRepositoryRandom = new SourceOfRandomness(new Random());
		repo = new GeneratorRepository(generatorRepositoryRandom).register(new ServiceLoaderGeneratorSource());
		distro = new GeometricDistribution();
		seedLog = LoggerFactory.getLogger("junit-quickcheck.seed-reporting");
	}

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	protected JqwikExecutionContext createExecutionContext(ExecutionRequest request) {
		// Generators should have same "random" behaviour on each test run of same engine
		generatorRepositoryRandom.setSeed(seed);
		return new JqwikExecutionContext();
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueEngineId) {
		JqwikEngineDescriptor engineDescriptor = new JqwikEngineDescriptor(uniqueEngineId);
		resolveSelectors(discoveryRequest, engineDescriptor, new Random(seed));
		return engineDescriptor;
	}

	private void resolveSelectors(EngineDiscoveryRequest discoveryRequest, JqwikEngineDescriptor engineDescriptor,
			Random random) {
		discoveryRequest.getSelectorsByType(ClassSelector.class).forEach(
			classSelector -> resolveClass(classSelector, engineDescriptor, random));
		discoveryRequest.getSelectorsByType(MethodSelector.class).forEach(
			methodSelector -> resolveMethod(methodSelector, engineDescriptor, random));
		discoveryRequest.getSelectorsByType(UniqueIdSelector.class).forEach(
			uniqueIdSelector -> resolveUniqueId(uniqueIdSelector, engineDescriptor, random));
	}

	private void resolveUniqueId(UniqueIdSelector uniqueIdSelector, JqwikEngineDescriptor engineDescriptor,
			Random random) {
		UniqueId uniqueId = UniqueId.parse(uniqueIdSelector.getUniqueId());
		List<UniqueId.Segment> segments = uniqueId.getSegments();
		segments.remove(0); // Drop engine segment

		resolveUniqueIdRest(segments, engineDescriptor, random);
	}

	private void resolveUniqueIdRest(List<UniqueId.Segment> rest, TestDescriptor parent, Random random) {
		if (rest.isEmpty())
			return;

		UniqueId.Segment head = rest.remove(0);
		if (head.getType().equals(SEGMENT_TYPE_CLASS)) {
			Optional<Class<?>> containerClass = ReflectionUtils.loadClass(head.getValue());
			if (containerClass.isPresent()) {
				Class<?> testClass = containerClass.get();
				JqwikClassDescriptor classDescriptor = resolveClassWithoutChildren(testClass, parent);
				if (rest.isEmpty())
					resolveClassMethods(testClass, classDescriptor, random);
				else
					resolveUniqueIdRest(rest, classDescriptor, random);
			}
			else {
				return;
			}
		} else if (head.getType().equals(SEGMENT_TYPE_METHOD)) {
			JqwikClassDescriptor classDescriptor = (JqwikClassDescriptor) parent;
			Class<?> testClass = classDescriptor.getTestClass();
			String methodName = head.getValue();
			Method propertyMethod = ReflectionUtils.findMethods(testClass, m -> m.getName().equals(methodName)).get(0);
			resolveMethodForClass(propertyMethod, classDescriptor, random);
		}
	}

	private void resolveMethod(MethodSelector methodSelector, JqwikEngineDescriptor engineDescriptor, Random random) {
		JqwikClassDescriptor classDescriptor = resolveClassWithoutChildren(methodSelector.getTestClass(),
			engineDescriptor);
		resolveMethodForClass(methodSelector.getTestMethod(), classDescriptor, random);
	}

	private void resolveClass(ClassSelector classSelector, JqwikEngineDescriptor engineDescriptor, Random random) {
		Class<?> testClass = classSelector.getTestClass();
		JqwikClassDescriptor classDescriptor = resolveClassWithoutChildren(testClass, engineDescriptor);
		resolveClassMethods(testClass, classDescriptor, random);
	}

	private JqwikClassDescriptor resolveClassWithoutChildren(Class<?> testClass, TestDescriptor engineDescriptor) {
		UniqueId uniqueId = engineDescriptor.getUniqueId().append(SEGMENT_TYPE_CLASS, testClass.getName());
		JqwikClassDescriptor classDescriptor = new JqwikClassDescriptor(uniqueId, testClass);
		engineDescriptor.addChild(classDescriptor);
		return classDescriptor;
	}

	private void resolveClassMethods(Class<?> testClass, JqwikClassDescriptor classDescriptor, Random random) {
		Predicate<Method> isPropertyMethod = method -> AnnotationUtils.isAnnotated(method, Property.class);
		ReflectionUtils.findMethods(testClass, isPropertyMethod).forEach(propertyMethod -> {
			resolveMethodForClass(propertyMethod, classDescriptor, random);
		});

	}

	private void resolveMethodForClass(Method propertyMethod, JqwikClassDescriptor classDescriptor, Random random) {
		resolveMethodForClass(propertyMethod, classDescriptor, random.nextLong());
	}

	private void resolveMethodForClass(Method propertyMethod, JqwikClassDescriptor classDescriptor, long propertySeed) {
		if (ReflectionUtils.isPrivate(propertyMethod)) {
			LOG.warning(() -> String.format("Method '%s' not a property because it is private",
				methodDescription(propertyMethod)));
			return;
		}

		UniqueId uniqueId = classDescriptor.getUniqueId().append(SEGMENT_TYPE_METHOD, propertyMethod.getName()).append(
			SEGMENT_TYPE_SEED, Long.toString(propertySeed));

		PropertyStatement propertyStatement = new PropertyStatement(propertyMethod, classDescriptor.getTestClass(),
			propertyMethod.getName(), repo, distro, propertySeed, seedLog);

		classDescriptor.addChild(
			new JqwikPropertyDescriptor(uniqueId, propertyStatement, new JavaSource(propertyMethod)));
	}

	private String methodDescription(Method method) {
		return method.getDeclaringClass().getName() + "#" + method.getName();
	}

}
