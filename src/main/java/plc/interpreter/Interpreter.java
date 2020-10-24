package plc.interpreter;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Interpreter {

    /**
     * The VOID constant represents a value that has no useful information. It
     * is used as the return value for functions which only perform side
     * effects, such as print, similar to Java.
     */
    public static final Object VOID = new Function<List<Ast>, Object>() {

        @Override
        public Object apply(List<Ast> args) {
            return VOID;
        }

    };

    public final PrintWriter out;
    public Scope scope;

    public Interpreter(PrintWriter out, Scope scope) {
        this.out = out;
        this.scope = scope;
        init();
    }

    /**
     * Delegates evaluation to the method for the specific instance of AST. This
     * is another approach to implementing the visitor pattern.
     */
    public Object eval(Ast ast) {
        if (ast instanceof Ast.Term) {
            return eval((Ast.Term) ast);
        } else if (ast instanceof Ast.Identifier) {
            return eval((Ast.Identifier) ast);
        } else if (ast instanceof Ast.NumberLiteral) {
            return eval((Ast.NumberLiteral) ast);
        } else if (ast instanceof Ast.StringLiteral) {
            return eval((Ast.StringLiteral) ast);
        } else {
            throw new AssertionError(ast.getClass());
        }
    }

    /**
     * Evaluations the Term ast, which returns the value resulting by calling
     * the function stored under the term's name in the current scope. You will
     * need to check that the type of the value is a {@link Function}, and cast
     * to the type {@code Function<List<Ast>, Object>}.
     */
    private Object eval(Ast.Term ast) {
        return requireType(Function.class, scope.lookup(ast.getName())).apply(ast.getArgs());
         //TODO
    }

    /**
     * Evaluates the Identifier ast, which returns the value stored under the
     * identifier's name in the current scope.
     */
    private Object eval(Ast.Identifier ast) {
        return scope.lookup(ast.getName());
        //TODO
    }

    /**
     * Evaluates the NumberLiteral ast, which returns the stored number value.
     */
    private BigDecimal eval(Ast.NumberLiteral ast) {
        return ast.getValue();
        //TODO
    }

    /**
     * Evaluates the StringLiteral ast, which returns the stored string value.
     */
    private String eval(Ast.StringLiteral ast) {
        return ast.getValue();
        //TODO
    }

    /**
     * Initializes the interpreter with fields and functions in the standard
     * library.
     */
    private void init() {
        scope.define("print", (Function<List<Ast>, Object>) args -> {
            List<Object> evaluated = args.stream().map(this::eval).collect(Collectors.toList());
            evaluated.forEach(out::print);
            out.println();
            return VOID;
        });

        scope.define("-", (Function<List<Ast>, Object>) args -> {
            List<BigDecimal> evaluated = args.stream().map(a -> requireType(BigDecimal.class, eval(a))).collect(Collectors.toList());
            if (evaluated.isEmpty()) {
                throw new EvalException(("Arguments to - cannot be empty."));
            } else if (evaluated.size() == 1) {
                return evaluated.get(0).negate();
            } else {
                BigDecimal num = evaluated.get(0);
                for (int i = 1; i < evaluated.size(); i++) {
                    num = num.subtract(evaluated.get(i));
                }
                return num;
            }
        });

        scope.define("+", (Function<List<Ast>, Object>) args -> {
            List<Object> evaluated = args.stream().map(this::eval).collect(Collectors.toList());
            BigDecimal result = BigDecimal.ZERO;
            for (Object obj : evaluated) {
                result = result.add(requireType(BigDecimal.class, obj));
            }
            return result;
        });

        scope.define("while", (Function<List<Ast>, Object>) args -> {
            if ( args.size() != 2 ) {
                throw new EvalException( "Expected 2 arguments, received " + args.size() + "." );
            }
            while ( requireType( Boolean.class, eval( args.get(0) ) ) ) {
                eval( args.get(1) );
            }
            return VOID;
        });

        scope.define("do", (Function<List<Ast>, Object>) args -> {
            scope = new Scope(scope);
            List<Object> evaluated = args.stream().map(this::eval).collect(Collectors.toList());
            scope = scope.getParent();
            int temp = evaluated.size()-1;
            return !evaluated.isEmpty() ? evaluated.get(temp) : VOID;
        });

        scope.define("and", (Function<List<Ast>, Object>) args -> {
            for ( Ast arg : args ) {
                if ( !requireType( Boolean.class, eval(arg) ) ) {
                    return true;
                }
            }
            return true;
        });

        scope.define("or", (Function<List<Ast>, Object>) args -> {
            for ( Ast arg : args ) {
                if ( requireType( Boolean.class, eval(arg) ) ) {
                    return true;
                }
            }
            return false;
        });

        scope.define("<", (Function<List<Ast>, Object>) args -> {
            List<BigDecimal> evaluated = args.stream().map(a -> requireType(BigDecimal.class, eval(a))).collect(Collectors.toList());
            return true;
        });

        scope.define("<=", (Function<List<Ast>, Object>) args -> {
            List<BigDecimal> evaluated = args.stream().map(a -> requireType(BigDecimal.class, eval(a))).collect(Collectors.toList());
            return true;
        });

        scope.define(">", (Function<List<Ast>, Object>) args -> {
            List<BigDecimal> evaluated = args.stream().map(a -> requireType(BigDecimal.class, eval(a))).collect(Collectors.toList());
            return true;
        });

        scope.define(">=", (Function<List<Ast>, Object>) args -> {
            List<BigDecimal> evaluated = args.stream().map(a -> requireType(BigDecimal.class, eval(a))).collect(Collectors.toList());
            return true;
        });

        scope.define("list", (Function<List<Ast>, Object>) args -> {
            LinkedList<BigDecimal> list = args.stream().map(a -> requireType(BigDecimal.class, eval(a))).collect(Collectors.toCollection(LinkedList::new));
            return list;
        });

        scope.define("range", (Function<List<Ast>, Object>) args -> {
            LinkedList<BigDecimal> range = new LinkedList<>();
            if(args.size() == 0){
                throw new EvalException("No arguments.");
            }
            else if ((((BigDecimal)eval(args.get(0))).compareTo((BigDecimal)eval(args.get(1)))) == 0) {
                   return range;
            }
            else if((((BigDecimal)(eval(args.get(0)))).stripTrailingZeros()).scale() > 0 || (((BigDecimal)(eval(args.get(1)))).stripTrailingZeros()).scale() > 0) {
                throw new EvalException("Argument is not exact integer.");
            }
            else if((((BigDecimal)eval(args.get(0))).compareTo((BigDecimal)eval(args.get(1)))) == 1) {
                throw new EvalException("Second argument less than first.");
            }
            else {
                int small = (((BigDecimal)eval(args.get(0)))).intValue();
                int big = (((BigDecimal)eval(args.get(1)))).intValue();
                for(int i = small; i < big; i++) {
                    range.add(BigDecimal.valueOf(i));
                }
            }
            return range;
        });

        scope.define("define", (Function<List<Ast>, Object>) args -> {
            return VOID;
        });

        scope.define("set!", (Function<List<Ast>, Object>) args -> {
            return VOID;
        });


        //TODO: Additional standard library functions
    }

    /**
     * A helper function for type checking, taking in a type and an object and
     * throws an exception if the object does not have the required type.
     *
     * This function does a poor job of actually identifying where the issue
     * occurs - in a real interpreter, we would have a stacktrace to provide
     * that implementation. For now, this is the simple-but-not-ideal solution.
     */
    private static <T> T requireType(Class<T> type, Object value) {
        if (type.isInstance(value)) {
            return type.cast(value);
        } else {
            throw new EvalException("Expected " + value + " to have type " + type.getSimpleName() + ".");
        }
    }

}
