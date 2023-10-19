grammar Boolean;

expr:
	LEFT_BRACKET expr RIGHT_BRACKET
	| NOT expr
	| expr (OR expr)+
	| expr (AND expr)+
	| VAR
	| ZERO
	| ONE;

LEFT_BRACKET: '(';
RIGHT_BRACKET: ')';
NOT: '~';
OR: '+';
AND: '&';
VAR: (('a' ..'z') | ('A' ..'Z'))+;
ZERO: '0';
ONE: '1';

WS: ( '\t' | ' ' | '\r' | '\n')+ -> channel(HIDDEN);
ERROR:
	. {
    System.err.println("Invalid char: " + getText() + " at line " + getLine());
    } -> channel(HIDDEN);