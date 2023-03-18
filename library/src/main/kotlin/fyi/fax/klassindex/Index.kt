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
 * The interface to generated index classes.
 */
interface Index {

    /**
     * Indicates that the application has achieved an illegal state that could not be achieved if the annotation
     * processor were properly executed.
     */
    class AnnotationNotProcessedException(message: String) : IllegalStateException(message)

    /**
     * Access the compiled index map.
     */
    fun index(): Map<KClass<*>, Set<KClass<*>>>

}
