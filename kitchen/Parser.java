package kitchen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private Expr expression() {
        return first();
    }

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDeclaration();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    private Stmt statement() {
        if (match(GRAB)) return varDeclaration();
        if (match (ADD) || match(OR) || match(POUR)) return varAssignment();
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        if (match(PRINT)) return printStatement();
        return expressionStatement();
    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expect container name.");

        consume(DOT, "Expect '.' after variable definition.");
        return new Stmt.Var(name, null);
    }

    private Stmt varAssignment() {
        Expr initializer = expression();
        consume(INTO, "Expect a variable to declare.");
        Token name = consume(IDENTIFIER, "Expect container name.");
        consume(DOT, "Expect '.' after variable declaration.");
        return new Stmt.Var(name, initializer);
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

    private Stmt printStatement() {
        Expr value = expression();
        consume(DOT, "Expect '.' after value.");
        return new Stmt.Print(value);
    }

    private Stmt expressionStatement() {
        Expr expr = expression();
        consume(DOT, "Expect '.' after expression.");
        return new Stmt.Expression(expr);
    }

    // "top of <container>"
    private Expr first() {
        Expr expr = rest();
        consume(OF, "Expect 'of' after 'top'.");
        Token container = consume(IDENTIFIER, "Expect a container after 'top of'.");

        expr = Expr.Property(TOP, container);

        return expr;
    }

    // "rest of <container>"
    private Expr rest() {
        Expr expr = comparison();
        consume(OF, "Expect 'of' after 'rest'.");
        Token container = consume(IDENTIFIER, "Expect a container after 'rest of'.");

        expr = Expr.Property(REST, container);

        return expr;
    }

    // <container> is [not] empty
    private Expr comparison() {
        Expr expr = count();

        Token container = previous();
        Token operator = advance();
        Token not = null;

        if (match(NOT)) {
            not = previous();
        }

        consume(EMPTY, "Expect an 'empty'.");
        expr = Expr.Binary(container, operator, new Token(EMPTY, "empty", null, container.line));

        //while (match(IS)) {
        //     Token operator = previous();
        //     Expr right = term();
        //     expr = new Expr.Binary(expr, operator, right);
        // }
        return expr;


    }



    // private Expr assignment() {
    //     Expr expr = or();

    //     if (match(EQUAL)) {
    //         Token equals = previous(); // for error reporting
    //         Expr value = assignment();

    //         if (expr instanceof Expr.Variable) {
    //             Token name = ((Expr.Variable) expr).name;
    //             return new Expr.Assign(name, value);
    //         }
    //         error(equals, "Invalid assignment target.");
    //     }

    //     return expr;
    // }

    // private Expr or() {
    //     Expr expr = and();

    //     while (match(OR)) {
    //         Token operator = previous();
    //         Expr right = and();
    //         expr = new Expr.Logical(expr, operator, right);
    //     }

    //     return expr;
    // }

    // private Expr and() {
    //     Expr expr = equality();

    //     while (match(AND)) {
    //         Token operator = previous();
    //         Expr right = equality();
    //         expr = new Expr.Logical(expr, operator, right);
    //     }
    //     return expr;
    // }



    // private Expr comparison() {
    //     Expr expr = term();

    //     while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
    //         Token operator = previous();
    //         Expr right = term();
    //         expr = new Expr.Binary(expr, operator, right);
    //     }
    //     return expr;
    // }

    // private Expr term() {
    //     Expr expr = factor();

    //     while (match(MINUS, PLUS)) {
    //         Token operator = previous();
    //         Expr right = factor();
    //         expr = new Expr.Binary(expr, operator, right);
    //     }
    //     return expr;
    // }

    // private Expr factor() {
    //     Expr expr = unary();

    //     while (match(SLASH, STAR)) {
    //         Token operator = previous();
    //         Expr right = unary();
    //         expr = new Expr.Binary(expr, operator, right);
    //     }
    //     return expr;
    // }

    // private Expr unary() {
    //     if (match(BANG, MINUS)) {
    //         Token operator = previous();
    //         Expr right = unary();
    //         return new Expr.Unary(operator, right);
    //     }
    //     return primary();
    // }

    // private Expr primary() {
    //     if (match(FALSE)) return new Expr.Literal(false);
    //     if (match(TRUE)) return new Expr.Literal(true);
    //     if (match(NIL)) return new Expr.Literal(null);

    //     if (match(NUMBER, STRING)) {
    //         return new Expr.Literal(previous().literal);
    //     }

    //     if (match(IDENTIFIER)) {
    //         return new Expr.Variable(previous());
    //     }

    //     if (match(LEFT_PAREN)) {
    //         Expr expr = expression();
    //         consume(RIGHT_PAREN, "Expect ')' after expression.");
    //         return new Expr.Grouping(expr);
    //     }

    //     throw error(peek(), "Expect expression.");
    // }




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