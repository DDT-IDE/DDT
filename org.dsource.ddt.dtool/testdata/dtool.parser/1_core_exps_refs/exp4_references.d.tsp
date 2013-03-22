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
#@PREFIX!《
  ►#?AST_STRUCTURE_EXPECTED!【int●RefPrimitive】●
¤》
#@PREFIX_TODO!《
  ►#?AST_STRUCTURE_EXPECTED!【int●RefPrimitive】●
  ►#?AST_STRUCTURE_EXPECTED!【(int)●ExpParentheses(RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【(Foo[int])●RefIndexing(RefIdentifier RefPrimitive)】●
  ►#?AST_STRUCTURE_EXPECTED!【(Foo[foo*])●RefIndexing(RefIdentifier ?)】●
¤》
#@PREFIX.init
#AST_STRUCTURE_EXPECTED:
ExpReference(RefQualified(#@PREFIX RefIdentifier))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂ This tests valid references that are being parse as expressions
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
