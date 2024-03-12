package net.jqwik.kotlin.internal

import kotlinx.coroutines.runBlocking
import net.jqwik.api.lifecycle.*
import net.jqwik.api.lifecycle.ResolveParameterHook.ParameterSupplier
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Method
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.reflect.KFunction
import kotlin.reflect.full.callSuspend
import kotlin.reflect.jvm.kotlinFunction

/**
 * Provide continuation object in those cases where property methods are modified with "suspend".
 * Invoke suspended method using [callSuspend].
 */
class SuspendedPropertyMethodsHook : ResolveParameterHook, InvokePropertyMethodHook {

    override fun propagateTo(): PropagationMode = PropagationMode.ALL_DESCENDANTS

    override fun appliesTo(element: Optional<AnnotatedElement>) =
        element.map { e -> e.isSuspendFunction() }.orElse(false)

    @Suppress("UNCHECKED_CAST")
    override fun invoke(method: Method, target: Any, vararg args: Any): Any {
        val kotlinFunction: KFunction<Any> =
            (method.kotlinFunction ?: return InvokePropertyMethodHook.DEFAULT.invoke(
                method,
                target,
                args
            )) as KFunction<Any>
        return runBlocking {
            val rest = args.copyOfRange(0, args.size - 1)
            kotlinFunction.callSuspend(target, *rest)
        }
    }

    private fun AnnotatedElement.isSuspendFunction() =
        this is Method
            // Added because of https://github.com/jqwik-team/jqwik/issues/557
            && this.declaringClass.isKotlinClass()
            && this.kotlinFunction?.isSuspend ?: false

    override fun resolve(
        parameterContext: ParameterResolutionContext,
        lifecycleContext: LifecycleContext
    ): Optional<ParameterSupplier> {
        if (!parameterContext.typeUsage().isOfType(Continuation::class.java)) {
            return Optional.empty()
        }
        if (parameterContext.parameter().name != "\$completion") {
            return Optional.empty()
        }
        if (parameterContext.parameter().kotlinParameter != null) {
            return Optional.empty()
        }
        if (!parameterContext.optionalMethod().isPresent) {
            return Optional.empty()
        }

        val continuationSupplier = ParameterSupplier { SuspendedPropertyContinuation<Any>() }
        return Optional.of(continuationSupplier)
    }
}

private class SuspendedPropertyContinuation<T> : Continuation<T> {
    override fun resumeWith(result: Result<T>) {
    }

    override val context get() = EmptyCoroutineContext

    override fun toString(): String {
        return "SuspendedPropertyContinuation"
    }
}

