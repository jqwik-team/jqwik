package net.jqwik.kotlin.internal

import net.jqwik.api.Tuple.Tuple2
import net.jqwik.api.providers.TypeUsage
import java.lang.reflect.Parameter
import kotlin.reflect.KParameter

class ParameterAnnotationEnhancer : TypeUsage.Enhancer {

    override fun forParameter(original: TypeUsage, parameterInfo: Tuple2<Parameter, Int>): TypeUsage {
        val kParameter: KParameter? = kotlinParameter(parameterInfo.get1(), parameterInfo.get2())
        val typeAnnotations: List<Annotation> = kParameter?.type?.annotations ?: listOf()
        if (typeAnnotations.isEmpty()) {
            return original
        }
        return typeAnnotations.fold(original) { acc, annotation -> acc.withAnnotation(annotation) }
    }
}