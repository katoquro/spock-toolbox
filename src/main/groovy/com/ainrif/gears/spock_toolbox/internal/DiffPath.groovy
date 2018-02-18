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

import static java.util.Collections.singletonList

class DiffPath {
    static final String WILDCARD = '*'
    static final List<String> ROOT = singletonList(DiffNode.ROOT_DESIGNATION)

    private int offset = 0
    private List<String> tokens

    boolean hasNext() {
        return offset + 1 < tokens.size()
    }

    String getToken() {
        tokens.get(offset)
    }

    DiffPath nextToken() {
        new DiffPath(tokens: tokens, offset: offset + 1)
    }

    static DiffPath fromString(String path) {
        def tokens = path.split(/\./).collectMany {
            if (it.contains('[')) {
                def array = it.split(/\[/, 2)
                if (2 == array[1].size() && WILDCARD == array[1][0]) {
                    return [array[0], WILDCARD]
                }
                def index = array[1].find(~/\d{1,3}/)
                return [array[0], index]
            } else {
                return singletonList(it)
            }
        }

        return new DiffPath(tokens: ROOT + tokens)
    }

}
