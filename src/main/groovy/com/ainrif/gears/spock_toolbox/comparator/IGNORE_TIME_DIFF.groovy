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

package com.ainrif.gears.spock_toolbox.comparator

import org.unitils.reflectionassert.ReflectionComparator
import org.unitils.reflectionassert.comparator.Comparator
import org.unitils.reflectionassert.difference.Difference

import java.time.temporal.Temporal

/**
 * Comparator that checks whether 2 dates are both null or not null, the actual time-value is not compared.
 * This can be useful when the actual time/date is not known is advance but you still want to check whether
 * a value has been set or not, e.g. a last modification timestamp in the database.
 */
class IGNORE_TIME_DIFF implements Comparator {

    @Override
    boolean canCompare(Object left, Object right) {
        if (!left && !right) {
            return true
        }
        def leftIsDate = left && [Temporal, Date, Calendar].any { it.isAssignableFrom(left.class) }
        def rightIsDate = right && [Temporal, Date, Calendar].any { it.isAssignableFrom(right.class) }

        if (leftIsDate && rightIsDate) {
            return true
        }

        if ((!right && leftIsDate) || (!left && rightIsDate)) {
            return true
        }

        return false
    }

    @Override
    Difference compare(Object left, Object right,
                       boolean onlyFirstDifference, ReflectionComparator reflectionComparator) {
        def leftIsDate = left && [Temporal, Date, Calendar].any { it.isAssignableFrom(left.class) }
        def rightIsDate = right && [Temporal, Date, Calendar].any { it.isAssignableFrom(right.class) }

        if ((!right && leftIsDate) || (!left && rightIsDate)) {
            return new Difference("Time difference is ignored, but not both instantiated or both null", left, right);
        }

        return null
    }
}
