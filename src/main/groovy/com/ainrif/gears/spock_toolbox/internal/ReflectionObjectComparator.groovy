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

package com.ainrif.gears.spock_toolbox.internal

import org.unitils.reflectionassert.ReflectionComparator
import org.unitils.reflectionassert.comparator.impl.ObjectComparator
import org.unitils.reflectionassert.difference.ClassDifference
import org.unitils.reflectionassert.difference.Difference
import org.unitils.reflectionassert.difference.ObjectDifference

import java.lang.reflect.Field

import static java.lang.reflect.Modifier.isStatic
import static java.lang.reflect.Modifier.isTransient

class ReflectionObjectComparator extends ObjectComparator {
    @Override
    Difference compare(Object left, Object right,
                       boolean onlyFirstDifference,
                       ReflectionComparator reflectionComparator) {
        // check different class type
        Class<?> clazz = left.getClass()
        if (!right.getClass().isAssignableFrom(left.getClass())) {
            return new ClassDifference("Left class (${left}) should coincide or be a subclass of right(${right})",
                    left, right, left.getClass(), right.getClass())
        }
        // compare all fields of the object using reflection
        ObjectDifference difference = new ObjectDifference("Different field values", left, right)
        compareFields(left, right, clazz, difference, onlyFirstDifference, reflectionComparator)

        if (difference.fieldDifferences.isEmpty()) {
            return null
        }

        return difference
    }

    @Override
    protected void compareFields(Object left, Object right,
                                 Class<?> clazz,
                                 ObjectDifference difference, boolean onlyFirstDifference,
                                 ReflectionComparator reflectionComparator) {
        Field[] fields = clazz.declaredFields

        for (Field field : fields) {
            // skip transient and static fields
            if (isTransient(field.getModifiers()) || isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue
            }
            field.setAccessible(true)
            // recursively check the value of the fields

            def rightValue
            try {
                rightValue = field.get(right)
            } catch (IllegalArgumentException ignore) {
                rightValue = null
            }
            def innerDifference = reflectionComparator.getDifference(field.get(left), rightValue, onlyFirstDifference)
            if (innerDifference != null) {
                difference.addFieldDifference(field.getName(), innerDifference)
                if (onlyFirstDifference) {
                    return
                }
            }
        }

        // compare fields declared in superclass
        Class<?> superclazz = clazz.superclass
        while (superclazz && !superclazz.getName().startsWith("java.lang")) {
            compareFields(left, right, superclazz, difference, onlyFirstDifference, reflectionComparator)
            superclazz = superclazz.superclass
        }
    }
}
