package kitchen;

enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, TILDE,

    // One or two character tokens.
    ARROW_DASH, ARROW_HEAD,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, TO, GRAB, PUT, ONTO, FOR, USING, SERVE, IF,
    ELSE, RECIPE, TRUE, FALSE, 


    EOF
}
