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

import java.util.*
import javax.xml.bind.JAXBContext

/**
 * Index all subclasses of the annotated class or package.
 *
 * During compilation, ClassIndexProcessor creates a resource files listing all classes
 * extending annotated class or located inside annotated package.
 *
 * You can retrieve the list at runtime using either [KlassIndex.getSubclasses]
 * or [KlassIndex.getPackageClasses].
 *
 * For subclasses of the annotated class the resource file name is compatible with
 * what [ServiceLoader] expects. So if all the subclasses have a zero-argument constructor
 * you can use [ServiceLoader]. For subclasses of given package index file is named
 * "jaxb.index", it is located inside the package folder and it's format is compatible with
 * what [JAXBContext.newInstance] expects.
 */
@MustBeDocumented
@Retention
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE)
annotation class IndexSubclasses
