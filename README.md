# KlassIndex

[![](https://jitpack.io/v/matfax/klassindex.svg)](https://jitpack.io/#matfax/klassindex)
[![Build Status](https://travis-ci.com/matfax/klassindex.svg?branch=master)](https://travis-ci.com/matfax/klassindex)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/3241ef70d6614b049355a4bff6da7df3)](https://www.codacy.com/app/matfax/klassindex?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=matfax/klassindex&amp;utm_campaign=Badge_Grade)
![GitHub License](https://img.shields.io/github/license/matfax/klassindex.svg)
![GitHub last commit](https://img.shields.io/github/last-commit/matfax/klassindex.svg)

## About
KlassIndex is the younger Kotlin brother of atteo/classindex. However, it differs from ClassIndex in various aspects.

| Aspects                | ClassIndex                               | KlassIndex                                 |
|------------------------|------------------------------------------|--------------------------------------------|
| Language               | Java                                     | Kotlin                                     |
| Supported Classes      | Any Java class                           | `Any` Kotlin class                         |
| Supported Build Tools  | Maven and outdated Gradle versions<sup>7</sup> | Any Gradle version                         |
| Supported Scopes       | Annotations, Subclasses, Packages        | Annotations, Subclasses<sup>6</sup>                    |
| Service Loader Support | Yes                                      | No, superfluous<sup>8</sup>                            |
| JaxB Index             | Yes                                      | No                                         |
| Stores Documentation   | Yes                                      | No                                         |
| IDE Support            | Eclipse, Netbeans, Jetbrains (limited)<sup>3</sup>    | Full Jetbrains support  |
| Android Support        | Limited<sup>2</sup>                      | Yes                           |
| Runtime Performance    | Great                                    | Even Greater<sup>1</sup>                   |
| Filtering Support      | Limited                                  | Complete functional support<sup>4</sup>              |
| Extension Support      | Yes                                      | Theoretically, TBD                         |
| Jar Shading Support    | Yes                                      | Maybe                                      |
| Compile Time Safety    | Limited<sup>5</sup>                      | Complete                                   |
| Class Loader Required  | Yes                                      | No                                         |
| License                | Apache 2.0                               | Apache 2.0                                 |

### Explanation

1. ClassIndex stores the qualified names of the classes to load at run time in the jar's resources. The resource parsing and class loading comes with a cost. In contrast, KlassIndex statically compiles the references. That means resource and classloading are not necessary. Excluded, however, are use cases where you have tons of classes and want only to load few of them, ClassIndex might show a better performance.

2. ClassIndex depends on resource and class loading. In use cases in Android where no context is available or a different class loader is to be used, ClassIndex will fail. KlassIndex compiles the references statically to enable full support.

3. New IntelliJ and Android Studio versions require newer versions of Gradle to provide full support.

4. KlassIndex provides all functional methods from Kotlin's `Iterable`, not just filters.

5. ClassIndex uses workarounds to detect if classes are valid and still existent. KlassIndex, however uses statically compiled generated classes so that the compiler can check the validity.

6. Kotlin does not (yet?) have such a concept such as Java's package files. Thus, package indexing has been removed to be consistent with the Kotlin.

7. ClassIndex does not provide separate dependency modules for annotation processing and compilation as required by newer Gradle versions (i.e., deprecated in Gradle 4.x, to be dropped in Gradle 5.x). KlassIndex provides a spearate annotation processor to be used with Gradle's `kotlin-kapt`.

8. The only use case in which a service loader is to be preferred over statical compilation are plugin-based systems.

## Methods

- the list of classes annotated by a given annotation (see: [getAnnotated()](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html#getAnnotated-java.lang.Class-)),
- the list of classes implementing a given interface (see: [getSubclasses()](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html#getSubclasses-java.lang.Class-)),
- the list of subclasses of a given class (see: [getSubclasses()](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html#getSubclasses-java.lang.Class-)),
- the list of classes from a given package (see: [getPackageClasses()](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html#getPackageClasses-java.lang.String-)).
- Javadoc summary (see: [getClassSummary()](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html#getClassSummary-java.lang.Class-))
- and [more](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html)

ClassIndex:
- is as fast as reading a file, it is not impacted by the usual performance penalty of the classpath scanning,
- is based on standard APIs provided by Java, like [annotation processing](https://jcp.org/en/jsr/detail?id=269),
it does not assume any inner workings of the ClassLoaders, it does not analyse bytecode of a compiled classes,
- supports incremental compilation in IntelliJ, NetBeans and Eclipse,
- is compatible with Java modules,
- is compatible with [ServiceLoader](http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html),
- is compatible with [jaxb.index](https://docs.oracle.com/javaee/6/api/javax/xml/bind/JAXBContext.html#newInstance(java.lang.String,%20java.lang.ClassLoader)),


## How to use it?

1. Add dependency

    1. Maven
        ```xml
        <dependency>
            <groupId>org.atteo.classindex</groupId>
            <artifactId>classindex</artifactId>
            <version>3.4</version>
        </dependency>
        ```
    1. Gradle
        ```groovy
        compile 'org.atteo.classindex:classindex:3.4'
        ```
    1. For other see: [Maven Central](https://search.maven.org/artifact/org.atteo.classindex/classindex/3.4/jar)
1. Annotate your annotation with @IndexAnnotated
    ```java
       @IndexAnnotated
       public @interface Entity {}
    ```
    
1. Retrieve a list of annotated classes at run-time
    ```java
       Iterable<Class<?>> klasses = ClassIndex.getAnnotated(Entity.class);
    ``` 
        
## How it works?

### Indexer

ClassIndex indexes your classes at compile time by providing the implementation of the standard [annotation 
processor](http://www.jcp.org/en/jsr/detail?id=269). Adding ClassIndex to your compile classpath is sufficient to 
trigger indexing process thanks to
[automatic discovery](https://stackoverflow.com/questions/11685498/what-is-the-default-annotation-processors-discovery-process)
of annotation processors by 'javac'.

### Run-time API

ClassIndex provides a [convenient API](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html)
to read the indexes generated by the annotation processor.

## Why ClassIndex?

### Speed

Traditional classpath scanning is a [very](https://www.leveluplunch.com/blog/2015/08/11/reducing-startup-times-spring-applications-context/)
[slow](https://wiki.apache.org/tomcat/HowTo/FasterStartUp) process. 
Replacing it with compile-time indexing speeds Java applications bootstrap considerably.

Here are the results of the [benchmark](https://github.com/atteo/classindex-benchmark) comparing ClassIndex with various scanning solutions.

| Library                  | Application startup time |
| :----------------------- |-------------------------:|
| None - hardcoded list    |                  0:00.18 |
| [Scannotation](http://scannotation.sourceforge.net/)             |                  0:05.11 |
| [Reflections](https://github.com/ronmamo/reflections)            |                  0:05.37 |
| Reflections Maven plugin |                                                          0:00.52 |
| [Corn](https://sites.google.com/site/javacornproject/corn-cps)   |                  0:24.60 |
| ClassIndex               |                  0:00.18 |

Notes: benchmark was performed on Intel i5-2520M CPU @ 2.50GHz, classpath size was set to 121MB.

### Compatibility

The traditional approach to retrieve a list of classes annotated with a given annotation is a process
called classpath scanning. This involves:
 1. finding out the application run-time classpath and
 1. analysing the bytecode of the classes located there.
 
The first part is not really possible in a general case, because
[ClassLoader API](https://docs.oracle.com/javase/10/docs/api/java/lang/ClassLoader.html) does not provide
a method to retrieve its classpath. Well, the ClassLoader can even generate the classes on the fly.
Usually, though, the specific classloader implementation used is a
[URLClassLoader](https://docs.oracle.com/javase/10/docs/api/java/net/URLClassLoader.html) from which the classpath
can be retrieved using its [getURLs()](https://docs.oracle.com/javase/10/docs/api/java/net/URLClassLoader.html#getURLs())
method.

ClassIndex, on the other hand, uses annotation processing to generate the index of classes at compile-time
ant put it with the rest of the compiled .class files. This does not slow the compilation process in any measurable way.
The index is then available at run-time using the
[getResource() method](https://docs.oracle.com/javase/10/docs/api/java/lang/ClassLoader.html#getResource(java.lang.String))
which must be implemented by all classloaders.


## Usage

### Class Indexing

There are two annotations which trigger compile-time indexing:

* [@IndexSubclasses](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/IndexSubclasses.html)
	* when placed on interface makes an index of all classes implementing the interface,
	the classes can then be retrieved using
	[getSubclasses()](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html#getSubclasses-java.lang.Class-) method.
	* when placed on a class makes an index of its subclasses
	* and finally when placed in package-info.java it creates an index of all classes inside that package (directly -
	 without subpackages), the classes can then be retrieved using
	[getPackageClasses()](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html#getPackageClasses-java.lang.String-) method.
	 .
* [@IndexAnnotated](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/IndexAnnotated.html) when 
placed on an annotation makes an index of all classes marked with that annotation,
the classes can then be retrieved using
[getAnnotated()](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html#getAnnotated-java.lang.Class-) method.

Here is an example:

```java
@IndexAnnotated
public @interface Entity {
}
 
@Entity
public class Car {
}
 
...
 
for (Class<?> klass : ClassIndex.getAnnotated(Entity.class)) {
    System.out.println(klass.getName());
}
```

You can also find the complete example as a Maven project on Github:
https://github.com/atteo/classindex-maven-example


### Service Loader

For subclasses of the given class the index file name and format is compatible with what
[ServiceLoader](http://docs.oracle.com/javase/10/docs/api/java/util/ServiceLoader.html) expects.

This means that if you annotate your service interface with @IndexSubclasses,
the files in META-INF/services/ will be generated automatically for you.

Keep in mind that ServiceLoader also requires for the classes to have a zero-argument default constructor.

### jaxb.index

For classes inside given package the index file is named "jaxb.index", it is located inside the package folder and it's format is compatible with what [JAXBContext.newInstance(String)](http://docs.oracle.com/javase/7/docs/api/javax/xml/bind/JAXBContext.html#newInstance(java.lang.String)) expects.

This means that if you annotate your package in package-info.java file with @IndexSubclasses,
jaxb.index file will be generated automatically for you.

### Javadoc storage

From version 2.0 [@IndexAnnotated](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/IndexAnnotated.html) and [@IndexSubclasses](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/IndexSubclasses.html) allow to specify storeJavadoc attribute. When set to true Javadoc comment for the indexed classes will be stored. You can retrieve first sentence of the Javadoc using [ClassIndex.getClassSummary()](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/ClassIndex.html#getClassSummary(java.lang.Class)).

```java
@IndexAnnotated(storeJavadoc = true)
public @interface Entity {
}
 
/**
 * This is car.
 * Detailed car description follows.
 */
@Entity
public class Car {
}
 
...
 
assertEquals("This is car", ClassIndex.getClassSummary(Car.class));
```

### Class filtering

Filtering allows you to select only classes with desired characteristics. Here are some basic samples:

* Selecting only top-level classes

```java
ClassFilter.only()
	.topLevel()
	.from(ClassIndex.getAnnotated(SomeAnnotation.class));
```

* Selecting only classes which are top level and public at the same time

```java
ClassFilter.only()
	.topLevel()
	.withModifiers(Modifier.PUBLIC)
	.from(ClassIndex.getAnnotated(SomeAnnotation.class));

```

* Selecting classes which are top-level or enclosed in given class:

```java
ClassFilter.any(
	ClassFilter.only().topLevel(),
	ClassFilter.only().enclosedIn(WithInnerClassesInside.class)
).from(ClassIndex.getAnnotated(SomeAnnotation.class);
```

### Indexing when annotations cannot be used

Sometimes you cannot easily use annotations to trigger compile time indexing because you don't control the source code of the classes which should be annotated. For instance you cannot add @IndexAnnotated meta-annotation to [@Entity](http://docs.oracle.com/javaee/7/api/javax/persistence/Entity.html) annotation. Although not so straightforward, it is still possible to use ClassIndex in this case.

There are two steps necessary:

First create a custom annotation processor by extending ClassIndexProcessor

```java
public class MyImportantClassIndexProcessor extends ClassIndexProcessor {
    public MyImportantClassIndexProcessor() {
        indexAnnotations(Entity.class);
    }
}
```
In the constructor specify what indexes should be created by calling apriopriate methods:
* [indexAnnotations(...)](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/processor/ClassIndexProcessor.html#indexAnnotations-java.lang.Class...-) - to create index of classes annotated with given annotations
* [indexSubclasses(...)](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/processor/ClassIndexProcessor.html#indexSubclasses-java.lang.Class...-) - to create index of subclasses of given parent classes
* [indexPackages(...)](http://www.atteo.org/static/classindex/apidocs/org/atteo/classindex/processor/ClassIndexProcessor.html#indexPackages-java.lang.String...-) - to create index of classes inside given packages.

Finally register the processor by creating the file 'META-INF/services/javax.annotation.processing.Processor' in your 
classpath with the full class name of your processor, see the example [here](https://github.com/atteo/classindex/blob/master/classindex/src/test/resources/META-INF/services/javax.annotation.processing.Processor)

Important note: you also need to ensure that your custom processor is always available on the classpath when 
compiling indexed classes. When that is not the case there will not be any error - those classes will be missing in the index.

### License

ClassIndex is available under [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0).

### Download

You can download the library from [here](http://search.maven.org/remotecontent?filepath=org/atteo/classindex/classindex/3.4/classindex-3.4.jar) or use the following Maven dependency:

