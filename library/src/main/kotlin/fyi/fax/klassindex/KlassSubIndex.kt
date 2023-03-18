/*
 * Copyright 2018 matfax
 * Copyright 2013 Atteo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fyi.fax.klassindex

import java.lang.reflect.Modifier
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.jvm.JvmInline

/**
 * Provides helper functions on a wrapped set of [KClass].
 */
@JvmInline
value class KlassSubIndex<T : Any>(private val classes: Set<KClass<out T>>) : Iterable<KClass<out T>> {

    /**
     * Provides an iterator on the wrapped set of [KClass].
     */
    override fun iterator(): Iterator<KClass<out T>> {
        return classes.iterator()
    }

    /**
     * Overloads [Iterable.filter] to return a [KlassSubIndex].
     */
    fun filter(predicate: (KClass<out T>) -> Boolean): KlassSubIndex<T> {
        return classes.filter(predicate).toIndex()
    }

    /**
     * Filters the index for top level classes.
     */
    fun topLevel(): KlassSubIndex<T> {
        return this.filter { it.java.enclosingClass == null }
    }

    /**
     * Filters the index for top level classes or classes that are statically nested.
     */
    fun topLevelOrStaticNested(): KlassSubIndex<T> {
        return this.filter { it.java.enclosingClass == null || it.java.modifiers and Modifier.STATIC != 0 }
    }

    /**
     * Filters the index for classes that are transitively enclosed by [enclosing].
     */
    fun enclosedIn(enclosing: KClass<out T>): KlassSubIndex<T> {
        return this.filter {
            val enclosingSequence = generateSequence(it.java.enclosingClass) { next ->
                next.enclosingClass
            }
            return@filter enclosingSequence.contains(enclosing.java)
        }
    }

    /**
     * Filters the index for classes that are directly enclosed by [enclosing].
     */
    fun enclosedDirectlyIn(enclosing: KClass<out T>): KlassSubIndex<T> {
        return this.filter { it.java.enclosingClass == enclosing.java }
    }

    /**
     * Filters the index for classes that are annotated with [annotation].
     */
    fun annotatedWith(annotation: KClass<out Annotation>): KlassSubIndex<T> {
        val retention = annotation.java.getAnnotation(kotlin.annotation.Retention::class.java)
        if (retention == null || retention.value != AnnotationRetention.RUNTIME) {
            throw IllegalStateException("Cannot filter annotated with annotation without retention policy set to RUNTIME: ${annotation.java.name}")
        }
        return this.filter { it.java.isAnnotationPresent(annotation.java) }
    }

    /**
     * Filters the index for classes that have the given [modifiers].
     * The available modifiers are listed in [Modifier].
     */
    fun withModifiers(modifiers: Int): KlassSubIndex<T> {
        return this.filter { it.java.modifiers and modifiers != 0 }
    }

    /**
     * Filters the index for classes that do not have the given [modifiers].
     * The available modifiers are listed in [Modifier].
     */
    fun withoutModifiers(modifiers: Int): KlassSubIndex<T> {
        return this.filter { it.java.modifiers and modifiers == 0 }
    }

    /**
     * Filters the index for classes that have a public default constructor.
     */
    fun withPublicDefaultConstructor(): KlassSubIndex<T> {
        return this.filter {
            try {
                val constructor = it.java.getConstructor()
                return@filter constructor.modifiers and Modifier.PUBLIC != 0
            } catch (e: NoSuchMethodException) {
                return@filter false
            } catch (e: SecurityException) {
                return@filter false
            }
        }
    }

    /**
     * Filters the index for classes that are Kotlin singletons and returns their initialized object instances.
     */
    fun objects(): List<T> {
        return this.map { it.objectInstance }.filterNotNull()
    }

    /**
     * Returns the classes that are nested in the classes from the index.
     */
    fun nested(): KlassSubIndex<*> {
        return this.flatMap { it.nestedClasses }.toIndex()
    }

    /**
     * Returns the classes that are sealed.
     */
    fun sealed(): KlassSubIndex<*> {
        return this.filter { it.isSealed }
    }

    /**
     * Returns all sealed classes that are nested in the classes from the index.
     */
    fun abstract(): KlassSubIndex<*> {
        return this.filter { it.isAbstract }
    }

    /**
     * Returns all class members from the index'es classes.
     */
    fun members(): List<KCallable<*>> {
        return this.flatMap { it.members }
    }

    /**
     * Filters the index for classes that have companion objects and returns their initialized instances.
     */
    fun companionObjects(): List<Any> {
        return this.mapNotNull { it.companionObjectInstance }
    }

    /**
     * Gets the fully qualified names of the classes from the index.
     */
    fun qualifiedNames(): List<String> {
        return this.mapNotNull { it.qualifiedName }
    }

    /**
     * Gets the simple names of the classes from the index.
     */
    fun simpleNames(): List<String> {
        return this.mapNotNull { it.simpleName }
    }
}

/**
 * Converts a [List] of [KClass] to a [KlassSubIndex].
 */
fun <T : Any> List<KClass<out T>>.toIndex(): KlassSubIndex<T> {
    return KlassSubIndex(this.toSet())
}

/**
 * Converts a [Set] of [KClass] to a [KlassSubIndex].
 */
fun <T : Any> Set<KClass<out T>>.toIndex(): KlassSubIndex<T> {
    return KlassSubIndex(this)
}
