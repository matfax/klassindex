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
import assertk.assertions.contains
import assertk.assertions.containsOnly
import assertk.assertions.isNotEmpty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.lang.reflect.Modifier
import kotlin.reflect.full.companionObjectInstance

class KlassSubIndexTest {

    private val list = listOf(
            GivenAbstractKlass::class,
            SecondComponent::class,
            InnerClasses.InnerComponent::class,
            InnerClasses.InnerComponent.InnerInnerComponent::class,
            InnerClasses.InnerModule::class,
            Service::class
    ).toIndex()

    @Test
    fun shouldReturnTopLevelClasses() {
        val result = list.topLevel().toList()
        assert(result).containsOnly(
                GivenAbstractKlass::class,
                SecondComponent::class,
                Service::class
        )
    }

    @Test
    fun shouldReturnTopLevelOrStaticNestedClasses() {
        val result = list.topLevelOrStaticNested().toList()
        assert(result).containsOnly(
                GivenAbstractKlass::class,
                SecondComponent::class,
                InnerClasses.InnerComponent::class,
                Service::class
        )
    }

    @Test
    fun shouldReturnOnlyEnclosedInGivenClass() {
        val result = list.enclosedIn(InnerClasses::class).toList()
        assert(result).containsOnly(
                InnerClasses.InnerComponent::class,
                InnerClasses.InnerModule::class,
                InnerClasses.InnerComponent.InnerInnerComponent::class
        )
    }

    @Test
    fun shouldReturnOnlyEnclosedDirectlyInGivenClass() {
        val result = list.enclosedDirectlyIn(InnerClasses::class).toList()
        assert(result).containsOnly(InnerClasses.InnerComponent::class, InnerClasses.InnerModule::class)
    }

    @Test
    fun shouldReturnOnlyAnnotatedWith() {
        val result = list.annotatedWith(Component::class).toList()
        assert(result).containsOnly(
                SecondComponent::class,
                InnerClasses.InnerComponent::class,
                InnerClasses.InnerComponent.InnerInnerComponent::class
        )
    }

    @Test
    fun shouldFailWhenUsingAnnotationWithoutRuntimeRetention() {
        Assertions.assertThrows(IllegalStateException::class.java) {
            list.annotatedWith(InheritedAnnotation::class)
        }
    }

    @Test
    fun shouldReturnOnlyWithModifiers() {
        val result = list.withModifiers(Modifier.STATIC).toList()
        assert(result).containsOnly(InnerClasses.InnerComponent::class)
    }

    @Test
    fun shouldReturnOnlyWithoutModifiers() {
        val result = list.withoutModifiers(Modifier.STATIC).toList()
        assert(result).containsOnly(
                GivenAbstractKlass::class,
                SecondComponent::class,
                InnerClasses.InnerComponent.InnerInnerComponent::class,
                InnerClasses.InnerModule::class,
                Service::class
        )
    }

    @Test
    fun shouldReturnOnlyWithPublicDefaultConstructor() {
        val result = list.withPublicDefaultConstructor().toList()
        assert(result).containsOnly(
                GivenAbstractKlass::class
        )
    }

    @Test
    fun shouldReturnOnlyObjects() {
        val result = list.objects().toList()
        assert(result).containsOnly(SecondComponent)
    }

    @Test
    fun shouldReturnOnlyAbstractClasses() {
        val result = list.abstract().toList()
        assert(result).containsOnly(
                Service::class,
                GivenAbstractKlass::class
        )
    }

    @Test
    fun shouldReturnOnlySealedClasses() {
        val result = list.sealed().toList()
        assert(result).containsOnly(
                InnerClasses.InnerComponent::class
        )
    }

    @Test
    fun shouldReturnMembers() {
        val result = list.members().toList()
        assert(result).isNotEmpty()
    }

    @Test
    fun shouldReturnOnlyCompanionObjects() {
        val result = list.companionObjects().toList()
        assert(result).containsOnly(InnerClasses.InnerComponent::class.companionObjectInstance)
    }

    @Test
    fun shouldReturnNestedClasses() {
        val result = list.nested().toList()
        assert(result).contains(InnerClasses.InnerComponent.InnerInnerComponent::class)
    }

    @Test
    fun shouldReturnNames() {
        val result = list.simpleNames().toList()
        assert(result).containsOnly(
                "SecondComponent",
                "GivenAbstractKlass",
                "InnerComponent",
                "InnerInnerComponent",
                "InnerModule",
                "Service"
        )
    }
}
