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

public class RootedFileRuleTest {
    @Test
    public void testMatchFilenameOnly() throws Exception {
        // Arrange
        final String definition = "/foo";
        final String relativePath = "foo";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo")
        );
        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testNonMatchFilenameOnly() throws Exception {
        // Arrange
        final String definition = "/foo";
        final String relativePath = "bar";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo")
        );
        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testMatchFilenameAndExtension() throws Exception {
        // Arrange
        final String definition = "/foo.txt";
        final String relativePath = "foo.txt";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo.txt")
        );
        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testNonMatchFilenameAndExtension() throws Exception {
        // Arrange
        final String definition = "/foo.txt";
        final String relativePath = "bar.baz";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo.txt")
        );
        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testMatchFilenameWithGlob() throws Exception {
        // Arrange
        final String definition = "/foo*";
        final String relativePath = "foobarbaz";

        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo"),
                new Part(IgnoreLineParser.Token.MATCH_ANY)
        );

        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testNonMatchFilenameWithGlob() throws Exception {
        // Arrange
        final String definition = "/foo*";
        final String relativePath = "boobarbaz";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo"),
                new Part(IgnoreLineParser.Token.MATCH_ANY)
        );

        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testMatchFilenameAndExtensionWithFilenameGlob() throws Exception {
        // Arrange
        final String definition = "/foo*.txt";
        final String relativePath = "foobarbaz.txt";

        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo"),
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, ".txt")
        );

        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testNonMatchFilenameAndExtensionWithFilenameGlob() throws Exception {
        // Arrange
        final String definition = "/foo*qux.txt";
        final String relativePath = "foobarbaz.txt";

        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo"),
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, "qux.txt")
        );

        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testMatchFilenameAndExtensionWithExtensionGlob() throws Exception {
        // Arrange
        final String definition = "/foo.*";
        final String relativePath = "foo.bak";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo."),
                new Part(IgnoreLineParser.Token.MATCH_ANY)
        );
        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testMatchFilenameAndExtensionWithMultiplePeriods() throws Exception {
        // Arrange
        final String definition = "/foo*.xyzzy.txt";
        final String relativePath = "foo.bar.baz.xyzzy.txt";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo"),
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, ".xyzzy.txt")
        );
        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testNonMatchFilenameAndExtensionWithMultiplePeriods() throws Exception {
        // Arrange
        final String definition = "/foo*.xyzzy.txt";
        final String relativePath = "foo.bar.baz.qux.txt";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo"),
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, ".xyzzy.txt")
        );
        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertFalse(actual);
    }

    @Test
    public void testMatchWithoutLeadingForwardSlash() throws Exception {
        // Arrange
        final String definition = "foo*.xyzzy.txt";
        final String relativePath = "foo.bar.baz.xyzzy.txt";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "foo"),
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, ".xyzzy.txt")
        );
        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertTrue(actual);
    }

    @Test
    public void testMatchesOnlyRooted() throws Exception {
        // Arrange
        final String definition = "/path/to/some/foo*.xyzzy.txt";
        final String relativePath = "foo.bar.baz.xyzzy.txt";
        final List<Part> syntax = Arrays.asList(
                new Part(IgnoreLineParser.Token.ROOTED_MARKER),
                new Part(IgnoreLineParser.Token.TEXT, "path"),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.TEXT, "to"),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.TEXT, "some"),
                new Part(IgnoreLineParser.Token.PATH_DELIM),
                new Part(IgnoreLineParser.Token.TEXT, "oo"),
                new Part(IgnoreLineParser.Token.MATCH_ANY),
                new Part(IgnoreLineParser.Token.TEXT, ".xyzzy.txt")
        );
        Rule rule = new RootedFileRule(syntax, definition);
        Boolean actual = null;

        // Act
        actual = rule.matches(relativePath);

        // Assert
        assertFalse(actual);
    }
}
