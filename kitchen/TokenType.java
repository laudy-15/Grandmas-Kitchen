package kitchen;

enum TokenType {
    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,
    TILDE,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Keywords.
    AND, CLASS, ELSE, FALSE, FUN,
    FOR, IF, THEN, NIL, OR, PRINT,
    RETURN, SUPER, THIS, TRUE,
    VAR, WHILE, INTO, UNTIL, CONTINUE,
    GRAB, ADD, POUR, IS, TOP, OF, REST,
    EMPTY, NOT,

    // CHAPTER 9 CHALLENGE
    BREAK,

    EOF
}