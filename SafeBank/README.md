# TextLab

A Java console application for inspecting and cleaning written submissions.

## Technologies
- Java 17+
- Maven
- JUnit 5
- Java Standard Library

## How to run

1. Open TextLab in IntelliJ IDEA.
2. Allow Maven to download dependencies.
3. Open TextLabApplication.java.
4. Run the main method.

## How to run tests

Execute:

mvn test

Or use IntelliJ IDEA's test runner.

## Features

1. Character count with and without whitespace.
2. Word count.
3. Sentence count.
4. Whitespace cleanup.
5. Whole-word search.
6. Whole-word replacement.
7. Palindrome checking.
8. Longest-word identification.
9. Original and edited passage comparison.

## Processing Rules

- A word is a sequence of letters or digits.
- Punctuation separates words.
- One or more consecutive full stops, exclamation marks or question marks
  form a sentence boundary.
- A final non-empty fragment counts as a sentence without closing punctuation.
- Whole-word search ignores case.
- Java must not match JavaScript.
- Palindrome checking ignores case, spaces and punctuation.
- Empty or whitespace-only input must not crash the application.

## Testing

JUnit tests cover:

- Repeated spaces, tabs and line breaks.
- Java, JAVA and java.
- Java versus JavaScript.
- Panama palindrome.
- Punctuation-only passages.
- Empty input.
- Whole-word replacement.
- Longest-word uniqueness.

Author: Aramide Ashiwaju
