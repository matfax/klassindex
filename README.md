# KlassIndex

[![](https://jitpack.io/v/matfax/klassindex.svg)](https://jitpack.io/#matfax/klassindex)
[![Build Status](https://travis-ci.com/matfax/classindex.svg?branch=master)](https://travis-ci.com/matfax/classindex)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3241ef70d6614b049355a4bff6da7df3)](https://www.codacy.com/app/matfax/klassindex?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=matfax/klassindex&amp;utm_campaign=Badge_Grade)
![GitHub License](https://img.shields.io/github/license/matfax/klassindex.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/matfax/klassindex.svg)

## About
KlassIndex is the younger Kotlin brother of [atteo/classindex](https://github.com/atteo/classindex). However, it differs from ClassIndex in various aspects.

| Aspects | ClassIndex | KlassIndex |
|------------------------|----------------------------------------------------|-----------------------------------------|
| Language | Java | Kotlin |
| Supported Classes | Any Java class | `Any` Kotlin class |
| Supported Build Tools | Maven and outdated Gradle versions<sup>7</sup> | Any Gradle version |
| Supported Scopes | Annotations, Subclasses, Packages | Annotations, Subclasses<sup>6</sup> |
| Service Loader Support | Yes | No, superfluous<sup>8</sup> |
| JaxB Index | Yes | No |
| Stores Documentation | Yes | No |
| IDE Support | Eclipse, Netbeans, Jetbrains (limited)<sup>3</sup> | Full Jetbrains support |
| Android Support | Limited<sup>2</sup> | Yes |
| Runtime Performance | Great | Even Greater<sup>1</sup> |
| Filtering Support | Limited | Complete functional support<sup>4</sup> |
| Extension Support | Yes | Theoretically, TBD |
| Jar Shading Support | Yes | Maybe |
| Compile Time Safety | Limited<sup>5</sup> | Complete |
| Class Loader Required | Yes | No |
| License | Apache 2.0 | Apache 2.0 |

### Explanation

1. ClassIndex stores the qualified names of the classes to load at run time in the jar's resources. The resource parsing and class loading comes with a cost. In contrast, KlassIndex statically compiles the references. That means resource and classloading are not necessary. Excluded, however, are use cases where you have tons of classes and want only to load few of them, ClassIndex might show a better performance.

2. ClassIndex depends on resource and class loading. In use cases in Android where no context is available or a different class loader is to be used, ClassIndex will fail. KlassIndex compiles the references statically to enable full support.

3. New IntelliJ and Android Studio versions require newer versions of Gradle to provide full support.

4. KlassIndex provides all functional methods from Kotlin's `Iterable`, not just filters.

5. ClassIndex uses workarounds to detect if classes are valid and still existent. KlassIndex, however uses statically compiled generated classes so that the compiler can check the validity.

6. Kotlin does not (yet?) have such a concept such as Java's package files. Thus, package indexing has been removed to be consistent with the Kotlin.

7. ClassIndex does not provide separate dependency modules for annotation processing and compilation as required by newer Gradle versions (i.e., deprecated in Gradle 4.x, to be dropped in Gradle 5.x). KlassIndex provides a spearate annotation processor to be used with Gradle's `kotlin-kapt`.

8. The only use case in which a service loader is to be preferred over statical compilation are plugin-based systems.

### Methods

- the list of classes annotated by a given annotation getAnnotated()
- the list of classes implementing a given interface getSubclasses()

### Advantages
KlassIndex
- is faster than reading a file, it is not impacted by the usual performance penalty of the classpath scanning
- is not depending on a class loader
- is leight-weight and simple
- supports incremental compilation in IntelliJ and Android Studio

## How to use it?

### Add Dependency
1. Gradle
* Add repository
```groovy
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```
* Add dependencies
```groovy
compile 'com.github.matfax.klassindex:library:4.+'
kapt 'com.github.matfax.klassindex:processor:4.+'
```
1. Gradle in Kotlin DSL
* Add repository
```kotlin
allprojects {
    repositories {
    	maven("https://jitpack.io")
	}
}
```
* Add dependencies
```kotlin
compile("com.github.matfax.klassindex:library:4.+")
kapt("com.github.matfax.klassindex:processor:4.+")
```
1. For others, check: [Jitpack](https://jitpack.io/#matfax/klassindex)

### Annotations

1. Annotate your annotation with @IndexAnnotated
```kotlin
@IndexAnnotated
annotation class YourAnnotation
```
1. Retrieve a list of annotated classes at run-time
```kotlin
KlassIndex.getAnnotated(Component::class)
```

### Subclasses

1. Annotate your superclass with @IndexSubclasses
```kotlin
@IndexSubclasses
interface YourSuperclass
```
1. Retrieve a list of annotated classes at run-time
```kotlin
KlassIndex.getSubclasses(YourSuperclass::class)
```

### Index Processing

Filtering allows you to select only classes with desired characteristics. Here are some basic samples:

* Selecting only top-level classes

```kotlin
KlassIndex.getAnnotated(SomeAnnotation.class).topLevel()
```

* Selecting only classes which are top level and public at the same time

```kotlin
KlassIndex.getAnnotated(SomeAnnotation.class).topLevel().withModifiers(Modifier.PUBLIC)
```

* Selecting only the object instances from singleton classes that are annotated with an additional annotation.

```kotlin
KlassIndex.getAnnotated(SomeAnnotation.class).annotatedWith(SecondAnnotation::class).objects()
```

For more examples, check the [test file](https://github.com/matfax/klassindex/blob/master/test/src/test/kotlin/com/github/matfax/klassindex/KlassSubIndexTest.kt).

## How it works?

### Annotation Index Processor

KlassIndex indexes your classes at compile time by providing the implementation of the standard [annotation 
processor](http://www.jcp.org/en/jsr/detail?id=269). The index is then used by kapt utilizing kotlinpoet to generate new Kotlin source files that hold the static references to the indexed classes. The compiler uses the source files as if they were manually written.

### Run Time Library

KlassIndex provides a library to load the statically compiled index from the generated classes and to process them.

## Why KlassIndex?

### Speed

Traditional classpath scanning is a [very](https://www.leveluplunch.com/blog/2015/08/11/reducing-startup-times-spring-applications-context/)
[slow](https://wiki.apache.org/tomcat/HowTo/FasterStartUp) process. 
Replacing it with compile-time indexing speeds Java applications bootstrap considerably.

Here are the results of the [benchmark](https://github.com/atteo/classindex-benchmark) comparing ClassIndex with various scanning solutions.

| Library | Application startup time |
|----------------------------------------------------------------|-------------------------:|
| None - hardcoded list | 0:00.18 |
| [Scannotation](http://scannotation.sourceforge.net/) | 0:05.11 |
| [Reflections](https://github.com/ronmamo/reflections) | 0:05.37 |
| Reflections Maven plugin | 0:00.52 |
| [Corn](https://sites.google.com/site/javacornproject/corn-cps) | 0:24.60 |
| ClassIndex | 0:00.18 |
| KlassIndex | TBD |

Notes: benchmark was performed on Intel i5-2520M CPU @ 2.50GHz, classpath size was set to 121MB.
