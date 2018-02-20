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

/**
 * Compare doubles with precision 1e-14
 * <p>
 * precision can be configured with {@link DOUBLE_SCALE#scale(double)}
 */
class DOUBLE_SCALE implements Comparator {

    /**
     * Gen new customised comparator
     *
     * @param doubleError precision
     * @return comparator instance
     */
    static DOUBLE_SCALE scale(double doubleError) {
        new DOUBLE_SCALE(doubleError)
    }

    protected double doubleError

    DOUBLE_SCALE() {
        this(1e-14d)
    }

    protected DOUBLE_SCALE(double doubleError) {
        this.doubleError = doubleError
    }

    @Override
    boolean canCompare(Object left, Object right) {
        return left instanceof Double && right instanceof Double
    }

    @Override
    Difference compare(Object left, Object right, boolean onlyFirstDifference, ReflectionComparator reflectionComparator) {
        return Math.abs((double) left - (double) right) > doubleError ?
                new Difference("Different double values", left, right) :
                null
    }
}