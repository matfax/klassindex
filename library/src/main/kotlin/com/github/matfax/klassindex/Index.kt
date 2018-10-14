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
 * The interface to generated index classes.
 */
interface Index {

    /**
     * Access the compiled index map.
     */
    fun index(): Map<KClass<*>, Set<KClass<*>>>

    companion object {

        /**
         * Load an index class with the given [className] from the class loader.
         */
        fun load(className: String): Index {
            return Class.forName(
                    "${this::class.java.`package`.name}.$className"
            ).kotlin.objectInstance as Index
        }
    }

}
