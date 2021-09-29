package net.jqwik.kotlin.api

import net.jqwik.api.Arbitrary
import net.jqwik.api.Example
import net.jqwik.api.Group
import net.jqwik.api.Label
import net.jqwik.kotlin.internal.isKotlinClass
import net.jqwik.kotlin.internal.isMarkedNullable
import net.jqwik.kotlin.internal.kotlinParameter
import org.assertj.core.api.Assertions.assertThat
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.javaMethod

@Group
class ReflectionSupportTests {

    @Group
    @Label("Class.isKotlinClass()")
    inner class IsKotlinClass {
        @Example
        fun topLevelKotlinClass() {
            assert(ReflectionSupportTests::class.java.isKotlinClass())
        }

        @Example
        fun innerKotlinClass() {
            assert(IsKotlinClass::class.java.isKotlinClass())
        }

        @Example
        fun javaClass() {
            assert(!Arbitrary::class.java.isKotlinClass())
        }
    }

    @Group
    @Label("Parameter.kotlinParameter")
    inner class KotlinParameter {

        fun aFunction(first: String, second: Int?) {}

        @Example
        fun functionParameters() {
            val first = this::aFunction.javaMethod!!.parameters!![0]
            val second = this::aFunction.javaMethod!!.parameters!![1]

            val firstKParameter: KParameter? = first.kotlinParameter
            assertThat(firstKParameter?.name).isEqualTo("first")
            assertThat(firstKParameter?.type).isEqualTo(String::class.createType())

            val secondKParameter: KParameter? = second.kotlinParameter
            assertThat(secondKParameter?.name).isEqualTo("second")
            assertThat(secondKParameter?.isMarkedNullable).isTrue()
        }

        @Example
        fun constructorParameters() {
            val first = ClassWithConstructor::class.java.constructors[0].parameters[0]
            val second = ClassWithConstructor::class.java.constructors[0].parameters[1]

            val firstKParameter: KParameter? = first.kotlinParameter
            assertThat(firstKParameter?.name).isEqualTo("first")
            assertThat(firstKParameter?.type).isEqualTo(String::class.createType())

            val secondKParameter: KParameter? = second.kotlinParameter
            assertThat(secondKParameter?.name).isEqualTo("second")
            assertThat(secondKParameter?.isMarkedNullable).isTrue()
        }

    }

    class ClassWithConstructor(first: String, second: Int?) {}
}