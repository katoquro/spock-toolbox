/*
 * Copyright 2014-2018 Ainrif <support@ainrif.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ainrif.gears.spock_toolbox

import com.ainrif.gears.spock_toolbox.internal.ReflectionMatcherBuilder
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam

class SpockToolbox {
    /**
     * @see com.ainrif.gears.spock_toolbox.internal.ReflectionMatcherBuilder
     */
    static ReflectionMatcherBuilder reflects(def actual, def expected) {
        return new ReflectionMatcherBuilder(actual, expected)
    }

    /**
     * Create instance of given type and check that all were touched,
     * e.g. fields were set in init closure or assigned to null
     *
     * @param type to initialise
     * @param init post-initialise closure delegated to new instance
     *
     * @return new initialized object
     */
    static <T> T replicate(@DelegatesTo.Target Class<T> type,
                           @DelegatesTo(strategy = Closure.DELEGATE_FIRST, genericTypeIndex = 0)
                           @ClosureParams(FirstParam.FirstGenericType.class) Closure init) {
        return Replicator.replicate(type, null, init)
    }

    /**
     * The same as {@link #replicate(java.lang.Class, groovy.lang.Closure)} but uses non default constructors
     * to initialise instance.
     * <p>
     * Use this method if you want to initialise class with final fields which are set via the constructor
     * otherwise use {@link #replicate(java.lang.Class, groovy.lang.Closure)} even if java type doesn't have
     * default constructor
     *
     * @param type to initialise
     * @param args params for constructor, type should have corresponding constructor
     * @param init post-initialise closure delegated to new instance
     *
     * @return new initialized object
     */
    static <T> T replicate(@DelegatesTo.Target Class<T> type,
                           List<Object> args,
                           @DelegatesTo(strategy = Closure.DELEGATE_FIRST, genericTypeIndex = 0)
                           @ClosureParams(FirstParam.FirstGenericType.class) Closure init) {
        return Replicator.replicate(type, args, init)
    }
}
