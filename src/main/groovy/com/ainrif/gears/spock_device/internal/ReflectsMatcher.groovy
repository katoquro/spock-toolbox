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

import org.unitils.reflectionassert.comparator.Comparator
import org.unitils.reflectionassert.difference.Difference
import org.unitils.reflectionassert.report.impl.DefaultDifferenceView

class ReflectsMatcher {
    private final def actual
    private final def expected
    private List<Class<? extends Comparator>> modes = []
    private List<Comparator> comparators = []
    private List<String> excludedFields = []

    private List<String> excludedReport
    private List<String> diffReport

    ReflectsMatcher(actual, expected) {
        this.actual = actual
        this.expected = expected
    }

    public ReflectsMatcher modes(Class<? extends Comparator>... modes) {
        this.modes = modes as List
        this
    }

    public ReflectsMatcher mode(Class<? extends Comparator> mode) {
        this.modes += mode
        this
    }

    public ReflectsMatcher modes(Comparator... comparators) {
        this.comparators = comparators as List
        this
    }

    public ReflectsMatcher comparator(Comparator comparator) {
        this.comparators += comparator
        this
    }

    public ReflectsMatcher excludeFields(String... fields) {
        this.excludedFields = fields as List
        this
    }

    public ReflectsMatcher excludeField(String field) {
        this.excludedFields += field
        this
    }

    boolean asBoolean() {
        Difference difference = ExtendedReflectionComparatorFactory
                .create(comparators, modes)
                .getDifference(expected, actual)

        if (difference) {
            def report = new DefaultDifferenceView().createView(difference)
            (excludedReport, diffReport) = report.split(/\r?\n(?!\s)/)
                    .split { diff -> excludedFields.any { diff =~ /^$it(\[|\.|:).*/ } }
        }

        return !diffReport
    }

    @Override
    String toString() {
        return diffReport.join('\r\n')
    }
}
