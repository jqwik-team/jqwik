package net.jqwik.kotlin.internal

import net.jqwik.api.providers.TypeUsage
import java.lang.reflect.Parameter

/**
 * Annotations of a Kotlin's parameter's type (e.g. aParam: @MyAnnotation MyType) must be added explicitly.
 */
class ParameterAnnotationEnhancer : TypeUsage.Enhancer {

    override fun forParameter(original: TypeUsage, parameter: Parameter): TypeUsage {
        val parameterDeclaredInKotlinClass = parameter.declaringExecutable.declaringClass.isKotlinClass()
        if (!parameterDeclaredInKotlinClass) return original
        val typeAnnotations = parameter.annotatedType.annotations
        if (typeAnnotations.isEmpty()) {
            return original
        }
        return typeAnnotations.fold(original) { acc, annotation -> acc.withAnnotation(annotation) }
    }
}
