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
 * Empty template for the AnnotationIndex.
 * It is to be overridden by kapt, but necessary for the compiler.
 */
object AnnotationIndex : Index {
    override fun index(): Map<KClass<*>, Set<KClass<*>>> {
        throw Index.AnnotationNotProcessedException(
                "${this::class.simpleName} was not created by the annotation processor."
        )
    }

}
