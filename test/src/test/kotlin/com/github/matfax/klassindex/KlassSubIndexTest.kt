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

/**
 * Performs test for the [KlassSubIndex]
 */
class KlassSubIndexTest {

    private val list = listOf(
            GivenAbstractKlass::class,
            SecondComponent::class,
            InnerClasses.InnerComponent::class,
            InnerClasses.InnerComponent.InnerInnerComponent::class,
            InnerClasses.InnerModule::class,
            Service::class
    ).toIndex()

    /**
     * Check if [KlassSubIndex.topLevel] returns only top level classes.
     */
    @Test
    fun shouldReturnTopLevelClasses() {
        val result = list.topLevel().toList()
        assert(result).containsOnly(
                GivenAbstractKlass::class,
                SecondComponent::class,
                Service::class
        )
    }

    /**
     * Check if [KlassSubIndex.topLevelOrStaticNested] returns only top level or static nested classes.
     */
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

    /**
     * Check if [KlassSubIndex.enclosedIn] returns enclosed classes.
     */
    @Test
    fun shouldReturnOnlyEnclosedInGivenClass() {
        val result = list.enclosedIn(InnerClasses::class).toList()
        assert(result).containsOnly(
                InnerClasses.InnerComponent::class,
                InnerClasses.InnerModule::class,
                InnerClasses.InnerComponent.InnerInnerComponent::class
        )
    }

    /**
     * Check if [KlassSubIndex.enclosedDirectlyIn] returns only directly enclosed classes.
     */
    @Test
    fun shouldReturnOnlyEnclosedDirectlyInGivenClass() {
        val result = list.enclosedDirectlyIn(InnerClasses::class).toList()
        assert(result).containsOnly(InnerClasses.InnerComponent::class, InnerClasses.InnerModule::class)
    }

    /**
     * Check if [KlassSubIndex.annotatedWith] returns only annotated classes.
     */
    @Test
    fun shouldReturnOnlyAnnotatedWith() {
        val result = list.annotatedWith(Component::class).toList()
        assert(result).containsOnly(
                SecondComponent::class,
                InnerClasses.InnerComponent::class,
                InnerClasses.InnerComponent.InnerInnerComponent::class
        )
    }

    /**
     * Check if [KlassSubIndex.annotatedWith] throws a [IllegalStateException]
     * if an annotation without [Retention] is used.
     */
    @Test
    fun shouldFailWhenUsingAnnotationWithoutRuntimeRetention() {
        Assertions.assertThrows(IllegalStateException::class.java) {
            list.annotatedWith(InheritedAnnotation::class)
        }
    }

    /**
     * Check if [KlassSubIndex.withModifiers] returns only the classes with the given modifier.
     */
    @Test
    fun shouldReturnOnlyWithModifiers() {
        val result = list.withModifiers(Modifier.STATIC).toList()
        assert(result).containsOnly(InnerClasses.InnerComponent::class)
    }

    /**
     * Check if [KlassSubIndex.withoutModifiers] returns only the classes without the given modifier.
     */
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

    /**
     * Check if [KlassSubIndex.withPublicDefaultConstructor] returns only the classes with a public default constructor.
     */
    @Test
    fun shouldReturnOnlyWithPublicDefaultConstructor() {
        val result = list.withPublicDefaultConstructor().toList()
        assert(result).containsOnly(
                GivenAbstractKlass::class
        )
    }

    /**
     * Check if [KlassSubIndex.objects] returns only Kotlin singleton objects.
     */
    @Test
    fun shouldReturnOnlyObjects() {
        val result = list.objects().toList()
        assert(result).containsOnly(SecondComponent)
    }

    /**
     * Check if [KlassSubIndex.abstract] returns only abstract classes.
     */
    @Test
    fun shouldReturnOnlyAbstractClasses() {
        val result = list.abstract().toList()
        assert(result).containsOnly(
                Service::class,
                GivenAbstractKlass::class
        )
    }

    /**
     * Check if [KlassSubIndex.sealed] returns only sealed classes.
     */
    @Test
    fun shouldReturnOnlySealedClasses() {
        val result = list.sealed().toList()
        assert(result).containsOnly(
                InnerClasses.InnerComponent::class
        )
    }

    /**
     * Check if [KlassSubIndex.members] returns members.
     */
    @Test
    fun shouldReturnMembers() {
        val result = list.members().toList()
        assert(result).isNotEmpty()
    }

    /**
     * Check if [KlassSubIndex.companionObjects] returns the expected companion objects.
     */
    @Test
    fun shouldReturnOnlyCompanionObjects() {
        val result = list.companionObjects().toList()
        assert(result).containsOnly(InnerClasses.InnerComponent::class.companionObjectInstance)
    }

    /**
     * Check if [KlassSubIndex.nested] returns only nested classes.
     */
    @Test
    fun shouldReturnNestedClasses() {
        val result = list.nested().toList()
        assert(result).contains(InnerClasses.InnerComponent.InnerInnerComponent::class)
    }

    /**
     * Check if [KlassSubIndex.simpleNames] returns the simple names of the classes.
     */
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
