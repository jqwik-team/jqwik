package net.jqwik.kotlin.internal

import net.jqwik.api.providers.TypeUsage
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter

class NullabilityEnhancer : TypeUsage.Enhancer {

    override fun forParameter(original: TypeUsage, parameter: Parameter): TypeUsage {
        val kParameter: KParameter? = parameter.kotlinParameter
        val isNullable: Boolean = kParameter?.isMarkedNullable ?: false
        return if (isNullable) original.asNullable() else original
    }
}