grammar BigCalc;

prog
    : expressionStatement + EOF
    ;

expressionStatement
    : (assignment | expression)* SC
    ;

expression
    : expression op=('*' | '/') expression      # mulDiv
    | expression op=('+' | '-') expression      # addSub
    | LPAREN expression RPAREN                  # parens
    | expression '?' expression ':' expression  # condi
    | id                                        # var
    | Number                                    # num
    ;

LPAREN
    : '('
    ;
RPAREN
    : ')'
    ;

assignment
    : ID EQ expression
    ;

id
    : ID
    ;

Exponent
    : ('E' | 'e') (('+'|'-')? Digit+)
    ;

Number
    : Digit* '.' Digit+
    | Digit+
    | Digit* '.' Digit+ Exponent?
    ;

Digit
    : [0-9]
    ;

ID
    : [a-zA-Z][a-zA-Z0-9_]*
    ;

EQ
    : '='
    ;

SC
    : ';'
    ;

WS
        : [ \t\r\n\u000C]+ -> skip
        ;
COMMENT
        : '/*' .*? '*/' -> skip
        ;
LINE_COMMENT
        : '//' ~[\r\n]* -> skip
        ;



