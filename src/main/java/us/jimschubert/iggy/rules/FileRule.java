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

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;

/**
 * Defines how files are processed for exclusion and inclusion rules.
 */
public class FileRule extends Rule {

    private PathMatcher matcher = null;

    /**
     * Constructs a new instance of a {@link FileRule}.
     *
     * @param syntax     The syntax as parsed from the original definition.
     * @param definition The original definition.
     */
    FileRule(List<Part> syntax, String definition) {
        super(syntax, definition);
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + this.getPattern());
    }

    /**
     * The constraints for inclusion or exclusion defined by the {@link FileRule}.
     *
     * @param relativePath The path relative to the ignore file to evaluate against the rules included in that ignore file.
     * @return {@code true} if the rule matches for exclusion, otherwise {@code false}.
     */
    @Override
    public Boolean matches(String relativePath) {
        return matcher.matches(FileSystems.getDefault().getPath(relativePath));
    }
}