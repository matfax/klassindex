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

package com.github.matfax.klassindex.processor

import com.github.matfax.klassindex.*
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.WildcardTypeName.Companion.STAR
import java.io.File
import java.io.IOException
import java.lang.annotation.Inherited
import java.util.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.NestingKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.util.ElementScanner8
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic
import kotlin.reflect.KClass

/**
 * Generates index files for [KlassIndex].
 */
@AutoService(Processor::class)
open class KlassIndexProcessor : AbstractProcessor() {
    private val subclassMap = mutableMapOf<String, MutableSet<String>>()
    private val annotatedMap = mutableMapOf<String, MutableSet<String>>()

    private var annotationDriven = true
    private val indexedAnnotations = mutableSetOf<String>()
    private val indexedSuperclasses = mutableSetOf<String>()

    private lateinit var types: Types
    private lateinit var filer: Filer
    private lateinit var elementUtils: Elements
    private lateinit var messager: Messager

    /**
     * Adds given annotations for indexing.
     */
    protected fun indexAnnotations(vararg classes: Class<*>) {
        annotationDriven = false
        classes.mapTo(indexedAnnotations) { it.canonicalName }
    }

    /**
     * Adds given classes for subclass indexing.
     */
    protected fun indexSubclasses(vararg classes: Class<*>) {
        annotationDriven = false
        classes.mapTo(indexedSuperclasses) { it.canonicalName }
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf("*")
    }

