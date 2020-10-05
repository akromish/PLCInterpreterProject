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

    final CharStream chars;

    Lexer(String input) {
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
    List<Token> lex() throws ParseException {
        ArrayList<Token> t = new ArrayList<Token>();
        while(chars.has(0)){
            if(match("[\t\n\b\r ]")){
                chars.reset();
            }else{
                t.add(lexToken());
            }
        }
        return t;

        //throw new UnsupportedOperationException(); //TODO
    }

    /**
     * Lexes the next token. It may be helpful to have this call other methods,
     * such as {@code lexIdentifier()} or {@code lexNumber()}, based on the next
     * character(s).
     *
     * Additionally, here is an example of lexing a character literal (not used
     * in this assignment) using the peek/match methods below.
     *
     * <pre>
     * {@code
     *     Token lexCharacter() {
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
    Token lexToken() throws ParseException {
        int res=0;
        if (match("\"")){
            res =1;
        }
        else if (match("[A-Za-z_+\\-*/:!?<>=]")||match("[.]","[A-Za-z0-9_+\\-*/.:!?<>=]")){
            res =2;
        }
        if(match("[0-9]") || match("[+-]","[0-9]")){
            res =3;
        }

        switch (res) {
            case 0:
                chars.advance();
                return chars.emit(Token.Type.OPERATOR);
            case 1:
                return lexString();
            case 2:
                return lexIdentifier();
            case 3:
                return lexNumber();
            default:
                break;

        }
//        }catch (UnsupportedOperationException e){
        throw new UnsupportedOperationException(); //TODO
//        }
    }

    Token lexIdentifier() {
        try {
            while (peek("[A-Za-z0-9_+\\-*/.:!?<>=]")){
                chars.advance();
            }
            return chars.emit(Token.Type.IDENTIFIER);
        }catch (UnsupportedOperationException e){
            throw new UnsupportedOperationException(e); //TODO
        }
    }

    Token lexNumber() {
        try {
            boolean bool = true;
            while (match("[0-9]")||peek("[.]","[0-9]")){
                if (peek("[.]","[0-9]") && bool){
                    chars.advance();
                    bool = false;
                }
            }
            return chars.emit(Token.Type.NUMBER);
        }catch (UnsupportedOperationException e){
            throw new UnsupportedOperationException(e); //TODO
        }
    }

    Token lexString() throws ParseException {
        while(match("([^\\\\\"']|\\\\[bnrt'\"\\\\])*")){}
//        chars.index--;
//        System.out.println(chars.get(0));
        if(!match("\"")) {
//            System.out.println(chars.index+ "khara" +chars.input.length());
            throw new ParseException("ERR", chars.index);
//            throw new UnsupportedOperationException();
        }
        return chars.emit(Token.Type.STRING);

    }

    /**
     * Returns true if the next sequence of characters match the given patterns,
     * which should be a regex. For example, {@code peek("a", "b", "c")} would
     * return true for the sequence {@code 'a', 'b', 'c'}
     */
    boolean peek(String... patterns) {
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
    boolean match(String... patterns) {
            if (peek(patterns)) {
                for (int i = 0; i < patterns.length ; i++) {
                    chars.advance();
                }
                return true;
            }
            else {
                return false;
            }
    }

    /**
     * This is basically a sequence of characters. The index is used to maintain
     * where in the input string the lexer currently is, and the builder
     * accumulates characters into the literal value for the next token.
     */
    static final class CharStream {

        final String input;
        int index = 0;
        int length = 0;

        CharStream(String input) {
            this.input = input;
        }

        /**
         * Returns true if there is a character at index + offset.
         */
        boolean has(int offset) {
            try {
                int val = index + offset;
                char c = input.charAt(val);
//                if(c == ' '){
//                    return false;
//                }else{
                return true;
//                }
            }
            catch (IndexOutOfBoundsException e){
                return false;
            }catch (Exception e){
                throw new UnsupportedOperationException(e);//TODO
            }
        }

        /**
         * Gets the character at index + offset.
         */
        char get(int offset) {
            char temp='l';
            try{
                int val = index + offset;
                char c = input.charAt(val);
                return c;
            }
            catch (Exception e) {
//                System.out.println(c);
                throw new UnsupportedOperationException(); //TODO
            }
        }

        /**
         * Advances to the next character, incrementing the current index and
         * length of the literal being built.
         */
        void advance() {
            try {
                index ++;
                length ++;
            }catch (Exception e) {
                throw new UnsupportedOperationException(e); //TODO
            }
        }

        /**
         * Resets the length to zero, skipping any consumed characters.
         */
        void reset() {
            try{
                length = 0;
            }catch (Exception e){
                throw new UnsupportedOperationException(e); //TODO
            }
        }

        /**
         * Returns a token of the given type with the built literal and resets
         * the length to zero. The index of the token should be the
         * <em>starting</em> index.
         */
        Token emit(Token.Type type) {
            try {
                Token token = new Token(type, input.substring(index-length,index),index -length);
                reset();
                return token;
            } catch (UnsupportedOperationException e) {
                throw new UnsupportedOperationException(e); //TODO
            }
        }

    }

}
