package net.jqwik.kotlin.internal

import net.jqwik.api.providers.TypeUsage
import net.jqwik.kotlin.api.kTypeMetaInfoKey
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter

class KTypeEnhancer : TypeUsage.Enhancer {

    override fun forParameter(original: TypeUsage, parameter: Parameter): TypeUsage {
        val kParameter: KParameter? = parameter.kotlinParameter
        return kParameter?.let { original.withMetaInfo(kTypeMetaInfoKey, it.type) } ?: original
    }
}