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

package com.github.matfax.klassindex

import java.lang.reflect.Modifier
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance

inline class KlassSubIndex<T : Any>(private val classes: List<KClass<out T>>) : Iterable<KClass<out T>> {
    override fun iterator(): Iterator<KClass<out T>> {
        return classes.iterator()
    }

    fun filter(predicate: (KClass<out T>) -> Boolean): KlassSubIndex<T> {
        return classes.filter(predicate).toIndex()
    }

    fun topLevel(): KlassSubIndex<T> {
        return this.filter { it.java.enclosingClass == null }
    }

    fun topLevelOrStaticNested(): KlassSubIndex<T> {
        return this.filter { it.java.enclosingClass == null || it.java.modifiers and Modifier.STATIC != 0 }
    }

    fun enclosedIn(enclosing: KClass<out T>): KlassSubIndex<T> {
        return this.filter {
            val enclosingSequence = generateSequence(it.java.enclosingClass) { next ->
                next.enclosingClass
            }
            return@filter enclosingSequence.contains(enclosing.java)
        }
    }

    fun enclosedDirectlyIn(enclosing: KClass<out T>): KlassSubIndex<T> {
        return this.filter { it.java.enclosingClass == enclosing.java }
    }

    fun annotatedWith(annotation: KClass<out Annotation>): KlassSubIndex<T> {
        val retention = annotation.java.getAnnotation(kotlin.annotation.Retention::class.java)
        if (retention == null || retention.value != AnnotationRetention.RUNTIME) {
            throw IllegalStateException("Cannot filter annotated with annotation without retention policy set to RUNTIME: ${annotation.java.name}")
        }
        return this.filter { it.java.isAnnotationPresent(annotation.java) }
    }

    fun withModifiers(modifiers: Int): KlassSubIndex<T> {
        return this.filter { it.java.modifiers and modifiers != 0 }
    }

    fun withoutModifiers(modifiers: Int): KlassSubIndex<T> {
        return this.filter { it.java.modifiers and modifiers == 0 }
    }

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

    fun objects(): List<T> {
        return this.map { it.objectInstance }.filterNotNull()
    }


    fun nested(): KlassSubIndex<*> {
        return this.flatMap { it.nestedClasses }.toIndex()
    }

    fun members(): List<KCallable<*>> {
        return this.flatMap { it.members }
    }

    fun companionObjects(): List<Any> {
        return this.map { it.companionObjectInstance }.filterNotNull()
    }

}

inline fun <T : Any> List<KClass<out T>>.toIndex(): KlassSubIndex<T> {
    return KlassSubIndex(this)
}
