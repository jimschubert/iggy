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

/**
 * Provides the tokenized representation of a part of a rule's definition.
 */
class Part {
    private final IgnoreLineParser.Token token;
    private final String value;

    /**
     * Constructs a new instance of {@see Part}, provided the token and whatever value it represents.
     *
     * @param token The token of the parsed part of the rule.
     * @param value The value of the rule part.
     */
    public Part(IgnoreLineParser.Token token, String value) {
        this.token = token;
        this.value = value;
    }

    /**
     * Constructs a new instance of {@see Part}, provided the token.
     *
     * @param token The token of the parsed part of the rule.
     */
    public Part(IgnoreLineParser.Token token) {
        this.token = token;
        this.value = token.getPattern();
    }


    /**
     * Gets the underlying token of this part
     *
     * @return the token
     */
    public IgnoreLineParser.Token getToken() {
        return token;
    }

    /**
     * Gets the underlying value of this part
     *
     * @return the string representing the {@link Part#getToken()}
     */
    public String getValue() {
        return value;
    }
}
