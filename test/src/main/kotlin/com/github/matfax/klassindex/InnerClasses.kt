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

/**
 * Inner classes test.
 */
class InnerClasses {

    // anonymous inner classes should not be indexed
    private val module = object : Module {

    }

    // both static and non-static inner classes should be indexed
    class InnerService : Service

    inner class InnerModule : Module

    @Component
    class InnerComponent {
        @Component
        internal inner class InnerInnerComponent

        companion object
    }

    // local classes should not be indexed
    fun testMethod() {
        class NotIndexedClass
    }
}
