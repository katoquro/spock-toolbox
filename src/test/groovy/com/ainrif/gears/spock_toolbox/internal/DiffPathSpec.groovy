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

import spock.lang.Specification

import java.util.Map.Entry

import static com.ainrif.gears.spock_toolbox.Replicator.replicate
import static com.ainrif.gears.spock_toolbox.internal.DiffNode.ROOT_DESIGNATION

class DiffPathSpec extends Specification {
    def "path should have ability to be created from string"(Entry<String, DiffPath> data) {
        when:
        def actual = DiffPath.fromString(data.key)

        then:
        actual.offset == data.value.offset
        actual.tokens == data.value.tokens

        where:
        data << [
                a         : replicate(DiffPath) {
                    offset = 0
                    tokens = [ROOT_DESIGNATION, 'a']
                },
                'a.b'     : replicate(DiffPath) {
                    offset = 0
                    tokens = [ROOT_DESIGNATION, 'a', 'b']
                },
                'a.b[*].c': replicate(DiffPath) {
                    offset = 0
                    tokens = [ROOT_DESIGNATION, 'a', 'b', '*', 'c']
                },
                'a.b[1].c': replicate(DiffPath) {
                    offset = 0
                    tokens = [ROOT_DESIGNATION, 'a', 'b', '1', 'c']
                },
                'a.b.*.c' : replicate(DiffPath) {
                    offset = 0
                    tokens = [ROOT_DESIGNATION, 'a', 'b', '*', 'c']
                },
                'a.b.1.c' : replicate(DiffPath) {
                    offset = 0
                    tokens = [ROOT_DESIGNATION, 'a', 'b', '1', 'c']
                }
        ]
    }

    def "path should return token corresponding to next calls"() {
        given:
        def root = DiffPath.fromString('a.b.c[*].d[1].f')

        expect:
        root.token == ROOT_DESIGNATION
        def next0 = root.nextToken()
        next0.token == 'a'
        def next1 = next0.nextToken()
        next1.token == 'b'
        def next2 = next1.nextToken()
        next2.token == 'c'
        def next3 = next2.nextToken()
        next3.token == '*'
        def next4 = next3.nextToken()
        next4.token == 'd'
        def next5 = next4.nextToken()
        next5.token == '1'
        def next6 = next5.nextToken()
        next6.token == 'f'
        !next6.hasNext()
    }
}
