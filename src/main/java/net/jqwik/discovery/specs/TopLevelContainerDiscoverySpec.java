package net.jqwik.discovery.specs;

import net.jqwik.api.Group;
import net.jqwik.discovery.predicates.IsTopLevelClass;

import java.util.function.Predicate;

import static net.jqwik.support.JqwikReflectionSupport.isStatic;

public class TopLevelContainerDiscoverySpec extends PotentialContainerDiscoverySpec {

	private final static Predicate<Class<?>> isTopLevelClass = new IsTopLevelClass();
	private final static Predicate<Class<?>> isStaticNonGroupMember = candidate -> isStatic(candidate) && !candidate.isAnnotationPresent(Group.class);

	@Override
	public boolean shouldBeDiscovered(Class<?> candidate) {
		if (!super.shouldBeDiscovered(candidate))
			return false;
		return isTopLevelClass.test(candidate) || isStaticNonGroupMember.test(candidate);
	}

	@Override
	public boolean butSkippedOnExecution(Class<?> candidate) {
		return super.butSkippedOnExecution(candidate);
	}

	@Override
	public String skippingReason(Class<?> candidate) {
		return super.skippingReason(candidate);
	}
}
