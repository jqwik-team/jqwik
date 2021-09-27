package net.jqwik.api.lifecycle;

import org.apiguardian.api.*;

import net.jqwik.api.*;

import static org.apiguardian.api.API.Status.*;

/**
 * The context of a test container (a container class or the whole jqwik suite).
 */
@API(status = MAINTAINED, since = "1.4.0")
@NonNullApi
public interface ContainerLifecycleContext extends LifecycleContext {

}
