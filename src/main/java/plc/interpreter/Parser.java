package plc.interpreter;

import javax.swing.plaf.basic.BasicDesktopIconUI;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * The parser takes the sequence of tokens emitted by the lexer and turns that
 * into a structured representation of the program, called the Abstract Syntax
 * Tree (AST).
 *
 * The parser has a similar architecture to the lexer, just with {@link Token}s
 * instead of characters. As before, {@link #peek(Object...)} and {@link
 * #match(Object...)} are helpers to make the implementation easier.
 *
 * This type of parser is called <em>recursive descent</em>. Each rule in our
 * grammar will have it's own function, and reference to other rules correspond
 * to calling that functions.
 */
public final class Parser {

    private final TokenStream tokens;

    private Parser(String input) {
        tokens = new TokenStream(Lexer.lex(input));
    }

    /**
     * Parses the input and returns the AST
     */
    public static Ast parse(String input) {
        return new Parser(input).parse();
    }

    /**
     * Repeatedly parses a list of ASTs, returning the list as arguments of an
     * {@link Ast.Term} with the identifier {@code "source"}.
     */
    private Ast parse() {
        List<Ast> parseList= new ArrayList<Ast>();
        while(tokens.has(0)) {
            parseList.add(parseAst());
        }
        return new Ast.Term("source", parseList);
    }

    /**
     * Parses an AST from the given tokens based on the provided grammar. Like
     * the lexToken method, you may find it helpful to have this call other
     * methods like {@code parseTerm()}. In a recursive descent parser, each
     * rule in the grammar would correspond with a {@code parseX()} function.
     *
     * Additionally, here is an example of parsing a function call in a language
     * like Java, which has the form {@code name(args...)}.
     *
     * <pre>
     * {@code
     *     private Ast.FunctionExpr parseFunctionExpr() {
     *         //In a real parser this would be more complex, as the parser
     *         //wouldn't know this should be a function call until reaching the
     *         //opening parenthesis, like name(... <- here. You won't have this
     *         //problem in this project, but will for the compiler project.
     *         if (!match(Token.Type.IDENTIFIER)) {
     *             throw new ParseException("Expected the name of a function.");
     *         }
     *         String name = tokens.get(-1).getLiteral();
     *         if (!match("(")) {
     *             throw new ParseException("Expected opening bra
     *         }
     *         List<Ast> args = new ArrayList<>();
     *         while (!match(")")) {
     *             //recursive call to parseExpr(), not shown here
     *             args.add(parseExpr());
     *             //next token must be a closing parenthesis or comma
     *             if (!peek(")") && !match(",")) {
     *                 throw new ParseException("Expected closing parenthesis or comma after argument.", tokens.get(-1).getIndex());
     *             }
     *         }
     *         return new Ast.FunctionExpr(name, args);
     *     }
     * }
     * </pre>
     */
    private Ast parseAst() {
//        System.out.println(tokens.get(-1).getLiteral());
        if(peek(Token.Type.NUMBER)) {
            System.out.println("Number");
            return parseNumberLiteral();
        }
        else if(peek(Token.Type.IDENTIFIER)) {
            System.out.println("Identifier");
            return parseIdentifier();
        }
        else if(peek(Token.Type.STRING)) {
            System.out.println("String");
            return parseStringLiteral();
        }
        else {
                System.out.println("Operator");
                return parseTerm();

        }
    }

    private Ast.Term parseTerm() {
        if (!match("(") && !match('[')) {
            throw new ParseException("Expected opening parentheses or square bracket", tokens.index);
        }
        if (!peek(Token.Type.IDENTIFIER)) {
            throw new ParseException("Expected an identifier.", tokens.index);
        }
        String name = tokens.get(-1).getLiteral();
       // System.out.println(name);
        List<Ast> args = new ArrayList<>();
        while (!peek(")") && !peek("]")) {
            args.add(parseAst());
        }
        if (!match(")") && !match("]")) {
            throw new ParseException("Expected closing parenthesis or square bracket after argument.", tokens.index);
        }
        return new Ast.Term(name, args);
    }

    private Ast.Identifier parseIdentifier() {
        if (!match(Token.Type.IDENTIFIER)) {
            throw new ParseException("Expected Identifier.", tokens.index);
        }
        return new Ast.Identifier(tokens.get(-1).getLiteral());

    }

    private Ast.NumberLiteral parseNumberLiteral() {
        if (!match(Token.Type.NUMBER)) {
            throw new ParseException("Expected Number", tokens.index);
            }
        return new Ast.NumberLiteral(new BigDecimal(tokens.get(0).getLiteral()));

    }

    private Ast.StringLiteral parseStringLiteral() {
        if(!match(Token.Type.STRING)) {
            throw new ParseException("Expected String", tokens.index);
        } // add more tests
        return new Ast.StringLiteral(tokens.get(0).getLiteral().replaceAll("\"",""));
    }

    /**
     * As in the lexer, returns {@code true} if the current sequence of tokens
     * matches the given patterns. Unlike the lexer, the pattern is not a regex;
     * instead it is either a {@link Token.Type}, which matches if the token's
     * type is the same, or a {@link String}, which matches if the token's
     * literal is the same.
     *
     * In other words, {@code Token(IDENTIFIER, "literal")} is matched by both
     * {@code peek(Token.Type.IDENTIFIER)} and {@code peek("literal")}.
     */
    private boolean peek(Object... patterns) {
        for (int i = 0; i < patterns.length; i++) {
            if (patterns[i] instanceof Token.Type) {
                if(!tokens.has(i) || tokens.get(i).getType() != patterns[i]) {
                    return false;
                }
            }
            else if (patterns[i] instanceof String) {
                if(!tokens.has(i) || !tokens.get(i).getLiteral().equals((String)patterns[i])) {
                    return false;
                }
            }
        }
        return true; //TODO
    }

    /**
     * As in the lexer, returns {@code true} if {@link #peek(Object...)} is true
     * and advances the token stream.
     */
    private boolean match(Object... patterns) {
        if (peek(patterns)) {
            for (int i = 0; i < patterns.length ; i++) {
                tokens.advance();
            }
            return true;
        }
        else {
            return false;
        }
    } //TODO


    private static final class TokenStream {

        private final List<Token> tokens;
        private int index = 0;

        private TokenStream(List<Token> tokens) {
            this.tokens = tokens;
        }

        /**
         * Returns true if there is a token at index + offset.
         */
        public boolean has(int offset) {
            return index + offset < tokens.size(); //TODO
        }

        /**
         * Gets the token at index + offset.
         */
        public Token get(int offset) {
            return tokens.get(index+offset); //TODO
        }

        /**
         * Advances to the next token, incrementing the index.
         */
        public void advance() {
            index++; //TODO
        }

    }

}
