# iggy

Iggy is an ignore file processor for java. It was originally a part of swagger-codegen.

[![build status](https://gitlab.com/jimschubert/iggy/badges/master/build.svg)](https://gitlab.com/jimschubert/iggy/commits/master)

[![Maven Central](https://img.shields.io/maven-central/v/us.jimschubert/iggy.svg?label=maven:%20iggy)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22iggy%22)  

## Usage

Given an ignore file, for example `/your/directory/.ignore`:

```
# This is an example ignore file

# Match everything below this directory
path/to/dir/**

# Match all Test files with all extensions
**/*Test*.*

# Match files with one character filename and extension
**/?.?

# Match all files beginning with first or second
**/{first*,second*}
```

This file can be read and evaluated like this:

```java
IgnoreProcessor processor = new IgnoreProcessor("/your/directory");

processor.allowsFile(new File("path/to/dir/ignored"));//= false
processor.allowsFile(new File("other/path/to/dir/ignored"));//= true (DirectoryRule isn't implicitly recursive)
processor.allowsFile(new File("nested/test/SomeTest.java"));//= false
processor.allowsFile(new File("nested/a.b"));//= false
processor.allowsFile(new File("nested/abc.d"));//= true
processor.allowsFile(new File("nested/first.txt"));//= false
processor.allowsFile(new File("nested/second.txt"));//= false
processor.allowsFile(new File("nested/third.txt"));//= true
```

## Patterns

File patterns follow closely to that of `.gitignore`. All ignore patterns allow glob patterns supported by [java.nio.file.PathMatcher](https://docs.oracle.com/javase/tutorial/essential/io/find.html),
unless otherwise noted.

* Rooted file pattern: `/*.ext`
  - Must exist in the root of the directory
  - Must begin with a forward slash `/`
  - Supports `*` or `*.ext` pattern
  - Does not support `PathMatcher` patterns
* Directory Rule
  - Matches against directories (`dir/`) or directory contents (`dir/**`)
  - Must either end in `/` or `**`
* File Rule
  - Matches an individual `filename` or `filename.ext`
  - Supports all `PathMatcher` patterns

Similar to `.gitignore` processing, a double asterisk (`**`) can be used in place of a directory to indicate recursion.

For example:

```
path\to\**\file
```

matches both `path\to\some\file` and `path\to\some\nested\file`.

Single asterisks (`*`) match any characters within a pattern.

For example:

```
path\to\*file
```

matches both `path\to\your_file` and `path\to\my_file`, as well as `path\to\file`.

These are the base cases for most uses. For more details on supported glob patterns, see [What is a Glob?](https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob)

# License

Apache 2.0.

see [License](./LICENSE)
