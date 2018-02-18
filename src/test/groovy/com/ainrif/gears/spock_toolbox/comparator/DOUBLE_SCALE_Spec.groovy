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

import spock.lang.Specification

class DOUBLE_SCALE_Spec extends Specification {
    def "should compare only doubles"() {
        given:
        double d1 = 1.2
        Double d2 = 1.2
        BigDecimal bd = 1.2

        expect:
        new DOUBLE_SCALE().canCompare(d1, d2)
        !new DOUBLE_SCALE().canCompare(d2, bd)
    }

    def "should compare with given precision"() {
        given:
        double d1 = 1 - 1e-15
        double d2 = 1
        double d3 = 1.5

        expect:
        new DOUBLE_SCALE().compare(d1, d2, false, null) == null
        new DOUBLE_SCALE().compare(d2, d3, false, null) != null
        DOUBLE_SCALE.scale(0.5).compare(d2, d3, false, null) == null
    }
}