    @Synchronized
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        types = processingEnv.typeUtils
        filer = processingEnv.filer
        elementUtils = processingEnv.elementUtils
        messager = processingEnv.messager
    }

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        kotlin.runCatching {
            roundEnv.rootElements
                    .filter { it is TypeElement }
                    .forEach { element ->
                        element.accept(object : ElementScanner8<Void, Void>() {
                            override fun visitType(typeElement: TypeElement, o: Void?): Void? {
                                try {
                                    typeElement.annotationMirrors
                                            .map { it.annotationType.asElement() as TypeElement }
                                            .forEach { storeAnnotation(it, typeElement) }
                                    indexSupertypes(typeElement, typeElement)
                                } catch (e: IOException) {
                                    messager.printMessage(
                                            Diagnostic.Kind.ERROR,
                                            "[KlassIndexProcessor] ${e.message}"
                                    )
                                }

                                return super.visitType(typeElement, o)
                            }
                        }, null)
                    }

            if (!roundEnv.processingOver()) {
                return false
            }

            storeIndex(SUBCLASS_INDEX!!, subclassMap)
            storeIndex(ANNOTATION_INDEX!!, annotatedMap)
        }.onFailure { e: Throwable ->
            val msg = if (e is IOException) {
                "[KlassIndexProcessor] Can't write index file: ${e.message}"
            } else {
                "[KlassIndexProcessor] Internal error: ${e.message}"
            }
            messager.printMessage(Diagnostic.Kind.ERROR, msg)
            messager.printMessage(Diagnostic.Kind.ERROR, e.stackTrace.joinToString("\n"))
        }

        return false
    }

    private fun storeIndex(className: String, index: Map<String, Set<String>>) {
        val indexTable: ParameterizedTypeName = Map::class.asClassName().parameterizedBy(
                KClass::class.asClassName().parameterizedBy(STAR),
                Set::class.asClassName().parameterizedBy(KClass::class.asClassName().parameterizedBy(STAR))
        )

        val indexBuilder = FunSpec.builder("index")
                .addModifiers(KModifier.OVERRIDE)
                .returns(indexTable)
                .addStatement("val result = mutableMapOf<KClass<*>, Set<KClass<*>>>()")
        index.forEach {
            val values = it.value.map { "$it::class" }.joinToString(",\n")
            indexBuilder.addStatement("result[${it.key}::class] = setOf($values)")
        }

        indexBuilder.addStatement("return result.toMap()")

        val file = FileSpec.builder(KlassIndex::class.java.`package`.name, className)
                .addType(
                        TypeSpec.objectBuilder(className)
                                .addSuperinterface(Index::class)
                                .addFunction(indexBuilder.build())
                                .build()
                ).build()

        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        file.writeTo(File(kaptKotlinGeneratedDir, "$className.kt"))
    }

    /**
     * Index super types for [IndexSubclasses] and any [IndexAnnotated]
     * additionally accompanied by [Inherited].
     */
    @Throws(IOException::class)
    private fun indexSupertypes(rootElement: TypeElement, element: TypeElement) {

        types.directSupertypes(element.asType())
                .filter { it.kind == TypeKind.DECLARED }
                .forEach { mirror ->

                    val superType = mirror as DeclaredType
                    val superTypeElement = superType.asElement() as? TypeElement
                    superTypeElement?.let { element ->

                        storeSubclass(element, rootElement)

                        element.annotationMirrors
                                .asSequence()
                                .map {
                                    it.annotationType
                                            .asElement() as TypeElement
                                }
                                .filter { hasAnnotation(it, Inherited::class.java) }
                                .forEach { storeAnnotation(it, rootElement) }

                        indexSupertypes(rootElement, element)
                    }
                }
    }

    private fun hasAnnotation(element: TypeElement, inheritedClass: Class<out Annotation>): Boolean {
        try {
            return element.annotationMirrors.any { it.annotationType.toString() == inheritedClass.name }
        } catch (e: RuntimeException) {
            if (e.javaClass.name != "com.sun.tools.javac.code.Symbol\$CompletionFailure") {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "[KlassIndexProcessor] Can't check annotation: ${e.message}"
                )
            }
        }

        return false
    }

    @Throws(IOException::class)
    private fun storeAnnotation(annotationElement: TypeElement, rootElement: TypeElement) {
        if (indexedAnnotations.contains(annotationElement.qualifiedName.toString())) {
            putElement(annotatedMap, annotationElement.qualifiedName.toString(), rootElement)
        } else if (annotationDriven) {
            val indexAnnotated = annotationElement.getAnnotation(IndexAnnotated::class.java)
            if (indexAnnotated != null) {
                putElement(annotatedMap, annotationElement.qualifiedName.toString(), rootElement)
            }
        }
    }

    @Throws(IOException::class)
    private fun storeSubclass(superTypeElement: TypeElement, rootElement: TypeElement) {
        if (indexedSuperclasses.contains(superTypeElement.qualifiedName.toString())) {
            putElement(subclassMap, superTypeElement.qualifiedName.toString(), rootElement)
        } else if (annotationDriven) {
            val indexSubclasses = superTypeElement.getAnnotation(IndexSubclasses::class.java)
            if (indexSubclasses != null) {
                putElement(subclassMap, superTypeElement.qualifiedName.toString(), rootElement)
            }
        }
        if (indexedSuperclasses.contains(superTypeElement.qualifiedName.toString())
                || annotationDriven && superTypeElement.getAnnotation(IndexSubclasses::class.java) != null) {
            putElement(subclassMap, superTypeElement.qualifiedName.toString(), rootElement)
        }
    }

    private fun <K> putElement(map: MutableMap<K, MutableSet<String>>, keyElement: K, valueElement: TypeElement) {
        val fullName = getFullName(valueElement)
        if (fullName != null) {
            putElement(map, keyElement, fullName)
        }
    }

    private fun <K> putElement(map: MutableMap<K, MutableSet<String>>, keyElement: K, valueElement: String) {
        if (!map.containsKey(keyElement)) {
            map[keyElement] = TreeSet()
        }
        map[keyElement]?.add(valueElement)
    }

    private fun getFullName(typeElement: TypeElement): String? {
        when (typeElement.nestingKind) {
            NestingKind.TOP_LEVEL -> return typeElement.qualifiedName.toString()
            NestingKind.MEMBER -> {
                val enclosingElement = typeElement.enclosingElement
                if (enclosingElement is TypeElement) {
                    val enclosingName = getFullName(enclosingElement)
                    if (enclosingName != null) {
                        return "$enclosingName.${typeElement.simpleName}"
                    }
                }
                return null
            }
            else -> return null
        }
    }

    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
        val SUBCLASS_INDEX = SubclassIndex::class.simpleName
        val ANNOTATION_INDEX = AnnotationIndex::class.simpleName
    }

}
