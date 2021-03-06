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
import java.util.regex.Pattern;

/**
 * A special case rule which matches files only if they're located
 * in the same directory as the .swagger-codegen-ignore file.
 */
public class RootedFileRule extends Rule {
    private String definedFilename = null;
    private String definedExtension = null;

    /**
     * Constructs a new instance of a {@link RootedFileRule}.
     *
     * @param syntax     The syntax as parsed from the original definition.
     * @param definition The original definition.
     */
    RootedFileRule(List<Part> syntax, String definition) {
        super(syntax, definition);

        int separatorIndex = definition.lastIndexOf(".");
        definedFilename = getFilenamePart(definition, separatorIndex);
        definedExtension = getExtensionPart(definition, separatorIndex);
    }

    private String getFilenamePart(final String input, int stopIndex) {
        return input.substring('/' == input.charAt(0) ? 1 : 0, stopIndex > 0 ? stopIndex : input.length());
    }

    private String getExtensionPart(final String input, int stopIndex) {
        return input.substring(stopIndex > 0 ? stopIndex + 1 : input.length(), input.length());
    }

    /**
     * The constraints for inclusion or exclusion defined by the {@link RootedFileRule}.
     *
     * @param relativePath The path relative to the ignore file to evaluate against the rules included in that ignore file.
     * @return {@code true} if the rule matches for exclusion, otherwise {@code false}.
     */
    @Override
    public Boolean matches(String relativePath) {
        // NOTE: Windows-style separator isn't supported, so File.pathSeparator would be incorrect here.
        // NOTE: lastIndexOf rather than contains because /file.txt is acceptable while path/file.txt is not.
        //       relativePath will be passed by CodegenIgnoreProcessor and is relative to .codegen-ignore.
        boolean isSingleFile = relativePath.lastIndexOf("/") <= 0;

        if (isSingleFile) {
            int separatorIndex = relativePath.lastIndexOf(".");
            final String filename = getFilenamePart(relativePath, separatorIndex);
            final String extension = getExtensionPart(relativePath, separatorIndex);
            boolean extensionMatches = definedExtension.equals(extension) || definedExtension.equals(IgnoreLineParser.Token.MATCH_ANY.getPattern());

            if (extensionMatches && definedFilename.contains(IgnoreLineParser.Token.MATCH_ANY.getPattern())) {
                // TODO: Evaluate any other escape requirements here.
                Pattern regex = Pattern.compile(
                        definedFilename
                                .replaceAll(Pattern.quote("."), "\\\\Q.\\\\E")
                                .replaceAll(Pattern.quote("*"), ".*?") // non-greedy match on 0+ any character
                );
                return regex.matcher(filename).matches();
            }

            return extensionMatches && definedFilename.equals(filename);
        }

        return false;
    }
}
