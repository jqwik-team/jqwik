package net.jqwik.kotlin.internal

import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.kotlinFunction

fun kotlinParameter(parameter: Parameter, index: Int): KParameter? {
    val executable = parameter.declaringExecutable
    if (executable is Method) {
        val parameters = executable.kotlinFunction?.parameters ?: listOf()
        val isFirstNotAValue = parameters[0].kind != KParameter.Kind.VALUE
        val kotlinIndex: Int = if (isFirstNotAValue) index + 1 else index
        return parameters.get(kotlinIndex)
    }
    return null;
}

val KParameter.isMarkedNullable: Boolean
    get() {
        return this.type.isMarkedNullable
    }
