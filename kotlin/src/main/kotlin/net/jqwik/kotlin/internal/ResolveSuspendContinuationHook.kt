package net.jqwik.kotlin.internal

import net.jqwik.api.lifecycle.LifecycleContext
import net.jqwik.api.lifecycle.ParameterResolutionContext
import net.jqwik.api.lifecycle.PropagationMode
import net.jqwik.api.lifecycle.ResolveParameterHook
import net.jqwik.api.lifecycle.ResolveParameterHook.ParameterSupplier
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Provide continuation object in those cases where property methods are modified with "suspend"
 */
class ResolveSuspendContinuationHook : ResolveParameterHook {

    override fun propagateTo(): PropagationMode = PropagationMode.ALL_DESCENDANTS

    override fun appliesTo(element: Optional<AnnotatedElement>) = element.map { e -> e is Method }.orElse(false)

    override fun resolve(
        parameterContext: ParameterResolutionContext,
        lifecycleContext: LifecycleContext
    ): Optional<ParameterSupplier> {
        if (!parameterContext.typeUsage().isOfType(Continuation::class.java)) {
            return Optional.empty()
        }
        if (parameterContext.index() != 0) {
            return Optional.empty()
        }
        if (parameterContext.parameter().kotlinParameter != null) {
            return Optional.empty()
        }
        if (!parameterContext.optionalMethod().isPresent) {
            return Optional.empty()
        }

        val continuationSupplier = ParameterSupplier { SuspendPropertyContinuation<Any>(parameterContext.optionalMethod().get()) }
        return Optional.of(continuationSupplier)
    }
}

private class SuspendPropertyContinuation<T>(private val method: Method) : Continuation<T> {
    override fun resumeWith(result: Result<T>) {
        val message = "Property Method [$method] with suspend modifier should never be called explicitly."
        throw RuntimeException(message)
    }

    override val context: CoroutineContext
        get() = EmptyCoroutineContext
}