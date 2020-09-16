package plc.interpreter;

import java.util.regex.Pattern;

/**
 * Contains {@link Pattern} constants, which are compiled regular expressions.
 * See the assignment page for resources on regex's as needed.
 */
public class Regex {

    public static final Pattern
            EMAIL = Pattern.compile("[A-Za-z0-9._-]+@[A-Za-z0-9-]*\\.[a-z]{2,3}"),
            FILE_NAMES = Pattern.compile("(?<name>(^([^.]*)))(.*\\.(class|java))"),
            EVEN_STRINGS = Pattern.compile("(?=(^(..)*$))(^(.){10,20}$)"),
            INTEGER_LIST = Pattern.compile("\\[\\]|\\[([1-9][0-9]*,\\s?)*([1-9][0-9]*)+\\]"),
            IDENTIFIER = Pattern.compile("(^$|[A-Za-z_+\\-\\*/:!?<>=][A-Za-z_+\\-\\*/:!?<>=0-9]*|[A-Za-z_+\\-\\*/.:!?<>=][A-Za-z_+\\-\\*/.:!?<>=0-9]+)"), //might need to remove empty string
            NUMBER = Pattern.compile("(\\+|-)?\\d+\\.\\d+|(\\+|-)?\\d+"),
            STRING = Pattern.compile("");

}
