package kitchen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kitchen.Expr.*;
import kitchen.Stmt.*;
import static kitchen.TokenType.*;

public class Parser {
    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    List<Stmt> parse() {
        List<Stmt> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private Stmt statement() {
        if (match(RETURN)) return returnStatement();
        if (match(PRINT)) return printStatement();
        if (match(GRAB)) return varDeclaration();
        if (match (ADD) || match(OR) || match(POUR)) return varAssignment();
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        return expressionStatement();
    }

    /* Expression Methods: */

    private Expr expression() {

        // <num> [cups|spoons|..] of <ingredient> 
        if (check(TokenType.NUMBER)) {
            Token tok = advance();   // the number
            Token units = consume(IDENTIFIER, "expected units or ingredient after number");
            Ingredient ingr;
            if (check(OF)) {
                advance();    // the `OF`
                ingr = ingredient();
            } else {
                ingr = new Expr.Ingredient(units);
                units = null;
            }
            return new Quantity((Double)tok.literal, units, ingr);
        }

        // top of <container>
        if (check(TokenType.TOP)) {
            Token tok = advance(); // string 'top'
            System.out.println("Printing the lexeme of the given token: " + tok.lexeme);
            consume(OF, "expected `of` after 'top'");
            Container cont = container();
            return new Expr.Top(cont);
        }

        // rest of <container>
        if (check(TokenType.REST)) {
            Token tok = advance(); // string 'rest'
            System.out.println("Printing the lexeme of the given token: " + tok.lexeme);
            consume(OF, "expected `of` after 'rest'");
            Container cont = container();
            return cont;
        }

        // <expr> with <expr>
        if (check(TokenType.WITH)) {
            // TO-DO get the previous expression??
        }

        // <container>
        if (check(TokenType.IDENTIFIER)) {
            Container cont = container();

            // <container> is [not] empty
            if (check(TokenType.IS)) {
                consume(IS, "expected 'is' after container.");
                boolean not = false; //false as a baseline
                if (check(TokenType.NOT)) {
                    consume(NOT, "'not' expected after container");
                    not = true;
                }
                consume(EMPTY, "expected 'empty' after container");
                return new Expr.Empty(cont, not);
            }

            return cont; 
        }

        // <name> [with <expr>+]? -- function calls 

        return null;
    }

    // <ingredient>
    private Ingredient ingredient() {
        Token tok = consume(IDENTIFIER, "expected ingredient");
        
        // TODO: check that it's a valid ingredient  ??? are they user-decided or fixed by the language ???
        return new Expr.Ingredient(tok);
    }

    // <container>
    private Container container() {
        Token tok = consume(IDENTIFIER, "expected container");

        // TODO: check that it's a valid container
        return new Expr.Container(tok);
    }


    /* Statement Methods: */

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }


    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect container name.");

        consume(DOT, "Expect '.' after variable definition.");
        return new Stmt.Define(name);
    }

    private Stmt varAssignment() {
        Expr initializer = expression();
        consume(INTO, "Expect a variable to declare.");
        Token name = consume(IDENTIFIER, "Expect container name.");
        consume(DOT, "Expect '.' after variable declaration.");
        return new Stmt.Assign(name, initializer);
    }

    private Stmt ifStatement() {
        Expr condition = expression();
        consume(THEN, "Expect 'then' after expression.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) {
            elseBranch = statement();
        }

        consume(AND, "Expect an 'and' after if-statement.");
        consume(CONTINUE, "Expect 'continue' after if-statement.");
        consume(DOT, "Expect '.' after if-statement.");

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    private Stmt whileStatement() {
        Stmt body = statement();
        consume(UNTIL, "Expect an 'until' after while-statement body.");
        Expr condition = expression();
        consume(DOT, "Expect '.' after while-statement.");
        
        return new Stmt.While(condition, body);
    }

    private Stmt returnStatement() {
        Expr value = expression();
        consume(DOT, "Expect '.' after value.");
        return new Stmt.Return(value);
    }

    private Stmt printStatement() {
        Expr value = expression();
        consume(DOT, "Expect '.' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(DOT, "Expect '.' after expression.");
        return null; //new Stmt.Expression(expr);
    }


    /* General parsing utility methods */

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(peek(), message);
    }

    /** Consumes the next token if it matches any of the given type(s) */
    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private ParseError error(Token token, String message) {
        Kitchen.error(token.line, message);
        return new ParseError();
    }
    
    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON) return;

            switch (peek().type) {
                case CLASS: case FUN: case VAR:
                case FOR: case IF: case WHILE:
                case PRINT: case RETURN:
                    return;
                default:
                    break;
            }

            advance();
        }
    }
}