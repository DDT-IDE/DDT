
grammar SimpleParser;


options {
  language=Java;
  output=AST;
} 

tokens {
	PLUS 	= '+' ;
	MINUS	= '-' ;
	MULT	= '*' ; 
	DIV	= '/' ;
	AGE;
}

entry : (d=DOB n=NAME a=AGE(SEMI)
      { 
        System.out.println(
          "Name: "    + 
          n.getText() +
          ", Age: "   +
          a.getText() + 
          ", DOB: "   +
          d.getText()
        );
      })*
      ;

NAME : ('a'..'z'|'A'..'Z')+;

/*DOB  : ('0'..'9' '0'..'9' '/')=> 
       (('0'..'9')('0'..'9')'/')(('0'..'9')('0'..'9')'/')('0'..'9')('0'..'9') 
     | ('0'..'9' '0'..'9') 'satou'  ;

AGE :	('===')+    ;
*/
DOB : ('0'..'9')('0'..'9')'/' ('0'..'9')('0'..'9')'/' ('0'..'9')('0'..'9') ;
AGE : ('0'..'9')+ ;


WS     :
    (' ' 
    | '\t' 
    | '\r' '\n' 
    | '\n'      
    ) 
    { channel = 99; } 
  ;
  
  
fragment WSNL 	:
      '\r' '\n' 
    | '\n'      ;

SEMI : ';' ;


