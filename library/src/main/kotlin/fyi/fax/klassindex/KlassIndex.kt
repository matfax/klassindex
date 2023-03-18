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

import kotlin.reflect.KClass

/**
 * Access to the compile-time generated index of classes.
 *
 * Use &#064;[IndexAnnotated] and &#064;[IndexSubclasses] annotations to force the classes to be indexed.
 *
 */
object KlassIndex {

    private val subclassIndex by lazy { SubclassIndex.index() }
    private val annotationIndex by lazy { AnnotationIndex.index() }

    /**
     * Retrieves a list of subclasses of the given class.
     *
     * The class must be annotated with [IndexSubclasses] for it's subclasses to be indexed at compile-time.
     *
     * @param superClass class to find subclasses for
     * @return sequence of subclasses
     */
    fun <T : Any> getSubclasses(superClass: KClass<T>): KlassSubIndex<T> {
        return subclassIndex[superClass].orEmpty().filterIsInstance<KClass<T>>().toIndex()
    }

    /**
     * Retrieves a list of classes annotated by given annotation.
     *
     * The annotation must be annotated with [IndexAnnotated] for annotated classes to be indexed at compile-time.
     *
     * @param annotation annotation to search class for
     * @return list of annotated classes
     */
    fun getAnnotated(annotation: KClass<out Annotation>): KlassSubIndex<Any> {
        return annotationIndex[annotation].orEmpty().toIndex()
    }
}
