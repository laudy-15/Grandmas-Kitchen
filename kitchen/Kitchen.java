package kitchen;

import java.util.List;

public class Kitchen {
    static boolean hadError = false;

    public static void main(String[] args) {
        List<Token> toks = new Scanner("pot is empty.").scanTokens();
        System.out.println(toks);
        Parser parser = new Parser(toks);
        List<Stmt> statements = parser.parse();
        System.out.println(statements);
    
    }


    
    static void error(int line, String message) {
        report(line, "", message);
    }

    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
