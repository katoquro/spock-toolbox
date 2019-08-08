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

import static java.lang.System.lineSeparator

class DiffReport {
    private DiffNode root
    private String report

    DiffReport(DiffNode root) {
        this.root = root
    }

    String createReport() {
        if (report) {
            return report
        }

        if (!root.containsDiff()) {
            report = 'objects are equal'
            return report
        }

        def builder = new StringBuilder()
        processNode(root, '', builder)

        report = builder.toString()
        return report
    }

    void processNode(DiffNode node, String designationPrefix, StringBuilder builder) {
        def currentDesignation = "${designationPrefix}${designationPrefix ? '.' : ''}${node.designation}"
        if (node.hasDiff()) {
            builder.append(lineSeparator())
                    .append('at ').append(currentDesignation).append(lineSeparator())
                    .append('\texpected: ').append(node.expected).append(lineSeparator())
                    .append('\t but was: ').append(node.actual).append(lineSeparator())

        }

        if (!node.excluded) {
            node.nodes.each {
                processNode(it, currentDesignation, builder)
            }
        }
    }
}
