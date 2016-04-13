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

package com.ainrif.gears.spock_device

import spock.lang.FailsWith
import spock.lang.Specification

import static Replicator.replicate

class ReplicatorSpec extends Specification {
    def "all fields should be set"() {
        when:
        def actual = replicate(Pojo) {
            stringValue = '1'
            intValue = 1
        }

        then:
        actual.stringValue == '1'
        actual.intValue == 1
    }

    def "null values also considered as set"() {
        when:
        def actual = replicate(Pojo) {
            stringValue = null
            intValue = 1
        }

        then:
        !actual.stringValue
        actual.intValue == 1
    }

    def "should fail if at least one fields was omit"() {
        when:
        replicate(Pojo) {
            stringValue = null
        }

        then:
        thrown AssertionError
    }

    def "should be able to process classes w/o default constructor"() {
        when: 'immutable class'
        def actual = replicate(Pojo.Immutable, ['c']) {}

        then:
        actual.finalString == 'c'

        when: 'try use newInstance w/o args'
        actual = replicate(Pojo.WithOutDefaultConstructor) {
            stringValue = '1'
            intValue = 1
            childStringValue = '2'
        }

        then:
        actual.stringValue == '1'
        actual.intValue == 1
        actual.childStringValue == '2'
    }

    @FailsWith(value = AssertionError, reason = "while constructor params are not supported")
    def "fields which were set in constructor should be takes to account"() {
        when: 'try use newInstance w/o args'
        def actual = replicate(Pojo.WithOutDefaultConstructor, ['2']) {
            stringValue = '1'
            intValue = 1
        }

        then:
        actual.stringValue == '1'
        actual.intValue == 1
        actual.childStringValue == '2'
    }

    static class Pojo {
        public String stringValue
        protected int intValue

        static class Immutable {
            final String finalString

            Immutable(String finalString) {
                this.finalString = finalString
            }
        }

        static class WithOutDefaultConstructor extends Pojo {
            String childStringValue

            WithOutDefaultConstructor(String childStringValue) {
                this.childStringValue = childStringValue
            }
        }
    }
}
