package plc.interpreter;

import java.util.ArrayList;
import java.util.List;

/**
 * The lexer works through three main functions:
 *
 *  - {@link #lex()}, which repeatedly calls lexToken() and skips whitespace
 *  - {@link #lexToken()}, which lexes the next token
 *  - {@link CharStream}, which manages the state of the lexer and literals
 *
 * If the lexer fails to parse something (such as an unterminated string) you
 * should throw a {@link ParseException}.
 *
 * The {@link #peek(String...)} and {@link #match(String...)} functions are
 * helpers, they're not necessary but their use will make the implementation a
 * lot easier. Regex isn't the most performant way to go but it gets the job
 * done, and the focus here is on the concept.
 */
public final class Lexer {

    private final CharStream chars;

    private Lexer(String input) {
        chars = new CharStream(input);
    }

    /**
     * Lexes the input and returns the list of tokens.
     */
    public static List<Token> lex(String input) throws ParseException {
        return new Lexer(input).lex();
    }

    /**
     * Repeatedly lexes the next token using {@link #lexToken()} until the end
     * of the input is reached, returning the list of tokens lexed. This should
     * also handle skipping whitespace.
     */
    private List<Token> lex() throws ParseException {

        List<Token> tList = new ArrayList<>();
        while (chars.has(0)) {
            if (!match("[ \n\r\t]")) {
//            if(!match("\n","\r","\t"," ")){
                tList.add(lexToken());
            } else {
                chars.reset();
            }
        }
        return tList;

    }


    /**
     * Lexes the next token. It may be helpful to have this call other methods,
     * such as {@code lexIdentifier()} or {@code lexNumber()}, based on the next
     * character(s).
     * <p>
     * Additionally, here is an example of lexing a character literal (not used
     * in this assignment) using the peek/match methods below.
     *
     * <pre>
     * {@code
     *     private plc.interpreter.Token lexCharacter() {
     *         if (!match("\'")) {
     *             //Your lexer should prevent this from happening, as it should
     *             // only try to lex a character literal if the next character
     *             // begins a character literal.
     *             //Additionally, the index being passed back is a 'ballpark'
     *             // value. If we were doing proper diagnostics, we would want
     *             // to provide a range covering the entire error. It's really
     *             // only for debugging / proof of concept.
     *             throw new ParseException("Next character does not begin a character literal.", chars.index);
     *         }
     *         if (!chars.has(0) || match("\'")) {
     *             throw new ParseException("Empty character literal.",  chars.index);
     *         } else if (match("\\")) {
     *             //lex escape characters...
     *         } else {
     *             chars.advance();
     *         }
     *         if (!match("\'")) {
     *             throw new ParseException("Unterminated character literal.", chars.index);
     *         }
     *         return chars.emit(Token.Type.CHARACTER);
     *     }
     * }
     * </pre>
     */

    private Token lexToken() throws ParseException {

        if (match("[+-]", "[0-9]") || match("[0-9]")) {
            return lexNumber();
        } else if (match("[A-Za-z_+\\-*/:!?<>=]") || match("[.]", "[A-Za-z0-9_+\\-*/.:!?<>=]")) {
            return lexIdentifier();
        } else if (match("\"")) {
            return lexString();
        } else {
            return lexOperator();
        }
        // exception needed?

    }

    private Token lexIdentifier() {
        while (match("[A-Za-z0-9_+\\-*/.:!?<>=]")) {
            // do nothing
        }
        return chars.emit(Token.Type.IDENTIFIER);
        // need exception error?
    }


    private Token lexNumber() {

        boolean notMatched = true;
        while (peek("[.]", "[0-9]") || match("[0-9]")) {
            if (notMatched && match("[.]", "[0-9]")) {
                notMatched = false;
            }
        }
        return chars.emit(Token.Type.NUMBER);
        // exception catch?
    }

    private Token lexString() throws ParseException {
        while (match("[^\"\\\\]|\\\\[bnrt'\"\\\\]")) {
            //do nothing
        }
        if (!match("\"")) {
            throw new ParseException("err", chars.index);
        }
        return chars.emit(Token.Type.STRING);
    }

    private Token lexOperator() throws ParseException {
        chars.advance();
        return chars.emit(Token.Type.OPERATOR);
        //throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Returns true if the next sequence of characters match the given patterns,
     * which should be a regex. For example, {@code peek("a", "b", "c")} would
     * return true for the sequence {@code 'a', 'b', 'c'}
     */
    private boolean peek(String... patterns) {
        for (int i = 0; i < patterns.length; i++) {
            if (!chars.has(i) || !String.valueOf(chars.get(i)).matches(patterns[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true in the same way as peek, but also advances the CharStream to
     * if the characters matched.
     */
    private boolean match(String... patterns) {
        boolean check = false;
        while (peek(patterns)) {
            chars.advance();
            check = true;
        }
        return check;

    }

    /**
     * This is basically a sequence of characters. The index is used to maintain
     * where in the input string the lexer currently is, and the builder
     * accumulates characters into the literal value for the next token.
     */
    private static final class CharStream {

        private final String input;
        private int index = 0;
        private int length = 0;

        private CharStream(String input) {
            this.input = input;
        }

        /**
         * Returns true if there is a character at index + offset.
         */
        public boolean has(int offset) {
            return index + offset < input.length();
        }

        /**
         * Gets the character at index + offset.
         */
        public char get(int offset) {
            return input.charAt(index + offset);
        }

        /**
         * Advances to the next character, incrementing the current index and
         * length of the literal being built.
         */
        public void advance() {
            index++;
            length++;
        }

        /**
         * Resets the length to zero, skipping any consumed characters.
         */
        public void reset() {
            length = 0;
        }

        /**
         * Returns a token of the given type with the built literal and resets
         * the length to zero. The index of the token should be the
         * <em>starting</em> index.
         */
        public Token emit(Token.Type type) {
            int start = index - length;
            reset(); //
            return new Token(type, input.substring(start, index), start);
        }
    }

}

