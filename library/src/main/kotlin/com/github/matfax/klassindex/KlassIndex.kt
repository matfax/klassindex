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

import kotlin.reflect.KClass

/**
 * Access to the compile-time generated index of classes.
 *
 * Use &#064;[IndexAnnotated] and &#064;[IndexSubclasses] annotations to force the classes to be indexed.
 *
 * Keep in mind that the class is indexed only when it is compiled with
 * classindex.jar file in classpath.
 *
 */
object KlassIndex {

    const val SUBCLASS_INDEX = "SubclassIndex"
    const val ANNOTATION_INDEX = "AnnotationIndex"

    private val subclassIndex by lazy { Index.load(SUBCLASS_INDEX).index() }
    private val annotationIndex by lazy { Index.load(ANNOTATION_INDEX).index() }

    /**
     * Retrieves a list of subclasses of the given class.
     *
     * The class must be annotated with [IndexSubclasses] for it's subclasses to be indexed
     * at compile-time by ClassIndexProcessor.
     *
     * @param superClass class to find subclasses for
     * @param classLoader classloader for loading classes
     * @return sequence of subclasses
     */
    fun <T : Any> getSubclasses(superClass: KClass<T>, classLoader: ClassLoader = Thread.currentThread().contextClassLoader): KlassSubIndex<T> {
        val entries = getSubclassesNames(superClass)
        return findClasses<T>(classLoader, entries).filter { superClass.java.isAssignableFrom(it.java) }
    }

    /**
     * Retrieves names of subclasses of the given class.
     *
     * The class must be annotated with [IndexSubclasses] for it's subclasses to be indexed
     * at compile-time by ClassIndexProcessor.
     *
     * @param superClass class to find subclasses for
     * @return names of subclasses
     */
    fun <T : Any> getSubclassesNames(superClass: KClass<T>): Set<String> {
        return subclassIndex[superClass.java.canonicalName].orEmpty()
    }

    /**
     * Retrieves a list of classes annotated by given annotation.
     *
     * The annotation must be annotated with [IndexAnnotated] for annotated classes
     * to be indexed at compile-time by ClassIndexProcessor.
     *
     * @param annotation annotation to search class for
     * @param classLoader classloader for loading classes
     * @return list of annotated classes
     */
    @JvmOverloads
    fun <T : Any> getAnnotated(
            annotation: KClass<out Annotation>,
            classLoader: ClassLoader = Thread.currentThread().contextClassLoader
    ): KlassSubIndex<T> {
        val entries = getAnnotatedNames(annotation)
        return findClasses(classLoader, entries)
    }

    /**
     * Retrieves names of classes annotated by given annotation.
     *
     * The annotation must be annotated with [IndexAnnotated] for annotated classes
     * to be indexed at compile-time by ClassIndexProcessor.
     *
     * Please note there is no verification if the class really exists. It can be missing when incremental
     * compilation is used. Use [.getAnnotated] if you need the verification.
     *
     * @param annotation annotation to search class for
     * @return names of annotated classes
     */
    fun getAnnotatedNames(annotation: KClass<out Annotation>): Set<String> {
        return annotationIndex[annotation.java.canonicalName].orEmpty()
    }

    private fun <T : Any> findClasses(classLoader: ClassLoader, entries: Iterable<String>): KlassSubIndex<T> {
        val classes = entries.mapNotNull {
            try {
                classLoader.loadClass(it)
            } catch (e: ClassNotFoundException) {
                null
            }
        }.map {
            it as Class<T>
        }.map {
            it.kotlin
        }
        return KlassSubIndex(classes)
    }
}
