package net.jqwik.kotlin

import net.jqwik.api.*
import net.jqwik.api.constraints.Size
import net.jqwik.api.constraints.StringLength
import net.jqwik.api.constraints.UseType
import net.jqwik.kotlin.api.any
import net.jqwik.testing.SuppressLogging
import org.assertj.core.api.Assertions.assertThat

@PropertyDefaults(tries = 100)
class KotlinTypesCompatibilityTests {

    @Property
    fun kStrings(@ForAll @StringLength(5) aString: String) {
        assertThat(aString is String).isTrue
        assertThat(aString).hasSize(5)
    }

    @Target(AnnotationTarget.VALUE_PARAMETER)
    @Retention(AnnotationRetention.RUNTIME)
    @StringLength(5)
    annotation class OfLength5

    @Property
    fun kAnnotations(@ForAll @OfLength5 aString: String) {
        assertThat(aString).hasSize(5)
    }

    enum class Friend { Josh, Mira, Xin, Sarah }

    @Property
    fun kEnum(@ForAll friend: Friend) {
        assertThat(friend is Friend).isTrue
    }

    data class MyMoney(val amount: Long, val currency: String)

    @Property
    fun dataClasses(@ForAll @UseType money: MyMoney) {
        assertThat(money.amount is Long).isTrue
        assertThat(money.currency is String).isTrue
    }

    data class MyUser(val name: String, val age: Int = -1)

    @Property
    @SuppressLogging("edge case generation exceeded warning")
    fun defaultParameterInConstructorIsIgnored(@ForAll @UseType user: MyUser) {
        assertThat(user.name is String).isTrue
        assertThat(user.age is Int).isTrue
    }

    @Group
    inner class CollectionTypes {

        @Property
        fun immutableLists(@ForAll @Size(5) aList: List<Int>) {
            assertThat(aList is List).isTrue
            assertThat(aList).hasSize(5)
            assertThat(aList).allMatch { i -> i is Int }
        }

        @Property
        fun mutableLists(@ForAll @Size(5) aList: MutableList<Int>) {
            assertThat(aList is MutableList).isTrue
            assertThat(aList).hasSize(5)
            assertThat(aList).allMatch { i -> i is Int }
            aList.add(42)
            assertThat(aList).hasSize(6)
        }

        @Property
        fun immutableSets(@ForAll @Size(5) aSet: Set<Int>) {
            assertThat(aSet is Set).isTrue
            assertThat(aSet).hasSize(5)
            assertThat(aSet).allMatch { i -> i is Int }
        }

        @Property
        fun mutableSets(@ForAll @Size(5) aSet: MutableSet<Int>) {
            assertThat(aSet is MutableSet).isTrue
            assertThat(aSet).hasSize(5)
            assertThat(aSet).allMatch { i -> i is Int }
            aSet.add(42)
            assertThat(aSet).contains(42)
        }

        @Property
        fun immutableMaps(@ForAll @Size(5) aMap: Map<Int, String>) {
            assertThat(aMap is Map).isTrue
            assertThat(aMap).hasSize(5)
            assertThat(aMap).allSatisfy { k, v -> k is Int && v is String }
        }

        @Property
        fun mutableMaps(@ForAll @Size(5) aMap: MutableMap<Int, String>) {
            assertThat(aMap is MutableMap).isTrue
            assertThat(aMap).hasSize(5)
            assertThat(aMap).allSatisfy { k, v -> k is Int && v is String }
            aMap.put(42, "hello")
            assertThat(aMap[42]).isEqualTo("hello")
        }
    }

    @Group
    inner class Arrays {

        @Property
        fun arrayOfStrings(@ForAll @Size(5) anArray: Array<String>) {
            assertThat(anArray is Array<String>).isTrue
            assertThat(anArray).hasSize(5)
            assertThat(anArray).allMatch { i -> i is String }
        }

        @Property
        fun arrayOfBytes(@ForAll @Size(5) anArray: Array<Byte>) {
            assertThat(anArray is Array<Byte>).isTrue
            assertThat(anArray).hasSize(5)
            assertThat(anArray).allMatch { i -> i is Byte }
        }

        @Property
        fun byteArray(@ForAll @Size(5) anArray: ByteArray) {
            assertThat(anArray is ByteArray).isTrue
            assertThat(anArray).hasSize(5)
            assertThat(anArray.toTypedArray()).allMatch { i -> i is Byte }
        }

    }

    @Group
    inner class UnsignedIntegers {

        @Property
        fun unsignedBytes(@ForAll anInt: UByte) {
            assertThat(anInt is UByte).isTrue
            assertThat(anInt).isBetween(UByte.MIN_VALUE, UByte.MAX_VALUE)
        }

        @Property
        fun unsignedShorts(@ForAll anInt: UShort) {
            assertThat(anInt is UShort).isTrue
            assertThat(anInt).isBetween(UShort.MIN_VALUE, UShort.MAX_VALUE)
        }

        @Property
        fun unsignedInts(@ForAll anInt: UInt) {
            assertThat(anInt is UInt).isTrue
            assertThat(anInt).isBetween(UInt.MIN_VALUE, UInt.MAX_VALUE)
        }

        @Property
        fun unsignedLongs(@ForAll anInt: ULong) {
            assertThat(anInt is ULong).isTrue
            assertThat(anInt).isBetween(ULong.MIN_VALUE, ULong.MAX_VALUE)
        }

        @Property
        @ExperimentalUnsignedTypes
        fun unsignedByteArray(@ForAll anArray: UByteArray) {
            assertThat(anArray is UByteArray).isTrue
            assertThat(anArray).allMatch { i -> i >= UByte.MIN_VALUE && i <= UByte.MAX_VALUE }
        }

        @Property
        @ExperimentalUnsignedTypes
        fun unsignedShortArray(@ForAll anArray: UShortArray) {
            assertThat(anArray is UShortArray).isTrue
            assertThat(anArray).allMatch { i -> i >= UShort.MIN_VALUE && i <= UShort.MAX_VALUE }
        }

        @Property
        @ExperimentalUnsignedTypes
        fun unsignedIntArray(@ForAll anArray: UIntArray) {
            assertThat(anArray is UIntArray).isTrue
            assertThat(anArray).allMatch { i -> i >= UInt.MIN_VALUE && i <= UInt.MAX_VALUE }
        }

        @Property
        @ExperimentalUnsignedTypes
        fun unsignedLongArray(@ForAll anArray: ULongArray) {
            assertThat(anArray is ULongArray).isTrue
            assertThat(anArray).allMatch { i -> i >= ULong.MIN_VALUE && i <= ULong.MAX_VALUE }
        }
    }

    @JvmInline
    value class Password(private val s: String) {
        fun size() = s.length
    }

    @Group
    inner class InlineClasses {
        @Property
        fun inlinedStringWithAnnotation(@ForAll password: @StringLength(31) Password) {
            assertThat(password is Password)
            assertThat(password.size()).isEqualTo(31)
        }

        @Property
        fun provideInlinedClassInsteadOfInlineClass(@ForAll("passwords") password: Password) {
            assertThat(password is Password)
            assertThat(password.size()).isEqualTo(31)
        }

        @Provide
        fun passwords() = String.any().ofLength(31)
    }

}