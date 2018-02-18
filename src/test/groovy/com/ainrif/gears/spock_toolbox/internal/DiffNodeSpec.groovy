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

import static com.ainrif.gears.spock_toolbox.Replicator.replicate

class DiffNodeSpec extends Specification {
    def "should support exclude of plain fields"() {
        given:
        def node = replicate(DiffNode) {
            array = false
            designation = ROOT_DESIGNATION
            actual = null
            expected = null
            excluded = false
            nodes = [replicate(DiffNode) {
                array = false
                designation = 'a'
                nodes = []
                actual = null
                expected = null
                excluded = false
            }, replicate(DiffNode) {
                array = false
                designation = 'b'
                nodes = []
                actual = null
                expected = null
                excluded = false
            }]
        }
        when:
        node.exclude(DiffPath.fromString('b'))

        then:
        !node.excluded
        !node.nodes.find { it.designation == 'a' }.excluded
        node.nodes.find { it.designation == 'b' }.excluded
    }

    def "should support exclude of nested object (Map) fields"() {
        given:
        def node = replicate(DiffNode) {
            array = false
            designation = ROOT_DESIGNATION
            actual = null
            expected = null
            excluded = false
            nodes = [replicate(DiffNode) {
                array = false
                designation = 'objectOrMap'
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
            }]
        }
        when:
        node.exclude(DiffPath.fromString('objectOrMap.a'))

        then:
        !node.excluded
        !node.nodes.first().excluded
        node.nodes.first().nodes.first().excluded
    }

    def "should support exclude of array fields"() {
        given:
        def node = replicate(DiffNode) {
            array = false
            designation = ROOT_DESIGNATION
            actual = null
            expected = null
            excluded = false
            nodes = [replicate(DiffNode) {
                array = false
                designation = 'arrayField'
                actual = null
                expected = null
                excluded = false
                nodes = []
            }]
        }
        when:
        node.exclude(DiffPath.fromString('arrayField'))

        then:
        !node.excluded
        node.nodes.first().excluded
    }

    def "should support exclude of arrays by index"() {
        given:
        def node = replicate(DiffNode) {
            array = false
            designation = ROOT_DESIGNATION
            actual = null
            expected = null
            excluded = false
            nodes = [replicate(DiffNode) {
                array = false
                designation = 'arrayField'
                actual = null
                expected = null
                excluded = false
                nodes = [replicate(DiffNode) {
                    array = false
                    designation = '0'
                    actual = '1'
                    expected = '2'
                    excluded = false
                    nodes = []
                }, replicate(DiffNode) {
                    array = false
                    designation = '1'
                    actual = '1'
                    expected = '2'
                    excluded = false
                    nodes = []
                }]
            }]
        }
        when:
        node.exclude(DiffPath.fromString('arrayField[1]'))

        then:
        !node.excluded
        !node.nodes.first().excluded
        !node.nodes.first().nodes.find { it.designation == '0' }.excluded
        node.nodes.first().nodes.find { it.designation == '1' }.excluded
    }
}
