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

class ReflectionObjectComparator extends ObjectComparator {
    @Override
    Difference compare(Object left, Object right,
                       boolean onlyFirstDifference,
                       ReflectionComparator reflectionComparator) {
        // check different class type
        Class<?> clazz = left.class
        if (!right.class.isAssignableFrom(left.class)) {
            return new ClassDifference("Left class (${left}) should coincide or be a subclass of right(${right})",
                    left, right, left.class, right.class)
        }
        // compare all fields of the object using reflection
        ObjectDifference difference = new ObjectDifference("Different field values", left, right)
        compareFields(left, right, clazz, difference, onlyFirstDifference, reflectionComparator)

        if (difference.fieldDifferences.isEmpty()) {
            return null
        }

        return difference
    }
}
