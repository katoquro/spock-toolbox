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

package com.ainrif.gears.spock_device.internal

import com.ainrif.gears.spock_device.comparator.DOUBLE_SCALE
import com.ainrif.gears.spock_device.comparator.STRICT_ORDER
import spock.lang.Specification

import static com.ainrif.gears.spock_device.Replicator.replicate

class ReflectionMatcherBuilderSpec extends Specification {
    def "similar objects with different hierarchy shouldn't be equal"() {
        given:
        def actual = replicate(Pojo1) {
            stringValue = '42'
            intValue = 42
        }

        def expected = replicate(Pojo2) {
            stringValue = '42'
            intValue = 42
        }

        expect:
        !new ReflectionMatcherBuilder(actual, expected)
    }

    def "similar subclasses should be equal"() {
        given:
        def actual = replicate(Pojo3) {
            stringValue = '42'
            intValue = 42
        }

        def expected = replicate(Pojo1) {
            stringValue = '42'
            intValue = 42
        }

        expect:
        new ReflectionMatcherBuilder(actual, expected)
    }

    def "superclasses cannot be equal to subclasses"() {
        given:
        def actual = replicate(Pojo1) {
            stringValue = '42'
            intValue = 42
        }

        def expected = replicate(Pojo3) {
            stringValue = '42'
            intValue = 42
        }

        expect:
        !new ReflectionMatcherBuilder(actual, expected)
    }

    def "by default order in arrays is ignored"() {
        given:
        def pojo1 = replicate(Pojo1) {
            stringValue = '42'
            intValue = 42
        }
        def pojo2 = replicate(Pojo1) {
            stringValue = '42_2'
            intValue = 42_2
        }
        def actual = replicate(WithList) {
            list = [pojo2, pojo1]
        }
        def expected = replicate(WithList) {
            list = [pojo1, pojo2]
        }

        expect:
        new ReflectionMatcherBuilder(actual, expected)
        !new ReflectionMatcherBuilder(actual, expected).mode(STRICT_ORDER)
    }

    def "any field should be available to exclude"() {
        given:
        def pojo1 = replicate(Pojo1) {
            stringValue = '42'
            intValue = 42
        }
        def pojo2 = replicate(Pojo1) {
            stringValue = '42_2'
            intValue = 42
        }
        def actual = replicate(WithListAndNested) {
            list = [pojo2]
            pojo = pojo1
            stringValue = 'a'
        }
        def expected = replicate(WithListAndNested) {
            list = [new Object()]
            pojo = pojo2
            stringValue = 'b'
        }

        expect:
        new ReflectionMatcherBuilder(actual, expected)
                .excludeFields('list', 'pojo.stringValue', 'stringValue')
    }

    def "given comparator instances shouldn't be cached and should be used once"() {
        given:
        def actual = new WithDoubles(d: 1 - 1e-15)
        def expected = new WithDoubles(d: 1)

        expect:
        new ReflectionMatcherBuilder(actual, expected)

        when:
        actual = new WithDoubles(d: 0.025)
        expected = new WithDoubles(d: 0.026)

        then:
        !new ReflectionMatcherBuilder(actual, expected).comparator(DOUBLE_SCALE.scale(0.00001))
        new ReflectionMatcherBuilder(actual, expected).comparator(DOUBLE_SCALE.scale(0.001))
        !new ReflectionMatcherBuilder(actual, expected)
    }

    private static class Pojo1 {
        public String stringValue
        protected int intValue
    }

    private static class Pojo2 {
        public String stringValue
        protected int intValue
    }

    private static class Pojo3 extends Pojo1 {
    }

    private static class WithList {
        List<Pojo1> list
    }

    private static class WithListAndNested {
        List<Pojo1> list
        Pojo1 pojo
        String stringValue
    }

    private static class WithDoubles {
        double d
    }
}
