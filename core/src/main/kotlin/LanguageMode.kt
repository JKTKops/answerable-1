package edu.illinois.cs.cs125.answerable

import edu.illinois.cs.cs125.answerable.classmanipulation.TypePool
import edu.illinois.cs.cs125.answerable.classmanipulation.getDefiningKotlinFileClass
import edu.illinois.cs.cs125.answerable.testing.Gen
import edu.illinois.cs.cs125.answerable.testing.arraySimpleCases
import edu.illinois.cs.cs125.answerable.testing.defaultPrimitiveEdgeCases
import edu.illinois.cs.cs125.answerable.testing.primitiveGenerators
import edu.illinois.cs.cs125.answerable.testing.valueSimpleCases
import java.lang.reflect.Array as ReflectArray

internal interface LanguageMode {
    val defaultSimpleCases: Map<Class<*>, ArrayWrapper>
    val defaultEdgeCases: Map<Class<*>, ArrayWrapper>
    val defaultGenerators: Map<Class<*>, Gen<*>>
    fun findControlClass(clazz: Class<*>, typePool: TypePool): Class<*>?
}

internal object JavaMode : LanguageMode {
    override val defaultSimpleCases: Map<Class<*>, ArrayWrapper>
        get() = valueSimpleCases + arraySimpleCases
    override val defaultEdgeCases: Map<Class<*>, ArrayWrapper>
        get() = (defaultPrimitiveEdgeCases + mapOf(String::class.java to ArrayWrapper(arrayOf(null, "")))).let {
            it + it.map { (clazz, _) ->
                val emptyArray = ReflectArray.newInstance(clazz, 0)
                emptyArray.javaClass to ArrayWrapper(arrayOf(emptyArray, null))
            }
        }
    override val defaultGenerators: Map<Class<*>, Gen<*>>
        get() = primitiveGenerators

    override fun findControlClass(clazz: Class<*>, typePool: TypePool): Class<*>? =
        null
}

internal object KotlinMode : LanguageMode {
    override val defaultSimpleCases: Map<Class<*>, ArrayWrapper>
        get() = valueSimpleCases + arraySimpleCases
    override val defaultEdgeCases: Map<Class<*>, ArrayWrapper>
        get() = (defaultPrimitiveEdgeCases + mapOf(String::class.java to ArrayWrapper(arrayOf("")))).let {
            it + it.map { (clazz, _) ->
                val emptyArray = ReflectArray.newInstance(clazz, 0)
                emptyArray.javaClass to ArrayWrapper(arrayOf(emptyArray))
            }
        }
    override val defaultGenerators: Map<Class<*>, Gen<*>>
        get() = primitiveGenerators

    override fun findControlClass(clazz: Class<*>, typePool: TypePool): Class<*>? =
        getDefiningKotlinFileClass(clazz, typePool)
}

// This is used in the Precondition verifier, but it really shouldn't be.
internal fun Class<*>.languageMode() =
    if (isAnnotationPresent(Metadata::class.java)) {
        KotlinMode
    } else {
        JavaMode
    }
