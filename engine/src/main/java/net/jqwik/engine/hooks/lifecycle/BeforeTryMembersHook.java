package net.jqwik.engine.hooks.lifecycle;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.*;

import org.junit.platform.commons.support.*;
import org.junit.platform.engine.support.hierarchical.*;

import net.jqwik.api.*;
import net.jqwik.api.lifecycle.*;
import net.jqwik.engine.hooks.*;
import net.jqwik.engine.support.*;

import static org.junit.platform.commons.support.AnnotationSupport.*;

public class BeforeTryMembersHook implements AroundTryHook {

	private static List<Field> findBeforeTryFields(Class<?> testClass) {
		Predicate<Field> isAnnotated = method -> isAnnotated(method, BeforeTry.class);
		return JqwikReflectionSupport.findFieldsPotentiallyOuter(testClass, isAnnotated, HierarchyTraversalMode.TOP_DOWN);
	}

	private void beforeTry(TryLifecycleContext context) {
		List<Field> beforeTryFields = findBeforeTryFields(context.containerClass());
		initializeFields(beforeTryFields, context);
	}

	private void initializeFields(List<Field> fields, TryLifecycleContext context) {
		Object testInstance = context.testInstance();
		ThrowableCollector throwableCollector = new ThrowableCollector(ignore -> false);
		for (Field field : fields) {
			if (JqwikReflectionSupport.isStatic(field)) {
				String message = String.format("Static field <%s> must not be annotated with @BeforeTry.", field);
				throw new JqwikException(message);
			}
			throwableCollector.execute(() -> initializeField(field, testInstance));
		}
		throwableCollector.assertEmpty();
	}

	private void initializeField(Field field, Object target) {
		Store<Object> initialFieldValue = Store.getOrCreate(
			Tuple.of(BeforeTryMembersHook.class, field),
			Lifespan.PROPERTY,
			() -> JqwikReflectionSupport.readFieldPotentiallyOuter(field, target)
		);
		JqwikReflectionSupport.setFieldPotentiallyOuter(field, initialFieldValue.get(), target);
	}

	@Override
	public PropagationMode propagateTo() {
		return PropagationMode.ALL_DESCENDANTS;
	}

	@Override
	public boolean appliesTo(Optional<AnnotatedElement> element) {
		return element.map(e -> e instanceof Method).orElse(false);
	}

	@Override
	public int aroundTryProximity() {
		return Hooks.AroundTry.BEFORE_TRY_MEMBERS_PROXIMITY;
	}

	@Override
	public TryExecutionResult aroundTry(TryLifecycleContext context, TryExecutor aTry, List<Object> parameters) {
		beforeTry(context);
		return aTry.execute(parameters);
	}
}
