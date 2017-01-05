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

package us.jimschubert.iggy;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.FileVisitResult.CONTINUE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class IgnoreProcessorTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IgnoreProcessorTest.class);
    private final String filename;
    private final String ignoreDefinition;
    private final String description;
    private Boolean allowed;
    private Boolean skip = false;
    private String outputDir;
    private File target;
    private Path temp;

    private IgnoreProcessorTest(String filename, String ignoreDefinition, String description) throws IOException {
        this.filename = filename;
        this.ignoreDefinition = ignoreDefinition;
        this.description = description;
    }

    @Factory
    public static Object[] factoryMethod() throws IOException {
        return new Object[]{
                // Matching filenames
                new IgnoreProcessorTest("build.sh", "build.sh", "A file when matching should ignore.").ignored(),
                new IgnoreProcessorTest("build.sh", "*.sh", "A file when matching glob should ignore.").ignored(),
                new IgnoreProcessorTest("src/build.sh", "*.sh", "A nested file when matching non-nested simple glob should allow.").allowed(),
                new IgnoreProcessorTest("src/build.sh", "**/build.sh", "A file when matching nested files should ignore.").ignored(),
                new IgnoreProcessorTest("Build.sh", "build.sh", "A file when non-matching should allow.").allowed().skipOnCondition(SystemUtils.IS_OS_WINDOWS),
                new IgnoreProcessorTest("build.sh", "/build.sh", "A rooted file when matching should ignore.").ignored(),
                new IgnoreProcessorTest("nested/build.sh", "/build.sh", "A rooted file definition when non-matching should allow.").allowed(),
                new IgnoreProcessorTest("src/IO.Swagger.Test/Model/AnimalFarmTests.cs", "src/IO.Swagger.Test/Model/AnimalFarmTests.cs", "A file when matching exactly should ignore.").ignored(),

                // Matching spaces in filenames
                new IgnoreProcessorTest("src/properly escaped.txt", "**/properly escaped.txt", "A file when matching nested files with spaces in the name should ignore.").ignored(),
                new IgnoreProcessorTest("src/improperly escaped.txt", "**/improperly\\ escaped.txt", "A file when matching nested files with spaces in the name (improperly escaped rule) should allow.").allowed(),

                // Match All
                new IgnoreProcessorTest("docs/somefile.md", "docs/**", "A recursive file (0 level) when matching should ignore.").ignored(),
                new IgnoreProcessorTest("docs/1/somefile.md", "docs/**", "A recursive file (1 level) when matching should ignore.").ignored(),
                new IgnoreProcessorTest("docs/1/2/3/somefile.md", "docs/**", "A recursive file (n level) when matching should ignore.").ignored(),

                // Match Any
                new IgnoreProcessorTest("docs/1/2/3/somefile.md", "docs/**/somefile.*", "A recursive file with match-any extension when matching should ignore.").ignored(),
                new IgnoreProcessorTest("docs/1/2/3/somefile.java", "docs/**/*.java", "A recursive file with match-any file name when matching should ignore.").ignored(),
                new IgnoreProcessorTest("docs/1/2/3/4/somefile.md", "docs/**/*", "A recursive file with match-any file name when matching should ignore.").ignored(),
                new IgnoreProcessorTest("docs/1/2/3/4/5/somefile.md", "docs/**/anyfile.*", "A recursive file with match-any extension when non-matching should allow.").allowed(),

                // Directory matches
                new IgnoreProcessorTest("docs/1/Users/a", "docs/**/Users/", "A directory rule when matching should be ignored.").ignored(),
                new IgnoreProcessorTest("docs/1/Users1/a", "docs/**/Users/", "A directory rule when non-matching should be allowed.").allowed(),

                // Negation of excluded recursive files
                new IgnoreProcessorTest("docs/UserApi.md", "docs/**\n!docs/UserApi.md", "A pattern negating a previous ignore FILE rule should be allowed.").allowed(),

                // Negation of excluded directories
                new IgnoreProcessorTest("docs/1/Users/UserApi.md", "docs/**/Users/\n!docs/1/Users/UserApi.md", "A pattern negating a previous ignore DIRECTORY rule should be ignored.").ignored(),

                // Other matches which may not be parsed for correctness, but are free because of PathMatcher
                new IgnoreProcessorTest("docs/1/2/3/Some99File.md", "**/*[0-9]*", "A file when matching against simple regex patterns when matching should be ignored.").ignored(),
                new IgnoreProcessorTest("docs/1/2/3/SomeFile.md", "**/*.{java,md}", "A file when matching against grouped subpatterns for extension when matching (md) should be ignored.").ignored(),
                new IgnoreProcessorTest("docs/1/2/3/SomeFile.java", "**/*.{java,md}", "A file when matching against grouped subpatterns for extension when matching (java) should be ignored.").ignored(),
                new IgnoreProcessorTest("docs/1/2/3/SomeFile.txt", "**/*.{java,md}", "A file when matching against grouped subpatterns for extension when non-matching should be allowed.").allowed(),

                new IgnoreProcessorTest("docs/1/2/3/foo.c", "**/*.?", "A file when matching against required single-character extension when matching should be ignored.").ignored(),
                new IgnoreProcessorTest("docs/1/2/3/foo.cc", "**/*.?", "A file when matching against required single-character extension when non-matching should be allowed.").allowed()

        };
    }

    IgnoreProcessorTest allowed() {
        this.allowed = true;
        return this;
    }

    IgnoreProcessorTest skipOnCondition(Boolean condition) {
        this.skip = Boolean.TRUE.equals(condition);
        return this;
    }

    IgnoreProcessorTest ignored() {
        this.allowed = false;
        return this;
    }

    private void prepareTestFiles() throws IOException {
        // NOTE: Each test needs its own directory because .ignore needs to exist at the root.
        temp = Files.createTempDirectory(getClass().getSimpleName());
        this.outputDir = temp.toFile().getAbsolutePath();

        target = new File(this.outputDir, this.filename);

        boolean mkdirs = target.getParentFile().mkdirs();
        if (!mkdirs) {
            LOGGER.warn("Failed to create directories for IgnoreProcessorTest test file. Directory may already exist.");
        }

        Path created = Files.createFile(target.toPath());
        if (!created.toFile().exists()) {
            throw new IOException("Failed to write IgnoreProcessorTest test file.");
        }

        // System.out.print(String.format("Created codegen ignore processor test file: %s\n", created.toAbsolutePath()));
        File ignoreFile = new File(this.outputDir, ".ignore");
        try (FileOutputStream stream = new FileOutputStream(ignoreFile)) {
            stream.write(this.ignoreDefinition.getBytes());
        }
    }

    @AfterTest
    public void afterTest() throws IOException {
        if (temp != null && temp.toFile().exists() && temp.toFile().isDirectory()) {
            Path dir = Paths.get(temp.toFile().toURI());
            try {
                Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {

                    @Override
                    public FileVisitResult visitFile(
                            Path file,
                            BasicFileAttributes attrs
                    ) throws IOException {
                        Files.delete(file);
                        return CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(
                            Path dir,
                            IOException ex
                    ) throws IOException {
                        if (ex == null) {
                            Files.delete(dir);
                            return CONTINUE;
                        } else {
                            throw ex;
                        }
                    }

                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void evaluate() {
        if (this.skip) {
            return;
        }

        // Arrange
        try {
            // Lazily setup files to avoid conflicts and creation when these tests may not even run.
            prepareTestFiles();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Failed to prepare test files. " + e.getMessage());
        }
        IgnoreProcessor processor = new IgnoreProcessor(outputDir);
        Boolean actual = null;

        // Act
        actual = processor.allowsFile(target);

        // Assert
        assertEquals(actual, this.allowed, this.description);
    }
}