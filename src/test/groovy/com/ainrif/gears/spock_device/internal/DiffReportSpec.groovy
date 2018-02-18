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

package com.ainrif.gears.spock_device.internal

import spock.lang.Specification

import static com.ainrif.gears.spock_device.Replicator.replicate

class DiffReportSpec extends Specification {
    def "report should skip excluded nodes and their child nodes"() {
        given:
        def root = replicate(DiffNode) {
            array = false
            designation = ROOT_DESIGNATION
            actual = null
            expected = null
            excluded = false
            nodes = [replicate(DiffNode) {
                array = false
                designation = 'a'
                nodes = [replicate(DiffNode) {
                    array = false
                    designation = 'ab'
                    nodes = []
                    actual = '1'
                    expected = '2'
                    excluded = true
                }]
                actual = '1'
                expected = '2'
                excluded = true
            }]
        }
        when:
        def report = new DiffReport(root).createReport()

        then:
        report == 'objects are equal'
    }

    def "report should skip excluded fields"() {
        given:
        def root = replicate(DiffNode) {
            array = false
            designation = ROOT_DESIGNATION
            actual = null
            expected = null
            excluded = false
            nodes = [replicate(DiffNode) {
                array = false
                designation = 'a'
                nodes = [replicate(DiffNode) {
                    array = false
                    designation = 'ab'
                    nodes = []
                    actual = '3'
                    expected = '4'
                    excluded = true
                }]
                actual = '1'
                expected = '2'
                excluded = false
            }]
        }
        when:
        def report = new DiffReport(root).createReport()

        then:
        report == '''
            at _.a
            \texpected: 2
            \t but was: 1
            '''.stripIndent()
    }
}
