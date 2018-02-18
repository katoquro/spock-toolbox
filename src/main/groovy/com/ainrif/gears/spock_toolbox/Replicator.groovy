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

import com.ainrif.gears.spock_toolbox.internal.ReplicatorErrorDescription
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam

import java.lang.reflect.Field

import static java.lang.reflect.Modifier.isFinal

/**
 * Provides different ways to create or instantiate objects.
 * Usually used in `setup` or `given` stanzas
 */
class Replicator {

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
        return replicate(type, null, init)
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
        def instance = instantiateType(type, args as Object[])
        instance.metaClass.tricordered = [set: new HashSet<String>()]
        instance.metaClass.setProperty = { name, value ->
            delegate.@"$name" = value
            delegate.tricordered['set'] << name
        }
        init = init.clone() as Closure
        init.delegate = instance
        init.resolveStrategy = Closure.DELEGATE_FIRST
        init.call(instance)

        def fieldNames = getMutableFields(type)*.name
        def touchedFields = instance.tricordered['set'] as Set

        if (!(fieldNames.size() == touchedFields.size() && fieldNames.containsAll(touchedFields))) {
            def diff = fieldNames - touchedFields
            throw new AssertionError(new ReplicatorErrorDescription('Not all fields were set', fieldNames, touchedFields, diff))
        }

        return instance
    }

    /*
    * Protected Date Generator API
    */

    protected static List<Field> getMutableFields(Class clazz) {
        def _clazz = clazz;
        List<Field> fields = _clazz.interface ? [] : _clazz.declaredFields as List

        while (null != (_clazz = _clazz.superclass)) {
            fields += _clazz.declaredFields as List
        }

        return fields.findAll { !it.synthetic && !isFinal(it.modifiers) }
    }

    protected static <T> T instantiateType(Class<T> type, Object[] args = null) {
        T instance
        if (args) {
            instance = type.newInstance(args)
        } else {
            instance = type.newInstance()
        }

        return instance
    }
}
