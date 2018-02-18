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

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import static com.ainrif.gears.spock_toolbox.internal.DiffPath.WILDCARD

@ToString(includePackage = false, includeNames = true)
@EqualsAndHashCode
class DiffNode {
    static final String ROOT_DESIGNATION = '_'

    boolean array

    String designation = ROOT_DESIGNATION

    List<DiffNode> nodes = []

    String actual
    String expected

    boolean excluded

    DiffNode leftShift(List<DiffNode> nestedNodes) {
        nodes.addAll(nestedNodes)
        return this
    }

    /**
     * @return true if current node or any nested node has diff-info
     *         and are not excluded
     */
    boolean containsDiff() {
        return !excluded && (hasDiff() || (nodes && nodes.any { it.containsDiff() }))
    }

    /**
     * @return true if current node has diff-info and are not excluded
     */
    boolean hasDiff() {
        return !excluded && (actual || expected)
    }

    void exclude(DiffPath path) {
        if (path.hasNext()) {
            def next = path.nextToken()
            if (array) {
                if (WILDCARD == next.token) {
                    nodes.each { it.exclude(next) }
                } else {
                    def nextNode = nodes.find { it.designation == next.token }
                    nextNode.exclude(next)
                }
            } else {
                def nextNode = nodes.find { it.designation == next.token }
                nextNode.exclude(next)
            }
        } else {
            excluded = true
        }
    }
}
