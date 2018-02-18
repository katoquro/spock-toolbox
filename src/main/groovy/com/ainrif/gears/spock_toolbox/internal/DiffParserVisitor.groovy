/*
 * Copyright 2014-2017 Ainrif <support@ainrif.com>
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

import org.unitils.core.util.ObjectFormatter
import org.unitils.reflectionassert.difference.*

import static org.apache.commons.lang.ClassUtils.getShortClassName

class DiffParserVisitor implements DifferenceVisitor<DiffNode, DiffNode> {

    public static final String NO_MATCH = '(no-match)'

    protected ObjectFormatter objFormatter = new ObjFormatter()

    DiffNode parse(Difference difference) {
        def diffNode = difference.accept(this, null)

        return diffNode
    }

    @Override
    DiffNode visit(Difference difference, DiffNode argument) {
        return (argument ?: new DiffNode()).with {
            actual = objFormatter.format(difference.leftValue)
            expected = objFormatter.format(difference.rightValue)

            return it
        }
    }

    @Override
    DiffNode visit(ObjectDifference objectDifference, DiffNode argument) {
        return (argument ?: new DiffNode()).with {
            it << objectDifference.fieldDifferences.collect { name, diff ->
                diff.accept(this, new DiffNode(designation: name))
            }

            return it
        }
    }

    @Override
    DiffNode visit(ClassDifference classDifference, DiffNode argument) {
        return (argument ?: new DiffNode()).with {
            actual = getShortClassName(classDifference.leftClass)
            expected = getShortClassName(classDifference.rightClass)

            return it
        }
    }

    @Override
    DiffNode visit(MapDifference mapDifference, DiffNode argument) {
        return (argument ?: new DiffNode()).with {
            it << mapDifference.valueDifferences.collect { key, diff ->
                diff.accept(this, new DiffNode(designation: objFormatter.format(key)))
            }

            def leftMap = mapDifference.leftMap
            def rightMap = mapDifference.rightMap

            it << mapDifference.leftMissingKeys.collect {
                new DiffNode(designation: objFormatter.format(it),
                        actual: objFormatter.format(leftMap[it]),
                        expected: '')
            }
            it << mapDifference.rightMissingKeys.collect {
                new DiffNode(designation: objFormatter.format(it),
                        actual: '',
                        expected: objFormatter.format(rightMap[it]))
            }

            return it
        }
    }

    @Override
    DiffNode visit(CollectionDifference collectionDifference, DiffNode argument) {
        return (argument ?: new DiffNode()).with {
            array = true
            it << collectionDifference.elementDifferences.collect { index, diff ->
                diff.accept(this, new DiffNode(designation: index))
            }

            def leftList = collectionDifference.leftList
            def rightList = collectionDifference.rightList

            it << collectionDifference.leftMissingIndexes.collect {
                new DiffNode(designation: it, actual: objFormatter.format(leftList[it]), expected: NO_MATCH)
            }

            it << collectionDifference.rightMissingIndexes.collect {
                new DiffNode(designation: it, actual: NO_MATCH, expected: objFormatter.format(rightList[it]))
            }

            return it
        }
    }

    @Override
    DiffNode visit(UnorderedCollectionDifference unorderedCollectionDifference, DiffNode argument) {
        return (argument ?: new DiffNode()).with {
            array = true

            def leftList = unorderedCollectionDifference.leftList
            def rightList = unorderedCollectionDifference.rightList

            if (leftList.size() != rightList.size()) {
                actual = leftList.size() as String
                expected = rightList.size() as String
            }

            it << unorderedCollectionDifference.bestMatchingIndexes.collect { left, right ->
                if (-1 == left) {
                    return new DiffNode(designation: right,
                            actual: NO_MATCH,
                            expected: objFormatter.format(rightList[right]))
                }

                if (-1 == right) {
                    return new DiffNode(designation: left,
                            actual: objFormatter.format(leftList[right]),
                            expected: NO_MATCH)
                }

                def diff = unorderedCollectionDifference.getElementDifference(left, right)

                return diff.accept(this, new DiffNode(designation: left))
            }

            return it
        }
    }
}
