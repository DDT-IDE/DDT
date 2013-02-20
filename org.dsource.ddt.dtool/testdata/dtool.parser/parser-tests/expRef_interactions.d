▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
typeid( 7 * #error(TYPE_AS_EXP_VALUE)《int》 )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  ExpInfix(Integer ExpReference(RefPrimitive))  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
typeid(foo *** #@X《foo●#error(TYPE_AS_EXP_VALUE){int}》)
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  ExpInfix(* ExpPrefix(ExpPrefix(ExpReference(#@《RefIdentifier●RefPrimitive》(X) ))) )  )
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
typeid(foo * * * #@X《 ● [foo*]》 )
#AST_STRUCTURE_EXPECTED:
ExpTypeId( #@《●RefIndexing(》(X) 
  RefTypePointer(RefTypePointer(RefTypePointer(RefIdentifier))) 
  #@《●RefTypePointer(RefIdentifier) )》(X)
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
typeid(foo[])
#AST_STRUCTURE_EXPECTED:
ExpTypeId(RefTypeDynArray(RefIdentifier))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
typeid(foo[7*7])  // TODO
#AST_STRUCTURE_EXPECTED:
ExpTypeId(RefIndexing(RefIdentifier ExpInfix(Integer Integer)))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ ambiguous refOrExp
#PARSE(EXPRESSION)
typeid(foo * [])
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  RefTypeDynArray(RefTypePointer(RefIdentifier) )  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
typeid(foo *[1*bar]* zee)
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  ExpInfix(
  ExpInfix(ExpReference(?) ExpLiteralArray(ExpInfix(* *)) ) 
  ExpReference(?)
))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
typeid(foo *[1*bar]* )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  RefTypePointer(RefIndexing( RefTypePointer(RefIdentifier) ExpInfix(* *) )) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ ambiguous refOrExp
#PARSE(EXPRESSION)
typeid(foo * [6])
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  RefIndexing(RefTypePointer(RefIdentifier) Integer)  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ ambiguous refOrExp
#PARSE(EXPRESSION)
typeid( foo*[xxx]*[bar * [6]] )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  RefIndexing(
  RefTypePointer(RefIndexing(RefTypePointer(RefIdentifier)  RefIdentifier))
  RefIndexing(RefTypePointer(RefIdentifier) Integer)
))

Ⓗ▂▂▂▂▂▂▂
#@NO_EXP《#?AST_STRUCTURE_EXPECTED!【/*MISSING_EXP*/ #error(EXPRULE_exp)●MissingExpression】》
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ array literal turing the parsing into exp
#PARSE(EXPRESSION)
typeid(foo * #@X《[bar:bar]●[1,2]●[ #@NO_EXP ,2]》  * #@Z《#error(EXPRULE_exp)●[bar*#error(EXPRULE_exp)]》   )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  ExpInfix(
  ExpInfix(ExpReference(?) 
    #@《ExpLiteralMapArray(*)●ExpLiteralArray(Integer Integer)●ExpLiteralArray(#@NO_EXP Integer)》(X) ) 
  #@《  ●ExpLiteralArray(ExpInfix(ExpReference(RefIdentifier)))》(Z)
))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ array literal turing the parsing into ref
#PARSE(EXPRESSION)【
typeid(foo *[bar*]* #error(EXP_CLOSE_PARENS) 】 zee )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  RefTypePointer(RefIndexing( RefTypePointer(RefIdentifier) RefTypePointer(RefIdentifier) )) )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ ref primitive turing the parsing into ref
#PARSE(EXPRESSION)【
typeid(int * #error(EXP_CLOSE_PARENS) 】 bar )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  RefTypePointer(RefPrimitive)  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ ref primitive turing the parsing into ref  -- *under array literal*
#PARSE(EXPRESSION)【
typeid(foo * [int * #error(EXP_CLOSE_BRACKET) #error(EXP_CLOSE_PARENS) 】 bar ] )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  RefIndexing( RefTypePointer(RefIdentifier) RefTypePointer(RefPrimitive) )  )

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ array literal turing the parsing into ref  -- *under array literal*
#PARSE(EXPRESSION)
typeid(foo * [bar * [zee *]] )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  RefIndexing( 
  RefTypePointer(RefIdentifier) 
  RefIndexing(RefTypePointer(RefIdentifier) RefTypePointer(RefIdentifier)) )
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ test op category mismatch
#PARSE(EXPRESSION)
typeid(foo / * #error(EXPRULE_exp) )
#AST_STRUCTURE_EXPECTED:
ExpTypeId(  ExpInfix(ExpReference(?) ExpPrefix() )  )


