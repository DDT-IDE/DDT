
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
7 * #error(TYPE_AS_EXP_VALUE)《int》*.init

#AST_STRUCTURE_EXPECTED:
ExpInfix(ExpInfix(Integer  ExpReference(RefPrimitive))  ExpReference(RefModuleQualified(?)))

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

