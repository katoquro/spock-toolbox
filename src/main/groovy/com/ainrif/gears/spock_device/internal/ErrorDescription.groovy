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

class ErrorDescription {
    def message
    def expected
    def butWas
    def diff

    ErrorDescription(def message, def expected, def butWas, def diff = null) {
        this.message = message
        this.expected = expected
        this.butWas = butWas
        this.diff = diff
    }

    @Override
    public String toString() {
        return message +
                '\nExpected: ' + expected +
                '\n     but: ' + butWas +
                (diff ? '\n    diff: ' + diff : '')
    }
}
