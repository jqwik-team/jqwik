package net.jqwik.kotlin

import net.jqwik.api.ForAll
import net.jqwik.api.Property
import net.jqwik.api.constraints.CharRange
import net.jqwik.api.constraints.IntRange
import net.jqwik.api.constraints.NumericChars
import net.jqwik.api.constraints.StringLength

class ParameterAnnotationsTests {

    @Property
    fun annotationAtTypeIsRespected(@ForAll anInt: @IntRange(min = 42, max = 142) Int) {
        assert(anInt >= 42) { -> "Should be >= 42" }
        assert(anInt <= 142) { -> "Should be <= 142" }
    }

    @Property
    fun annotationWithDefaultValuesAtTypeIsRespected(@ForAll aString: @StringLength(5) String) {
        assert(aString.length == 5) { -> "String length should be 5" }
    }

    @Property
    fun annotationAtTypeIsCombinedWithAnnotationAtParameter(@ForAll @StringLength(5) aString: @NumericChars String) {
        assert(aString.length == 5) { -> "String length should be 5" }
        assert(aString.chars().allMatch(Character::isDigit)) { -> "String contains only numerics" }
    }

    @Property
    fun moreThanOneAnnotationCanBeAtType(
        @ForAll aString: @NumericChars @CharRange(
            from = 'a',
            to = 'c'
        ) String
    ) {
        assert(aString.chars().allMatch { Character.isDigit(it) || ('a'..'c').contains(it.toChar()) })
        { -> "$aString violates: numerics or a to c" }
    }

    @Property
    fun metaAnnotationsInKotlinAnnotationAreRespected(@ForAll @MyStringLength aString: String) {
        assert(aString.length == 5) { -> "String length should be 5" }
    }

    @Property
    fun kotlinAnnotationAtParameterTypeIsRespected(@ForAll aString: @MyStringLength String) {
        assert(aString.length == 5) { -> "String length should be 5" }
    }

}

@kotlin.annotation.Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.TYPE)
@StringLength(value = 5)
annotation class MyStringLength