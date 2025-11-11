package kitchen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Kitchen {
    private static final Interpreter interpreter = new Interpreter();

    static boolean hadError = false;
    static boolean hadRuntimeError = false;

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: Grandma's Kitchen [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) { System.exit(65); }
        if (hadRuntimeError) { System.exit(70); }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        System.out.println("Welcome to Grandma's Kitchen!");

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            run(line);
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        List<Token> tokens = scanner.scanTokens();
        System.out.println(tokens);

        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        System.out.println(statements);
        // Stop if there was a syntax error.
        if (hadError) return;

        // Print the AST.
        //System.out.println("Parsed expression: " + expression.toString());

        // Don't have interpreter yet, leave commented out
        // Resolver resolver = new Resolver(interpreter);
        // resolver.resolve(statements);
        // if (hadError) return;

        interpreter.interpret(statements);
        interpreter.dump();
    }

    // public static void main(String[] args) {
    //     List<Token> toks = new Scanner(".").scanTokens();
    //     System.out.println(toks);
    //     Parser parser = new Parser(toks);
    //     List<Stmt> statements = parser.parse();
    //     System.out.println(statements);
    
    // }


    
    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[Line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}
