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

package com.ainrif.gears.spock_device.comparator

import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime

class IGNORE_TIME_DIFF_Spec extends Specification {
    def "should compare only pairs that contain jdk time types"() {
        given:
        def comparator = new IGNORE_TIME_DIFF()

        expect:
        comparator.canCompare(left, right) == expected

        where:
        left                | right                | expected
        null                | null                 | true
        new Date()          | null                 | true
        null                | new Date()           | true
        LocalDateTime.now() | null                 | true
        null                | LocalDate.now()      | true
        Calendar.instance   | new java.sql.Date(0) | true
        new Date()          | 1                    | false
    }

    def "should ignore difference between time types"() {
        given:
        def comparator = new IGNORE_TIME_DIFF()

        when:
        def actual = comparator.compare(left, right, false, null) == null

        then:
        actual == expected

        where:
        left              | right                | expected
        null              | null                 | true
        new Date()        | null                 | false
        null              | new Date()           | false
        Calendar.instance | new java.sql.Date(0) | true
    }
}
