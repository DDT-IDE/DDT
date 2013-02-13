
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
7 * #error(TYPE_AS_EXP_VALUE)《int》*.init

#AST_STRUCTURE_EXPECTED:
InfixExpression(InfixExpression(Integer  ExpReference(RefPrimitive))  ExpReference(RefModuleQualified(?)))

▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
7 * #error(TYPE_AS_EXP_VALUE)《int》 ** .init

#AST_STRUCTURE_EXPECTED:
InfixExpression(
  InfixExpression(Integer ExpReference(RefPrimitive))  
  PrefixExpression(ExpReference(RefModuleQualified(?)))
)
▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂▂
#PARSE(EXPRESSION)
7 + foo * * #error(EXPRULE_exp)

#AST_STRUCTURE_EXPECTED:
InfixExpression(Integer  InfixExpression(ExpReference(RefIdentifier)  PrefixExpression()))

