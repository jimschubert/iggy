/*
 *  Copyright 2016 SmartBear Software
 *  Modifications Copyright 2017 Jim Schubert
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 *  or implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 */

package us.jimschubert.iggy.rules;

import java.util.List;

public class InvalidRule extends Rule {
    private final String reason;

    InvalidRule(List<Part> syntax, String definition, String reason) {
        super(syntax, definition);
        this.reason = reason;
    }

    @Override
    public Boolean matches(String relativePath) {
        return null;
    }

    @Override
    public Operation evaluate(String relativePath) {
        return Operation.NOOP;
    }

    public String getReason() {
        return reason;
    }
}