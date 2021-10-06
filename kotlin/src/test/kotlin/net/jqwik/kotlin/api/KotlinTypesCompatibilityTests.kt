package net.jqwik.kotlin.api

import net.jqwik.api.ForAll
import net.jqwik.api.Group
import net.jqwik.api.Property
import net.jqwik.api.PropertyDefaults
import net.jqwik.api.constraints.Size
import net.jqwik.api.constraints.StringLength
import org.assertj.core.api.Assertions.assertThat

@PropertyDefaults(tries = 100)
class KotlinTypesCompatibilityTests {

    @Property
    fun kStrings(@ForAll @StringLength(5) aString: String) {
        assertThat(aString is String).isTrue
        assertThat(aString).hasSize(5)
    }

    @Group
    inner class CollectionTypes {

        @Property
        fun immutableLists(@ForAll @Size(5) aList: List<Int>) {
            assertThat(aList is List).isTrue
            assertThat(aList).hasSize(5)
            assertThat(aList).allMatch() { i -> i is Int }
        }

        @Property
        fun mutableLists(@ForAll @Size(5) aList: MutableList<Int>) {
            assertThat(aList is MutableList).isTrue
            assertThat(aList).hasSize(5)
            assertThat(aList).allMatch() { i -> i is Int }
            aList.add(42)
            assertThat(aList).hasSize(6)
        }

        @Property
        fun immutableSets(@ForAll @Size(5) aSet: Set<Int>) {
            assertThat(aSet is Set).isTrue
            assertThat(aSet).hasSize(5)
            assertThat(aSet).allMatch() { i -> i is Int }
        }

        @Property
        fun mutableSets(@ForAll @Size(5) aSet: MutableSet<Int>) {
            assertThat(aSet is MutableSet).isTrue
            assertThat(aSet).hasSize(5)
            assertThat(aSet).allMatch() { i -> i is Int }
            aSet.add(42)
            assertThat(aSet).contains(42)
        }

        @Property
        fun immutableMaps(@ForAll @Size(5) aMap: Map<Int, String>) {
            assertThat(aMap is Map).isTrue
            assertThat(aMap).hasSize(5)
            assertThat(aMap).allSatisfy() { k, v -> k is Int && v is String }
        }

        @Property
        fun mutableMaps(@ForAll @Size(5) aMap: MutableMap<Int, String>) {
            assertThat(aMap is MutableMap).isTrue
            assertThat(aMap).hasSize(5)
            assertThat(aMap).allSatisfy() { k, v -> k is Int && v is String }
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
            assertThat(anArray).allMatch() { i -> i is String }
        }

        @Property
        fun arrayOfBytes(@ForAll @Size(5) anArray: Array<Byte>) {
            assertThat(anArray is Array<Byte>).isTrue
            assertThat(anArray).hasSize(5)
            assertThat(anArray).allMatch() { i -> i is Byte }
        }

    }

}