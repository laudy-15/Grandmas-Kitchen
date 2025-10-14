package kitchen;

enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, TILDE,

    // One or two character tokens.
    ARROW_DASH, ARROW_HEAD, BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, TO, GRAB, PUT, RETURN, IF,
    ELSE, FUN, TRUE, FALSE, NIL, CLASS,  OR, PRINT,
    SUPER, THIS, VAR, WHILE,


    EOF
}
