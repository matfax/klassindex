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

@Suppress("DEPRECATION")
class KlassIndexTest {
    @Test
    fun shouldIndexSubclasses() {
        val subclasses = KlassIndex.getSubclasses(Service::class).toList()
        assert(subclasses).containsOnly(SecondService::class, InnerClasses.InnerService::class)
    }

    @Test
    fun shouldIndexAnnotated() {
        val annotated = KlassIndex.getAnnotated(Component::class).toList()
        assert(annotated).containsOnly(
                FirstComponent::class,
                SecondComponent::class,
                InnerClasses.InnerComponent::class,
                InnerClasses.InnerComponent.InnerInnerComponent::class
        )
    }

    @Test
    fun shouldIndexKaptSubclassArguments() {
        val subclasses = KlassIndex.getSubclasses(GivenAbstractKlass::class).toList()
        assert(subclasses).containsOnly(
                InnerClasses::class,
                GivenKlass::class
        )
    }

    @Test
    fun shouldIndexExternalKaptArguments() {
        val subclasses = KlassIndex.getSubclasses(Exception::class).toList()
        assert(subclasses).containsOnly(
                InnerClasses.MyException::class
        )
    }

    @Test
    fun shouldIndexKaptAnnotationArguments() {
        val annotated = KlassIndex.getAnnotated(GivenAnnotation::class).toList()
        assert(annotated).containsOnly(
                FirstComponent::class,
                GivenKlass::class
        )
    }

    @Test
    fun shouldIndexMultipleKaptAnnotationArguments() {
        val annotated = KlassIndex.getAnnotated(AnotherGivenAnnotation::class).toList()
        assert(annotated).containsOnly(
                AnotherGivenKlass::class
        )
    }

    @Test
    fun shouldIndexWhenAnnotationIsInherited() {
        val annotated = KlassIndex.getAnnotated(InheritedAnnotation::class).toList()
        assert(annotated).containsOnly(
                Service::class,
                SecondService::class,
                InnerClasses.InnerService::class
        )
    }

    @Test
    fun shouldNotIndexNotAnnotatedAnnotation() {
        val annotated = KlassIndex.getAnnotated(MeaninglessAnnotation::class).toList()
        assert(annotated).isEmpty()
    }

    @Test
    fun shouldNotIndexNotAnnotatedSuperclass() {
        val subclasses = KlassIndex.getSubclasses(SecondComponent::class).toList()
        assert(subclasses).isEmpty()
    }

    @Test
    fun shouldReturnNamesOfAnnotated() {
        val annotated = KlassIndex.getAnnotated(Component::class).qualifiedNames().toList()
        assert(annotated).containsOnly(
                FirstComponent::class.qualifiedName,
                SecondComponent::class.qualifiedName,
                InnerClasses.InnerComponent::class.qualifiedName,
                InnerClasses.InnerComponent.InnerInnerComponent::class.qualifiedName
        )
    }

    @Test
    fun shouldReturnNamesOfSubclasses() {
        val services = KlassIndex.getSubclasses(Service::class).qualifiedNames().toList()
        assert(services).containsOnly(
                SecondService::class.java.canonicalName,
                InnerClasses.InnerService::class.qualifiedName
        )
    }
}
