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

import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test

/**
 * Performs test for the [KlassIndexTest]
 */
class KlassIndexTest {

    /**
     * Check if [KlassIndex.getSubclasses] returns the expected subclasses.
     */
    @Test
    fun shouldIndexSubclasses() {
        val subclasses = KlassIndex.getSubclasses(Service::class).toList()
        assertThat(subclasses).containsOnly(SecondService::class, InnerClasses.InnerService::class)
    }

    /**
     * Check if [KlassIndex.getAnnotated] returns the expected annotated classes.
     */
    @Test
    fun shouldIndexAnnotated() {
        val annotated = KlassIndex.getAnnotated(Component::class).toList()
        assertThat(annotated).containsOnly(
                FirstComponent::class,
                SecondComponent::class,
                InnerClasses.InnerComponent::class,
                InnerClasses.InnerComponent.InnerInnerComponent::class
        )
    }

    /**
     * Check if [KlassIndex.getSubclasses] returns the expected subclasses that have been defined using kapt arguments.
     */
    @Test
    fun shouldIndexKaptSubclassArguments() {
        val subclasses = KlassIndex.getSubclasses(GivenAbstractKlass::class).toList()
        assertThat(subclasses).containsOnly(
                InnerClasses::class,
                GivenKlass::class
        )
    }

    /**
     * Check if [KlassIndex.getSubclasses] returns the expected subclasses that have been defined using kapt arguments
     * and which's superclasses are external to this project.
     */
    @Test
    fun shouldIndexExternalKaptArguments() {
        val subclasses = KlassIndex.getSubclasses(Exception::class).toList()
        assertThat(subclasses).containsOnly(
                InnerClasses.MyException::class
        )
    }

    /**
     * Check if [KlassIndex.getAnnotated] returns the expected annotated classes
     * that have been defined using kapt arguments.
     */
    @Test
    fun shouldIndexKaptAnnotationArguments() {
        val annotated = KlassIndex.getAnnotated(GivenAnnotation::class).toList()
        assertThat(annotated).containsOnly(
                FirstComponent::class,
                GivenKlass::class
        )
    }

    /**
     * Check if [KlassIndex.getAnnotated] returns the expected annotated classes
     * that have been defined using multiple kapt arguments.
     */
    @Test
    fun shouldIndexMultipleKaptAnnotationArguments() {
        val annotated = KlassIndex.getAnnotated(AnotherGivenAnnotation::class).toList()
        assertThat(annotated).containsOnly(
                AnotherGivenKlass::class
        )
    }

    /**
     * Check if [KlassIndex.getAnnotated] returns the expected annotated classes which's annotation is inherited.
     */
    @Test
    fun shouldIndexWhenAnnotationIsInherited() {
        val annotated = KlassIndex.getAnnotated(InheritedAnnotation::class).toList()
        assertThat(annotated).containsOnly(
                Service::class,
                SecondService::class,
                InnerClasses.InnerService::class
        )
    }

    /**
     * Check if [KlassIndex.getAnnotated] returns the no annotated classes if the annotation is not annotated.
     */
    @Test
    fun shouldNotIndexNotAnnotatedAnnotation() {
        val annotated = KlassIndex.getAnnotated(MeaninglessAnnotation::class).toList()
        assertThat(annotated).isEmpty()
    }

    /**
     * Check if [KlassIndex.getSubclasses] returns the no subclasses if the superclass is not annotated.
     */
    @Test
    fun shouldNotIndexNotAnnotatedSuperclass() {
        val subclasses = KlassIndex.getSubclasses(SecondComponent::class).toList()
        assertThat(subclasses).isEmpty()
    }

    /**
     * Check if [KlassIndex.getAnnotated] returns the the expected names of the annotated classes.
     */
    @Test
    fun shouldReturnNamesOfAnnotated() {
        val annotated = KlassIndex.getAnnotated(Component::class).qualifiedNames().toList()
        assertThat(annotated).containsOnly(
                FirstComponent::class.qualifiedName,
                SecondComponent::class.qualifiedName,
                InnerClasses.InnerComponent::class.qualifiedName,
                InnerClasses.InnerComponent.InnerInnerComponent::class.qualifiedName
        )
    }

    /**
     * Check if [KlassIndex.getSubclasses] returns the the expected names of the subclasses.
     */
    @Test
    fun shouldReturnNamesOfSubclasses() {
        val services = KlassIndex.getSubclasses(Service::class).qualifiedNames().toList()
        assertThat(services).containsOnly(
                SecondService::class.java.canonicalName,
                InnerClasses.InnerService::class.qualifiedName
        )
    }
}
