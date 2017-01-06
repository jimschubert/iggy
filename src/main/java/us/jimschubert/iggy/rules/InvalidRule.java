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

/**
 * Defines a rule which indicates the rule is invalid (e.g. can't be parsed).
 */
public class InvalidRule extends Rule {
    private final String reason;

    /**
     * Constructs a new instance of a {@link InvalidRule}.
     *
     * @param syntax     The syntax as parsed from the original definition.
     * @param definition The original definition.
     * @param reason     The reason why this definition is invalid.
     */
    InvalidRule(List<Part> syntax, String definition, String reason) {
        super(syntax, definition);
        this.reason = reason;
    }

    /**
     * The constraints for inclusion or exclusion defined by the {@link InvalidRule}.
     *
     * @param relativePath The path relative to the ignore file to evaluate against the rules included in that ignore file.
     * @return null. The rule is invalid!
     */
    @Override
    public Boolean matches(String relativePath) {
        return null;
    }

    /**
     * Evaluates a path against a derived {@link Rule}'s match constraints, resulting in the defined {@link Operation} for that rule.
     *
     * @param relativePath The path relative to the ignore file to evaluate against the rules included in that ignore file.
     * @return The {@link Operation} for an inclusion or exclusion rule (that is, {@link Operation#NOOP}).
     */
    @Override
    public Operation evaluate(String relativePath) {
        return Operation.NOOP;
    }

    /**
     * Gets the reason for considering this rule invalid.
     *
     * @return The string message
     */
    @SuppressWarnings("unused")
    public String getReason() {
        return reason;
    }
}