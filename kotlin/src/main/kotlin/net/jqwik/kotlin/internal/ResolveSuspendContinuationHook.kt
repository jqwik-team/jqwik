package net.jqwik.kotlin.internal

import kotlinx.coroutines.runBlocking
import net.jqwik.api.lifecycle.*
import net.jqwik.api.lifecycle.ResolveParameterHook.ParameterSupplier
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.jvm.kotlinFunction

/**
 * Provide continuation object in those cases where property methods are modified with "suspend"
 */
class ResolveSuspendContinuationHook : ResolveParameterHook, AroundTryHook {

    override fun propagateTo(): PropagationMode = PropagationMode.ALL_DESCENDANTS

    override fun aroundTry(
        context: TryLifecycleContext,
        aTry: TryExecutor,
        parameters: MutableList<Any>
    ): TryExecutionResult {
        val result = runBlocking(EmptyCoroutineContext) {
            suspendCoroutine<TryExecutionResult> { continuation ->
                parameters.set(0, continuation)
                val r = aTry.execute(parameters)
                //continuation.resume(r)
            }
        }
        return result
    }

    override fun appliesTo(element: Optional<AnnotatedElement>) =
        element.map { e -> e.isSuspendFunction() }.orElse(false)

    private fun AnnotatedElement.isSuspendFunction() =
        this is Method && this.kotlinFunction?.isSuspend ?: false

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

        val continuationSupplier =
            ParameterSupplier { SuspendPropertyContinuation<Any>(parameterContext.optionalMethod().get()) }
        return Optional.of(continuationSupplier)
    }
}

private class SuspendPropertyContinuation<T>(private val method: Method) : Continuation<T> {
    override fun resumeWith(result: Result<T>) {
        val message = "Property Method [$method] with suspend modifier should never be called explicitly."
        throw RuntimeException(message)
    }

    override val context get() = EmptyCoroutineContext
}

