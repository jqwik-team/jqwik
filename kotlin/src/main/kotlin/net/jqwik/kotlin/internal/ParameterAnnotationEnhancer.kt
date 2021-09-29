package net.jqwik.kotlin.internal

import net.jqwik.api.providers.TypeUsage
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter

class ParameterAnnotationEnhancer : TypeUsage.Enhancer {

    override fun forParameter(original: TypeUsage, parameter: Parameter): TypeUsage {
        val kParameter: KParameter? = parameter.kotlinParameter
        val typeAnnotations: List<Annotation> = kParameter?.type?.annotations ?: listOf()
        if (typeAnnotations.isEmpty()) {
            return original
        }
        return typeAnnotations.fold(original) { acc, annotation -> acc.withAnnotation(annotation) }
    }
}