This repository is a small self-contained example of bazel not respecting the default Error Prone checks.

In Error Prone, the OperatorPrecedence check is [enabled as a warning by default](http://errorprone.info/bugpattern/OperatorPrecedence).  While the Error Prone compiler implements this as expected, bazel 0.5.1 does not: the OperatorPrecedence warning is only logged if explicitly enabled.  Bazel should implement the default checks from Error Prone.

The following examples were run with:

```
$ bazel version
Build label: 0.5.1
Build target: bazel-out/local-fastbuild/bin/src/main/java/com/google/devtools/build/lib/bazel/BazelServer_deploy.jar
Build time: Tue Jun 6 10:34:11 2017 (1496745251)
Build timestamp: 1496745251
Build timestamp as int: 1496745251

$ java -version
openjdk version "1.8.0_131"
OpenJDK Runtime Environment (build 1.8.0_131-b11)
OpenJDK 64-Bit Server VM (build 25.131-b11, mixed mode)
```

## Build with Error Prone

```
$ wget https://repo1.maven.org/maven2/com/google/errorprone/error_prone_ant/2.0.19/error_prone_ant-2.0.19.jar
# ...
$ java -Xbootclasspath/p:error_prone_ant-2.0.19.jar com.google.errorprone.ErrorProneCompiler ErrorProneExample.java
ErrorProneExample.java:5: warning: [OperatorPrecedence] Use grouping parenthesis to make the operator precedence explicit
    return a << 16 | a << 8 & 0xFF00L;
                            ^
    (see http://errorprone.info/bugpattern/OperatorPrecedence)
  Did you mean 'return a << 16 | (a << 8 & 0xFF00L);'?
1 warning
```

As documented, the OperatorPrecedence check prints a warning by default.

## Build with Bazel

```
$ bazel clean && bazel build //:ep-test
INFO: Starting clean (this may take a while). Consider using --async if the clean takes more than several minutes.
INFO: Found 1 target...
Target //:ep-test up-to-date:
  bazel-bin/libep-test.jar
INFO: Elapsed time: 1.687s, Critical Path: 0.82s
```

The OperatorPrecedence warning is not printed.  Build again with it explicitly enabled:

```
$ bazel clean && bazel build //:ep-test --javacopt="-Xep:OperatorPrecedence:WARN"
INFO: Starting clean (this may take a while). Consider using --async if the clean takes more than several minutes.
INFO: Found 1 target...
INFO: From Building libep-test.jar (1 source file):
ErrorProneExample.java:5: warning: [OperatorPrecedence] Use grouping parenthesis to make the operator precedence explicit
    return a << 16 | a << 8 & 0xFF00L;
                            ^
    (see http://errorprone.info/bugpattern/OperatorPrecedence)
  Did you mean 'return a << 16 | (a << 8 & 0xFF00L);'?
Target //:ep-test up-to-date:
  bazel-bin/libep-test.jar
INFO: Elapsed time: 1.599s, Critical Path: 0.84s
```

Now, the warning is printed.  The explicit `-Xep` flag should not be required for this default check.
