package plc.interpreter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Contains JUnit tests for {@link Regex}. Tests declarations for steps 1 & 2
 * are provided, you must add your own for step 3.
 *
 * To run tests, either click the run icon on the left margin or execute the
 * gradle test task, which can be done by clicking the Gradle tab in the right
 * sidebar and navigating to Tasks > verification > test Regex(double click to run).
 */
public class RegexTests {

    /**
     * This is a parameterized test for the {@link Regex#EMAIL} regex. The
     * {@link ParameterizedTest} annotation defines this method as a
     * parameterized test, and {@link MethodSource} tells JUnit to look for the
     * static method {@link #testEmailRegex()}.
     *
     * For personal preference, I include a test name as the first parameter
     * which describes what that test should be testing - this is visible in
     * IntelliJ when running the tests.
     */
    @ParameterizedTest
    @MethodSource
    public void testEmailRegex(String test, String input, boolean success) {
        test(input, Regex.EMAIL, success);
    }

    /**
     * This is the factory method providing test cases for the parameterized
     * test above - note that it is static, takes no arguments, and has the same
     * name as the test. The {@link Arguments} object contains the arguments for
     * each test to be passed to the function above
     */
    public static Stream<Arguments> testEmailRegex() {
        return Stream.of(
                Arguments.of("Alphanumeric", "thelegend27@gmail.com", true),
                Arguments.of("UF Domain", "otherdomain@ufl.edu", true),
                Arguments.of("Missing Domain Dot", "missingdot@gmailcom", false),
                Arguments.of("Symbols", "symbols#$%@gmail.com", false),
                Arguments.of("Dot before @", "dot.before@gmail.com", true),
                Arguments.of("Yahoo Domain", "yahoodomain@yahoo.com", true),
                Arguments.of("Dash before @", "dash-before@gmail.com", true),
                Arguments.of("Two Dots before @", "two.dots.before@yahoo.com", true),
                Arguments.of("Number as domain", "numberdomain@1.com", true),
                Arguments.of("Two Dots after @", "twodots@after.com.com", false),
                Arguments.of("One letter after domain", "oneletter@gmail.c", false),
                Arguments.of("Parentheses", "parentheses()@yahoo.com", false),
                Arguments.of("Missing @ sign", "missingatsigngmail.com", false),
                Arguments.of("Symbols as domain", "symbolsdomain@#$%.com", false)
        );
    }


    @ParameterizedTest
    @MethodSource
    public void testFileNamesRegex(String test, String input, boolean success) {
        //this one is different as we're also testing the file name capture
        Matcher matcher = test(input, Regex.FILE_NAMES, success);
        if (success) {
            Assertions.assertEquals(input.substring(0, input.indexOf(".")), matcher.group("name"));
        }
    }

    public static Stream<Arguments> testFileNamesRegex() {
        return Stream.of(
                Arguments.of("Java File", "Regex.tar.java", true),
                Arguments.of("Java Class", "RegexTests.class", true),
                Arguments.of("Directory", "directory", false),
                Arguments.of("Python File", "scrippy.py", false),
                Arguments.of("2 extensions", "2extensions.pdf.java", true),
                Arguments.of("java and class", "regex.java.class", true),
                Arguments.of("class and java", "regex.class.java", true),
                Arguments.of("No base, java", ".java", true),
                Arguments.of("No base, class", ".class", true),
                Arguments.of("java before end", "regex.java.tar", false),
                Arguments.of("class before end", "regex.class.tar", false),
                Arguments.of("start with java", "java.regex.tar", false),
                Arguments.of("start with class", "class.regex.tar", false),
                Arguments.of("dot after extension", "regex.tar..class.", false)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testEvenStringsRegex(String test, String input, boolean success) {
        test(input, Regex.EVEN_STRINGS, success);
    }

    public static Stream<Arguments> testEvenStringsRegex() {
        return Stream.of(
                Arguments.of("14 Characters", "thishas14chars", true),
                Arguments.of("10 Characters", "i<3pancakes!", true),
                Arguments.of("6 Characters", "6chars", false),
                Arguments.of("15 Characters", "i<3pancakes!!", false),
                Arguments.of("12 Characters", "12characters", true),
                Arguments.of("14 Characters", "14charactersss", true),
                Arguments.of("16 Characters", "16charactersssss", true),
                Arguments.of("18 Characters", "18charactersssssss", true),
                Arguments.of("20 Characters", "20charactersssssssss", true),
                Arguments.of("White Space", "w h i t e s p a ce", true),
                Arguments.of("Symbols pass", "@#$*&^%)*%*(&%", true),
                Arguments.of("22 Characters", "22charactersssssssssss", false),
                Arguments.of("11 Characters", "11character", false),
                Arguments.of("Symbols fail", "^&($&!(^#&%^@", false),
                Arguments.of("Sentence Test", "This sentence has many characters and thus should fail this test.", false),
                Arguments.of("Empty string","", false)
        );
    }

    @ParameterizedTest
    @MethodSource
    public void testIntegerListRegex(String test, String input, boolean success) {
        test(input, Regex.INTEGER_LIST, success);
    }

    public static Stream<Arguments> testIntegerListRegex() {
        return Stream.of(
                Arguments.of("Empty List", "[]", true),
                Arguments.of("Single Element", "[1]", true),
                Arguments.of("Multiple Elements", "[1,2,3]", true),
                Arguments.of("Missing Brackets", "1,2,3", false),
                Arguments.of("Missing Commas", "[1 2 3]", false),
                Arguments.of("Trailing Comma", "[1,2,3,]", false),
                Arguments.of("Single space after comma all numbers", "[1, 2, 3]", true),
                Arguments.of("Single space after one comma", "[1, 2,3]", true),
                Arguments.of("Many elements", "[3,5,1,7,2,7,2,1,6,3]", true),
                Arguments.of("Numbers with more than 2 digits", "[12511,34631,2367]", true),
                Arguments.of("Two Digit Numbers", "[10,20,30]", true),
                Arguments.of("Negative Numbers", "[-1,-2,-3]", false),
                Arguments.of("Symbols", "[!,@,#]", false),
                Arguments.of("Multiple square brackets", "[1][2][3]", false),
                Arguments.of("Space after number, no comma", "[1 ]", false),
                Arguments.of("Zero in list", "[1,5,0,5]", false),
                Arguments.of("Decimals", "[1.1,5.1,5]", false)
        );
    }

    /**
     * Asserts that the input matches the given pattern and returns the matcher
     * for additional assertions.
     */
    private static Matcher test(String input, Pattern pattern, boolean success) {
        Matcher matcher = pattern.matcher(input);
        Assertions.assertEquals(success, matcher.matches());
        return matcher;
    }

}
