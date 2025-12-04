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
    private final List<Container> containers = new ArrayList<>();
    private final List<Ingredient> ingredients = new ArrayList<>();
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
        if (match(GRAB)) return varDefinition();
        if (match (ADD) || match(COMBINE) || match(POUR)) return varAssignment();
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        if (match(MIX)) return mixStatement();
        if (match(SHAKE)) return shakeStatement();
        if (match(RECIPE)) return recipeStatement();
        if (match(RETURN)) return returnStatement();
        if (match(PRINT)) return printStatement();
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
            consume(OF, "expected `of` after 'top'");
            Container cont = container();
            return new Expr.Top(cont);
        }

        // rest of <container>
        if (check(TokenType.REST)) {
            Token tok = advance(); // string 'rest'
            consume(OF, "expected `of` after 'rest'");
            Container cont = container();
            return new Expr.Rest(cont);
        }

        // <expr> with <expr>
        if (check(TokenType.WITH)) {
            // TO-DO get the previous expression??
        }

        // <container>
        if (check(TokenType.IDENTIFIER)) {
            Container cont = container(); //this could also be the function name

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
            if (check(TokenType.LEFT_PAREN)) {
                // function call
                Token paren = advance(); // consume '('
                List<Expr> arguments = new ArrayList<>();
                if (!check(RIGHT_PAREN)) {
                    do {
                        arguments.add(expression());
                    } while (match(COMMA));
                }
                consume(RIGHT_PAREN, "Expect ')' after arguments.");
                
                return new Expr.Call(cont, paren, arguments);
            }
            

            return cont; 
        }

        // <name> [with <expr>+]? -- function calls 

        if (check(TokenType.STRING)) {
            Token tok = advance();
            return new Expr.Literal(tok.lexeme);
        }
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
        // for (Expr.Container cont: containers) {
        //     if (cont.tok.lexeme.equals(tok.lexeme)) {
        //         System.out.println("Container already exists: " + tok.lexeme);
        //     } else {
        //         containers.add(new Expr.Container(tok));
        //         System.out.println("Containers: " + containers.toString());
        //         break;
        //     }
        // }
        containers.add(new Expr.Container(tok));
        return new Expr.Container(tok);
    }


    /* Statement Methods: */

    private Stmt declaration() {
        try {
            if (match(VAR)) return varDefinition();
            return statement();
        } catch (ParseError error) {
            synchronize();
            return null;
        }
    }

    // Define : Token keyword
    private Stmt varDefinition() {
        Token name = consume(IDENTIFIER, "Expect container name.");

        consume(DOT, "Expect '.' after variable definition.");
        return new Stmt.Define(name);
    }

    // Assign: Token cont, Expr object
    private Stmt varAssignment() {
        Expr initializer = expression();
        consume(INTO, "Expect a variable to declare.");
        Token name = consume(IDENTIFIER, "Expect container name.");
        consume(DOT, "Expect '.' after variable declaration.");
        return new Stmt.Assign(name, initializer);
    }

    // If : Expr condition, Stmt thenBranch
    private Stmt ifStatement() {
        Expr condition = expression();
        consume(THEN, "Expect 'then' after expression.");

        Stmt thenBranch = statement();
        Stmt elseBranch = null;
        if (match(ELSE)) { //"otherwise"
            elseBranch = statement();
        }

        consume(AND, "Expect an 'and' after if-statement.");
        consume(CONTINUE, "Expect 'continue' after if-statement.");
        consume(DOT, "Expect '.' after if-statement.");

        return new Stmt.If(condition, thenBranch, elseBranch);
    }

    // While : Expr condition, Stmt body
    private Stmt whileStatement() {
        List<Stmt> body = new ArrayList<>();
        while (!check(UNTIL)) {
            body.add(statement());
        }
        consume(UNTIL, "Expect an 'until' after while-statement body.");
        Expr condition = expression();
        consume(DOT, "Expect '.' after while-statement.");
        
        return new Stmt.While(body, condition);
    }

    // Mix : Token cont
    private Stmt mixStatement() {
        Token cont = consume(TokenType.IDENTIFIER, "expected a container after 'mix'");
        consume(DOT, "Expect '.' after mix statement.");
        return new Stmt.Mix(cont);
    }

    // Shake : Token cont
    private Stmt shakeStatement() {
        Token cont = consume(TokenType.IDENTIFIER, "expected a container after 'shake'");
        consume(DOT, "Expect '.' after shake statement.");
        return new Stmt.Shake(cont);
    }

    // Recipe : Token name, List<Token> params, List<Stmt> body
    // Function Declaration
    private Stmt recipeStatement() {
        consume(FOR, "expected 'for' after 'recipe'");
        Token name = consume(TokenType.IDENTIFIER, "expected function name after 'recipe for'");
        List<Token> params = new ArrayList<>();
        if (match(USING)) {
            params.add(consume(TokenType.IDENTIFIER, "expected ingredient name as parameter"));

            while (match(COMMA)) {
                params.add(consume(TokenType.IDENTIFIER, "expected ingredient name as parameter"));
            }
        }
        consume(LEFT_PAREN, "Expect '(' after function declaration header.");

        List<Stmt> body = new ArrayList<>();
        while (!check(RIGHT_PAREN) && !isAtEnd()) {
            body.add(declaration());
        }

        consume(RIGHT_PAREN, "Expect ')' to close the recipe.");
        consume(DOT, "Expect '.' after shake statement.");

        return new Stmt.Recipe(name, params, body);
    }


    // Serve : Expr value
    private Stmt returnStatement() {
        Expr value = expression();
        consume(DOT, "Expect '.' after value.");
        return new Stmt.Serve(value);
    }

    // Print : Expr value
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