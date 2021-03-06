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

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class FileRuleTest {
    @Test
    public void testMatchComplex() throws Exception {
        // Arrange
        final String definition = "path/to/**/complex/*.txt";
        final String relativePath = "path/to/some/nested/complex/xyzzy.txt";

        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "path"),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.TEXT, "to"),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.MATCH_ALL),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.TEXT, "complex"),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, ".txt")
        );

        Rule rule = new FileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testNonMatchComplex() throws Exception {
        // Arrange
        final String definition = "path/to/**/complex/*.txt";
        final String relativePath = "path/to/some/nested/invalid/xyzzy.txt";

        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "path"),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.TEXT, "to"),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.MATCH_ALL),
                new Part(IgnoreLineParser.Token.TEXT, "complex"),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, ".txt")
        );

        Rule rule = new FileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testGlobbingRecursive() throws Exception {
        // Arrange
        final String definition = "*.txt";
        final String relativePath = "path/to/some/nested/location/xyzzy.txt";

        // Act
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.MATCH_ALL),
                new Part(IgnoreLineParser.Token.DIRECTORY_MARKER),
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, ".txt")
        );

        Rule rule = new FileRule(syntax, definition);
        Boolean actual = rule.matches(relativePath);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testGlobbingNotRecursive() throws Exception {
        // Arrange
        final String definition = "*.txt";
        final String relativePath = "path/to/some/nested/location/xyzzy.txt";

        // Act
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, ".txt")
        );

        Rule rule = new FileRule(syntax, definition);
        Boolean actual = rule.matches(relativePath);

        // Assert
        assertFalse(actual);
    }
}