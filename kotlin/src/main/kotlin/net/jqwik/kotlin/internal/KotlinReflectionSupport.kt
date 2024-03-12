package net.jqwik.kotlin.internal

import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.coroutines.Continuation
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

private const val metadataFqName = "kotlin.Metadata"

fun Class<*>.isKotlinClass(): Boolean = declaredAnnotations.any { it.annotationClass.java.name == metadataFqName }

val Parameter.kotlinParameter: KParameter?
    get() {
        if (!this.declaringExecutable.declaringClass.isKotlinClass()) {
            return null
        }
        if (isSuspendFunctionContinuationParameter()) {
            return null
        }
        val executable = this.declaringExecutable
        val index = executable.parameters.indexOf(this)
        val kotlinFunction = executable.kotlinFunction
        val parameters = kotlinFunction?.parameters ?: return null
        val isFirstNotAValue = parameters[0].name == null
        val kotlinIndex: Int = if (isFirstNotAValue) index + 1 else index
        if (kotlinIndex >= parameters.size) {
            // A generated parameter e.g. DefaultConstructorMarker
            return null
        }
        return parameters.get(kotlinIndex)
    }

private fun Parameter.isSuspendFunctionContinuationParameter() =
    name == "\$completion" && type == Continuation::class.java

val Executable.kotlinFunction: KFunction<*>?
    get() {
        if (this is Method) return this.kotlinFunction
        if (this is Constructor<*>) return this.kotlinFunction
        return null
    }

val KParameter.isMarkedNullable: Boolean
    get() {
        return this.type.isMarkedNullable
    }
