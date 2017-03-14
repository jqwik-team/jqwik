package net.jqwik.descriptor;

import net.jqwik.discovery.JqwikDiscoverer;
import org.junit.platform.engine.UniqueId;

public class OverloadedExampleMethodDescriptor extends ExampleMethodDescriptor {

    public OverloadedExampleMethodDescriptor(ExampleMethodDescriptor toOverload, int id) {
        this(toOverload.getUniqueId().append(JqwikDiscoverer.OVERLOADED_SEGMENT_TYPE, String.valueOf(id)), toOverload, id);
    }

    public OverloadedExampleMethodDescriptor(UniqueId uniqueId, ExampleMethodDescriptor toOverload, int id) {
        super(uniqueId, toOverload.getExampleMethod(), toOverload.gerContainerClass());
    }

}
