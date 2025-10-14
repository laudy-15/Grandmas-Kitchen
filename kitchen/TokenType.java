package kitchen;

enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT,

    // One or two character tokens.
    ARROW,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, TO, GRAB, PUT, FOR, USING, SERVE, IF,
    ELSE,


    EOF
}
