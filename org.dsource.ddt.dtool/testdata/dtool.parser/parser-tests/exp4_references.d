▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
foo
#AST_STRUCTURE_EXPECTED:
ExpReference(RefIdentifier)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
#error(TYPE_AS_EXP_VALUE)《int》
#AST_STRUCTURE_EXPECTED:
ExpReference(RefPrimitive)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
int.init
#AST_STRUCTURE_EXPECTED:
ExpReference(RefQualified(RefPrimitive RefIdentifier))
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
Foo[int].bar
#AST_STRUCTURE_EXPECTED:
ExpReference(RefQualified(RefIndexing(? ?) RefIdentifier))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ RawType - Should this be an error?
#PARSE(EXPRESSION)
#@LIT_KEYWORDS《
►                          foo●
►#error(TYPE_AS_EXP_VALUE)《int》●
►#error(TYPE_AS_EXP_VALUE)《char》*#error(EXPRULE_exp)●
►                          foo* #error(EXPRULE_exp)●
►#error(TYPE_AS_EXP_VALUE)《foo[]》●
►#error(TYPE_AS_EXP_VALUE)《foo[int]》●
►#error(TYPE_AS_EXP_VALUE)《int[foo]》●
¤》
#AST_STRUCTURE_EXPECTED:
#@《
  ►ExpReference(RefIdentifier)●
  ►ExpReference(RefPrimitive)●
  ►ExpInfix(ExpReference(RefPrimitive))●
  ►ExpInfix(ExpReference(RefIdentifier))●
  ►ExpReference(RefTypeDynArray(?))●
  ►ExpReference(RefIndexing(? ?))●
  ►ExpReference(RefIndexing(? ?))●
¤》(LIT_KEYWORDS)

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION) #@EXP_UNARY_REFS
#AST_STRUCTURE_EXPECTED:
#@EXP_UNARY_REFS

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
foo * #error(TYPE_AS_EXP_VALUE)《int》*.init

#AST_STRUCTURE_EXPECTED:
ExpInfix(ExpInfix(ExpReference(RefIdentifier)  ExpReference(RefPrimitive))  ExpReference(RefModuleQualified(?)))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
7 * #error(TYPE_AS_EXP_VALUE)《int》 ** .init

#AST_STRUCTURE_EXPECTED:
ExpInfix(
  ExpInfix(Integer ExpReference(RefPrimitive))  
  ExpPrefix(ExpReference(RefModuleQualified(?)))
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
7 + foo * * #error(EXPRULE_exp)

#AST_STRUCTURE_EXPECTED:
ExpInfix(Integer  ExpInfix(ExpReference(RefIdentifier)  ExpPrefix()))
