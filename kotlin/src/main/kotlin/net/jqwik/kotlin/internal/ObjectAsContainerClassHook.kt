package net.jqwik.kotlin.internal

import net.jqwik.api.lifecycle.PropagationMode
import net.jqwik.api.lifecycle.ProvidePropertyInstanceHook
import java.lang.reflect.AnnotatedElement
import java.util.*
import kotlin.reflect.full.callSuspend

/**
 * Provide continuation object in those cases where property methods are modified with "suspend".
 * Invoke suspended method using [callSuspend].
 */
class ObjectAsContainerClassHook : ProvidePropertyInstanceHook {

    override fun propagateTo(): PropagationMode = PropagationMode.ALL_DESCENDANTS

    override fun appliesTo(element: Optional<AnnotatedElement>): Boolean {
        return element.map { e -> e.isSingletonObject() }.orElse(false)
    }

    override fun provide(containerClass: Class<out Any>): Any {
        return containerClass.kotlin.objectInstance ?: throw NullPointerException()
    }

    private fun AnnotatedElement.isSingletonObject(): Boolean {
        if (this is Class<*>) {
            val objectInstance = this.kotlin.objectInstance
            return objectInstance != null
        }
        return false
    }
}
