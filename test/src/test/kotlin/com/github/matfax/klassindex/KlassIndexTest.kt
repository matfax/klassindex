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

import assertk.assert
import assertk.assertions.containsOnly
import assertk.assertions.isEmpty
import org.junit.jupiter.api.Test

class KlassIndexTest {
    @Test
    fun shouldIndexSubclasses() {
        val services = KlassIndex.getSubclasses(Service::class).toList()
        assert(services).containsOnly(SecondService::class, InnerClasses.InnerService::class)
    }

    @Test
    fun shouldReturnNamesOfSubclasses() {
        val services = KlassIndex.getSubclassesNames(Service::class).toList()
        assert(services).containsOnly(
                SecondService::class.java.canonicalName,
                "${InnerClasses::class.java.canonicalName}$${InnerClasses.InnerService::class.simpleName}"
        )
    }

    @Test
    fun shouldIndexAnnotated() {
        val annotated = KlassIndex.getAnnotated<Any>(Component::class).toList()
        assert(annotated).containsOnly(
                SecondComponent::class,
                InnerClasses.InnerComponent::class,
                InnerClasses.InnerComponent.InnerInnerComponent::class
        )
    }

    @Test
    fun shouldReturnNamesOfAnnotated() {
        val annotated = KlassIndex.getAnnotatedNames(Component::class).toList()
        assert(annotated).containsOnly(
                SecondComponent::class.java.canonicalName,
                "${InnerClasses::class.java.canonicalName}$${InnerClasses.InnerComponent::class.simpleName}",
                "${InnerClasses::class.java.canonicalName}$${InnerClasses.InnerComponent::class.simpleName}$${InnerClasses.InnerComponent.InnerInnerComponent::class.simpleName}"
        )
    }

    @Test
    fun shouldIndexWhenAnnotationIsInherited() {
        val annotated = KlassIndex.getAnnotated<Any>(InheritedAnnotation::class).toList()
        assert(annotated).containsOnly(
                Service::class,
                SecondService::class,
                InnerClasses.InnerService::class
        )
    }

    @Test
    fun shouldNotIndexNotAnnotated() {
        val annotated = KlassIndex.getAnnotated<Any>(MeaninglessAnnotation::class).toList()
        assert(annotated).isEmpty()
    }
}
