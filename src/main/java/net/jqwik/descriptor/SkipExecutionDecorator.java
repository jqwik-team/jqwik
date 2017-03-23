package net.jqwik.descriptor;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.TestTag;
import org.junit.platform.engine.UniqueId;

import java.util.Optional;
import java.util.Set;

public class SkipExecutionDecorator implements TestDescriptor {

	private final TestDescriptor wrapped;
	private final String reason;

	public SkipExecutionDecorator(TestDescriptor wrapped, String reason) {
		this.wrapped = wrapped;
		this.reason = reason;
	}

	@Override
	public UniqueId getUniqueId() {
		return wrapped.getUniqueId();
	}

	@Override
	public String getDisplayName() {
		return wrapped.getDisplayName();
	}

	@Override
	public Set<TestTag> getTags() {
		return wrapped.getTags();
	}

	@Override
	public Optional<TestSource> getSource() {
		return wrapped.getSource();
	}

	@Override
	public Optional<TestDescriptor> getParent() {
		return wrapped.getParent();
	}

	@Override
	public void setParent(TestDescriptor parent) {
		wrapped.setParent(parent);
	}

	@Override
	public Set<? extends TestDescriptor> getChildren() {
		return wrapped.getChildren();
	}

	@Override
	public void addChild(TestDescriptor descriptor) {
		wrapped.addChild(descriptor);
	}

	@Override
	public void removeChild(TestDescriptor descriptor) {
		wrapped.removeChild(descriptor);
	}

	@Override
	public void removeFromHierarchy() {
		wrapped.removeFromHierarchy();
	}

	@Override
	public Type getType() {
		return wrapped.getType();
	}

	@Override
	public Optional<? extends TestDescriptor> findByUniqueId(UniqueId uniqueId) {
		return wrapped.findByUniqueId(uniqueId);
	}

	public String getSkippingReason() {
		return reason;
	}
}
