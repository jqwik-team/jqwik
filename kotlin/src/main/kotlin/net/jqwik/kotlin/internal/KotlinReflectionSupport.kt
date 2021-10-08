package net.jqwik.kotlin.internal

import java.lang.reflect.Constructor
import java.lang.reflect.Executable
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

private const val metadataFqName = "kotlin.Metadata"

fun Class<*>.isKotlinClass(): Boolean = declaredAnnotations.any { it.annotationClass.java.name == metadataFqName }

val Parameter.kotlinParameter: KParameter?
    get() {
        val executable = this.declaringExecutable
        val index = executable.parameters.indexOf(this)
        val kotlinFunction = executable.kotlinFunction
        val parameters = kotlinFunction?.parameters ?: return null
        val isFirstNotAValue = parameters[0].kind != KParameter.Kind.VALUE
        val kotlinIndex: Int = if (isFirstNotAValue) index + 1 else index
        return parameters.get(kotlinIndex)
    }

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
