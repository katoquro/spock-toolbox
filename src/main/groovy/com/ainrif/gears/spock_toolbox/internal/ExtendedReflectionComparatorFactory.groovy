/*
 * Copyright 2014-2016 Ainrif <support@ainrif.com>
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

import com.ainrif.gears.spock_toolbox.comparator.DOUBLE_SCALE
import com.ainrif.gears.spock_toolbox.comparator.IGNORE_DEFAULTS
import com.ainrif.gears.spock_toolbox.comparator.IGNORE_TIME_DIFF
import com.ainrif.gears.spock_toolbox.comparator.STRICT_ORDER
import org.unitils.reflectionassert.ReflectionComparator
import org.unitils.reflectionassert.ReflectionComparatorFactory
import org.unitils.reflectionassert.comparator.Comparator

/**
 * @see ReflectionComparatorFactory
 */
class ExtendedReflectionComparatorFactory extends ReflectionComparatorFactory {

    protected static HashMap<Class<? extends Comparator>, ? extends Comparator> modesRegistry = [
            (STRICT_ORDER)    : new STRICT_ORDER(),
            (IGNORE_TIME_DIFF): new IGNORE_TIME_DIFF(),
            (IGNORE_DEFAULTS) : new IGNORE_DEFAULTS(),
            (DOUBLE_SCALE)    : new DOUBLE_SCALE(),
    ]

    /**
     * Creates a reflection comparator for the given modes and comparators.
     * If no mode is given, a strict comparator will be created (excluded collection order)
     *
     * @param customComparators this instances will not be cached as singletons
     * @param modes null for strict comparison
     * @return reflection comparator
     */
    static ReflectionComparator create(List<Comparator> customComparators,
                                       List<Class<? extends Comparator>> modes) {
        List<Comparator> comparators = []
        comparators += customComparators

        modes.each {
            comparators << modesRegistry.computeIfAbsent(it, { type -> type.newInstance() })
        }

        if (!modes.contains(STRICT_ORDER)) {
            comparators << LENIENT_ORDER_COMPARATOR
        }

        comparators << modesRegistry[DOUBLE_SCALE]
        comparators << LENIENT_NUMBER_COMPARATOR
        comparators << SIMPLE_CASES_COMPARATOR
        comparators << MAP_COMPARATOR
        comparators << HIBERNATE_PROXY_COMPARATOR
        comparators << OBJECT_COMPARATOR

        return new ReflectionComparator(comparators)
    }
}
