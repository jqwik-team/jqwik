package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The context of a test container (a container class or the whole jqwik suite).
 */
@API(status = EXPERIMENTAL, since = "1.0")
public interface ContainerLifecycleContext extends LifecycleContext {

}
