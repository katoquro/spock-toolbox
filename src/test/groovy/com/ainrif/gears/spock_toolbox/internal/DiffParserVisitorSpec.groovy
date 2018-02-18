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

import org.unitils.reflectionassert.difference.Difference
import spock.lang.Specification

import static com.ainrif.gears.spock_toolbox.Replicator.replicate

class DiffParserVisitorSpec extends Specification {

    def "smoke of hierarchy parsing result"() {
        given:
        def leftModel = replicate(Pogo) {
            plainField = 'a'
            plainMap = [a: '1']
            plainArray = ['1-a', '2']
            objectArray = [new Pogo(plainField: 'a-a')]
        }
        def rightModel = replicate(Pogo) {
            plainField = 'b'
            plainMap = [a: '2']
            plainArray = ['1-b', '2']
            objectArray = [new Pogo(plainField: 'b-b')]
        }
        and:
        def expectedResult = replicate(DiffNode) {
            array = false
            designation = ROOT_DESIGNATION
            actual = null
            expected = null
            excluded = false

            nodes = [
                    replicate(DiffNode) {
                        array = false
                        designation = 'plainField'
                        actual = 'a'
                        expected = 'b'
                        excluded = false
                        nodes = []
                    },
                    replicate(DiffNode) {
                        array = false
                        designation = 'plainMap'
                        actual = null
                        expected = null
                        excluded = false
                        nodes = [replicate(DiffNode) {
                            array = false
                            designation = 'a'
                            actual = '1'
                            expected = '2'
                            excluded = false
                            nodes = []
                        }]
                    },
                    replicate(DiffNode) {
                        array = true
                        designation = 'plainArray'
                        actual = null
                        expected = null
                        excluded = false
                        nodes = [replicate(DiffNode) {
                            array = false
                            designation = '0'
                            actual = '1-a'
                            expected = '1-b'
                            excluded = false
                            nodes = []
                        }]
                    },
                    replicate(DiffNode) {
                        array = true
                        designation = 'objectArray'
                        actual = null
                        expected = null
                        excluded = false
                        nodes = [replicate(DiffNode) {
                            array = false
                            designation = '0'
                            actual = null
                            expected = null
                            excluded = false
                            nodes = [replicate(DiffNode) {
                                array = false
                                designation = 'plainField'
                                actual = 'a-a'
                                expected = 'b-b'
                                excluded = false
                                nodes = []
                            }]
                        }]
                    }
            ]
        }

        and:
        Difference modelDiff = ExtendedReflectionComparatorFactory
                .create([], [])
                .getDifference(leftModel, rightModel)

        when:
        def result = new DiffParserVisitor().parse(modelDiff)

        then:
        !ExtendedReflectionComparatorFactory
                .create([], [])
                .getDifference(expectedResult, result)
    }

    static class Pogo {
        String plainField
        Map<String, String> plainMap
        List<String> plainArray
        List<Pogo> objectArray
    }
}
