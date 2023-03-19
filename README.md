# KlassIndex

![GitHub Workflow Status (with branch)](https://img.shields.io/github/actions/workflow/status/matfax/klassindex/build.yml?branch=main&style=for-the-badge)
[![CodeFactor](https://www.codefactor.io/repository/github/matfax/klassindex/badge?style=for-the-badge)](https://www.codefactor.io/repository/github/matfax/klassindex)
[![GitHub License](https://img.shields.io/github/license/matfax/klassindex.svg?style=for-the-badge)](https://github.com/matfax/klassindex/blob/master/LICENSE)
[![GitHub last commit](https://img.shields.io/github/last-commit/matfax/klassindex?color=9cf&style=for-the-badge)](https://github.com/matfax/klassindex/commits/master)
![GitHub Release Date](https://img.shields.io/github/release-date/matfax/klassindex?color=%23986293&style=for-the-badge)
[![Maven Central](https://img.shields.io/maven-central/v/fyi.fax.klassindex/library?style=for-the-badge)](https://central.sonatype.com/namespace/fyi.fax.klassindex)

## About

**K**lassIndex is the younger Kotlin brother of [atteo/classindex](https://github.com/atteo/classindex). However, it differs from ClassIndex in various aspects.

| Aspects                | ClassIndex                                         | **K**lassIndex                          |
| ---------------------- | -------------------------------------------------- | --------------------------------------- |
| Language               | Java                                               | Kotlin                                  |
| Supported Classes      | Any Java class                                     | `Any` Kotlin class                      |
| Supported Build Tools  | Maven and Gradle                                   | Gradle                                  |
| Supported Scopes       | Annotations, Subclasses, Packages                  | Annotations, Subclasses<sup>5</sup>     |
| Service Loader Support | Yes                                                | No, superfluous<sup>6</sup>             |
| JaxB Index             | Yes                                                | No                                      |
| Stores Documentation   | Yes                                                | No                                      |
| Android Support        | Limited<sup>2</sup>                                | Yes                                     |
| Runtime Performance    | Great                                              | Even Greater<sup>1</sup>                |
| Filtering Support      | Limited                                            | Complete functional support<sup>3</sup> |
| Extension Support      | Yes                                                | Theoretically                           |
| Index external classes | Yes, by extending the processor                    | Yes, using kapt arguments               |
| Jar Shading Support    | Yes                                                | Maybe                                   |
| Compile Time Safety    | Limited<sup>4</sup>                                | Complete                                |
| Class Loader Required  | Yes                                                | No                                      |
| License                | Apache 2.0                                         | Apache 2.0                              |

### Explanation

1.  ClassIndex stores the qualified names of the classes to load at run time in the jar's resources. The resource parsing and class loading comes at a cost. In contrast, **K**lassIndex statically compiles the references. That means resource and classloading are not necessary. Excluded, however, are use cases where there are tons unreferenced of classes with only few of them to be loaded. ClassIndex might show a better performance then.
2.  ClassIndex depends on resource and class loading. In use cases in Android where no context is available or a different class loader is to be used, ClassIndex will fail. **K**lassIndex compiles the references statically to enable full support.
3.  **K**lassIndex provides all functional methods from Kotlin's `Iterable`, not just filters.
4.  ClassIndex uses workarounds to detect if classes are valid and still existent. **K**lassIndex, however, uses statically compiled generated classes so that the compiler can check their validity.
5.  Kotlin does not (yet?) have such a concept such as Java's package files. Thus, package indexing has been removed to be consistent with the Kotlin.
6.  The only use case in which a service loader is to be preferred over statical compilation is in plugin-based systems.

### Methods

-   the list of classes annotated by a given annotation `getAnnotated()`
-   the list of classes implementing a given interface `getSubclasses()`

### Advantages

**K**lassIndex

-   is faster than reading a file, it is not impacted by the usual performance penalty of the classpath scanning
-   does not depend on a class loader
-   is light-weight and simple
-   supports incremental compilation in IntelliJ and Android Studio

## How to use it?

### Add Dependency

#### Gradle

-   Add kapt

```groovy
plugins {
    // Replace with the latest Kotlin version
    id "org.jetbrains.kotlin.kapt" version "1.+"
}
```

-   Add dependencies

```groovy
// Replace with the latest versions from Jitpack
compile 'fyi.fax.klassindex:library:4.+'
kapt 'fyi.fax.klassindex:processor:4.+'
```

-   (Optional) Enable kapt build cache

```groovy
kapt {
    useBuildCache = true
}
```

#### Gradle in Kotlin DSL

-   Add kapt

```kotlin
plugins {
    // Replace with the latest Kotlin version
    kotlin("kapt") version "1.+"
}
```

-   Add dependencies

```kotlin
// Replace with the latest versions from Jitpack
compile("fyi.fax.klassindex:library:4.+")
kapt("fyi.fax.klassindex:processor:4.+")
```

-   (Optional) Enable kapt build cache

```kotlin
kapt {
    useBuildCache = true
}
```

### Annotations

-   Annotate your annotation with @IndexAnnotated

```kotlin
@IndexAnnotated
annotation class YourAnnotation
```

-   Retrieve a list of annotated classes at run-time

```kotlin
KlassIndex.getAnnotated(Component::class)
```

### Subclasses

-   Annotate your superclass with @IndexSubclasses

```kotlin
@IndexSubclasses
interface YourSuperclass
```

-   Retrieve a list of annotated classes at run-time

```kotlin
KlassIndex.getSubclasses(YourSuperclass::class)
```

### Index Traversing

Filtering allows you to select only classes with desired characteristics. Here are some basic samples:

-   Selecting only top-level classes

```kotlin
KlassIndex.getAnnotated(SomeAnnotation.class).topLevel()
```

-   Selecting only classes which are top level and public at the same time

```kotlin
KlassIndex.getAnnotated(SomeAnnotation.class).topLevel().withModifiers(Modifier.PUBLIC)
```

-   Selecting only the object instances from singleton classes that are annotated with an additional annotation.

```kotlin
KlassIndex.getAnnotated(SomeAnnotation.class).annotatedWith(SecondAnnotation::class).objects()
```

For more examples, check the [test file](https://github.com/matfax/klassindex/blob/master/test/src/test/kotlin/fyi/fax/klassindex/KlassSubIndexTest.kt).

### Indexing Without Annotations

Sometimes, you cannot easily use annotations to trigger compile time indexing
because you don't control the source code of the classes which should be annotated.
For instance, you cannot add `@IndexSubclasses` meta-annotation to [@Exception](https://docs.oracle.com/javase/8/docs/api/index.html?java/lang/Exception.html).

To add the regarding annotations to the desired external class, just add an argument to kapt.

```groovy
kapt {
    arguments {
        arg(
                "fyi.fax.klassindex.IndexSubclasses", // IndexAnnotated alternatively
                "java.lang.Exception" // this is a vararg
        )
    }
}
```

Please consider that this option only provides you internal classes at the moment.
That means, for the `Exception` example, you will only get your declared exceptions, not all available Java and Kotlin exceptions.

Make sure not to use Kotlin type aliases, they will not be recognized (e.g., not `kotlin.Exception`).

## How the Magic Happens

### Annotation Index Processor

**K**lassIndex indexes your classes at compile time by providing the implementation of the standard [annotation
processor](http://www.jcp.org/en/jsr/detail?id=269). The index is then used by kapt utilizing kotlinpoet to generate new Kotlin source files that hold the static references to the indexed classes. The compiler uses the source files as if they were manually written.

### Run Time Library

**K**lassIndex provides a library to access the statically compiled index from the generated classes and to process them.

## Why KlassIndex

### Speed

Traditional classpath scanning is a [very](https://www.leveluplunch.com/blog/2015/08/11/reducing-startup-times-spring-applications-context/)
[slow](https://wiki.apache.org/tomcat/HowTo/FasterStartUp) process.
Replacing it with compile-time indexing speeds Java applications bootstrap considerably.

Here are the results of the [benchmark](https://github.com/atteo/classindex-benchmark) comparing ClassIndex with various scanning solutions.

| Library                                                        | Application startup time |
| -------------------------------------------------------------- | -----------------------: |
| None - hardcoded list                                          |                  0:00.18 |
| [Scannotation](http://scannotation.sourceforge.net/)           |                  0:05.11 |
| [Reflections](https://github.com/ronmamo/reflections)          |                  0:05.37 |
| Reflections Maven plugin                                       |                  0:00.52 |
| [Corn](https://sites.google.com/site/javacornproject/corn-cps) |                  0:24.60 |
| ClassIndex                                                     |                  0:00.18 |
| **K**lassIndex                                                 |                      TBD |

Notes: benchmark was performed on Intel i5-2520M CPU @ 2.50GHz, classpath size was set to 121MB.
